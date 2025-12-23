package com.atguigu.cloud.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.atguigu.cloud.entities.Pay;
import com.atguigu.cloud.resp.ResultData;
import com.atguigu.cloud.service.PayService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

@RestController
public class PayGateWayController
{
    @Resource
    PayService payService;

    @GetMapping(value = "/pay/gateway/get/{id}")
    public ResultData<Pay> getById(@PathVariable("id") Integer id)
    {
        Pay pay = payService.getById(id);
        return ResultData.success(pay);
    }

    @GetMapping(value = "/pay/gateway/info")
    public ResultData<String> getGatewayInfo()
    {
        return ResultData.success("gateway info test："+ IdUtil.simpleUUID());
    }

    @GetMapping(value = "/pay/gateway/filter")
    public ResultData<String> getGatewayFilter(HttpServletRequest request)
    {
        String result = "";
        Enumeration<String> headers = request.getHeaderNames();
        while(headers.hasMoreElements())//后台打印请求头
        {
            String headName = headers.nextElement();
            String headValue = request.getHeader(headName);
            System.out.println("请求头名: " + headName +"\t\t\t"+"请求头值: " + headValue);
            if(headName.equalsIgnoreCase("X-Request1") //测试是否添加这两个请求头
                    || headName.equalsIgnoreCase("X-Request2")) {
                result = result+headName + "\t " + headValue +" ";
            }
        }
        //后台打印所有参数
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            String[] values = request.getParameterValues(name);//一个参数可以有多个值  url?tag=1&tag=2
            System.out.println("request Parameter " + name + ": " + java.util.Arrays.toString(values));
        }

        String header=request.getHeader("userid");

//        System.out.println("=============================================");
//        String customerId = request.getParameter("customerId");
//        System.out.println("request Parameter customerId: "+customerId);
//        String customerName = request.getParameter("customerName");
//        System.out.println("request Parameter customerName: "+customerName);
//        System.out.println("=============================================");

        return ResultData.success("getGatewayFilter 过滤器 test： "+result+" \t "
                +"header "+header+" \t "+ DateUtil.now());
    }
}
