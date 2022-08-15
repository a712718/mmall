package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by mahong on 2022/7/28.
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str, String type);
    ServerResponse<String> selectQuestion(String username);
}
