package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.entity.User;
import com.yavor.projects.weather.api.security.UnauthorizedExcpetion;

public interface AuthorizationService {

    User generateJWT(User user) throws UnauthorizedExcpetion;

    String verifyAndGetUsername(String token);
}