package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置Mybaits Plus的分页组件
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 通过拦截器方式把插件加进来
     * 创建一个拦截器对象
     * 调用其方法添加内部过滤器addInnerInterceptor
     * 创建一个新的分页拦截器PaginationInnerInterceptor
     * 返回拦截器
     */

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
