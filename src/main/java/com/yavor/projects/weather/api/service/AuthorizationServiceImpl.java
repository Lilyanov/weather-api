package com.yavor.projects.weather.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.yavor.projects.weather.api.entity.User;
import com.yavor.projects.weather.api.repository.UserRepository;
import com.yavor.projects.weather.api.security.UnauthorizedExcpetion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

    private final Algorithm algorithm;

    private final JWTVerifier jwtVerifier;

    private final UserRepository userRepository;

    private LoadingCache<String, Integer> attemptsCache;


    public AuthorizationServiceImpl(ResourceLoader resourceLoader, UserRepository userRepository) throws Exception {
        var keyFactory = KeyFactory.getInstance("RSA");

        var privateKeyBytes = resourceLoader.getResource("classpath:private_key.der").getInputStream().readAllBytes();
        var privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        var publicKeyBytes = resourceLoader.getResource("classpath:public_key.der").getInputStream().readAllBytes();
        var publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        this.algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
        this.jwtVerifier = JWT.require(algorithm).build();
        this.userRepository = userRepository;
        this.attemptsCache = CacheBuilder.newBuilder().
                expireAfterWrite(1, TimeUnit.HOURS).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }

    @Override
    public User generateJWT(String ip, User user) throws UnauthorizedExcpetion {
        if (ip == null || ip.isEmpty()) {
            throw new UnauthorizedExcpetion("Request couldn't be processed");
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()
                || user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new UnauthorizedExcpetion("Username and password are required !");
        }
        Optional<User> optionalUser = userRepository.findById(user.getUsername().toLowerCase());
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedExcpetion(String.format("User with username %s doesn't exist !", user.getUsername()));
        }
        if (isBlocked(ip + user.getUsername())) {
            throw new UnauthorizedExcpetion("User is blocked due to too much incorrect attempts. Please try again later!");
        }
        var existingUser = optionalUser.get();
        if (!existingUser.getPassword().equals(user.getPassword())) {
            loginFailed(ip + user.getUsername());
            throw new UnauthorizedExcpetion("Incorrect username or password !");
        }
        loginSucceeded(ip + user.getUsername());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 12);
        var token = JWT.create()
                .withIssuer("weather.api.bg")
                .withIssuedAt(new Date())
                .withExpiresAt(cal.getTime())
                .withClaim("username", existingUser.getUsername())
                .sign(algorithm);
        existingUser.setToken(token);
        existingUser.setPassword("");
        return existingUser;
    }

    @Override
    public User verifyAndGetUser(final String token) {
        var username = jwtVerifier.verify(token).getClaim("username").asString();
        var optional = userRepository.findById(username);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("User with this username doesn't exist");
        }
        return optional.get();
    }

    private void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    private void loginFailed(String ip) {
        LOGGER.info("Login failed for IP: {}", ip);
        int attempts = 0;
        try {
            attempts = attemptsCache.get(ip);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(ip, attempts);
    }

    private boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= 5;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
