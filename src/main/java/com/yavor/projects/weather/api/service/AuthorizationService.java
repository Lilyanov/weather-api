package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.entity.User;
import com.yavor.projects.weather.api.security.UnauthorizedExcpetion;

public interface AuthorizationService {

    User generateJWT(String ip, User user) throws UnauthorizedExcpetion;

    User verifyAndGetUser(String token);
}
