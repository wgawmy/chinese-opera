package com.example.springboot.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.constant.HttpStatus;
import com.example.springboot.common.constant.SystemConstants;
import com.example.springboot.domain.Shop;
import com.example.springboot.service.IShopService;
import com.example.springboot.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private IShopService shopService;

    /**
     * 查询所有商铺信息
     * @return 商铺列表
     */
    @GetMapping("/all")
    public ResponseResult getAllShop() {
        List<Shop> shops = shopService.list();
        return new ResponseResult(HttpStatus.SUCCESS, shops);
    }

    /**
     * 根据id查询商铺信息
     * @param id 商铺id
     * @return 商铺详情数据
     */
    @GetMapping("/{id}")
    public ResponseResult queryShopById(@PathVariable("id") Long id) {
        return shopService.queryById(id);
    }

    /**
     * 新增商铺信息
     * @param shop 商铺数据
     * @return 商铺id
     */
    @PostMapping
    public ResponseResult saveShop(@RequestBody Shop shop) {
        // 写入数据库
        shopService.save(shop);
        // 返回店铺id
        return new ResponseResult(HttpStatus.SUCCESS,shop.getId());
    }

    /**
     * 更新商铺信息
     * @param shop 商铺数据
     * @return 无
     */
    @PutMapping
    public ResponseResult updateShop(@RequestBody Shop shop) {
        // 写入数据库
        return new ResponseResult(HttpStatus.SUCCESS,shopService.updateById(shop));
    }

    /**
     * 根据商铺类型分页查询商铺信息
     * @param typeId 商铺类型
     * @param current 页码
     * @return 商铺列表
     */
//    @GetMapping("/of/type")
//    public ResponseResult queryShopByType(
//            @RequestParam("typeId") Integer typeId,
//            @RequestParam(value = "current", defaultValue = "1") Integer current)
//    {
//        // 根据类型分页查询
//        Page<Shop> page = shopService.query()
//                .eq("type_id", typeId)
//                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
//        // 返回数据
//        return new ResponseResult(HttpStatus.SUCCESS,page.getRecords());
//    }

    /**
     * 根据商铺名称关键字分页查询商铺信息
     * @param name 商铺名称关键字
     * @param current 页码
     * @return 商铺列表
     */
    @GetMapping("/of/name")
    public ResponseResult queryShopByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // 根据类型分页查询
        Page<Shop> page = shopService.query()
                .like(StrUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 返回数据
        return new ResponseResult(HttpStatus.SUCCESS,page);
    }
}
