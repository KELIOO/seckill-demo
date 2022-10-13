package com.example.seckilldemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.seckilldemo.entity.TOrder;
import com.example.seckilldemo.entity.TSeckillGoods;
import com.example.seckilldemo.entity.TSeckillOrder;
import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.exception.GlobalException;
import com.example.seckilldemo.mapper.TOrderMapper;
import com.example.seckilldemo.service.TGoodsService;
import com.example.seckilldemo.service.TOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckilldemo.service.TSeckillGoodsService;
import com.example.seckilldemo.service.TSeckillOrderService;
import com.example.seckilldemo.util.MD5Util;
import com.example.seckilldemo.util.UUIDUtil;
import com.example.seckilldemo.vo.GoodsVo;
import com.example.seckilldemo.vo.OrderDetailVo;
import com.example.seckilldemo.vo.RespBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author testjava
 * @since 2022-10-09
 */
@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements TOrderService {


    @Autowired
    private TSeckillGoodsService tSeckillGoodsService;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    private TSeckillOrderService tSeckillOrderService;
    @Autowired
    private TGoodsService tGoodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public TOrder seckill(TUser tUser, GoodsVo goodsVo) {
        TSeckillGoods goods = tSeckillGoodsService.getOne(new QueryWrapper<TSeckillGoods>().eq("goods_id", goodsVo.getId()));
        goods.setStockCount(goods.getStockCount() -1);
        //boolean update = tSeckillGoodsService.update(goods, new UpdateWrapper<TSeckillGoods>().set("stock_count", goods.getStockCount()).eq("id", goods.getId()).ge("stock_count", 0));
        if(goods.getStockCount() < 1){
            redisTemplate.opsForValue().set("isStockEmpty:"+ goods.getId(), "0");
            return null;
        }
        TOrder order = new TOrder();
        order.setUserId(tUser.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        TSeckillOrder seckillOrder = new TSeckillOrder();
        seckillOrder.setUserId(tUser.getId());
        seckillOrder.setGoodsId(goods.getGoodsId());
        seckillOrder.setOrderId(order.getId());
        tSeckillOrderService.save(seckillOrder);
        //将生成的订单表存入redis
        redisTemplate.opsForValue().set("order"+tUser.getId()+":"+goods.getId(),seckillOrder);
        return order;

    }

    /**
     * 订单详情, 通过对象传值,渲染到页面
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVo detail(Long orderId) throws GlobalException {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        TOrder order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = tGoodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsVo(goodsVo);
        return orderDetailVo;
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(TUser user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid()+ "123456");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId() + ":"+ goodsId,str,60, TimeUnit.SECONDS);
        return str;
    }

    @Override
    public boolean checkPath(TUser tUser, Long goodsId, String path) {
        if(tUser == null || goodsId < 0 || StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String)redisTemplate.opsForValue().get("seckillPath:" + tUser.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    @Override
    public boolean checkCaptcha(TUser user, Long goodsId, String captcha) {
        if(StringUtils.isEmpty(captcha) || user == null || goodsId<0){
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);

        return captcha.equals(redisCaptcha);
    }

}
