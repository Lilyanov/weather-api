package com.yavor.projects.weather.api.security;

public class UnauthorizedExcpetion extends Exception {

    public UnauthorizedExcpetion(String message) {
        super(message);
    }
}
