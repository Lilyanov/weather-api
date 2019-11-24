package com.yavor.projects.weather.api.service;

import com.yavor.projects.weather.api.dto.TokenDto;
import com.yavor.projects.weather.api.entity.User;
import com.yavor.projects.weather.api.security.UnauthorizedExcpetion;

public interface AuthorizationService {

    TokenDto generateJWT(User user) throws UnauthorizedExcpetion;

    String verifyAndGetUsername(String token);
}
