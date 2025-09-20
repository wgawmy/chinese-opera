<template>
  <div class="voucher-manage">
    <el-card>
      <div style="margin-bottom: 16px; display: flex; gap: 12px; align-items: center">
        <el-select
          v-model="filterType"
          placeholder="全部类型"
          style="width: 120px"
          @change="fetchList"
        >
          <el-option label="全部" value="" />
          <el-option label="普通券" :value="0" />
          <el-option label="秒杀券" :value="1" />
        </el-select>
        <el-select
          v-model="filterStatus"
          placeholder="全部状态"
          style="width: 120px"
          @change="fetchList"
        >
          <el-option label="全部" value="" />
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="2" />
          <el-option label="过期" :value="3" />
        </el-select>
        <el-button type="primary" @click="openAddDialog">新增优惠券</el-button>
      </div>
      <el-table :data="voucherList" border stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="shopName" label="所属店铺" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="subTitle" label="副标题" />
        <el-table-column prop="rules" label="使用规则" />
        <el-table-column prop="payValue" label="支付金额(元)" :formatter="fen2yuan" />
        <el-table-column prop="actualValue" label="抵扣金额(元)" :formatter="fen2yuan" />
        <el-table-column prop="type" label="类型" :formatter="typeFormatter" />
        <el-table-column prop="status" label="状态" :formatter="statusFormatter" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑优惠券' : '新增优惠券'">
      <el-form :model="form" label-width="90px">
        <el-form-item label="所属店铺" required>
          <el-select v-model="form.shopId" placeholder="请选择店铺">
            <el-option
              v-for="shop in shopList"
              :key="shop.id"
              :label="shop.name"
              :value="shop.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subTitle" />
        </el-form-item>
        <el-form-item label="使用规则">
          <el-input v-model="form.rules" type="textarea" />
        </el-form-item>
        <el-form-item label="支付金额(元)" required>
          <el-input-number v-model="form.payValueYuan" :min="0" :step="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="抵扣金额(元)" required>
          <el-input-number v-model="form.actualValueYuan" :min="0" :step="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="form.type">
            <el-option label="普通券" :value="0" />
            <el-option label="秒杀券" :value="1" />
          </el-select>
        </el-form-item>
        <!-- 秒杀券专属字段：仅type为1时显示 -->
        <template v-if="form.type === 1">
          <el-form-item label="库存" required>
            <el-input-number v-model="form.stock" :min="0" :step="1" />
          </el-form-item>
          <el-form-item label="开始时间" required>
            <el-date-picker
              v-model="form.beginTime"
              type="datetime"
              placeholder="选择开始时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="结束时间" required>
            <el-date-picker
              v-model="form.endTime"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%"
            />
          </el-form-item>
        </template>
        <el-form-item label="状态" required>
          <el-select v-model="form.status">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="2" />
            <el-option label="过期" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVouchers, addVoucher, updateVoucher, deleteVoucher } from '@/api/voucher'
import { getAllShops } from '@/api/shop'

const voucherList = ref([])
const filterType = ref('')
const filterStatus = ref('')
const dialogVisible = ref(false)
const form = ref({})
const shopList = ref([])

// 公共方法：根据shopId赋值shopName
const assignShopNames = () => {
  if (!voucherList.value.length || !shopList.value.length) return
  voucherList.value.forEach((voucher) => {
    const shop = shopList.value.find((s) => s.id === voucher.shopId)
    voucher.shopName = shop ? shop.name : ''
  })
}

const fetchList = async () => {
  const res = await getVouchers({ type: filterType.value, status: filterStatus.value })
  if (res && res.code === 200) {
    voucherList.value = res.data || []
  } else if (res && res.data) {
    voucherList.value = res.data
  } else {
    voucherList.value = []
  }
  assignShopNames()
}

const fetchShops = async () => {
  const res = await getAllShops()
  if (res && res.code === 200) {
    shopList.value = res.data
  }
  assignShopNames()
}

onMounted(() => {
  fetchShops()
  fetchList() // 原有的优惠券列表
})

const openAddDialog = () => {
  form.value = {
    type: 0,
    status: 1,
    payValueYuan: 0,
    actualValueYuan: 0,
    stock: 0,
    beginTime: '',
    endTime: '',
    shopId: ''
  }
  dialogVisible.value = true
}
const openEditDialog = (row) => {
  form.value = {
    ...row,
    payValueYuan: row.payValue ? row.payValue / 100 : 0,
    actualValueYuan: row.actualValue ? row.actualValue / 100 : 0,
    stock: row.stock || 0,
    beginTime: row.beginTime || '',
    endTime: row.endTime || '',
    shopId: row.shopId || ''
  }
  dialogVisible.value = true
}
const handleSave = async () => {
  // 校验
  if (
    !form.value.title ||
    form.value.payValueYuan <= 0 ||
    form.value.actualValueYuan <= 0 ||
    !form.value.shopId
  ) {
    ElMessage.warning('请填写完整信息')
    return
  }
  // 秒杀券校验
  if (form.value.type === 1) {
    if (!form.value.stock || !form.value.beginTime || !form.value.endTime) {
      ElMessage.warning('请填写完整的秒杀券信息')
      return
    }
  }
  // 元转分
  const data = {
    ...form.value,
    payValue: Math.round(form.value.payValueYuan * 100),
    actualValue: Math.round(form.value.actualValueYuan * 100)
  }
  delete data.payValueYuan
  delete data.actualValueYuan
  let res
  if (form.value.id) {
    res = await updateVoucher(data)
    if (res && res.code === 200) ElMessage.success('编辑成功')
  } else {
    res = await addVoucher(data)
    if (res && res.code === 200) ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  fetchList()
}
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该优惠券吗？', '提示', { type: 'warning' }).then(async () => {
    const res = await deleteVoucher(id)
    if (res && res.code === 200) ElMessage.success('删除成功')
    fetchList()
  })
}
const typeFormatter = (row) => (row.type === 0 ? '普通券' : '秒杀券')
const statusFormatter = (row) => {
  if (row.status === 1) return '上架'
  if (row.status === 2) return '下架'
  if (row.status === 3) return '过期'
  return '未知'
}
const fen2yuan = (row, col, val) => (val / 100).toFixed(2)
</script>

<style scoped>
.voucher-manage {
  max-width: 1100px;
  margin: 30px auto;
}
</style>
