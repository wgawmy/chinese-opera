import request from '@/utils/request'

// 查询优惠券列表（支持类型、状态筛选）
export function getVouchers(params) {
  return request({
    url: '/voucher/list',
    method: 'get',
    params
  })
}

// 新增优惠券（type=1时带上秒杀券字段）
export function addVoucher(data) {
  return request({
    url: '/voucher/add',
    method: 'post',
    data
  })
}

// 编辑优惠券（type=1时带上秒杀券字段）
export function updateVoucher(data) {
  return request({
    url: '/voucher/update',
    method: 'post',
    data
  })
}

// 删除优惠券
export function deleteVoucher(id) {
  return request({
    url: `/voucher/delete/${id}`,
    method: 'delete'
  })
}

//查找所有的优惠卷
export function getVoucherList(shopId) {
  return request({
    url: `/voucher/list/${shopId}`,
    method: 'get'
  })
}

//抢购优惠卷
export function seckillVoucher(voucherId) {
  return request({
    url: `/voucher-order/seckill/${voucherId}`,
    method: 'post'
  })
}
