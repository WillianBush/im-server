package com.imservices.im.bmm.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="commodity")
@SuppressWarnings("all")
public class CommodityEntity {

    @Id
    Long id;

    /**
     * 商品名称
     * */
    String name;

    /**
     * 商品明细
     * */
    String commodity_info;

    /**
     * 类别
     * */
    int category_id;

    /**
     * 单价
     * */
    String prize;

    /**
     * 库存
     * */
    String in_stock;

    /**
     * 图片地址
     * */
    String  img;


}
