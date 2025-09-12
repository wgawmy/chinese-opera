<template>
  <div class="shop-list">
    <h2>戏曲场馆列表</h2>
    <!-- 店铺列表 -->
    <el-row :gutter="20">
      <el-col :span="8" v-for="shop in shopList" :key="shop.id">
        <ShopCard :shop="shop" />
      </el-col>
    </el-row>
  </div>
  <!-- 分页 -->
  <el-pagination
    layout="prev, pager, next"
    :total="total"
    :page-size="pageSize"
    :current-page="currentPage"
    @current-change="handlePageChange"
    background
    class="pagination"
  />
</template>

<script>
import ShopCard from "@/components/Shop/ShopCard.vue";
import { getShopListByName } from "@/api/shop.js";

export default {
  components: {
    ShopCard,
  },
  data() {
    return {
      shopList: [],
      currentPage: 1,
      pageSize: 6,
      total: 0,
    };
  },
  methods: {
    fetchShopList(page) {
      getShopListByName(page).then(res => {
        this.shopList = res.data.records;
        this.total = res.data.total;
      });
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.fetchShopList(page);  // 获取新页的数据
    },
  },
  mounted() {
    this.fetchShopList(this.currentPage);  // 初始加载数据
  },
};
</script>

<style scoped>
.shop-list {
  max-width: 1200px;
  margin: auto;
  padding: 20px;
}

h2 {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.pagination {
  position: absolute;
  bottom: 10px;  
  left: 50%;
  transform: translateX(-50%); 
  display: flex;
  justify-content: center;
}

</style>
