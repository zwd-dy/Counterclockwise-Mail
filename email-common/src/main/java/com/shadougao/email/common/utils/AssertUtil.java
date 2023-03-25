package com.shadougao.email.common.utils;


import cn.hutool.core.util.StrUtil;
import com.shadougao.email.common.result.ResponseCode;
import com.shadougao.email.common.result.exception.BadRequestException;

public class AssertUtil {

    public static void validParamStringEmpty(String param) {
        validParam(StrUtil.isBlank(param), "参数不能为空");
    }

    // 400错误 参数校验失败
    public static void validParam(boolean flag, String msg) {
        isBadRequest(flag, ResponseCode.VALIDATE_FAILED, msg);
    }

    // 用户未登录
    public static void validUserLogin(boolean flag) {
        isBadRequest(flag, ResponseCode.USER_NOT_LOGIN, "用户未登陆");
    }


    // 资源已存在
    public static void validExist(boolean flag, String msg) {
        isBadRequest(flag, ResponseCode.RESOURCE_ALREADY_EXIST, msg);
    }

    // 资源402
    public static void validPayment(boolean flag, String msg) {
        isBadRequest(flag, ResponseCode.PAYMENT_REQUIRED, msg);
    }

    public static void validAuth(boolean flag) {
        isBadRequest(flag, ResponseCode.USER_NOT_AUTH, ResponseCode.USER_NOT_AUTH.getMessage());
    }

    // 判断400错误
    public static void isBadRequest(boolean flag, ResponseCode code, String msg) {
        if (flag) {
            throw new BadRequestException(code, msg);
        }
    }
}
