package com.example.seckilldemo.controller;

import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.exception.GlobalException;
import com.example.seckilldemo.service.TUserService;
import com.example.seckilldemo.vo.LoginVo;
import com.example.seckilldemo.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


/**
 * @author 86187
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class UserController {
    @Autowired
    private TUserService userService;

    /**
     * 用户信息(测试)
     * @return
     */

    @RequestMapping("/info")
    public RespBean info(TUser tUser){
        return RespBean.success(tUser);
    }

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 功能描述: 登录功能
     *
     * @param:
     * @return:
     * 乐字节：专注线上IT培训
     * 答疑老师微信：lezijie
     *
     * @since: 1.0.0
     * @Author: zhoubin*/
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) throws GlobalException {
        return userService.doLogin(loginVo,request,response);
    }
}
