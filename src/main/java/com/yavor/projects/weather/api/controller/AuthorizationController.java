package com.yavor.projects.weather.api.controller;

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

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping(value = "/get-token")
    public ResponseEntity<User> generateJWT(@RequestBody User user, HttpServletRequest request) throws UnauthorizedExcpetion {
        var existingUser = authorizationService.generateJWT(request.getRemoteAddr(), user);
        return new ResponseEntity<>(existingUser, HttpStatus.OK);
    }

    @ExceptionHandler(value = {UnauthorizedExcpetion.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<String> handleUnauthenticated(UnauthorizedExcpetion e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

}
