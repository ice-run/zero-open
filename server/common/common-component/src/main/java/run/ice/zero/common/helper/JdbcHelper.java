package run.ice.zero.common.helper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import run.ice.zero.common.model.PageData;
import run.ice.zero.common.model.PageParam;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnBean(JdbcClient.class)
public class JdbcHelper {

    @Resource
    private JdbcClient jdbcClient;

    public <P, D> PageData<D> page(PageParam<P> pageParam, String fromSql, Class<D> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<String> select = Arrays.stream(declaredFields).map(field -> {
            String fieldName = field.getName();
            String columnName = toSnakeCase(fieldName);
            return columnName + " AS " + fieldName;
        }).toList();
        return page(pageParam, select, fromSql, clazz);
    }

    public PageData<Map<String, Object>> page(PageParam<Map<String, Object>> pageParam, List<String> select, String fromSql) {
        Integer page = pageParam.getPage();
        Integer size = pageParam.getSize();
        List<PageParam.Order> orders = pageParam.getOrders();
        int offset = (page - 1) * size;
        Map<String, Object> param = pageParam.getParam();

        String selectSql = String.join(", ", select);
        String orderBySql = orderSql(orders);
        String limitSql = "LIMIT " + offset + ", " + size;
        String querySql = "SELECT " + selectSql + " " + fromSql + " " + orderBySql + " " + limitSql;
        String countSql = "SELECT COUNT(*)" + " " + fromSql;
        List<Map<String, Object>> list = jdbcClient.sql(querySql)
                .params(param)
                .query()
                .listOfRows();
        log.debug("list : {}", list);

        Long total = (Long) jdbcClient.sql(countSql)
                .params(param)
                .query()
                .singleValue();
        log.debug("total : {}", total);
        return new PageData<>(page, size, total, list);
    }

    public <P, D> PageData<D> page(PageParam<P> pageParam, List<String> select, String fromSql, Class<D> clazz) {
        Integer page = pageParam.getPage();
        Integer size = pageParam.getSize();
        List<PageParam.Order> orders = pageParam.getOrders();
        int offset = (page - 1) * size;
        P param = pageParam.getParam();

        String selectSql = String.join(", ", select);
        String orderBySql = orderSql(orders);
        String limitSql = "LIMIT " + offset + ", " + size;
        String querySql = "SELECT " + selectSql + " " + fromSql + " " + orderBySql + " " + limitSql;
        String countSql = "SELECT COUNT(*)" + " " + fromSql;

        List<D> list = jdbcClient.sql(querySql)
                .paramSource(param)
                .query(clazz)
                .list();
        log.debug("list : {}", list);

        Long total = (Long) jdbcClient.sql(countSql)
                .paramSource(param)
                .query()
                .singleValue();
        log.debug("total : {}", total);
        return new PageData<>(page, size, total, list);
    }

    public String orderSql(List<PageParam.Order> orders) {
        List<String> orderBy = orders.stream().map(order -> {
            String field = order.getProperty();
            String direction = order.getDirection().name();
            return toSnakeCase(field) + " " + direction;
        }).toList();
        return orderBy.isEmpty() ? "" : "ORDER BY " + String.join(", ", orderBy);
    }

    private static String toSnakeCase(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
