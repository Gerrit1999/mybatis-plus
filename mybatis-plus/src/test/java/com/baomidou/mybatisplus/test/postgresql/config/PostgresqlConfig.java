package com.baomidou.mybatisplus.test.postgresql.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testcontainers.jdbc.ContainerDatabaseDriver;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan("com.baomidou.mybatisplus.test.postgresql.mapper")
public class PostgresqlConfig {

    @Bean("dataSource")
    public DataSource dataSource() throws ClassNotFoundException {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new ContainerDatabaseDriver());
        dataSource.setUrl("jdbc:tc:postgresql:12-alpine://;/test?TC_DAEMON=true&TC_INITSCRIPT=postgresql/pgtable.ddl.sql");
        dataSource.setUsername("postgres");
        dataSource.setPassword("root");
        return dataSource;
    }


    @Bean("mybatisSqlSession")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        /* 数据源 */
        sqlSessionFactory.setDataSource(dataSource);
        /* 扫描 typeHandler */
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        /* 驼峰转下划线 */
        configuration.setMapUnderscoreToCamelCase(true);
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        sqlSessionFactory.setPlugins(mybatisPlusInterceptor);
        sqlSessionFactory.setConfiguration(configuration);
        GlobalConfig globalConfig = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        // 这里申明所有表名和字段名为大写
        dbConfig.setCapitalMode(true);
        dbConfig.setTableFormat("\"%s\"");
        dbConfig.setColumnFormat("\"%s\"");
        globalConfig.setDbConfig(dbConfig);
        sqlSessionFactory.setGlobalConfig(globalConfig);
        return sqlSessionFactory.getObject();
    }
}
