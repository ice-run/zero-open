import dayjs from "dayjs";
import editForm from "../form.vue";
import { handleTree } from "@/utils/tree";
import { message } from "@/utils/message";
import { ElMessageBox } from "element-plus";
import { usePublicHooks } from "../../hooks";
import { transformI18n } from "@/plugins/i18n";
import { addDialog } from "@/components/ReDialog";
import type { FormItemProps } from "../utils/types";
import type { PaginationProps } from "@pureadmin/table";
import { getKeyList, deviceDetection } from "@pureadmin/utils";
import { roleSearch, type RoleUpsert, roleUpsert } from "@/api/auth/role";
import { type Ref, reactive, ref, onMounted, h, toRaw, watch } from "vue";
import { rolePermissionList, rolePermissionUpsert } from "@/api/auth/rbac";
import { permissionSearch } from "@/api/auth/permission";
import { emptyToNull, listToTreeByCode } from "@/utils/zero/common";

export function useRole(treeRef: Ref) {
  const form = reactive({
    id: null,
    name: "",
    code: "",
    valid: null
  });
  const curRow = ref();
  const formRef = ref();
  const dataList = ref([]);
  const treeIds = ref([]);
  const treeData = ref([]);
  const beforePermissionIds = ref([]);
  const afterPermissionIds = ref([]);
  const isShow = ref(false);
  const loading = ref(true);
  const isLinkage = ref(false);
  const treeSearchValue = ref();
  const switchLoadMap = ref({});
  const isExpandAll = ref(false);
  const isSelectAll = ref(false);
  const { switchStyle } = usePublicHooks();
  const treeProps = {
    value: "id",
    label: "name",
    children: "children"
  };
  const pagination = reactive<PaginationProps>({
    total: 0,
    pageSize: 10,
    currentPage: 1,
    background: true
  });
  const columns: TableColumnList = [
    {
      label: "ID",
      prop: "id"
    },
    {
      label: "角色名称",
      prop: "name"
    },
    {
      label: "角色编码",
      prop: "code"
    },
    {
      label: "状态",
      cellRenderer: scope => (
        <el-switch
          size={scope.props.size === "small" ? "small" : "default"}
          loading={switchLoadMap.value[scope.index]?.loading}
          v-model={scope.row.valid}
          active-value={true}
          inactive-value={false}
          active-text="已启用"
          inactive-text="已停用"
          inline-prompt
          style={switchStyle.value}
          onChange={() => onChange(scope as any)}
        />
      ),
      minWidth: 90
    },
    {
      label: "创建时间",
      prop: "createTime",
      minWidth: 160,
      formatter: ({ createTime }) =>
        dayjs(createTime).format("YYYY-MM-DD HH:mm:ss")
    },
    {
      label: "操作",
      fixed: "right",
      width: 210,
      slot: "operation"
    }
  ];
  // const buttonClass = computed(() => {
  //   return [
  //     "h-[20px]!",
  //     "reset-margin",
  //     "text-gray-500!",
  //     "dark:text-white!",
  //     "dark:hover:text-primary!"
  //   ];
  // });

  function onChange({ row, index }) {
    ElMessageBox.confirm(
      `确认要<strong>${
        row.valid === true ? "启用" : "停用"
      }</strong><strong style='color:var(--el-color-primary)'>${
        row.name
      }</strong>吗?`,
      "系统提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        dangerouslyUseHTMLString: true,
        draggable: true
      }
    )
      .then(() => {
        switchLoadMap.value[index] = Object.assign(
          {},
          switchLoadMap.value[index],
          {
            loading: true
          }
        );
        setTimeout(() => {
          const param: RoleUpsert = emptyToNull({
            id: row.id,
            ...row
          }) as RoleUpsert;
          roleUpsert({ param })
            .then(({ data }) => {
              console.debug("data", data);
              switchLoadMap.value[index] = Object.assign(
                {},
                switchLoadMap.value[index],
                {
                  loading: false
                }
              );
              message("已成功修改数据状态", {
                type: "success"
              });
            })
            .catch(() => {
              switchLoadMap.value[index] = Object.assign(
                {},
                switchLoadMap.value[index],
                {
                  loading: false
                }
              );
              row.valid === true ? (row.valid = false) : (row.valid = true);
            });
        }, 300);
      })
      .catch(() => {
        row.valid === true ? (row.valid = false) : (row.valid = true);
      });
  }

  function handleDelete(row) {
    roleUpsert({ param: { id: row.id, ...row, valid: false } }).then(
      ({ data }) => {
        console.debug("data", data);
        message(`您删除了角色名称为${row.name}的这条数据`, { type: "success" });
        onSearch().then(_ => {});
      }
    );
  }

  function handleSizeChange(val: number) {
    console.log(`${val} items per page`);
  }

  function handleCurrentChange(val: number) {
    console.log(`current page: ${val}`);
  }

  function handleSelectionChange(val) {
    console.log("handleSelectionChange", val);
  }

  async function onSearch() {
    loading.value = true;
    const { data } = await roleSearch({
      param: { param: emptyToNull(toRaw(form)), size: 1000 }
    });
    dataList.value = data.list;
    pagination.total = data.total;
    pagination.pageSize = data.size;
    pagination.currentPage = data.page;

    setTimeout(() => {
      loading.value = false;
    }, 500);
  }

  const resetForm = formEl => {
    if (!formEl) return;
    formEl.resetFields();
    onSearch().then(_ => {});
  };

  function openDialog(title = "新增", row?: FormItemProps) {
    addDialog({
      title: `${title}角色`,
      props: {
        formInline: {
          id: row?.id ?? null,
          name: row?.name ?? "",
          code: row?.code ?? ""
        }
      },
      width: "40%",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(editForm, { ref: formRef, formInline: null }),
      beforeSure: (done, { options }) => {
        const FormRef = formRef.value.getRef();
        const curData = options.props.formInline as FormItemProps;
        function chores() {
          message(`您${title}了角色名称为${curData.name}的这条数据`, {
            type: "success"
          });
          done(); // 关闭弹框
          onSearch().then(_ => {}); // 刷新表格数据
        }
        FormRef.validate(valid => {
          if (valid) {
            // 表单规则校验通过
            if (title === "新增") {
              // 实际开发先调用新增接口，再进行下面操作
              roleUpsert({ param: { ...curData, id: null } }).then(_ => {
                chores();
              });
            } else {
              // 实际开发先调用修改接口，再进行下面操作
              roleUpsert({ param: { ...curData, id: curData.id } }).then(_ => {
                chores();
              });
            }
          }
        });
      }
    });
  }

  /** 角色权限 */
  async function handlePermission(row?: any) {
    const { id } = row;
    if (id) {
      curRow.value = row;
      isShow.value = true;
      const { data } = await rolePermissionList({ param: { id } });
      beforePermissionIds.value = data.map(item => item.id);
      afterPermissionIds.value = [];
      treeRef.value.setCheckedKeys(data.map(item => item.id));
    } else {
      curRow.value = null;
      isShow.value = false;
    }
  }

  /** 高亮当前权限选中行 */
  function rowStyle({ row: { id } }) {
    return {
      cursor: "pointer",
      background: id === curRow.value?.id ? "var(--el-fill-color-light)" : ""
    };
  }

  /** 角色权限-保存 */
  async function handleSave() {
    const { id, name } = curRow.value;
    afterPermissionIds.value = treeRef.value.getCheckedKeys();
    // 根据用户 id 调用实际项目中角色权限修改接口
    const insertIds = afterPermissionIds.value.filter(
      item => !beforePermissionIds.value.includes(item)
    );
    const deleteIds = beforePermissionIds.value.filter(
      item => !afterPermissionIds.value.includes(item)
    );
    if (insertIds.length) {
      for (const permissionId of insertIds) {
        await rolePermissionUpsert({
          param: {
            roleId: id,
            permissionId: permissionId,
            valid: true
          }
        }).then(_ => {});
      }
    }
    if (deleteIds.length) {
      for (const permissionId of deleteIds) {
        await rolePermissionUpsert({
          param: {
            roleId: id,
            permissionId: permissionId,
            valid: false
          }
        }).then(_ => {});
      }
    }
    message(`角色名称为${name}的角色权限修改成功`, {
      type: "success"
    });
    const { data } = await rolePermissionList({ param: { id } });
    beforePermissionIds.value = data.map(item => item.id);
  }

  /** 数据权限 可自行开发 */
  // function handleDatabase() {}

  const onQueryChanged = (query: string) => {
    treeRef.value!.filter(query);
  };

  const filterMethod = (query: string, node) => {
    return transformI18n(node.title)!.includes(query);
  };

  onMounted(async () => {
    onSearch().then(_ => {});
    const { data } = await permissionSearch({
      param: { size: 1000, param: {} }
    });
    treeIds.value = getKeyList(data.list, "id");
    treeData.value = handleTree(listToTreeByCode(data.list));
  });

  watch(isExpandAll, val => {
    val
      ? treeRef.value.setExpandedKeys(treeIds.value)
      : treeRef.value.setExpandedKeys([]);
  });

  watch(isSelectAll, val => {
    val
      ? treeRef.value.setCheckedKeys(treeIds.value)
      : treeRef.value.setCheckedKeys([]);
  });

  return {
    form,
    isShow,
    curRow,
    loading,
    columns,
    rowStyle,
    dataList,
    treeData,
    treeProps,
    isLinkage,
    pagination,
    isExpandAll,
    isSelectAll,
    treeSearchValue,
    // buttonClass,
    onSearch,
    resetForm,
    openDialog,
    handlePermission,
    handleSave,
    handleDelete,
    filterMethod,
    transformI18n,
    onQueryChanged,
    // handleDatabase,
    handleSizeChange,
    handleCurrentChange,
    handleSelectionChange
  };
}
