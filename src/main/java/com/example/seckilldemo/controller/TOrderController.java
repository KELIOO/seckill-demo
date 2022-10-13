package com.example.seckilldemo.controller;


import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.exception.GlobalException;
import com.example.seckilldemo.service.TOrderService;
import com.example.seckilldemo.vo.OrderDetailVo;
import com.example.seckilldemo.vo.RespBean;
import com.example.seckilldemo.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author testjava
 * @since 2022-10-09
 */
@RestController
@RequestMapping("/order")
public class TOrderController {


    @Autowired
    private TOrderService tOrderService;
    /**
     * 订单详情
     * @param tUser
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    public RespBean detail(TUser tUser,Long orderId) throws GlobalException {
        if(tUser == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detailVo = tOrderService.detail(orderId);
        return RespBean.success(detailVo);
    }
}

