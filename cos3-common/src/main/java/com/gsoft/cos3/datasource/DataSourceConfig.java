package com.gsoft.cos3.datasource;

import com.zaxxer.hikari.util.DriverDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
public class DataSourceConfig {

    private Logger logger = Logger.getLogger(DataSourceConfig.class);

    /**
     * 自定义数据源
     */
    public Map<Object, Object> customDataSources = new HashMap<>();

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    private static final List<String> custNames = new ArrayList<>();

    @Primary
    @RefreshScope
    @Bean(name = "datasource")
    public DynamicDataSource dynamicDataSource() {
        logger.info("=====初始化动态数据源=====");
        Properties properties = new Properties();
        DataSource dataSource = new DriverDataSource(url, driver, properties, username, password);
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        // 默认数据源
        dynamicDataSource.setDefaultTargetDataSource(dataSource);
        // 配置多数据源
        Map<Object, Object> dsMap = new HashMap<Object, Object>();
        Map<Object, Object> customDataSources = new HashMap<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select C_CODE,C_DSURL,C_DSDRIVERCLASSNAME,C_DSUSERNAME,C_DSPASSWORD from COS_SAAS_CUSTOMER");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(1);
                custNames.add(name);
                dsMap.put("url", resultSet.getString(2));
                dsMap.put("driver", resultSet.getString(3));
                dsMap.put("username", resultSet.getString(4));
                dsMap.put("password", resultSet.getString(5));
                DataSource ds = buildDataSource(dsMap);
                customDataSources.put(name, ds);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dynamicDataSource.setTargetDataSources(customDataSources);
        this.customDataSources = customDataSources;
        logger.info("已加载租户库数据源" + custNames);
        return dynamicDataSource;
    }

    @Primary
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("datasource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public static DataSource buildDataSource(Map<Object, Object> dataSourceMap) {
        String driverClassName = dataSourceMap.get("driver").toString();
        String url = dataSourceMap.get("url").toString();
        String username = dataSourceMap.get("username").toString();
        String password = dataSourceMap.get("password").toString();
        // hikari配置参数 目前使用默认设置
        Properties properties = new Properties();
        DriverDataSource driverDataSource = new DriverDataSource(url, driverClassName, properties, username, password);
        return driverDataSource;
    }

}
