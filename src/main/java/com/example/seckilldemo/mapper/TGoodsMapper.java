package com.example.seckilldemo.mapper;

import com.example.seckilldemo.entity.TGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seckilldemo.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author testjava
 * @since 2022-10-09
 */
public interface TGoodsMapper extends BaseMapper<TGoods> {

    List<GoodsVo> findGoodsVo();

    //
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
