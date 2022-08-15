package com.mmall.service.Impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.sun.corba.se.spi.activation.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mahong on 2022/7/28.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        System.out.println("lpppppp");
        System.out.println(username);
        System.out.println(password);
        int resultCount = userMapper.checkUsername(username);
        System.out.println("resultCount");
        System.out.println(resultCount);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        System.out.println("selectLogin user");
        System.out.println(user);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    public ServerResponse<String> register(User user){
         // checkValid return的内容不能直接return到register函数外，要validResponse承接一下
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!validResponse.isSuccess()) {
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if(!validResponse.isSuccess()) {
            return validResponse;
        }
//        int resultCount = userMapper.checkUsername(user.getUsername());
//        if (resultCount > 0) {
//            return ServerResponse.createByErrorMessage("用户名已经存在");
//        }
//        resultCount = userMapper.checkEmail(user.getEmail());
//        if (resultCount > 0) {
//            return  ServerResponse.createByErrorMessage("邮箱已经存在");
//        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str, String type) {
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(type)) {
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }
            if(Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已经存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            // 用户不存在
            return  ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(question)){
            return ServerResponse.createByErrorMessage(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }
}
