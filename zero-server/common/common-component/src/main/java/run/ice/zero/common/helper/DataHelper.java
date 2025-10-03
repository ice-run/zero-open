package run.ice.zero.common.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import run.ice.zero.common.model.PageParam;

import java.util.List;

@Slf4j
@Component
public class DataHelper {

    public Sort sort(List<PageParam.Order> orders) {
        String defaultProperty = "id";
        return sort(orders, defaultProperty);
    }

    public Sort sort(List<PageParam.Order> orders, String defaultProperty) {
        Sort.Order defaultOrder = Sort.Order.asc(defaultProperty);
        return sort(orders, defaultOrder);
    }

    public Sort sort(List<PageParam.Order> orders, Sort.Order defaultOrder) {
        if (orders == null || orders.isEmpty()) {
            return Sort.by(defaultOrder);
        }
        List<Sort.Order> list = orders
                .stream()
                .map(item -> new Sort.Order(Sort.Direction.fromOptionalString(item.getDirection().name()).orElse(Sort.Direction.ASC), item.getProperty()))
                .toList();
        return Sort.by(list);
    }

    public ExampleMatcher matcher(List<PageParam.Match> matches) {
        ExampleMatcher matcher = ExampleMatcher.matching();
        if (matches == null || matches.isEmpty()) {
            return matcher;
        }
        for (PageParam.Match match : matches) {
            String property = match.getProperty();
            PageParam.Match.Mode mode = match.getMode();
            matcher = switch (mode) {
                case exact -> matcher.withMatcher(property, ExampleMatcher.GenericPropertyMatchers.exact());
                case contain -> matcher.withMatcher(property, ExampleMatcher.GenericPropertyMatchers.contains());
                case regex -> matcher.withMatcher(property, ExampleMatcher.GenericPropertyMatchers.regex());
            };
        }
        return matcher;
    }

}
