package com.example.springboot.controller;

import com.example.springboot.common.constant.HttpStatus;
import com.example.springboot.domain.Opera;
import com.example.springboot.service.AccessService;
import com.example.springboot.service.Impl.LoginServiceImpl;
import com.example.springboot.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/access")
public class AccessController {
    @Autowired
    private AccessService accessService;
    @GetMapping("/history")
    public ResponseResult getOperasByUserId(){
        long userId = LoginServiceImpl.getCurrentUserId();
        List<Opera> operas = accessService.getOperasByUserId(userId);
        return new ResponseResult(HttpStatus.SUCCESS,operas);
    }
    @GetMapping("/hot")
    public ResponseResult getHotList(){
        List<Opera> hotListByUserAccess = accessService.getHotListByUserAccess();
        return new ResponseResult<>(HttpStatus.SUCCESS,hotListByUserAccess);
    }
    @DeleteMapping
    public ResponseResult deleteOperasByIds(@RequestBody List<Integer> ids){
        long userId = LoginServiceImpl.getCurrentUserId();
        boolean isSuccess = accessService.deleteOperasByIds(userId,ids);
        if(isSuccess){
            return new ResponseResult(HttpStatus.SUCCESS,isSuccess);
        }
        return new ResponseResult(HttpStatus.ERROR,isSuccess);
    }
    //测试上传gitbub
}
