<!-- 1.这会根据点击的不同类型，展示不同类型的戏曲,这个页面可能会用到懒加载，因为从后端获取的戏曲资源较多。 -->
<!-- 2.我该如何对我自己设计的组件，设置其样式。 -->
<template>
  <Header :showBackground="false" :showLogo="false"></Header>
  <div class="opera-container">
    <div v-for="(opera, index) in operaInfoList" :key="index" class="single-opera">
      <SingleOpera :opera="opera" :style="{ width: '230px', height: '220px' }"></SingleOpera>
    </div>
    <!-- :hide-on-single-page="value"   这个可以决定是否隐藏分页，因为只有一页的时候 -->
  </div>
  <div class="page-container">
    <el-pagination
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
      :hide-on-single-page="total <= pageSize ? true : false"
      background
      layout="prev, pager, next"
      :total="total"
      :current-page="pageNum"
      :page-size="pageSize"
    />
  </div>
</template>

<script>
import SingleOpera from '@/components/Home/SingleOpera.vue'
import Header from '@/components/Home/Header.vue'
import { getOperaListByTag } from '@/api/opera.js'
export default {
  components: {
    SingleOpera,
    Header
  },
  data() {
    return {
      pageNum: 1,
      pageSize: 15,
      total: 0,
      operaTag: '',
      operaInfoList: []
    }
  },
  created() {
    this.operaTag = this.$route.query.operaTag
    this.getOperaList()
  },
  methods: {
    getOperaList() {
      const operaParam = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        operaTag: this.operaTag
      }
      getOperaListByTag(operaParam).then((res) => {
        this.total = res.total
        this.operaInfoList = res.records
      })
    },
    handlePageChange(newPageNum) {
      this.pageNum = newPageNum
      this.getOperaList()
    }
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
.page-container {
  width: 100%;
  // margin-top: 5px;
  display: flex;
  position: fixed;
  bottom: 0;
  justify-content: center;
  align-items: center;
}
</style>
