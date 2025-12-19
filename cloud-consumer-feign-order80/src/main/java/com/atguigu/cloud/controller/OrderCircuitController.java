package com.atguigu.cloud.controller;

import com.atguigu.cloud.apis.PayFeignApi;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 测试 Resilience4j CircuitBreaker 的例子
 */
@RestController
public class OrderCircuitController
{
    @Resource
    private PayFeignApi payFeignApi;

    //断路器
    @GetMapping(value = "/feign/pay/circuit/{id}")
    @RateLimiter(name = "nameof-ratelimiter")
    @Bulkhead(name = "nameof-bulkhead",type = Bulkhead.Type.SEMAPHORE)
    @CircuitBreaker(name = "nameof-circuitbreaker", fallbackMethod = "paymentFallback")//降级的服务
    public String myCircuitBreaker(@PathVariable("id") Integer id)
    {
        return payFeignApi.myCircuit(id);
    }

    public String paymentFallback(Integer id, Throwable t) {
        if (t instanceof CallNotPermittedException) {
            return "支付服务暂时熔断，请稍后再试";
        }
        if (t instanceof RequestNotPermitted) { // RateLimiter
            return "请求太多，被限流啦，请稍后重试";
        }
        if (t instanceof TimeoutException) { // TimeLimiter
            return "支付服务响应超时，请稍后再试";
        }
        if (t instanceof BulkheadFullException) { // 舱壁满了
            return "Bulkhead 满了，系统繁忙，请稍后再试";
        }
        // 其他异常走业务降级
        return "支付系统繁忙或出错，请稍后再试";
    }

    /**
     *舱壁隔离（信号量）
     */
//    @GetMapping(value = "/feign/pay/bulkhead/{id}")
//    @Bulkhead(name = "cloud-payment-service",fallbackMethod = "myBulkheadFallback",type = Bulkhead.Type.SEMAPHORE)
//    public String myBulkhead(@PathVariable("id") Integer id)
//    {
//        return payFeignApi.myBulkhead(id);
//    }
//    public String myBulkheadFallback(Throwable t)
//    {
//        return "myBulkheadFallback，隔板超出最大数量限制，系统繁忙，请稍后再试-----/(ㄒoㄒ)/~~";
//    }
    /**
     * resilience4j bulkhead舱壁,隔离,
     * THREADPOOL （固定线程池）
     */
//    @GetMapping(value = "/feign/pay/bulkhead/{id}")
//    @Bulkhead(name = "cloud-payment-service",fallbackMethod = "myBulkheadPoolFallback",type = Bulkhead.Type.THREADPOOL)
//    public CompletableFuture<String> myBulkheadTHREADPOOL(@PathVariable("id") Integer id)
//    {
//        System.out.println(Thread.currentThread().getName()+"\t"+"enter the method!!!");
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println(Thread.currentThread().getName()+"\t"+"exist the method!!!");
//
//        return CompletableFuture.supplyAsync(() -> payFeignApi.myBulkhead(id) + "\t" + " Bulkhead.Type.THREADPOOL");
//    }
//    public CompletableFuture<String> myBulkheadPoolFallback(Integer id,Throwable t)
//    {
//        return CompletableFuture.supplyAsync(() -> "Bulkhead.Type.THREADPOOL，系统繁忙，请稍后再试-----/(ㄒoㄒ)/~~");
//    }
//
//    @GetMapping(value = "/feign/pay/ratelimit/{id}")
//    @RateLimiter(name = "cloud-payment-service",fallbackMethod = "myRatelimitFallback")
//    public String myBulkhead(@PathVariable("id") Integer id)
//    {
//        return payFeignApi.myRatelimit(id);
//    }
//





}