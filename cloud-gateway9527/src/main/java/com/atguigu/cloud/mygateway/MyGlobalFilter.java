package com.atguigu.cloud.mygateway;

import com.atguigu.cloud.resp.ResultData;
import com.atguigu.cloud.resp.ReturnCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j//不用自己手写logger，用于日志
//global filter全局过滤器

public class MyGlobalFilter implements GlobalFilter, Ordered {
    @Value("${security.whitelist}")
    private String whitelist;
    @Value("${security.jwt.secret}")
    private String secret;    // HS256 密钥（建议>=32字节）

    private SecretKey key() {
        // 必须与 auth 服务发 token 的 key() 生成方式一致
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    private final AntPathMatcher matcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BEGIN_VISIT_TIME = "begin_visit_time";//开始访问时间
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        List<String> patterns = Arrays.stream(whitelist.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        //放行白名单接口
        if (patterns.stream().anyMatch(p -> matcher.match(p, path))) {
            return chain.filter(exchange);
        }
        //token校验
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (token == null) {
            return writeFail(exchange, ReturnCodeEnum.RC401.getCode(), "Missing token");
        }
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return writeFail(exchange, ReturnCodeEnum.RC401.getCode(), "Token expired");
        } catch (JwtException e) {
            return writeFail(exchange, ReturnCodeEnum.RC401.getCode(), "Invalid token");
        }

        // 5) 取 userId（你发 token 时 setSubject(userId) 且 claim("userid", userId)）
        String userId = claims.getSubject();
        if (userId == null || userId.isBlank()) {
            userId = claims.get("userid", String.class);
        }
        if (userId == null || userId.isBlank()) {
            return writeFail(exchange, ReturnCodeEnum.RC401.getCode(), "Token missing userid");
        }
        final String finalUserId = userId;
        // 6) 写入 header 透传给下游
        ServerWebExchange mutated = exchange.mutate()
                .request(builder -> builder.header("userid", finalUserId))
                .build();

        return chain.filter(mutated);
        //统计所有接口调用耗时
        //============================================================================================
//        exchange.getAttributes().put(BEGIN_VISIT_TIME, System.currentTimeMillis());
//       //exchange.getAttributes()是一个map用来在同一次请求链里传递数据
//        return chain.filter(exchange).then(Mono.fromRunnable(()->{//.then（）成功结束后做事
//            Long beginVisitTime = exchange.getAttribute(BEGIN_VISIT_TIME);
//            if (beginVisitTime != null){
//                log.info("访问接口主机: " + exchange.getRequest().getURI().getHost());
//                log.info("访问接口端口: " + exchange.getRequest().getURI().getPort());
//                log.info("访问接口URL: " + exchange.getRequest().getURI().getPath());
//                log.info("访问接口URL参数: " + exchange.getRequest().getURI().getRawQuery());
//                log.info("访问接口时长: " + (System.currentTimeMillis() - beginVisitTime) + "ms");
//                log.info("分割线: ###################################################");
//                System.out.println();
//            }
//        }));
        //============================================================================================
    }
    private Mono<Void> writeFail(ServerWebExchange exchange, String code, String message) {
        // 你想“业务 code”统一返回，但 HTTP 状态也建议配 401
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ResultData<Object> body = ResultData.fail(code, message);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            // 极端情况下兜底
            bytes = ("{\"code\":\"" + code + "\",\"message\":\"" + message + "\",\"timestamp\":" +
                    System.currentTimeMillis() + "}").getBytes(StandardCharsets.UTF_8);
        }

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }


    @Override

    public int getOrder() { //数字越小优先级越高
        return -10;
    }
}
