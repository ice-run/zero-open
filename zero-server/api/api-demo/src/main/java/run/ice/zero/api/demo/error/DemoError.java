package run.ice.zero.api.demo.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.ice.zero.common.constant.ServiceCode;
import run.ice.zero.common.error.ErrorEnum;

/**
 * @author DaoDao
 */
@Getter
@AllArgsConstructor
public enum DemoError implements ErrorEnum {

    ERROR(code("000"), "ERROR : demo 服务异常"),

    ;

    /**
     * 响应编码
     */
    public final String code;

    /**
     * 响应说明
     */
    public final String message;

    private static String code(String code) {
        return ServiceCode.DEMO + code;
    }

}
