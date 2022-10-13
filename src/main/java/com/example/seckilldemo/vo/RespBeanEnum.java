package com.example.seckilldemo.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 86187
 */
@ToString
@AllArgsConstructor
@Getter
public enum RespBeanEnum {

    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),
    //登录模块
    LOGIN_ERROR(500210,"用户名或密码不正确"),
    MOBILE_ERROR(500211,"手机号码格式不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    MOBILE_NOT_EXIST(500213,"手机号码不存在"),
    PASSWORD_UPDATE_FAIL(50214,"更新密码失败"),
    SESSION_ERROR(50215,"用户不存在"),
    REPUEST_ILLEGAL(500502,"非法请求, 请重新尝试"),
    EMPTY_STOCK(500500,"库存不足"),
    REPEATR_ERROR(500501,"每人限购一件"),
    ERROR_CAPTCHA(500503,"验证码错误,请重新输入"),

    ACCESS_LIMIT_REAHCED(500503,"访问受限"),
    ORDER_NOT_EXIST(500300,"订单信息")

    ;

    private final Integer code;
    private final String message;
}
