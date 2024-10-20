package edu.school21.tanks.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("db.properties")
@ComponentScan(basePackages = "edu.school21.tanks")
public class ApplicationConfig {

    @Autowired private Environment env;

    @Bean(value = "DataSource")
    public DataSource getDataSource() {

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(env.getProperty("db.url"));
        ds.setUsername(env.getProperty("db.user"));
        ds.setPassword(env.getProperty("db.password"));
        ds.setDriverClassName(env.getProperty("db.driver.name"));
        return ds;
    }
}
