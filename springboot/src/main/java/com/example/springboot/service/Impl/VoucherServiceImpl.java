package com.example.springboot.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.common.constant.HttpStatus;
import com.example.springboot.domain.DTO.VoucherDTO;
import com.example.springboot.domain.SeckillVoucher;
import com.example.springboot.domain.Voucher;
import com.example.springboot.mapper.SeckillVoucherMapper;
import com.example.springboot.mapper.VoucherMapper;
import com.example.springboot.service.ISeckillVoucherService;
import com.example.springboot.service.IVoucherService;
import com.example.springboot.utils.ResponseResult;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.springboot.common.constant.CacheConstants.SECKILL_STOCK_KEY;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher>implements IVoucherService {
//    @Resource
//    private ISeckillVoucherService seckillVoucherService;
    @Autowired
    private SeckillVoucherMapper seckillVoucherMapper;

    @Autowired
    private VoucherMapper voucherMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Override
    public List<VoucherDTO> listVouchers(Integer type, Integer status) {
        List<Voucher> vouchers = voucherMapper.selectByTypeAndStatus(type, null); // 不加status条件
        List<VoucherDTO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Voucher v : vouchers) {
            VoucherDTO dto = new VoucherDTO(v);
            boolean isExpired = false;
            if (v.getType() == 1) {
                SeckillVoucher seckill = seckillVoucherMapper.selectById(v.getId());
                if (seckill != null) {
                    dto.setStock(seckill.getStock());
                    dto.setBeginTime(seckill.getBeginTime());
                    dto.setEndTime(seckill.getEndTime());
                    if (dto.getEndTime() != null && dto.getEndTime().isBefore(now)) {
                        isExpired = true;
                    }
                }
            }
            // 普通券过期判断（如有endTime字段）
            // if (dto.getEndTime() != null && dto.getEndTime().isBefore(now)) {
            //     isExpired = true;
            // }
            if (isExpired) {
                dto.setStatus(3);
                if (v.getStatus() != 3) {
                    v.setStatus(3);
                    voucherMapper.updateById(v); // 实时修正数据库
                }
            }
            // 只返回实际状态匹配的券
            if (status == null || dto.getStatus() == status) {
                result.add(dto);
            }
        }
        return result;
    }

    @Override
    public void addVoucher(VoucherDTO dto) {
        Voucher v = dto.toVoucher();
        voucherMapper.insert(v);
        if (v.getType() == 1) {
            SeckillVoucher seckill = new SeckillVoucher();
            seckill.setVoucherId(v.getId());
            seckill.setStock(dto.getStock());
            seckill.setBeginTime(dto.getBeginTime());
            seckill.setEndTime(dto.getEndTime());
            seckillVoucherMapper.insert(seckill);
            redisTemplate.opsForValue().set(SECKILL_STOCK_KEY + v.getId(), dto.getStock());
        }
    }

    @Override
    public void updateVoucher(VoucherDTO dto) {
        Voucher v = dto.toVoucher();
        voucherMapper.updateById(v);
        if (v.getType() == 1) {
            SeckillVoucher seckill = seckillVoucherMapper.selectById(v.getId());
            if (seckill == null) {
                seckill = new SeckillVoucher();
                seckill.setVoucherId(v.getId());
                seckill.setStock(dto.getStock());
                seckill.setBeginTime(dto.getBeginTime());
                seckill.setEndTime(dto.getEndTime());
                seckillVoucherMapper.insert(seckill);
            } else {
                seckill.setStock(dto.getStock());
                seckill.setBeginTime(dto.getBeginTime());
                seckill.setEndTime(dto.getEndTime());
                seckillVoucherMapper.updateById(seckill);
            }
        } else {
            seckillVoucherMapper.deleteById(v.getId());
        }
    }

    @Override
    public void deleteVoucher(Long id) {
        voucherMapper.deleteById(id);
        seckillVoucherMapper.deleteById(id);
    }
    @Override
    public ResponseResult queryVoucherOfShop(Long shopId) {
        List<Voucher> vouchers = getBaseMapper().queryVoucherOfShop(shopId);
        return new ResponseResult<>(HttpStatus.SUCCESS,"成功",vouchers);
    }

//    @Override
//    @Transactional
//    public void addSeckillVoucher(Voucher voucher) {
//        save(voucher);
//        SeckillVoucher seckillVoucher = new SeckillVoucher();
//        seckillVoucher.setVoucherId(voucher.getId());
//        seckillVoucher.setStock(voucher.getStock());
//        seckillVoucher.setBeginTime(voucher.getBeginTime());
//        seckillVoucher.setEndTime(voucher.getEndTime());
//        seckillVoucherService.save(seckillVoucher);
//        redisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucher.getId(), voucher.getStock());
//    }
}
