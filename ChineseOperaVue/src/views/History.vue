<template>
  <div>
    <Header :showBackground="false" :showLogo="false"></Header>
    <div class="history-header">
      <span class="history-title">历史记录</span>
      <div class="button-group">
        <el-button @click="clearSelected">清空历史记录</el-button>

        <el-button @click="toggleBatchMode">{{
          isBatchMode ? '退出批量管理' : '批量管理'
        }}</el-button>

        <!-- 添加测试按钮 -->
        <!-- <el-button @click="testClick" type="primary">测试点击</el-button> -->
      </div>
    </div>

    <div class="opera-container">
      <div v-for="(opera, index) in validOperaList" :key="opera?.id || index" class="single-opera">
        <el-checkbox
          v-if="isBatchMode && opera"
          v-model="selectedOperas"
          :label="opera.id"
          @change="handleCheckboxChange(opera.id)"
        >
          <span></span>
        </el-checkbox>
        <div
          :class="{ 'batch-mode-overlay': isBatchMode }"
          @click="isBatchMode && opera ? handleOperaClick(opera) : null"
        >
          <SingleOpera
            v-if="opera"
            :opera="opera"
            :style="{ width: '230px', height: '220px' }"
            :class="{ 'batch-mode': isBatchMode }"
          ></SingleOpera>
          <div v-else class="error-placeholder">数据加载出错</div>
        </div>
      </div>
      <!-- :hide-on-single-page="value"   这个可以决定是否隐藏分页，因为只有一页的时候 -->
    </div>
    <!-- <div class="page-container">
                <el-pagination @current-change="handlePageChange" @size-change="handleSizeChange" 
                :hide-on-single-page="total<=pageSize?true:false"  background layout="prev, pager, next"
                :total="total" :current-page="pageNum" :page-size="pageSize"/>
        </div> -->
  </div>
</template>

<script>
import { getOperasFromUserAccess, deleteByOperaIds } from '@/api/access.js'
import SingleOpera from '@/components/Home/SingleOpera.vue'
import Header from '@/components/Home/Header.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
export default {
  name: 'History',
  components: {
    Header,
    SingleOpera
  },
  data() {
    return {
      operaList: [],
      isBatchMode: false, // 是否处于批量管理模式
      selectedOperas: []
      // pageNum:1,
      // pageSize:15,
      // total:0,
      // operaTag:'',
    }
  },
  computed: {
    // 过滤掉null或undefined的opera对象
    validOperaList() {
      return this.operaList.filter((opera) => opera && typeof opera === 'object' && opera.id)
    }
  },
  created() {
    this.getOperasByUserId()
  },
  methods: {
    getOperasByUserId() {
      console.log('获取用户历史记录...')
      getOperasFromUserAccess()
        .then((res) => {
          console.log('获取到的历史记录:', res.data)

          // 确保返回的是数组
          if (Array.isArray(res.data)) {
            // 过滤掉无效数据
            this.operaList = res.data.filter((opera) => {
              if (!opera || typeof opera !== 'object') {
                console.warn('发现无效的opera对象:', opera)
                return false
              }
              if (!opera.id) {
                console.warn('发现没有id的opera对象:', opera)
                return false
              }
              return true
            })
            console.log('过滤后的有效数据:', this.operaList)
          } else {
            console.error('API返回的数据不是数组格式:', res.data)
            this.operaList = []
            ElMessage.error('数据格式错误')
          }
        })
        .catch((error) => {
          console.error('获取历史记录失败:', error)
          this.operaList = []
          ElMessage.error('获取历史记录失败')
        })
    },
    handleCheckboxChange(operaId) {
      console.log('Checkbox状态变化:', operaId, this.selectedOperas)
    },
    handleOperaClick(opera) {
      console.log('批量模式下点击opera:', opera.id)
      // 在批量模式下点击opera卡片时，切换checkbox状态
      const index = this.selectedOperas.indexOf(opera.id)
      if (index > -1) {
        this.selectedOperas.splice(index, 1)
      } else {
        this.selectedOperas.push(opera.id)
      }
    },
    toggleBatchMode() {
      console.log('切换批量管理模式:', !this.isBatchMode)
      this.isBatchMode = !this.isBatchMode
      if (!this.isBatchMode) {
        this.selectedOperas = [] // 退出批量管理时清空选中项
      }
    },
    // 清空选中的历史记录
    clearSelected() {
      console.log('点击清空选中的历史记录, 当前选中:', this.selectedOperas)

      if (this.selectedOperas.length === 0) {
        ElMessage.warning('请选择要删除的历史记录！')
        return
      }

      ElMessageBox.confirm('确定要删除选中的历史记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          console.log('确认删除:', this.selectedOperas)
          deleteByOperaIds(this.selectedOperas)
            .then(() => {
              this.getOperasByUserId()
              ElMessage.success('删除成功')
            })
            .catch(() => {
              ElMessage.error('删除失败')
            })
        })
        .catch(() => {
          ElMessage.info('已取消删除')
        })
    },
    // testClick() {
    //   console.log('测试按钮点击成功！')
    //   alert('测试按钮点击成功！')
    // }
  }
}
</script>

<style lang="scss" scoped>
.opera-container {
  display: grid;
  padding: 0 60px;
  margin-top: 10px;
  grid-template-columns: repeat(5, 1fr);
  // grid-gap: 5px;
}

.single-opera {
  position: relative;

  .batch-mode-overlay {
    position: relative;
    cursor: pointer;

    &:hover {
      opacity: 0.8;
    }
  }

  .batch-mode {
    pointer-events: none; // 在批量模式下禁用SingleOpera的点击事件
  }

  .error-placeholder {
    width: 230px;
    height: 220px;
    background-color: #f5f5f5;
    border: 1px dashed #ccc;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #999;
    font-size: 14px;
    border-radius: 8px;
  }
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  border-bottom: 2px solid #ddd;

  .history-title {
    font-size: 24px;
    font-weight: bold;
  }

  .button-group {
    display: flex;
    gap: 10px; // 按钮之间的间距
  }
}
</style>
