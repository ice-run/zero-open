import editForm from "../form.vue";
import { handleTree } from "@/utils/tree";
import { message } from "@/utils/message";
import {
  permSearch,
  type PermUpsert,
  permUpsert
} from "@/api/auth/perm";
import { transformI18n } from "@/plugins/i18n";
import { addDialog } from "@/components/ReDialog";
import { reactive, ref, onMounted, h } from "vue";
import type { FormItemProps } from "../utils/types";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";
import { isAllEmpty, deviceDetection } from "@pureadmin/utils";
import dayjs from "dayjs";
import { usePublicHooks } from "@/views/system/hooks";
import { ElMessageBox } from "element-plus";
import { emptyToNull, listToTreeByCode } from "@/utils/zero/common";

export function usePerm() {
  const form = reactive({
    id: null,
    name: "",
    code: "",
    valid: null
  });

  const formRef = ref();
  const dataList = ref([]);
  const loading = ref(true);
  const switchLoadMap = ref({});
  const { switchStyle } = usePublicHooks();

  const columns: TableColumnList = [
    {
      label: "ID",
      prop: "id"
    },
    {
      label: "权限名称",
      prop: "name",
      align: "left",
      cellRenderer: ({ row }) => (
        <>
          <span class="inline-block mr-1">
            {h(useRenderIcon(row.icon), {
              style: { paddingTop: "1px" }
            })}
          </span>
          <span>{transformI18n(row.name)}</span>
        </>
      )
    },
    {
      label: "权限编码",
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

  function handleSelectionChange(val) {
    console.log("handleSelectionChange", val);
  }

  function resetForm(formEl) {
    if (!formEl) return;
    formEl.resetFields();
    onSearch().then(_ => {});
  }
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
          const param: PermUpsert = emptyToNull({
            id: row.id,
            ...row
          }) as PermUpsert;
          permUpsert({ param })
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

  async function onSearch() {
    loading.value = true;
    const { data } = await permSearch({
      param: { param: {}, size: 1000 }
    }); // 这里是返回一维数组结构，前端自行处理成树结构，返回格式要求：唯一id加父节点parentId，parentId取父节点id
    let newData = data.list;
    if (!isAllEmpty(form.name)) {
      // 前端搜索权限名称
      newData = newData.filter(item =>
        transformI18n(item.name).includes(form.name)
      );
    }
    dataList.value = handleTree(listToTreeByCode(newData)); // 处理成树结构
    setTimeout(() => {
      loading.value = false;
    }, 500);
  }

  function openDialog(title = "新增", row?: FormItemProps) {
    addDialog({
      title: `${title}权限`,
      props: {
        formInline: {
          id: row?.id ?? null,
          name:
            title === "新增" ? (row?.name ? row?.name + "--" : "") : row?.name,
          code:
            title === "新增" ? (row?.code ? row?.code + ":" : "") : row?.code
        }
      },
      width: "45%",
      draggable: true,
      fullscreen: deviceDetection(),
      fullscreenIcon: true,
      closeOnClickModal: false,
      contentRenderer: () => h(editForm, { ref: formRef, formInline: null }),
      beforeSure: (done, { options }) => {
        const FormRef = formRef.value.getRef();
        const curData = options.props.formInline as FormItemProps;
        function chores() {
          message(
            `您${title}了权限名称为${transformI18n(curData.name)}的这条数据`,
            {
              type: "success"
            }
          );
          done(); // 关闭弹框
          onSearch().then(_ => {}); // 刷新表格数据
        }
        FormRef.validate(valid => {
          if (valid) {
            console.log("curData", curData);
            // 表单规则校验通过
            if (title === "新增") {
              // 实际开发先调用新增接口，再进行下面操作
              permUpsert({ param: { ...curData, id: null } }).then(_ => {
                chores();
              });
            } else {
              // 实际开发先调用修改接口，再进行下面操作
              permUpsert({ param: { ...curData, id: curData.id } }).then(
                _ => {
                  chores();
                }
              );
            }
          }
        });
      }
    });
  }

  function handleDelete(row) {
    message(`您删除了权限名称为${transformI18n(row.title)}的这条数据`, {
      type: "success"
    });
    onSearch().then(_ => {});
  }

  onMounted(() => {
    onSearch().then(_ => {});
  });

  return {
    form,
    loading,
    columns,
    dataList,
    /** 搜索 */
    onSearch,
    /** 重置 */
    resetForm,
    /** 新增、修改权限 */
    openDialog,
    /** 删除权限 */
    handleDelete,
    handleSelectionChange
  };
}
