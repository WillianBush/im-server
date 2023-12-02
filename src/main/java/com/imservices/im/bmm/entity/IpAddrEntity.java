package com.imservices.im.bmm.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="IpAddrEntity")
@SuppressWarnings("all")
@Data
public class IpAddrEntity extends BaseEntity{
    private String country;
    private String province;
}
