<template>
  <el-button type="success" @click="addCollectionButton">合集管理</el-button>

  <el-button type="warning">导入</el-button>

  <el-button type="warning">导出</el-button>

  <!-- 搜索框 -->
  <!-- <div>
    <el-input
      v-model="inputContent"
      style="width: 240px"
      size="large"
      placeholder="Please Input"
    >
    </el-input>
    <el-icon :size="20">
        <Search/>
    </el-icon> 
  </div> -->
  
    
  

  <el-table :data="operaList" style="width: 100%">
    <el-table-column type="selection" width="55" />
    <el-table-column property="name" label="名称" width="150" show-overflow-tooltip/>
    <el-table-column property="singer" label="演唱者" width="150" show-overflow-tooltip/>
    <el-table-column property="des" label="描述" width="200" show-overflow-tooltip/>
    <el-table-column property="tag" label="Tag" width="100"></el-table-column>

    <el-table-column property="collectName" label="合集" width="200"/>
    <!-- 操作 -->
    <el-table-column  label="操作">
      <template #default="scope">
        <el-button link type="primary" size="small" @click="handleDeleteClick">
          删除
        </el-button>
        <el-button link type="primary" v-hasPermi="['manage:edit']" size="small" @click="handleEditClick(scope.row)">
          编辑
        </el-button>
      </template>
        
    </el-table-column>
  </el-table>

  <div class="pagination-fixed-bottom">  
       
    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      layout="prev, pager, next, jumper"
      :total="total"
      @current-change="handleCurrentChange"
    />
        
  </div>


  <el-dialog title="戏曲信息" v-model="dialogVisible" width="50%" center>
        <el-form :model="opera" label-width="120px">
          <el-form-item label="名称">
            <el-input v-model="opera.name" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="类型">
            <el-select
              v-model="opera.tag"
              placeholder="选择类型"
              size="large"
              style="width: 80%"
              @change="handleTagChange"
            >
              <el-option
                v-for="(tag,index) in operaTags"
                :key="index"
                :label="tag"
                :value="tag"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="合集">
            <el-select
              v-model="opera.collectName"
              placeholder="选择合集"
              size="large"
              style="width: 80%"
              @change="handleCollectChange"
            >
              <el-option
                v-for="(collect,index) in collectList"
                :key="index"
                :label="collect.title"
                :value="collect"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="演唱者">
            <el-input v-model="opera.singer" style="width: 80%"></el-input>
          </el-form-item>
          <el-form-item label="简介">
            <el-input v-model="opera.des" style="width: 80%"></el-input>
          </el-form-item>
        </el-form>

        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="updateOperaInfo">确 定</el-button>
          </span>
        </template> 
  </el-dialog>

  <!-- 新增合集信息 -->
  <el-dialog title="新增合集" v-model="collectionDialog" width="50%" center>
    <el-form :model="collection" label-width="120px">
      <el-form-item label="名称">
        <el-input v-model="collection.title" style="width: 80%"></el-input>
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="collection.description" style="width: 80%"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
          <span class="dialog-footer">
            <el-button @click="collectionDialog = false">取 消</el-button>
            <el-button type="primary" @click="saveCollectionInfo">确 定</el-button>
          </span>
        </template> 
  </el-dialog>
  
</template>

<script>
import {getAlloperaInfo,updateOpera,} from '@/api/opera.js'
import {getCollectionInfo,addCollection} from '@/api/collection.js'
export default {
  data(){
    return{
      inputContent:'',
      pageNum:1,
      pageSize:15,
      total:0,
      collectionDialog:false,
      dialogVisible:false,
      opera:[],
      collection:{},
      collectList:[],
      operaTags:['京剧', '黄梅戏', '豫剧','川剧','越剧','秦腔', '昆曲','湘剧','评剧'],
      operaList:[],
    }
  },
  created(){
    this.loadOperaInfo();
    this.loadCollectionInfo();
  },
  methods:{
    addCollectionButton(){
      this.collectionDialog = true;
    },
    handleCollectChange(collect) {
      // 当选择值发生改变时，可以做额外处理
      this.opera.collectName = collect.title;
      this.opera.collectionId= collect.collectionId;
    },
    handleTagChange(value){
      this.opera.tag = value;
    },
    async loadOperaInfo(){
      const params  = {
        pageNum:this.pageNum,
        pageSize:this.pageSize
      }
      const res = await getAlloperaInfo(params);
      this.operaList = res.data.records;
      this.total = res.data.total
    },
    async loadCollectionInfo(){
      const res = await getCollectionInfo();
      this.collectList = res.data;
    },
    handleCurrentChange(newPage){
      this.pageNum = newPage;
      this.loadOperaInfo();
      // 获取
    },
    handleEditClick(row){
      this.opera = row;
      this.dialogVisible = true;
    },
    handleDeleteClick(){

    },
    async updateOperaInfo(){
      const res = await updateOpera(this.opera);
      if(res.data.code==200){
        alert("更新数据成功");
        this.dialogVisible = false;
        return;
      }
      alert("更新数据失败");
      
    },
    async saveCollectionInfo(){
      this.collection.createdAt = new Date().toISOString();
      const res = await addCollection(this.collection);
      if(res.data.code==200){
        this.collectionDialog = false;
        return;
      }
      alert("更新数据失败");
    }
  }
}
</script>

<style scoped lang="scss">

.pagination-fixed-bottom {
  position: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  width:80%;
  bottom: 0;
  border-top: 1px solid #ebeef5; 
  // padding: 10px; 
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.1); 
}
</style>