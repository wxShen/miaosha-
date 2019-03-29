package com.example.moocmiaosha.service;

import com.example.moocmiaosha.error.BusinessException;
import com.example.moocmiaosha.service.model.UserModel;

public interface UserService {

    UserModel getUserbyId(Integer id);

    void register(UserModel userModel) throws BusinessException;

    /**
     * telphone :用户注册手机
     * password 用户注册加密后的密码
     * */

    UserModel validateLogin(String telphone,String encrptPassword) throws  BusinessException;



}
