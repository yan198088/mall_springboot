package com.yxy.service;


import com.yxy.model.pojo.User;

public interface UserService {

    User selectByPrimaryKey(Integer id);

    void register(String userName, String password);

    User login(String userName, String password);

    void updateInformation(User user);

    boolean checkAdminRole(User user);
}
