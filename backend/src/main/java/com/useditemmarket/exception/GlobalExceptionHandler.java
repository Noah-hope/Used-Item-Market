package com.useditemmarket.exception;

import com.useditemmarket.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    public static final String DEFAULT_ERROR_MESSAGE = "系统维护中，请稍后再试";

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse<Void> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("图片上传超过大小限制", e);
        return ApiResponse.fail(400, "图片大小不能超过5MB");
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse<Void> handleMultipartError(MultipartException e) {
        log.warn("图片上传请求异常", e);
        return ApiResponse.fail(400, "图片上传请求格式不正确");
    }

    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public ApiResponse<Void> handleBizError(HttpServletRequest req, BaseException e, HttpServletResponse response) {
        log.error("业务异常", e);
        response.setStatus(e.getCode());
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse<Void> handleAllError(HttpServletRequest req, Exception e) {
        log.error("系统内部异常", e);
        return ApiResponse.fail(500, DEFAULT_ERROR_MESSAGE);
    }
}
