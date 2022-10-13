package com.example.seckilldemo.service;

import com.example.seckilldemo.entity.TUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.exception.GlobalException;
import com.example.seckilldemo.vo.LoginVo;
import com.example.seckilldemo.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author testjava
 * @since 2022-10-07
 */
public interface TUserService extends IService<TUser> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) throws GlobalException;
    //根据cookie获取用户
    TUser getUserByCookie(String userUuid,HttpServletRequest request,HttpServletResponse response);

    RespBean updatePassword(String uuid,String password,HttpServletRequest request,HttpServletResponse response) throws GlobalException;

}
