package com.imservices.im;

import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@EnableScheduling
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class },scanBasePackages = {"com.imservices.im.bmm"})
// 开启事务管理
@EnableTransactionManagement(proxyTargetClass = true)
@AllArgsConstructor
public class KfImApplication extends SpringBootServletInitializer {

	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(KfImApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(KfImApplication.class);
	}

	@Bean(name = "dataSource")
	public DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		// application.properties 要有相数据源的配置
		dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
		dataSource.setUrl(env.getProperty("spring.datasource.url"));
		dataSource.setUsername(env.getProperty("spring.datasource.username"));
		dataSource.setPassword(env.getProperty("spring.datasource.password"));
//		try {
//			dataSource.setLoginTimeout(20000);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		return dataSource;
	}


	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource) throws Exception {
		Properties properties = new Properties();
		// application.properties 要有相关hibernate的配置
		properties.put("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
		properties.put("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
		properties.put("current_session_context_class", env.getProperty("spring.jpa.properties.hibernate.current_session_context_class"));
		// Fix Postgres JPA Error:
		// Method org.postgresql.jdbc.PgConnection.createClob() is not yet implemented.
		// properties.put("hibernate.temp.use_jdbc_metadata_defaults",false);
		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
		// 这里修改下，改成需要扫描Entity的类
		factoryBean.setPackagesToScan("com.imservices.im.bmm.entity");
		factoryBean.setDataSource(dataSource);
		factoryBean.setHibernateProperties(properties);
		factoryBean.afterPropertiesSet();
		//
		return factoryBean.getObject();
	}

	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		return new HibernateTransactionManager(sessionFactory);
	}

}
