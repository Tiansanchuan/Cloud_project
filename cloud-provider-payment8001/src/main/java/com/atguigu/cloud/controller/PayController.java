package com.atguigu.cloud.controller;

import cn.hutool.core.bean.BeanUtil;
import com.atguigu.cloud.entities.Pay;
import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import com.atguigu.cloud.service.PayService;
import com.mysql.cj.util.TimeUtil;
import io.micrometer.core.instrument.util.TimeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Tag(name = "支付微服务模块",description = "支付CRUD")
public class PayController {
    @Resource
    private PayService payService;

    @PostMapping(value = "/pay/add")
    @Operation(summary = "新增",description = "新增支付流水方法，json串做参数")
    public ResultData<String> addPay(@RequestBody Pay pay){
        System.out.println(pay.toString());
        int i= payService.add(pay);
        return ResultData.success("插入，返回值："+i);
    }
    @DeleteMapping(value = "/pay/del/{id}")
    @Operation(summary = "删除",description = "删除支付流水，传id")
    public ResultData<Integer> deletePay(@PathVariable("id")Integer id){
        return ResultData.success(payService.delete(id));

    }
    @PutMapping(value = "/pay/update")
    @Operation(summary = "修改",description = "按id修改支付流水，传json")
    public ResultData<String> updatePay(@RequestBody PayDTO payDTO){
        Pay pay =new Pay();
        BeanUtil.copyProperties(payDTO,pay);
        int update = payService.update(pay);
        return ResultData.success("修改，返回值："+update);
    }
    @Operation(summary = "查询",description = "按id查询支付流水")
    @GetMapping(value = "/pay/get/{id}")
    public ResultData<Pay> getById(@PathVariable("id") Integer id){
        try{
            TimeUnit.SECONDS.sleep(62);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        if(id<0)throw new RuntimeException("id不能为负数");
        return ResultData.success(payService.getById(id));

    }
    @GetMapping(value = "/pay/getall")
    @Operation(summary = "查询全部",description = "查询全部的支付流水")
    public ResultData<List<Pay>> getAll(){
        return ResultData.success(payService.getAll());
    }

    @Value("${server.port}")
    private String port;

    @GetMapping(value = "/pay/get/info")
    public String getInfoByConsul(@Value("${atguigu.info}") String atguiguInfo){
        return "atguiguInfo: "+atguiguInfo+"\t"+"port: "+port;

    }
}












