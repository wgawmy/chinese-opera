<template>
  <div>
    <h1>角色管理</h1>
    <el-card>
      <el-table :data="roles" style="width: 100%">
        <el-table-column prop="roleName" label="角色名称"></el-table-column>
        <el-table-column prop="meuns" label="权限" width="500px">
          <template #default="{ row }">
            <span v-if="row.meuns && row.meuns.length > 0">
              {{ getPermissionNames(row.meuns) }}
            </span>
            <span v-else>无权限</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRole(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="primary" style="margin-top: 10px" @click="openAddDialog">新增角色</el-button>
    </el-card>

    <!-- 新增角色弹窗 -->
    <el-dialog v-model="addDialogVisible" title="新增角色">
      <el-form :model="addRoleForm">
        <el-form-item label="角色名称">
          <el-input v-model="addRoleForm.name"></el-input>
        </el-form-item>
        <!-- <el-form-item label="权限">
          <el-select v-model="addRoleForm.permissions" multiple placeholder="选择权限">
            <el-option v-for="perm in allPermissions" :key="perm.id" :label="perm.name" :value="perm.id" />
          </el-select>
        </el-form-item> -->
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveNewRole">保存</el-button>
      </template>
    </el-dialog>

    <!-- 编辑角色弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑角色">
      <el-form :model="editRoleForm">
        <el-form-item label="角色名称">
          <el-input v-model="editRoleForm.name"></el-input>
        </el-form-item>
        <el-form-item label="权限">
          <el-select v-model="editRoleForm.permissions" multiple placeholder="选择权限">
            <el-option v-for="perm in allPermissions" :key="perm.id" :label="perm.name" :value="perm.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEditRole">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getroles,deleteRoleInfo,addrole,updateRole} from "@/api/role.js";
import { getpermission } from "@/api/menu.js";

export default {
  name: "ManagerRole",
  data() {
    return {
      roles: [],
      allPermissions: [],
      addDialogVisible: false,
      editDialogVisible: false,
      addRoleForm: {
        name: "",
        permissions: [],
      },
      editRoleForm: {
        id: null,
        name: "",
        permissions: [],
      },
    };
  },
  created() {
    this.getRolesInfo();
  },
  methods: {
    getRolesInfo() {
      getroles().then((res) => {
        this.roles = res.data;
      });
      getpermission().then((res) => {
        this.allPermissions = res.data;
      });
    },
    openAddDialog() {
      this.addRoleForm = { name: "", permissions: [] };
      this.addDialogVisible = true;
    },
    // openEditDialog(role) {
    //   if (role.meuns && role.meuns.length > 0) { 
    //     this.editRoleForm = {
    //       id: role.id,
    //       name: role.roleName,
    //       permissions: role.meuns.map((m) => m.name)
    //     };
    //   } else {
    //     this.editRoleForm = {
    //       id: role.id,
    //       name: role.roleName,
    //       permissions: [] // 如果 meuns 为空，设置 permissions 为空数组
    //     };
    //   }

    //   this.editDialogVisible = true;
    // },
    openEditDialog(role) {
      if (role.meuns && role.meuns.length > 0) { 
        this.editRoleForm = {
          id: role.id,
          name: role.roleName,
          permissions: role.meuns.map((m) => m.id) // 存储 menu 的 id
        };
      } else {
        this.editRoleForm = {
          id: role.id,
          name: role.roleName,
          permissions: [] // 如果 meuns 为空，设置为空数组
        };
      }

      this.editDialogVisible = true;
    },
    saveEditRole() {
      // 这里需要把数据传给后端，确保后端接收的是 menu.id
      // 发送给后端的数据
      const payload = {
        id: this.editRoleForm.id,
        menus: this.editRoleForm.permissions 
      };
      // 调用后端 API 更新角色信息
      updateRole(payload).then((res) => {
        this.getRolesInfo(); // 重新拉取角色数据
        this.editDialogVisible = false;
        this.$message.success("角色更新成功");
      });
    },

    saveNewRole() {
      // this.roles.push({
      //   id: Date.now(),
      //   roleName: this.addRoleForm.name,
      //   // permissions: [...this.addRoleForm.permissions],
      //   // meuns: this.addRoleForm.permissions.map((name) => ({ name })),
      // });
      this.addDialogVisible = false;
      addrole(this.addRoleForm).then(res=>{
        this.getRolesInfo();
        this.$message.success("新增角色成功");
      })
    },
    // saveEditRole() {
    //   const index = this.roles.findIndex((r) => r.id === this.editRoleForm.id);
    //   if (index !== -1) {
    //     this.roles[index].roleName = this.editRoleForm.name;
    //     this.roles[index].meuns = this.editRoleForm.permissions.map((name) => ({ name }));
    //   }
    //   console.log(this.editRoleForm)
    //   this.editDialogVisible = false;
    //   this.$message.success("角色更新成功");
    // },
    deleteRole(id) {
      this.roles = this.roles.filter((role) => role.id !== id);
      deleteRoleInfo(id).then(res=>{
        this.$message.success("角色删除成功");
      })
    },
    getPermissionNames(menus) {
      return menus.map((item) => item.name).join(",");
    },
  },
};
</script>

<style scoped>
.el-card {
  padding: 20px;
}
</style>
