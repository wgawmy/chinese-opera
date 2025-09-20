package com.example.springboot.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.common.constant.HttpStatus;
import com.example.springboot.common.constant.RabbitConstant;
import com.example.springboot.domain.LoginUser;
import com.example.springboot.domain.VoucherOrder;
import com.example.springboot.mapper.VoucherOrderMapper;
import com.example.springboot.service.ISeckillVoucherService;
import com.example.springboot.service.IVoucherOrderService;
import com.example.springboot.utils.RedisIdWorker;
import com.example.springboot.utils.ResponseResult;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder>
        implements IVoucherOrderService {
//    @Resource
//    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT = new DefaultRedisScript<Long>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }
    @Override
    public ResponseResult seckillVoucher(Long voucherId) {
        System.out.println("voucherId: " + voucherId.toString());
        //需要校验库存是否充足,用户是否重复下单
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        long orderId = redisIdWorker.nextId("order");
        Long result = (Long) stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString(),
                String.valueOf(orderId)
        );

        int r = result.intValue();
        //2.判断结果是否为0
        if (r != 0) {
            // 2.1.不为0 ，代表没有购买资格
            if(r==2){
                return new ResponseResult(HttpStatus.ERROR,"不能重复下单");
            }
            return new ResponseResult(HttpStatus.ERROR,"库存不足");
        }
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        rabbitTemplate.convertAndSend(RabbitConstant.SECKILL_ORDER_EXCHANGE, RabbitConstant.SECKILL_ORDER_ROUTING_KEY, voucherOrder);
        return new ResponseResult<>(HttpStatus.SUCCESS,"下单成功",orderId);
    }
}
