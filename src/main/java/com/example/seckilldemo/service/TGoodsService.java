package com.example.seckilldemo.service;

import com.example.seckilldemo.entity.TGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckilldemo.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author testjava
 * @since 2022-10-09
 */
public interface TGoodsService extends IService<TGoods> {
    //获取商品
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
