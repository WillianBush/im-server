package com.imservices.im.bmm.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="commodity_category")
@SuppressWarnings("all")
public class CommodityCategoryEntity {

    @Id
    Long id;

    /**
     * 类别名称
     * */
    String category_name;

}
