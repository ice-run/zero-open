package run.ice.zero.common.helper;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ValidatorHelper {

    @Resource
    private Validator validator;

    public <T> List<String> validate(T t) {
        Set<ConstraintViolation<T>> validate = validator.validate(t);
        return validate.stream().map((c) -> c.getPropertyPath().toString() + " : " + c.getInvalidValue() + " : " + c.getMessage()).toList();
    }

}
