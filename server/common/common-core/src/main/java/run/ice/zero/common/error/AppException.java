package run.ice.zero.common.error;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import run.ice.zero.common.model.Response;

/**
 * @author DaoDao
 */
@Slf4j
@Setter
@Getter
public class AppException extends RuntimeException {

    private String code;

    public AppException() {
        super();
        this.code = AppError.ERROR.code;
    }

    public AppException(String message) {
        super(message);
        this.code = AppError.ERROR.code;
    }

    public AppException(String code, String message) {
        super(message);
        this.code = code;
    }

    public AppException(Response<?> response) {
        super(response.getMessage());
        this.code = response.getCode();
    }

    public AppException(Exception exception) {
        super(exception);
        this.code = AppError.ERROR.code;
    }

    public AppException(String message, Exception exception) {
        super(message, exception);
        this.code = AppError.ERROR.code;
    }

    public AppException(String code, String message, Exception exception) {
        super(message, exception);
        this.code = code;
    }

    public <E extends Enum<E> & ErrorEnum> AppException(E e) {
        super(e.getMessage());
        this.code = e.getCode();
    }

    public <E extends Enum<E> & ErrorEnum> AppException(E e, String message) {
        super(e.getMessage() + " : " + message);
        this.code = e.getCode();
    }

    public <E extends Enum<E> & ErrorEnum> AppException(E e, Exception exception) {
        super(e.getMessage(), exception);
        this.code = e.getCode();
    }

    public <E extends Enum<E> & ErrorEnum> AppException(E e, String message, Exception exception) {
        super(e.getMessage() + " : " + message, exception);
        this.code = e.getCode();
    }

}
