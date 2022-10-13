package com.example.seckilldemo.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.seckilldemo.entity.SeckillMessage;
import com.example.seckilldemo.entity.TOrder;
import com.example.seckilldemo.entity.TSeckillOrder;
import com.example.seckilldemo.entity.TUser;
import com.example.seckilldemo.exception.GlobalException;
import com.example.seckilldemo.rabbitmq.MQsender;
import com.example.seckilldemo.service.TGoodsService;
import com.example.seckilldemo.service.TOrderService;
import com.example.seckilldemo.service.TSeckillOrderService;
import com.example.seckilldemo.util.JsonUtil;
import com.example.seckilldemo.vo.GoodsVo;
import com.example.seckilldemo.vo.RespBean;
import com.example.seckilldemo.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 86187
 */
@Slf4j
@RestController
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private TGoodsService tGoodsService;
    @Autowired
    private TSeckillOrderService tSeckillOrderService;
    @Autowired
    private TOrderService tOrderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQsender mQsender;
    @Autowired
    private RedisScript<Long> redisScript;

    private Map<Long,Boolean> map = new HashMap<>();
    /*@RequestMapping("/doSeckill")
    public String doseckill(Model model, TUser tUser,Long goodsId){
        if(tUser == null){
            return "login";
        }
        model.addAttribute("user",tUser);
        GoodsVo goodsVo = tGoodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }
        //判断是否重复抢购
        TSeckillOrder serviceOne = tSeckillOrderService.getOne(new QueryWrapper<TSeckillOrder>().eq("user_id", tUser.getId()).eq("goods_id", goodsId));
        if(serviceOne != null){
            model.addAttribute("errmsg",RespBeanEnum.REPEATR_ERROR.getMessage());
            return "secKillFail";
        }
        TOrder tOrder = tOrderService.seckill(tUser,goodsVo);
        model.addAttribute("order",tOrder);
        model.addAttribute("goods",goodsVo);
        return "orderDetail";
    }*/



    @RequestMapping(value = "/{path}/doSeckill",method = RequestMethod.POST)
    public RespBean doseckill(@PathVariable String path, TUser tUser, Long goodsId){
        if(tUser == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations ops = redisTemplate.opsForValue();


        boolean check = tOrderService.checkPath(tUser,goodsId,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REPUEST_ILLEGAL);
        }


        TSeckillOrder seckillOrder = (TSeckillOrder) ops.get("order" + tUser.getId() + ":" + goodsId);
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATR_ERROR);
        }
        if(map.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存  redis预减
        Long stock = ops.decrement("seckillGoods:" + goodsId);

        /*Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:"+goodsId),
                Collections.EMPTY_LIST);*/
        if(stock < 0){
            map.put(goodsId,true);
            ops.increment("seckillGoods:" +goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(tUser, goodsId);

        mQsender.sendSeckilMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
        /*//判断是否重复抢购
        TSeckillOrder serviceOne = tSeckillOrderService.getOne(new QueryWrapper<TSeckillOrder>().eq("user_id", tUser.getId()).eq("goods_id", goodsId));
        */
        /*GoodsVo goodsVo = tGoodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }*/

/*//*
/判断是否重复抢购
        TSeckillOrder serviceOne = tSeckillOrderService.getOne(new QueryWrapper<TSeckillOrder>().eq("user_id", tUser.getId()).eq("goods_id", goodsId));
        *//*


        TSeckillOrder serviceOne = (TSeckillOrder) redisTemplate.opsForValue().get("order" + tUser.getId() + ":" + goodsId);
        if(serviceOne != null){
            model.addAttribute("errmsg",RespBeanEnum.REPEATR_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATR_ERROR);
        }
        TOrder tOrder = tOrderService.seckill(tUser,goodsVo);

*/


    }

    /**
     * orderId:成功  -1:秒杀失败  0:排队中
     * @throws Exception
     */

    @RequestMapping(value = "/result",method = RequestMethod.GET)
    public RespBean getResult(TUser user,Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = tSeckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }
    /**
     * 获取秒杀地址
     */
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    public RespBean getPath(TUser user,Long goodsId,String captcha,HttpServletRequest request){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String uri = request.getRequestURI();

        Integer count = (Integer) opsForValue.get(uri + ":" + user.getId());
        if(count == null){
            opsForValue.set(uri + ":" + user.getId(),1,5,TimeUnit.SECONDS);
        }else if(count < 5){
            opsForValue.increment(uri + ":" + user.getId());
        }else{
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REAHCED);
        }
        boolean check = tOrderService.checkCaptcha(user,goodsId,captcha);
        if(!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str = tOrderService.createPath(user,goodsId);
        return RespBean.success(str);
    }


    /**
     * 验证码
     *
     * @param user
     * @param goodsId
     * @param httpServletResponse
     * @throws GlobalException
     */
    @RequestMapping(value = "/captcha",method = RequestMethod.GET)
    public void verifyCode(TUser user, Long goodsId, HttpServletResponse httpServletResponse) throws GlobalException {
        if(user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.REPUEST_ILLEGAL);
        }
        //设置请求头输出图片的类型
        httpServletResponse.setContentType("image/jpg");
        httpServletResponse.setHeader("Pargam","No-cache");
        httpServletResponse.setHeader("Cache-Control","no-cache");
        httpServletResponse.setDateHeader("Expires",0);
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(httpServletResponse.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
    }


    /**
     * 系统初始化,把商品库存加载到Redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVo = tGoodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(goodsVo)){
            return;
        }
        goodsVo.forEach(goodsVo1 -> {
            redisTemplate.opsForValue().set("seckillGoods:"+ goodsVo1.getId(),goodsVo1.getStockCount());
            map.put(goodsVo1.getId(),false);
        });
    }
}
