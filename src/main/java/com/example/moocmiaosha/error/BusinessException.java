package com.example.moocmiaosha.error;

public class BusinessException extends Exception implements CommonError {


    private CommonError commonError;

    //直接接受EmBusinessError 的传递参数用于构造业务
    public BusinessException(CommonError commonError){
        super();
        this.commonError = commonError;
    }

    //接受自定义的errMsg的方式定义构造业务异常
    public BusinessException(CommonError commonError,String errMsg){
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }


    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
