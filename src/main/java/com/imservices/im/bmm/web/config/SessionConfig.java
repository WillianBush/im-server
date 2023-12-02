package com.imservices.im.bmm.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;

//@Configuration
public class SessionConfig {


//    @Bean(name = "tenantTransactionManager")
//    public JpaTransactionManager transactionManager(EntityManagerFactory tenantEntityManager) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(tenantEntityManager);
//        return transactionManager;
//    }
}
