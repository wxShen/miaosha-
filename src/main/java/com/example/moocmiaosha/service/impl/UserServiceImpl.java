package com.example.moocmiaosha.service.impl;

import com.example.moocmiaosha.dao.UserDOMapper;
import com.example.moocmiaosha.dao.UserPasswordDOMapper;
import com.example.moocmiaosha.datapobject.UserDO;
import com.example.moocmiaosha.datapobject.UserPasswordDO;
import com.example.moocmiaosha.error.BusinessException;
import com.example.moocmiaosha.error.EmBusinessError;
import com.example.moocmiaosha.service.UserService;
import com.example.moocmiaosha.service.model.UserModel;
import com.example.moocmiaosha.validator.ValidationResult;
import com.example.moocmiaosha.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    //实现用户service的后台与数据库的相关的交互的操作

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Override
    public UserModel getUserbyId(Integer id) {

        //调用userDOMApper获取对应用户的dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null){
            return null;
        }
        //通过用户id 获取对应用户的密码的加密信息
        UserPasswordDO passwordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO,passwordDO);
    }

    private UserModel convertFromDataObject(UserDO userDO,UserPasswordDO userPasswordDO){

        if (userDO == null){
            return null;
        }
        UserModel  userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        if (userPasswordDO != null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if (userModel == null){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDTION_ERROR);
        }
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDTION_ERROR,result.getErrMsg());
        }

        //插入数据库 需要的是Do model --》 Dataobject
        UserDO userDO = convertFromModel(userModel);
        try {
            userDOMapper.insertSelective(userDO);
        }catch (DuplicateKeyException ex){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDTION_ERROR,"该手机号已经注册了，请直接登陆");
        }

        System.out.println("当前注册用户的id = " + userDO.getId());
        userModel.setId(userDO.getId());//获得当前数据库中的user 对应的id

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }


    private UserPasswordDO convertPasswordFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException{
        //通过用户的手机获取用户的信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO == null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        UserModel userModel = convertFromDataObject(userDO, userPasswordDO);
        //比对用户信息内加密的密码是否与传输进来的密码相匹配

        if (!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

}
