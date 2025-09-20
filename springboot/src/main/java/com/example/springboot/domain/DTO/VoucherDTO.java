package com.example.springboot.domain.DTO;

import com.example.springboot.domain.Voucher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDTO {
    private Long id;
    private String title;
    private String subTitle;
    private String rules;
    private Long shopId;
    private Long payValue;
    private Long actualValue;
    private Integer type;
    private Integer status;
    private Integer stock; // 秒杀券专属
    private LocalDateTime beginTime; // 秒杀券专属
    private LocalDateTime endTime;

    // 从Voucher实体构造DTO
    public VoucherDTO(Voucher v) {
        this.id = v.getId();
        this.title = v.getTitle();
        this.subTitle = v.getSubTitle();
        this.rules = v.getRules();
        this.payValue = v.getPayValue();
        this.actualValue = v.getActualValue();
        this.type = v.getType();
        this.status = v.getStatus();
        this.shopId = v.getShopId();
        // 秒杀券专属字段需单独set
    }

    // DTO转Voucher实体
    public Voucher toVoucher() {
        Voucher v = new Voucher();
        v.setId(this.id);
        v.setTitle(this.title);
        v.setSubTitle(this.subTitle);
        v.setRules(this.rules);
        v.setPayValue(this.payValue);
        v.setActualValue(this.actualValue);
        v.setType(this.type);
        v.setStatus(this.status);
        v.setShopId(this.shopId);
        return v;
    }
}
