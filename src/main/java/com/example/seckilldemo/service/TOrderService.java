package com.example.seckilldemo.service;

import com.example.seckilldemo.entity.TOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.exception.GlobalException;
import com.example.seckilldemo.vo.GoodsVo;
import com.example.seckilldemo.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author testjava
 * @since 2022-10-09
 */
public interface TOrderService extends IService<TOrder> {

    TOrder seckill(TUser tUser, GoodsVo goodsVo);

    OrderDetailVo detail(Long orderId) throws GlobalException;

    String createPath(TUser user, Long goodsId);

    boolean checkPath(TUser tUser, Long goodsId, String path);

    boolean checkCaptcha(TUser user, Long goodsId, String captcha);

}
