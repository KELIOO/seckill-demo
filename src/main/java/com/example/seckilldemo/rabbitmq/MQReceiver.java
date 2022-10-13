package com.example.seckilldemo.rabbitmq;


import com.example.seckilldemo.entity.SeckillMessage;
import com.example.seckilldemo.entity.TSeckillOrder;
import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.service.TGoodsService;
import com.example.seckilldemo.service.TOrderService;
import com.example.seckilldemo.util.JsonUtil;
import com.example.seckilldemo.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 86187
 */
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TGoodsService tGoodsService;
    @Autowired
    private TOrderService tOrderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收到的消息" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);

        Long goodId = seckillMessage.getGoodId();
        TUser user = seckillMessage.getUser();
        GoodsVo goodsVo = tGoodsService.findGoodsVoByGoodsId(goodId);
        if(goodsVo.getStockCount() < 1){
            return;
        }

        //判断是否重复抢购
        TSeckillOrder seckillOrder = (TSeckillOrder) redisTemplate.opsForValue().get("order" + user.getId() + ":" + goodsVo.getId());

        if(seckillOrder != null){
            return;
        }
        //下单操作
        tOrderService.seckill(user,goodsVo);
    }
}
