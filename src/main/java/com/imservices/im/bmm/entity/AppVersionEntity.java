package com.imservices.im.bmm.entity;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name="app_version")
@SuppressWarnings("all")
public class AppVersionEntity {

    @Id
    Long id;

    /**
     * 版本
     * */
    String version;

    /**
     * 下载地址
     * */
    String down_url;

    /**
     * 租户ID
     * */
    String org_id;

    /**
     * 系统
     * */
    String device_type;

    /**
     * 是否强更新：0->否；1->是
     * */
    Integer force_update;

    /**
     * 是否展示：0->否；1->是
     * */
    String  is_show;

    /**
     *
     * */
    String app_id;

    /**
     *
     * */
    String app_name;

    String descript;


}
