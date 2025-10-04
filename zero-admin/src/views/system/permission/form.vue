<script setup lang="ts">
import { ref } from "vue";
import { formRules } from "./utils/rule";
import { FormProps } from "./utils/types";

const props = withDefaults(defineProps<FormProps>(), {
  formInline: () => ({
    id: null,
    name: "",
    code: "",
    valid: null
  })
});

const ruleFormRef = ref();
const newFormInline = ref(props.formInline);

function getRef() {
  return ruleFormRef.value;
}

defineExpose({ getRef });
</script>

<template>
  <el-form
    ref="ruleFormRef"
    :model="newFormInline"
    :rules="formRules"
    label-width="82px"
  >
    <el-form-item label="权限名称" prop="name">
      <el-input
        v-model="newFormInline.name"
        clearable
        placeholder="请输入权限名称"
      />
    </el-form-item>

    <el-form-item label="权限编码" prop="code">
      <el-input
        v-model="newFormInline.code"
        clearable
        placeholder="请输入权限编码"
      />
    </el-form-item>
  </el-form>
</template>
