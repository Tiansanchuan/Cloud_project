package com.atguigu.cloud.config;

import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

//openfeign配置文件
@Configuration
public class FeignConfig {
    @Bean
    public Retryer myRetryer(){
        //默认是不重新发送请求
        //最大请求次数为3（1+2），初始间隔时间为100ms，重试调用时间为1s
//        return new Retryer.Default(100,1,3);
        return Retryer.NEVER_RETRY;
    }
    //配饰feign的日志级别
    @Bean
    Logger.Level feignLoggerLevel(){
//        return Logger.Level.FULL;
        return Logger.Level.BASIC;
    }
}
