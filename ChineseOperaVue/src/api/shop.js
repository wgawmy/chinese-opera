import request from '@/utils/request.js'
// export function getShopList(typeId,current){
//     return request({
//         url:`/shop/of/type?typeId=${typeId}&current=${current}`,
//         method:'get',
//     })
// }
export function getAllShops() {
  return request({
    url: '/shop/all',
    method: 'get'
  })
}

export function getShopListByName(current){
    return request({
        url:`/shop/of/name?current=${current}`,
        method:'get'
    })
}

export function getShopDetailById(id){
    return request({
        url:`/shop/${id}`,
        method:'get'
    })
}