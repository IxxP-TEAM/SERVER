package com.ip.api.apiPayload.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "VALUE-001", "올바르지 않은 입력값입니다."),   // Value
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD-001", "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER-001", "서버 에러가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "ENTITY-001", "존재하지 않는 엔티티입니다."),

    //user
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER-001", "이미 존재하는 이메일입니다."),
    USER_EMAIL_SEND_FAIL(HttpStatus.BAD_REQUEST, "USER-002", "이메일 발송에 실패하였습니다."),
    USER_PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "USER-003", "비밀번호 형식이 맞지 않습니다."),
    USER_BAD_CREDENTIAL(HttpStatus.BAD_REQUEST, "USER-004", "잘못된 이메일 혹은 비밀번호입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-005", "수정할 사용자를 찾을 수 없습니다."),
    USER_AUTHENTICATION_FAIL(HttpStatus.BAD_REQUEST, "USER-005", "인증 실패"),
    
    //product
    PRODUCT_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PRODUCT-001", "이미 존재하는 제품입니다."),
    
    ;
	
	

    private final String message;

    private final String code;
    private final HttpStatus status;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}