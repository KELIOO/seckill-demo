package com.example.seckilldemo.config;

import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.service.TUserService;
import com.example.seckilldemo.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 86187
 */
@Component
public class UserArgumentResolve implements HandlerMethodArgumentResolver {

    @Autowired
    private TUserService tUserService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //类型比对,成功执行下面方法
        Class<?> aClass = parameter.getParameterType();
        return aClass == TUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        String userUuid = CookieUtil.getCookieValue(request, "userUuid");
        if(StringUtils.isEmpty(userUuid)){
            return null;
        }
        //返回对象
        return tUserService.getUserByCookie(userUuid,request,response);
    }
}
