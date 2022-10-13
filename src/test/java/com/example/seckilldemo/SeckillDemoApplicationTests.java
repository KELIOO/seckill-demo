package com.example.seckilldemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class SeckillDemoApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void contextLoads() {
        ValueOperations ops = redisTemplate.opsForValue();
        //占位,如果key不存在才可用设置成功
        Boolean isLOck = ops.setIfAbsent("k1", "v1");
        //如果占位成功, 进行操作
        if(isLOck){
            ops.set("name","zqs");
            String name = (String) ops.get("name");
            System.out.println("name = "+name);
            //操作结束
            redisTemplate.delete("k1");
        }else{
            System.out.println("有线程在使用");
        }
    }

}
