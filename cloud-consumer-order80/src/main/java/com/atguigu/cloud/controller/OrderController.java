package com.atguigu.cloud.controller;

import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import jakarta.annotation.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
@RestController
public class OrderController{
    public static final String PaymentSrv_URL = "http://cloud-payment-service";//解决硬编码问题
    @Resource
    private RestTemplate restTemplate;

    @GetMapping(value = "/consumer/pay/add")
    public ResultData addOrder(@RequestBody PayDTO payDTO){
        return restTemplate.postForObject(PaymentSrv_URL+"/pay/add",payDTO, ResultData.class);
    }
    @GetMapping(value = "/consumer/pay/get/{id}")
    public ResultData getPayInfo(@PathVariable("id")Integer id){
        return restTemplate.getForObject(PaymentSrv_URL+"/pay/get/"+id, ResultData.class,id);
    }
    @GetMapping(value = "/consumer/pay/del/{id}")
    public ResultData deletePayById(@PathVariable("id")Integer id){
        restTemplate.delete(PaymentSrv_URL+"/pay/del/"+id);
        return ResultData.success("");
    }
    @GetMapping(value = "/consumer/pay/getall")
    public ResultData getAllPayInfo(){
        return restTemplate.getForObject(PaymentSrv_URL+"/pay/getall", ResultData.class);
    }
    @GetMapping(value = "/consumer/pay/update")
    public ResultData updatePayInfo(@RequestBody PayDTO payDTO){
        restTemplate.put(PaymentSrv_URL+"/pay/update",payDTO);
        return ResultData.success("");
    }
//    @GetMapping(value = "/consumer/pay/get/{id}")
//    public ResultData getPayInfo(@PathVariable("id")Integer id){
//        return restTemplate.getForObject(PaymentSrv_URL+"/pay/get/"+id, ResultData.class,id);
//    }

    @GetMapping(value = "/consumer/pay/get/info")
    private String getInfoByConsul()
    {
        return restTemplate.getForObject(PaymentSrv_URL + "/pay/get/info", String.class);
    }

}