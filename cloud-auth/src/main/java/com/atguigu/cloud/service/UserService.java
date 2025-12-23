package com.atguigu.cloud.service;

import com.atguigu.cloud.entities.User;

import java.util.List;

public interface UserService {
    public int add(User user);
    public int delete(Long id);
    public int update(User user);
    public User getById(Long id);
    public List<User> getAll();
    //return Id or null
    public Long verify(String username,String password);

}
