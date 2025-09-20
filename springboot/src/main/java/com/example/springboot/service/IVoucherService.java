package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.domain.DTO.VoucherDTO;
import com.example.springboot.domain.Voucher;
import com.example.springboot.utils.ResponseResult;

import java.util.List;

public interface IVoucherService extends IService<Voucher> {
    ResponseResult queryVoucherOfShop(Long shopId);

//    void addSeckillVoucher(Voucher voucher);

    List<VoucherDTO> listVouchers(Integer type, Integer status);
    void addVoucher(VoucherDTO dto);
    void updateVoucher(VoucherDTO dto);
    void deleteVoucher(Long id);
}
