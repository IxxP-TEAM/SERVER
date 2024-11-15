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
    USER_VERIFICATION_FAIL(HttpStatus.NOT_FOUND, "USER-006", "회원 등록시 입력했던 이메일을 입력해주세요."),
    CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-007", "인증 코드가 존재하지 않습니다."),
    CODE_EXPIRATION_TIME(HttpStatus.ACCEPTED, "USER-008", "인증 코드 유효 시간이 만료 되었습니다."),

    // leave
    LEAVE_NOT_FOUND(HttpStatus.NOT_FOUND, "LEAVE-001", "존재하지 않는 휴가입니다."),
    
    //attendance
    CHECK_IN_ALREADY(HttpStatus.BAD_REQUEST, "ATT-001", "이미 출근처리 되었습니다."),

    //product
    PRODUCT_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PRODUCT-001", "이미 존재하는 제품입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT-002", "해당 제품을 찾을 수 없습니다."),
    PRODUCTUSER_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT-003", "해당 사용자를 찾을 수 없습니다."),
    PRODUCT_EXISTS_IN_INVENTORY(HttpStatus.NOT_FOUND, "PRODUCT-004", "재고에 이미 존재하는 제품입니다."),
    
    //inventory
    INVENTORY_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "INVENTORY-001", "수량은 양수이어야 합니다."),
    OUTBOUND_QUANTITY_EXCEEDS_STOCK(HttpStatus.BAD_REQUEST,"INVENTORY-002","출고 수량이 재고 수량보다 많습니다."),
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND,"INVENTORY-003", "해당 유통기한을 찾을 수 없습니다."),
    INVENTORY_ADJUSTMENT_REASON_REQUIRED(HttpStatus.BAD_REQUEST,"INVENTORY", "조정 사유를 입력하세요")
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
