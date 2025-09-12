<template>
    <div class="user-profile">
      <el-card class="user-card">
        <div class="user-header">
          <el-avatar :size="100" :src="user.avatar" />
          <div class="user-info">
            <h2>{{ user.userName }}</h2>
          </div>
        </div>
  
        <el-divider />
  
        <el-descriptions title="用户信息" :column="1">
          <el-descriptions-item label="昵称">{{ user.nickName }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ user.email || "未填写" }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ user.phonenumber || "未填写" }}</el-descriptions-item>
          <el-descriptions-item label="性别">
            {{ user.sex === "0" ? "男" : user.sex === "1" ? "女" : "保密" }}
          </el-descriptions-item>
        </el-descriptions>
  
        <div class="button-group">
          <el-button type="primary" @click="editUser">修改</el-button>
        </div>
      </el-card>
  
      <!-- 修改用户信息的弹框 -->
      <el-dialog v-model="dialogVisible" title="修改用户信息" width="400px">
        <el-form ref="userForm" :model="tempUser" label-width="80px">
          <el-form-item label="昵称">
            <el-input v-model="tempUser.nickName" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="tempUser.email" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="tempUser.phonenumber" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="tempUser.sex">
              <el-radio label="0">男</el-radio>
              <el-radio label="1">女</el-radio>
              <el-radio label="2">保密</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
  
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="saveChanges">保存</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script>
  import { ElMessage } from "element-plus";
  import {updatePerson,getInfo} from '@/api/user.js'
 
  export default {
    data() {
      return {
        user: {
        },
        dialogVisible: false,
        tempUser: {}, // 临时存储用户数据
      };
    },
    created(){
        this.getUserInfo();
    },
    methods: {
        getUserInfo(){
            getInfo().then(res=>{
                this.user = res.data.user;
            })
        },
      // 点击“修改”按钮时，复制用户数据到 tempUser
      editUser() {
        this.tempUser = { ...this.user };
        this.dialogVisible = true;
      },
      // 点击“保存”按钮时，更新用户信息
      saveChanges() {
        this.user = { ...this.tempUser };
        this.dialogVisible = false;
        updatePerson(this.tempUser).then(res=>{
            this.getUserInfo();
            ElMessage.success("用户信息已更新！");
        })

      },
    },
  };
  </script>
  
  <style scoped>
  .user-profile {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
  }
  
  .user-card {
    width: 400px;
    padding: 20px;
    border-radius: 10px;
  }
  
  .user-header {
    display: flex;
    align-items: center;
  }
  
  .user-info {
    margin-left: 20px;
  }
  
  .user-info h2 {
    margin: 0;
    font-size: 20px;
  }
  
  .user-info p {
    margin: 5px 0;
    color: #666;
  }
  
  .button-group {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
  </style>
  