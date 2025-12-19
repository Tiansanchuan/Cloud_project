package com.atguigu.cloud.mygateway;

import jakarta.validation.constraints.NotNull;
import jdk.jfr.SettingDefinition;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.AfterRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;


//自己写的precicate配置类，实现更复杂的predicate功能
@Component
//命名必须是XXXRoutePredicateFactory在配置文件中写XXX
public class MyRoutePredicateFactory extends AbstractRoutePredicateFactory<MyRoutePredicateFactory.Config> {
    public MyRoutePredicateFactory() {
        super(MyRoutePredicateFactory.Config.class);
    }
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("userType");
    }

    @Validated
    public static class Config {
        private String userType;
        public Config() {
        }
        public Config(String userType) {
            this.userType = userType;
        }
        public String getUserType() {
            return userType;
        }
        public void setUserType(String userType) {
            this.userType = userType;
        }
        public String toString() {
            return "Config{userType = " + userType + "}";
        }
    }
    @Override
    public Predicate<ServerWebExchange> apply(MyRoutePredicateFactory.Config config) {
        return new Predicate<ServerWebExchange>() {
            public boolean test(ServerWebExchange serverWebExchange) {
                String userType=serverWebExchange.getRequest().getQueryParams().getFirst("userType");
                if(userType==null){
                    return false;
                }
                //参数名为userType是否等于配置的userType
                if(userType.equalsIgnoreCase(config.getUserType())){
                    return true;
                }
                return false;
            }
        };
    }

}
