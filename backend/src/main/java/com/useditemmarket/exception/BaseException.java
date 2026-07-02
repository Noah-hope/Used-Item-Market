package com.useditemmarket.exception;

public class BaseException extends RuntimeException
{
    private final int code;

    public BaseException(int code, String msg)
    {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
