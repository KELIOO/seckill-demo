package com.example.seckilldemo.vo;

import com.example.seckilldemo.entity.TGoods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 86187
 *
 *
 *  用于商品页面的数据展示  封装商品页面的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo extends TGoods {
    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
