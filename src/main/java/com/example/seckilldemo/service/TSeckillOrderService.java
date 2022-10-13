package com.example.seckilldemo.service;

import com.example.seckilldemo.entity.TSeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.entity.TUser;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author testjava
 * @since 2022-10-09
 */
public interface TSeckillOrderService extends IService<TSeckillOrder> {

    Long getResult(TUser user, Long goodsId) ;


}
