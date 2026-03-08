<template>
  <div class="template-manage">
    <el-card>
      <div
        style="
          margin-bottom: 16px;
          display: flex;
          gap: 12px;
          align-items: center;
          justify-content: space-between;
        "
      >
        <div>
          <span style="font-size: 22px; font-weight: bold">属性模板管理</span>
          <span style="color: #888; margin-left: 12px">管理生态大数据相关的属性模板及字段配置</span>
        </div>
        <el-button type="primary" @click="openAddDialog">新增模板</el-button>
      </div>
      <el-table :data="templates" border stripe style="width: 100%; margin-bottom: 16px">
        <el-table-column prop="name" label="模板名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="fieldCount" label="字段数" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '启用'" type="success">启用</el-tag>
            <el-tag v-else type="info">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="viewTemplate(scope.row)">详情</el-button>
            <el-button size="small" @click="editTemplate(scope.row)">编辑</el-button>
            <el-button size="small" type="warning" @click="toggleStatus(scope.row)">
              {{ scope.row.status === '启用' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="showDetailModal" title="模板详情" width="700px">
      <!-- <el-descriptions :title="currentTemplate?.name" :column="2" border>
        <el-descriptions-item label="模板ID">{{ currentTemplate?.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentTemplate?.status === '启用'" type="success">启用</el-tag>
          <el-tag v-else type="info">禁用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ currentTemplate?.creator }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{
          currentTemplate?.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{
          currentTemplate?.description
        }}</el-descriptions-item>
      </el-descriptions> -->
      <el-divider>字段信息</el-divider>
      <el-table :data="currentFields" border stripe>
        <el-table-column prop="sortOrder" label="排序" width="60" />
        <el-table-column prop="cnName" label="中文名称" />
        <el-table-column prop="enName" label="英文名称" />
        <el-table-column prop="type" label="数据类型">
          <template #default="scope">
            <el-tag>{{ getTypeLabel(scope.row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="required" label="必填" width="60">
          <template #default="scope">
            <el-icon v-if="scope.row.required"><i class="el-icon-check"></i></el-icon>
            <el-icon v-else><i class="el-icon-close"></i></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
      </el-table>
      <template #footer>
        <el-button @click="showDetailModal = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="showAddModal" :title="isEditing ? '编辑模板' : '新增模板'" width="800px">
      <el-form :model="form" label-width="100px" :rules="formRules" ref="formRef">
        <el-form-item label="模板名称" prop="name" required>
          <el-input v-model="form.name" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="启用" value="启用" />
            <el-option label="禁用" value="禁用" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-divider>字段配置</el-divider>
        <el-table :data="form.fields" border stripe style="margin-bottom: 8px">
          <el-table-column prop="sortOrder" label="排序" width="60">
            <template #default="scope">
              <el-input-number v-model="scope.row.sortOrder" :min="1" size="small" />
            </template>
          </el-table-column>
          <el-table-column prop="cnName" label="中文名称" width="120">
            <template #default="scope">
              <el-input v-model="scope.row.cnName" size="small" placeholder="如：碳汇量" />
            </template>
          </el-table-column>
          <el-table-column prop="enName" label="英文名称" width="120">
            <template #default="scope">
              <el-input v-model="scope.row.enName" size="small" placeholder="如：carbon_sink" />
            </template>
          </el-table-column>
          <el-table-column prop="type" label="数据类型" width="120">
            <template #default="scope">
              <el-select
                v-model="scope.row.type"
                size="small"
                @change="handleTypeChange(scope.$index)"
              >
                <el-option label="整数(int)" value="int" />
                <el-option label="字符串(varchar)" value="varchar" />
                <el-option label="小数(decimal)" value="decimal" />
                <el-option label="日期时间(datetime)" value="datetime" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="长度/小数位" width="140">
            <template #default="scope">
              <el-input-number
                v-if="['int', 'varchar', 'decimal'].includes(scope.row.type)"
                v-model="scope.row.length"
                :min="1"
                size="small"
                style="width: 60px"
                placeholder="长度"
              />
              <el-input-number
                v-if="scope.row.type === 'decimal'"
                v-model="scope.row.decimalPlaces"
                :min="0"
                size="small"
                style="width: 60px; margin-left: 4px"
                placeholder="小数位"
              />
            </template>
          </el-table-column>
          <el-table-column prop="required" label="必填" width="60">
            <template #default="scope">
              <el-checkbox v-model="scope.row.required" />
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述">
            <template #default="scope">
              <el-input v-model="scope.row.description" size="small" placeholder="字段含义..." />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template #default="scope">
              <el-button
                type="danger"
                size="small"
                icon="el-icon-delete"
                @click="removeField(scope.$index)"
                circle
              />
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" plain icon="el-icon-plus" @click="addField">添加字段</el-button>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveTemplate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      // 生态模板数据（写死）
      templates: [
        {
          id: 1,
          name: '森林碳汇监测模板',
          description:
            '用于记录森林生态系统中的碳汇数据，包括碳储量、固碳能力等关键指标，支持森林生态价值评估和碳汇交易核算。',
          status: '启用',
          creator: 'admin',
          createTime: '2024-05-10 09:30:00',
          fieldCount: 4
        },
        {
          id: 2,
          name: '湿地水质监测模板',
          description:
            '用于监测湿地水体质量，记录PH值、溶解氧、污染物含量等指标，评估湿地生态系统健康状况。',
          status: '启用',
          creator: 'eco_user',
          createTime: '2024-05-15 14:20:00',
          fieldCount: 5
        },
        {
          id: 3,
          name: '生物多样性调查模板',
          description:
            '记录特定区域内物种分布、数量及变化趋势，支持生物多样性保护和生态系统平衡评估。',
          status: '禁用',
          creator: 'admin',
          createTime: '2024-04-28 11:15:00',
          fieldCount: 6
        }
      ],

      // 生态字段数据（写死）
      allFields: [
        // 森林碳汇监测模板字段
        {
          id: 101,
          templateId: 1,
          cnName: '碳汇量',
          enName: 'carbon_sink',
          type: 'decimal',
          length: 10,
          decimalPlaces: 2,
          required: true,
          sortOrder: 1,
          description: '森林区域的碳汇总量，单位：吨'
        },
        {
          id: 102,
          templateId: 1,
          cnName: '植被类型',
          enName: 'vegetation_type',
          type: 'varchar',
          length: 50,
          decimalPlaces: null,
          required: true,
          sortOrder: 2,
          description: '主要植被种类，如：针叶林、阔叶林、混交林等'
        },
        {
          id: 103,
          templateId: 1,
          cnName: '监测时间',
          enName: 'monitor_time',
          type: 'datetime',
          length: null,
          decimalPlaces: null,
          required: true,
          sortOrder: 3,
          description: '数据采集的具体时间'
        },
        {
          id: 104,
          templateId: 1,
          cnName: '森林面积',
          enName: 'forest_area',
          type: 'decimal',
          length: 8,
          decimalPlaces: 2,
          required: false,
          sortOrder: 4,
          description: '监测区域的森林面积，单位：公顷'
        },

        // 湿地水质监测模板字段
        {
          id: 201,
          templateId: 2,
          cnName: 'PH值',
          enName: 'ph_value',
          type: 'decimal',
          length: 5,
          decimalPlaces: 1,
          required: true,
          sortOrder: 1,
          description: '水体酸碱度，正常范围6.5-8.5'
        },
        {
          id: 202,
          templateId: 2,
          cnName: '溶解氧',
          enName: 'dissolved_oxygen',
          type: 'decimal',
          length: 6,
          decimalPlaces: 2,
          required: true,
          sortOrder: 2,
          description: '水中溶解氧含量，单位：mg/L'
        },
        {
          id: 203,
          templateId: 2,
          cnName: '氨氮含量',
          enName: 'ammonia_nitrogen',
          type: 'decimal',
          length: 6,
          decimalPlaces: 3,
          required: false,
          sortOrder: 3,
          description: '水体中氨氮的含量，单位：mg/L'
        },
        {
          id: 204,
          templateId: 2,
          cnName: '采样点',
          enName: 'sampling_point',
          type: 'varchar',
          length: 50,
          decimalPlaces: null,
          required: true,
          sortOrder: 4,
          description: '水样采集的具体地点名称或编号'
        },
        {
          id: 205,
          templateId: 2,
          cnName: '采样时间',
          enName: 'sampling_time',
          type: 'datetime',
          length: null,
          decimalPlaces: null,
          required: true,
          sortOrder: 5,
          description: '水样采集的具体时间'
        }
      ],

      // 模态框状态
      showDetailModal: false,
      showAddModal: false,
      isEditing: false,

      // 当前选中的模板和字段
      currentTemplate: null,
      currentFields: [],

      // 表单数据
      form: {
        id: null,
        name: '',
        description: '',
        status: '启用',
        fields: []
      }
    }
  },
  methods: {
    // 查看模板详情
    viewTemplate(row) {
      this.currentTemplate = row
      this.currentFields = this.allFields
        .filter((f) => f.templateId === row.id)
        .sort((a, b) => a.sortOrder - b.sortOrder)
      this.showDetailModal = true
    },

    // 关闭详情模态框
    closeDetailModal() {
      this.showDetailModal = false
      this.currentTemplate = null
      this.currentFields = []
    },

    // 打开新增模板模态框
    openAddTemplateModal() {
      this.isEditing = false
      this.form = {
        id: null,
        name: '',
        description: '',
        status: '启用',
        fields: []
      }
      this.showAddModal = true
    },

    // 编辑模板
    editTemplate(id) {
      const template = this.templates.find((t) => t.id === id)
      this.form = {
        id: template.id,
        name: template.name,
        description: template.description,
        status: template.status,
        fields: [...this.allFields.filter((f) => f.templateId === id)]
      }
      this.isEditing = true
      this.showAddModal = true
    },

    // 关闭新增/编辑模态框
    closeAddModal() {
      this.showAddModal = false
    },

    // 添加字段
    addField() {
      const newId = Math.max(0, ...this.allFields.map((f) => f.id)) + 1
      this.form.fields.push({
        id: newId,
        templateId: this.form.id || null,
        cnName: '',
        enName: '',
        type: '',
        length: null,
        decimalPlaces: null,
        required: false,
        sortOrder: this.form.fields.length + 1,
        description: ''
      })
    },

    // 移除字段
    removeField(index) {
      this.form.fields.splice(index, 1)
      // 重新排序
      this.form.fields.forEach((field, i) => {
        field.sortOrder = i + 1
      })
    },

    // 处理字段类型变化
    handleTypeChange(index) {
      const field = this.form.fields[index]
      if (!['int', 'varchar', 'decimal'].includes(field.type)) {
        field.length = null
      }
      if (field.type !== 'decimal') {
        field.decimalPlaces = null
      }
    },

    // 切换模板状态
    toggleStatus(id) {
      const template = this.templates.find((t) => t.id === id)
      if (template) {
        template.status = template.status === '启用' ? '禁用' : '启用'
        alert(`模板"${template.name}"已${template.status}`)
      }
    },

    // 保存模板
    saveTemplate() {
      // 简单验证
      if (!this.form.name.trim()) {
        alert('请输入模板名称')
        return
      }

      if (this.form.fields.length > 0) {
        // 验证字段
        for (const field of this.form.fields) {
          if (!field.cnName.trim() || !field.enName.trim() || !field.type) {
            alert('请完善所有字段的中文名称、英文名称和数据类型')
            return
          }

          if (
            ['int', 'varchar', 'decimal'].includes(field.type) &&
            (field.length === null || field.length < 1)
          ) {
            alert(`字段"${field.cnName}"的长度必须大于0`)
            return
          }

          if (
            field.type === 'decimal' &&
            (field.decimalPlaces === null || field.decimalPlaces < 0)
          ) {
            alert(`字段"${field.cnName}"的小数位数不能为负数`)
            return
          }
        }
      }

      if (this.isEditing) {
        // 更新现有模板
        const index = this.templates.findIndex((t) => t.id === this.form.id)
        if (index !== -1) {
          this.templates[index] = {
            ...this.templates[index],
            name: this.form.name,
            description: this.form.description,
            status: this.form.status,
            fieldCount: this.form.fields.length
          }
        }

        // 更新字段
        const templateId = this.form.id
        // 先移除旧字段
        this.allFields = this.allFields.filter((f) => f.templateId !== templateId)
        // 添加新字段
        this.form.fields.forEach((field) => {
          this.allFields.push({ ...field, templateId })
        })

        alert('模板更新成功')
      } else {
        // 创建新模板
        const newId = Math.max(0, ...this.templates.map((t) => t.id)) + 1
        const newTemplate = {
          id: newId,
          name: this.form.name,
          description: this.form.description,
          status: this.form.status,
          creator: 'admin',
          createTime: new Date().toLocaleString(),
          fieldCount: this.form.fields.length
        }

        this.templates.push(newTemplate)

        // 添加新字段
        this.form.fields.forEach((field) => {
          this.allFields.push({ ...field, templateId: newId })
        })

        alert('模板创建成功')
      }

      this.closeAddModal()
    },

    // 获取字段类型样式
    getTypeClass(type) {
      switch (type) {
        case 'int':
          return 'bg-blue-100 text-blue-800'
        case 'varchar':
          return 'bg-purple-100 text-purple-800'
        case 'decimal':
          return 'bg-green-100 text-green-800'
        case 'datetime':
          return 'bg-yellow-100 text-yellow-800'
        default:
          return 'bg-gray-100 text-gray-800'
      }
    },

    // 获取字段类型显示文本
    getTypeLabel(field) {
      if (field.type === 'decimal') {
        return `decimal(${field.length},${field.decimalPlaces})`
      }
      if (['int', 'varchar'].includes(field.type) && field.length) {
        return `${field.type}(${field.length})`
      }
      return field.type
    }
  }
}
</script>
