package com.yavor.projects.weather.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.yavor.projects.weather.api.dto.TokenDto;
import com.yavor.projects.weather.api.entity.User;
import com.yavor.projects.weather.api.repository.UserRepository;
import com.yavor.projects.weather.api.security.UnauthorizedExcpetion;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final  Algorithm algorithm;

    private final JWTVerifier jwtVerifier;

    private final UserRepository userRepository;

    public AuthorizationServiceImpl(ResourceLoader resourceLoader, UserRepository userRepository) throws Exception {
        var keyFactory = KeyFactory.getInstance("RSA");

        var privateKeyBytes = resourceLoader.getResource("classpath:private_key.der").getInputStream().readAllBytes();
        var privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        var publicKeyBytes = resourceLoader.getResource("classpath:public_key.der").getInputStream().readAllBytes();
        var publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        this.algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
        this.jwtVerifier = JWT.require(algorithm).build();
        this.userRepository = userRepository;

    }

    @Override
    public TokenDto generateJWT(User user) throws UnauthorizedExcpetion {
        if (user.getUsername() == null || user.getUsername().isEmpty()
                || user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new UnauthorizedExcpetion("Username and password are required !");
        }
        Optional<User> optionalUser = userRepository.findById(user.getUsername());
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedExcpetion(String.format("User with username %s doesn't exist !", user.getUsername()));
        }
        if (!optionalUser.get().getPassword().equals(user.getPassword())) {
            throw new UnauthorizedExcpetion("Incorrect username or password !");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 12);
        var token = JWT.create()
                .withIssuer("weather.api.bg")
                .withIssuedAt(new Date())
                .withExpiresAt(cal.getTime())
                .withClaim("username", optionalUser.get().getUsername())
                .sign(algorithm);

        return new TokenDto(token);
    }

    @Override
    public String verifyAndGetUsername(final String token) {
        return jwtVerifier.verify(token).getClaim("username").asString();
    }
}
