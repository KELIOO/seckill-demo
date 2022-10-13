package com.example.seckilldemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 86187
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {

    private TUser user;
    private Long goodId;
}
