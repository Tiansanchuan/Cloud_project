package com.atguigu.cloud.service;

public interface JWTService {
    public String getToken(String UserId);
    public String decodeToken(String token);
    public Boolean verify(String token);
}
