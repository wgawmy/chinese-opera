<template>
    <div>
        <Header :showBackground="false" :showLogo="false"></Header>
        <div class="history-header">
            <span class="history-title">历史记录</span>
            <div class="button-group">
                <el-button @click="clearSelected">清空历史记录</el-button>
                
                <el-button @click="toggleBatchMode">{{ isBatchMode ? "退出批量管理" : "批量管理" }}</el-button>
            </div>
        </div>

        <div class="opera-container">
            
            <div v-for="(opera,index) in operaList" :key="index" class="single-opera">
                <el-checkbox v-if="isBatchMode" v-model="selectedOperas" :label="opera.id"><span></span></el-checkbox>
                <SingleOpera :opera="opera" :style="{width:'230px',height:'220px'}"></SingleOpera>
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
import {getOperasFromUserAccess,deleteByOperaIds} from '@/api/access.js';
import SingleOpera from '@/components/Home/SingleOpera.vue';
import Header from '@/components/Home/Header.vue';
import { ElMessage, ElMessageBox } from "element-plus";
export default {
    name:'History',
    components:{
    Header,
    SingleOpera
  },
    data(){
        return{
            operaList:[],
            isBatchMode: false, // 是否处于批量管理模式
            selectedOperas: [], 
            // pageNum:1,
            // pageSize:15,
            // total:0,
            // operaTag:'',
        }
    },
    created(){
        this.getOperasByUserId();
    },
    methods:{
        getOperasByUserId(){
            getOperasFromUserAccess().then(res=>{
                this.operaList = res.data;
            })
        },
        toggleBatchMode() {
            this.isBatchMode = !this.isBatchMode;
            if (!this.isBatchMode) {
                this.selectedOperas = []; // 退出批量管理时清空选中项
            }
        },
        // 清空选中的历史记录
        clearSelected() {
            if (this.selectedOperas.length === 0) {
                ElMessage.warning("请选择要删除的历史记录！");
                return;
            }

            ElMessageBox.confirm("确定要删除选中的历史记录吗？", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            }).then(() => {
                deleteByOperaIds(this.selectedOperas).then(()=>{
                    this.getOperasByUserId();
                    ElMessage.success("删除成功");
                }).catch(() => {
                    ElMessage.error("删除失败");
                });
               
                
            }).catch(() => {
                ElMessage.info("已取消删除");
            });
        }
    }
}
</script>

<style lang="scss" scoped>
.opera-container{
    display: grid;
    padding: 0 60px;
    margin-top: 10px;
    grid-template-columns: repeat(5,1fr);
    // grid-gap: 5px;
    
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