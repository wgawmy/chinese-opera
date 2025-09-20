<template>
  <div class="shop-detail">
    <!-- 标题区域 -->
    <div class="header">
      <h1>{{ shop.name }}</h1>
      <el-rate
        :model-value="shop.score / 10"
        disabled
        show-score
        score-template="{value}分"
      />
    </div>

    <!-- 图片轮播 -->
    <el-carousel class="carousel" height="320px" type="card" :interval="4000">
      <el-carousel-item v-for="(img, index) in imageList" :key="index">
        <img :src="img" class="carousel-img" />
      </el-carousel-item>
    </el-carousel>

    <!-- 信息卡片 -->
    <el-card class="info-card">
      <div class="info-grid">
        <p><el-icon><Location /></el-icon>{{ shop.area }} · {{ shop.address }}</p>
        <p><el-icon><Clock /></el-icon>营业时间：{{ shop.openHours }}</p>
        <p>人均消费：￥{{ shop.avgPrice }}</p>
        <p>销量：{{ shop.sold }} ｜ 评论：{{ shop.comments }}</p>
      </div>
    </el-card>

    <!-- 优惠券区域 -->
    <!-- <div class="coupons" v-if="coupons.length">
      <h3>🎟️ 可用优惠</h3>
      <el-row :gutter="20">
        <el-col :span="12" v-for="coupon in coupons" :key="coupon.id">
          <el-card class="coupon-card" shadow="hover">
            <div class="coupon-content">
              <div class="left">
                <h4>{{ coupon.title }}</h4>
                <p>{{ coupon.subTitle }}</p>
              </div>
              <div class="right">
                <el-button type="danger" size="small" @click="buyCoupon(coupon)">
                  🚀 抢购
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div> -->
    <div class="coupons" v-if="coupons.length">
      <h3>🎟️ 可用优惠</h3>
      <el-row :gutter="20">
        <el-col :span="12" v-for="coupon in coupons" :key="coupon.id">
          <el-card class="coupon-card" shadow="hover" :style="{ backgroundColor: coupon.stock === 0 ? '#888' : '' }">
            <div class="coupon-content">
              <div class="left">
                <h4>{{ coupon.title }}</h4>
                <p>{{ coupon.subTitle }}</p>
                <div v-if="coupon.type === 1">
                  <p>开始时间：{{ coupon.beginTime }}</p>
                  <p>结束时间：{{ coupon.endTime}}</p>
                  <p>剩余数量：{{ coupon.stock }}张</p>
                </div>
              </div>
              <div class="right">
                <el-button 
                  :type="coupon.stock === 0 ? 'default' : 'danger'" 
                  size="small" 
                  @click="buyCoupon(coupon)" 
                  :disabled="coupon.stock === 0">
                  🚀 {{ coupon.stock === 0 ? '已售罄' : '抢购' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { Location, Clock } from "@element-plus/icons-vue";
import {getShopDetailById} from '@/api/shop';
import {getVoucherList,seckillVoucher} from '@/api/voucher';
export default {
  name: "ShopDetail",
  components: {
    Location,
    Clock,
  },
  data() {
    return {
      shop:'',
      imageList: [],
      coupons: [],
    };
  },
  created() {
    this.fetchShopDetails();
  },
  computed: {
    shopId() {
      return this.$route.query.id;
    },
  },
  methods: {
    fetchShopDetails() {
      getShopDetailById(this.shopId).then(res => {
        this.shop = res.data;
        this.imageList = this.shop.images ? this.shop.images.split(",") : [];
      });
      getVoucherList(this.shopId).then(res => {
        this.coupons = res.data;
      })
    },
    buyCoupon(coupon) {
      seckillVoucher(coupon.id).then(res=>{
        if(res.code!==200){
          this.$message.error(res.msg);
          return;
        }
        this.$message.success(`已抢购：${coupon.title}`);
      })
    },
  },
};
</script>

<style scoped lang="scss">
.shop-detail {
  padding: 40px;
  min-height: 100vh;
  background: radial-gradient(circle at center, #8b1e3f, #2e0d13);
  background-repeat: repeat;
  background-size: contain;
  color: #f8e7d3;
  font-family: 'KaiTi', 'STKaiti', serif;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.header {
  text-align: center;
  margin-bottom: 32px;

  h1 {
    font-size: 40px;
    font-weight: bold;
    color: #ffd700;
    text-shadow: 2px 2px 6px #732634;
    border-bottom: 3px double #ffd700;
    padding-bottom: 10px;
  }

  .el-rate {
    margin-top: 10px;
    --el-rate-icon-color: #ffd700;
    --el-rate-disabled-void-color: #b8875b;
    --el-rate-text-color:#ffd700;
  }
}

.carousel {
  width: 90%;
  margin-bottom: 30px;

  .carousel-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(255, 215, 0, 0.3);
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.02);
    }
  }
}

.info-card {
  width: 90%;
  margin-bottom: 30px;
  background-color: rgba(255, 255, 255, 0.06);
  border: 1px solid #ffd700;
  border-radius: 16px;
  backdrop-filter: blur(6px);
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.4);

  .info-grid {
    font-size: 16px;
    line-height: 2;
    padding: 10px 20px;

    p {
      display: flex;
      align-items: center;
      color: #f1e0a5; /* 深灰色字体 */

      .el-icon {
        margin-right: 6px;
        color: #ffd700;
      }
    }
  }
}

.coupons {
  width: 90%;

  h3 {
    color: #e1c68f; /* 调整为稍微深一点的黄色 */
    font-size: 22px;
    margin-bottom: 16px;
    text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.6); /* 更强的文本阴影 */
  }

  .coupon-card {
    background: rgba(255, 255, 255, 0.07);
    border: 1px dashed #ffce00;
    border-radius: 12px;
    transition: transform 0.3s ease;
    color: #f1e0a5; /* 深灰色字体 */

    &:hover {
      transform: scale(1.03);
      box-shadow: 0 0 12px rgba(255, 215, 0, 0.5);
    }

    .coupon-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .left {
        h4 {
          font-size: 18px;
          margin-bottom: 5px;
          color: #f1e0a5; /* 调整为更深的浅黄色 */
          text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.6); /* 为标题添加阴影 */
        }

        p {
          font-size: 14px;
          color: #e1c68f; /* 调整为更深的浅黄色 */
          text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5); /* 为副标题添加阴影 */
        }
      }

      .right {
        .el-button {
          font-weight: bold;
          background: linear-gradient(145deg, #ff6e6e, #d7000f);
          color: #fff;
          border: none;
          box-shadow: 0 2px 6px rgba(255, 0, 0, 0.4);
          transition: 0.3s;

          &:hover {
            transform: scale(1.05);
            box-shadow: 0 4px 10px rgba(255, 0, 0, 0.6);
          }
        }
      }
    }
  }
}


</style>