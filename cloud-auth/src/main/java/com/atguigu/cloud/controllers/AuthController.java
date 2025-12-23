package com.atguigu.cloud.controllers;

import com.atguigu.cloud.resp.ResultData;
import com.atguigu.cloud.resp.ReturnCodeEnum;
import com.atguigu.cloud.service.JWTService;
import com.atguigu.cloud.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/oauth")
public class AuthController {
    @Resource
    private JWTService jwtService;
    @Resource
    private UserService userService;
    @PostMapping(value = "/token")
    public ResultData<String> Auth(@RequestParam("username") String username,
                                   @RequestParam("password") String password){
        Long id=userService.verify(username, password);
        if(id==null){
            return ResultData.fail(ReturnCodeEnum.CLIENT_AUTHENTICATION_FAILED.getCode(),
                    ReturnCodeEnum.CLIENT_AUTHENTICATION_FAILED.getMessage());
        }else{
            return ResultData.success(jwtService.getToken(id.toString()));
        }


    }
}
