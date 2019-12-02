package com.bank.httpserver.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@EnableTransactionManagement
@AutoConfigureAfter(DruidAutoConfiguration.class)
@MapperScan(value = {"com.bank.dao"})
public class MybatisAutoConfiguration{

    /**
     * mybatis 配置路径
     */
    private static String MYBATIS_CONFIG = "classpath:mybatis/mybatis-config.xml";

    /**
     * mybatis mapper xml
     */
    private static String[] MAPPER_LOCATIONS_CONFIG = new String[]{
            "classpath*:mapper/mysql/*.xml"
    };

    @Autowired
    private DruidDataSource dataSource;

    /**
     * 创建sqlSession
     * @return
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean createSqlSessionFactoryBean() throws Exception {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(MYBATIS_CONFIG);

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

        /** 设置mybatis configuration 扫描路径 */
        sqlSessionFactoryBean.setConfigLocation(resource);

        /** 设置datasource */
        sqlSessionFactoryBean.setDataSource(dataSource);

        /** 设置mapperLocations */
        List<Resource> all = new ArrayList<>();
        for (String mapperLocation : MAPPER_LOCATIONS_CONFIG) {
            all.addAll(Arrays.asList(resolver.getResources(mapperLocation)));
        }
        sqlSessionFactoryBean.setMapperLocations(all.toArray(new Resource[all.size()]));
        return sqlSessionFactoryBean;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 配置事务管理器
     */
    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        return new DataSourceTransactionManager(dataSource);
    }
}