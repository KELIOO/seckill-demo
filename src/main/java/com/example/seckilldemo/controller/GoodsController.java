package com.example.seckilldemo.controller;

import com.example.seckilldemo.entity.TUser;

import com.example.seckilldemo.service.TGoodsService;
import com.example.seckilldemo.service.TUserService;
import com.example.seckilldemo.vo.DetailVo;
import com.example.seckilldemo.vo.GoodsVo;
import com.example.seckilldemo.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
 import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author 86187
 */
@Slf4j
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private TUserService tUserService;
    @Autowired
    private TGoodsService tGoodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;



    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    public String toList(Model model,TUser tUser,
                         HttpServletRequest request,HttpServletResponse response){
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String html = (String) opsForValue.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //获取缓存中的页面
        model.addAttribute("user",tUser);
        model.addAttribute("goodsList",tGoodsService.findGoodsVo());
        //缓存为空
        WebContext webContext = new WebContext(request,response, request.getServletContext(),request.getLocale(), model.asMap());
        //缓存页面
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if(!StringUtils.isEmpty(html)){
            opsForValue.set("goodsList",html,60, TimeUnit.SECONDS);
        }
        return html;
    }

    /**
     * 跳转商品详情页
     * @param goodsId
     * @return
     */

    @RequestMapping(value = "/toDetail/{goodsId}",produces = "text/html;charset=utf-8")
    public String toDetail(Model model,TUser tUser,@PathVariable Long goodsId,
                           HttpServletRequest request,HttpServletResponse response){
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String html = (String) opsForValue.get("goodsDetail:" + goodsId);
        //不为空时
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user",tUser);
        GoodsVo goodsVo = tGoodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate= new Date();
        int secKillStatus = 0;
        int remainSeconds = 0;
        if(nowDate.before(startDate)){
            remainSeconds = (int)((startDate.getTime()-nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){
            //结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            //秒杀中
            secKillStatus = 1;
        }


        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("goods",goodsVo);
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        //return "goodsDetail";
        if(!StringUtils.isEmpty(html)){
            opsForValue.set("goodsDetails:" + goodsId,html,60,TimeUnit.SECONDS);
        }
        return html;
    }


    /**
     * 用于缓存页面中的静态数据
     * @param model
     * @param tUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/detail/{goodsId}")
    public RespBean toDetail(TUser tUser, @PathVariable Long goodsId){

        GoodsVo goodsVo = tGoodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate= new Date();
        int secKillStatus = 0;
        int remainSeconds = 0;
        if(nowDate.before(startDate)){
            remainSeconds = (int)((startDate.getTime()-nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){
            //结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            //秒杀中
            secKillStatus = 1;
        }

        DetailVo detailVo = new DetailVo();
        detailVo.setUser(tUser);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);
        return RespBean.success(detailVo);
    }

}
