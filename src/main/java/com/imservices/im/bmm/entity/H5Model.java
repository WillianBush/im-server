package com.imservices.im.bmm.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "h5")
@Data
@ToString
public class H5Model {

    private String url;



}
