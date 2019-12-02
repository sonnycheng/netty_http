package com.bank.httpserver.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;

@Configuration
public class DruidAutoConfiguration {

    @Autowired
    private DruidProperties properties;

    @Bean
    @Primary
    public DruidDataSource dataSource() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        dataSource.setInitialSize(properties.getInitialSize());
        dataSource.setMinIdle(properties.getMinIdle());
        dataSource.setMaxActive(properties.getMaxActive());
        dataSource.setMaxWait(properties.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        String validationQuery = properties.getValidationQuery();
        if (validationQuery != null && !"".equals(validationQuery)) {
            dataSource.setValidationQuery(validationQuery);
        }
        dataSource.setTestWhileIdle(properties.isTestWhileIdle());
        dataSource.setTestOnBorrow(properties.isTestOnBorrow());
        dataSource.setTestOnReturn(properties.isTestOnReturn());
        if (properties.isPoolPreparedStatements()) {
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());
        }
        String connectionPropertiesStr = properties.getConnectionProperties();
        if (connectionPropertiesStr != null && !"".equals(connectionPropertiesStr)) {
            Properties connectProperties = new Properties();
            String[] propertiesList = connectionPropertiesStr.split(";");
            for (String propertiesTmp : propertiesList) {
                String[] obj = propertiesTmp.split("=");
                String key = obj[0];
                String value = obj[1];
                connectProperties.put(key, value);
            }
            dataSource.setConnectProperties(connectProperties);
        }
        dataSource.setUseGlobalDataSourceStat(properties.isUseGlobalDataSourceStat());
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig);
        List<Filter> filters = new ArrayList<>();
        filters.add(wallFilter);
        filters.add(new StatFilter());
        filters.add(new Log4jFilter());
        dataSource.setProxyFilters(filters);
        return dataSource;
    }

    /**
     * Druid的Servlet
     * @return
     */
    @Bean
    public ServletRegistrationBean druidStatViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

        /** 添加初始化参数：initParams */
        /** 白名单，如果不配置或value为空，则允许所有 */
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1,192.0.0.1");
        /** 黑名单，与白名单存在相同IP时，优先于白名单 */
        servletRegistrationBean.addInitParameter("deny", "192.0.0.1");
        /** 用户名 */
        servletRegistrationBean.addInitParameter("loginUsername", properties.getLoginUsername());
        /** 密码 */
        servletRegistrationBean.addInitParameter("loginPassword", properties.getLoginPassword());
        /** 禁用页面上的“Reset All”功能 */
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    /**
     * Druid拦截器，用于查看Druid监控
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        /** 过滤规则 */
        filterRegistrationBean.addUrlPatterns("/*");
        /** 忽略资源 */
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
}
