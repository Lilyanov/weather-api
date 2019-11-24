package com.yavor.projects.weather.api.controller;

import com.yavor.projects.weather.api.dto.TokenDto;
import com.yavor.projects.weather.api.entity.User;
import com.yavor.projects.weather.api.security.UnauthorizedExcpetion;
import com.yavor.projects.weather.api.service.AuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping(value="/generate")
    public ResponseEntity<TokenDto> generateJWT(@RequestBody User user) throws UnauthorizedExcpetion {
        var token = authorizationService.generateJWT(user);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @ExceptionHandler(value = {UnauthorizedExcpetion.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<String> handleUnauthenticated(UnauthorizedExcpetion e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

}
