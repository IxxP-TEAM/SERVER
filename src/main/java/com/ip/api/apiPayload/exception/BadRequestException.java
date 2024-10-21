package com.ip.api.apiPayload.exception;

import com.ip.api.apiPayload.code.ErrorCode;

public class BadRequestException extends BusinessBaseException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}

