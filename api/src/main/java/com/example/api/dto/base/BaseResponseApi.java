package com.example.api.dto.base;

/** This class represent the body that will be return to client. */
public record BaseResponseApi<T>(BaseResponseStatus status, T data, BaseResponseMeta metaData) {

  private static final String SUCCESS_CODE = "ES-0000";
  private static final String SUCCESS_MSG = "Success";

  private BaseResponseApi(BaseResponseStatus status, BaseResponseMeta metaData) {
    this(status, null, metaData);
  }

  public static <T> BaseResponseApi<T> success(T data) {
    return success(new BaseResponseStatus(SUCCESS_CODE, SUCCESS_MSG), data);
  }

  public static <T> BaseResponseApi<T> success(BaseResponseStatus status, T data) {
    BaseResponseMeta metaData = BaseResponseMeta.generate();

    return new BaseResponseApi<>(status, data, metaData);
  }

  public static <T> BaseResponseApi<T> error(
      String errorCode, String errorMessage, T data) {
    BaseResponseStatus status = new BaseResponseStatus(errorCode, errorMessage);
    BaseResponseMeta metaData = BaseResponseMeta.generate();

    return new BaseResponseApi<>(status, data, metaData);
  }
    public static <T> BaseResponseApi<T> error(
            String errorCode, String errorMessage) {
        BaseResponseStatus status = new BaseResponseStatus(errorCode, errorMessage);
        BaseResponseMeta metaData = BaseResponseMeta.generate();

        return new BaseResponseApi<>(status,null, metaData);
    }
}
