<template>
  <div>
    <!-- 新增权限按钮 -->
    <el-button type="primary" @click="openAddDialog">新增权限</el-button>

    <el-table :data="menuList" style="width: 100%">
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="name" label="权限名称"></el-table-column>
      <el-table-column label="操作" width="180">
        <template v-slot="scope">
          <el-button type="primary" size="small" @click="editMenu(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteMenu(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增 / 编辑权限弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="400px">
      <el-form :model="editForm">
        <el-form-item label="权限名称">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <!-- 只有新增时显示权限路径输入框 -->
        <el-form-item v-if="!isEdit" label="权限路径">
          <el-input v-model="editForm.perms"></el-input>
        </el-form-item>
      </el-form>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMenu">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getpermission, updateMenu, deletemenuById, addMenu } from "@/api/menu.js";

export default {
  data() {
    return {
      menuList: [],
      dialogVisible: false,
      isEdit: false, // 是否为编辑模式
      editForm: {
        id: null,
        name: "",
        perms: "" // 仅新增时使用
      }
    };
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? "编辑权限" : "新增权限";
    }
  },
  created() {
    this.getUserpermission();
  },
  methods: {
    getUserpermission() {
      getpermission().then(res => {
        this.menuList = res.data;
      });
    },
    openAddDialog() {
      this.isEdit = false;
      this.editForm = { id: null, name: "", perms: "" }; // 清空表单
      this.dialogVisible = true;
    },
    editMenu(menu) {
      this.isEdit = true;
      this.editForm = { id: menu.id, name: menu.name }; // 不带 perms
      this.dialogVisible = true;
    },
    saveMenu() {
      if (this.isEdit) {
        // 编辑菜单（不修改 perms）
        updateMenu({ id: this.editForm.id, name: this.editForm.name }).then(() => {
          const index = this.menuList.findIndex(m => m.id === this.editForm.id);
          if (index !== -1) {
            this.menuList[index].name = this.editForm.name;
          }
          this.dialogVisible = false;
          this.$message.success("修改成功！");
        });
      } else {
      
        addMenu(this.editForm).then(res => {
          this.menuList.push(res.data); // 直接添加到列表
          this.dialogVisible = false;
          this.$message.success("新增成功！");
        });
      }
    },
    deleteMenu(id) {
      deletemenuById(id).then(() => {
        this.menuList = this.menuList.filter(m => m.id !== id);
        this.$message.success("删除成功！");
      });
    }
  }
};
</script>

<style scoped>
.el-table {
  margin-top: 20px;
}
.el-button {
  margin-bottom: 10px;
}
</style>
