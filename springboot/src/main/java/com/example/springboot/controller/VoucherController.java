package com.example.springboot.controller;

import com.example.springboot.common.constant.HttpStatus;
import com.example.springboot.domain.DTO.VoucherDTO;
import com.example.springboot.domain.Voucher;
import com.example.springboot.service.IVoucherService;
import com.example.springboot.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voucher")
public class VoucherController {
    @Autowired
    IVoucherService voucherService;
    // 查询列表
    @GetMapping("/list")
    public ResponseResult list(@RequestParam(required = false) Integer type,
                                 @RequestParam(required = false) Integer status) {
        List<VoucherDTO> voucherList = voucherService.listVouchers(type, status);
        return new ResponseResult<>(HttpStatus.SUCCESS,voucherList);
    }
    @PostMapping("/update")
    public ResponseResult<Void> update(@RequestBody VoucherDTO dto) {
        voucherService.updateVoucher(dto);
        return new ResponseResult<>(HttpStatus.SUCCESS, "成功", null);
    }
    // 删除
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return new ResponseResult<>(HttpStatus.SUCCESS, "成功", null);
    }
    /**
     * 查询店铺的优惠卷列表
     * @param shopId
     * @return
     */
    @GetMapping("/list/{shopId}")
    public ResponseResult queryVoucherOfShop(@PathVariable("shopId") Long shopId) {
        return voucherService.queryVoucherOfShop(shopId);
    }

    /**
     * 保存普通的优惠卷
     * @param voucher
     * @return
     */
    @PostMapping("/add")
    public ResponseResult addVoucher(@RequestBody VoucherDTO voucher) {
        voucherService.addVoucher(voucher);
        return new ResponseResult<>(HttpStatus.SUCCESS,"成功",voucher.getId());
    }

//    /**
//     * 新增秒杀卷
//     * @param voucher
//     * @return
//     */
//    @PostMapping("/seckill")
//    public ResponseResult addSeckillVoucher(@RequestBody Voucher voucher) {
//        voucherService.addSeckillVoucher(voucher);
//        return new ResponseResult(HttpStatus.SUCCESS,"成功",voucher.getId());
//    }
}
