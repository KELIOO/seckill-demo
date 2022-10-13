package com.example.seckilldemo.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 86187
 */
@Service
@Slf4j
public class MQsender {
    @Autowired
    private RabbitTemplate rabbitTemplate;


    //发送秒杀信息

    public void sendSeckilMessage(String message){
        log.info("发送的消息:" + message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.Message",message);
    }


}
