<template>
  <div style="width: 100%;">
    <el-table :data="users" stripe  style="width: 100%">
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="userName" label="用户名"></el-table-column>
      <el-table-column prop="nickName" label="昵称"></el-table-column>
      <el-table-column prop="status" label="状态" >
        <template v-slot="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="角色">
        <template v-slot="scope">
          <el-tag v-for="role in scope.row.roles" :key="role.id" type="info" class="role-tag">
            {{ role.roleName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button type="primary" size="small" @click="editUser(scope.row)">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 修改用户信息 -->
    <el-dialog title="修改用户" v-model="dialogVisible" width="500px">
      <el-form :model="editForm" label-width="80px">
        <!-- <el-form-item label="昵称">
          <el-input v-model="editForm.nickName"></el-input>
        </el-form-item> -->
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="请选择">
            <el-option label="正常" value="0"></el-option>
            <el-option label="停用" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option v-for="role in allRoles" :key="role.id" :label="role.roleName" :value="role.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {getallusers,updateuserinfo} from '@/api/user.js'
import {getroles} from '@/api/role.js'
export default {
  data() {
    return {
      users: [],
      allRoles: [
      ],
      dialogVisible: false,
      editForm: {
        id: null,
        // nickName: "",
        status: "",
        roleIds: []
      }
    };
  },
  created(){
    this.getUserInfo();
  },
  methods: {
    getUserInfo(){
      getallusers().then(res=>{
        this.users = res.data;
      }),
      getroles().then(res=>{
        this.allRoles = res.data;
      })
    },
    editUser(user) {
      this.editForm = {
        id: user.id,
        status: user.status,
        roleIds: user.roles.map(role => role.id)
      };
      this.dialogVisible = true;
    },
    saveEdit() {
      const index = this.users.findIndex(user => user.id === this.editForm.id);
      updateuserinfo(this.editForm).then(
        res=>{
          if (index !== -1) {
          this.users[index].status = this.editForm.status;
          this.users[index].roles = this.allRoles.filter(role => this.editForm.roleIds.includes(role.id));
          }
          this.dialogVisible = false;
          this.$message.success("修改成功！");
        }
      )
    }
  }
};
</script>

<style scoped>
.role-tag {
  margin: 2px;
  font-size: 12px;
}
</style>
