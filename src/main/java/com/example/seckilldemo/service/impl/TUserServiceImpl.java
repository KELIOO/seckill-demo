package com.example.seckilldemo.service.impl;

import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.exception.GlobalException;
import com.example.seckilldemo.mapper.TUserMapper;
import com.example.seckilldemo.service.TUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.util.CookieUtil;
import com.example.seckilldemo.util.MD5Util;
import com.example.seckilldemo.util.UUIDUtil;

import com.example.seckilldemo.vo.LoginVo;
import com.example.seckilldemo.vo.RespBean;
import com.example.seckilldemo.vo.RespBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author testjava
 * @since 2022-10-07
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements TUserService {

    @Autowired(required = false)
    private TUserMapper tUserMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) throws GlobalException {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        /*if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }*/
        //根据手机号码查询用户
        TUser user = tUserMapper.selectById(mobile);
        if(user == null){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
        if (!MD5Util.formPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成cookie
        String uuid = UUIDUtil.uuid();
        /*request.getSession().setAttribute(uuid,user);
        CookieUtil.setCookie(request,response,"userUuid",uuid);
       */
        //将用户信息存入redis中
       redisTemplate.opsForValue().set("user:"+uuid,user,60, TimeUnit.MINUTES);
        CookieUtil.setCookie(request,response,"userUuid",uuid);
        return RespBean.success(uuid);
    }

    @Override
    public TUser getUserByCookie(String userUuid,HttpServletRequest request,HttpServletResponse response) {
        if(StringUtils.isEmpty(userUuid)){
            return null;
        }
        TUser user = (TUser)redisTemplate.opsForValue().get("user:" + userUuid);
        if(user!=null){
            CookieUtil.setCookie(request,response,"userUuid",userUuid);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String uuid, String password, HttpServletRequest request, HttpServletResponse response) throws GlobalException {
        TUser user = getUserByCookie(uuid,request,response);
        if(user == null ) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password,user.getSlat()));
        int result = tUserMapper.updateById(user);
        if(result == 1) {
            //修改成功,删除原先缓存中的user
            redisTemplate.delete("user" + uuid);
            return RespBean.success();
        }

        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }

 }

    /**
     * 在更新密码时,缓存中的user的密码已经改变
     * 现在实现,密码修改后缓存也修改
     */


