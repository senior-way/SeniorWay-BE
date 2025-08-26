package com.seniorway.seniorway.enums.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 BadRequest
    AUTH_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "사용자 입력값이 올바르지 않습니다."),
    AUTH_EMAIL_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    BIND_ERROR(HttpStatus.BAD_REQUEST, "입력 값 바인딩 오류가 발생했습니다."),
    EMAIL_SEND_FAILURE(HttpStatus.BAD_REQUEST, "이메일 전송 중 오류가 발생했습니다."),
    EMAIL_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증 코드가 만료되었습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다"),
    INVALID_CONNECTED_ID(HttpStatus.BAD_REQUEST, "응답에 connectedId가 없습니다"),
    MATCHING_USER_EMPTY(HttpStatus.BAD_REQUEST, "매칭할 유저를 조회하지 못하였습니다."),
    SCORE_EMPTY(HttpStatus.BAD_REQUEST, "점수를 조회하지 못하였습니다."),
    HTTP_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 요청 실패"),

    // 401 Unauthorized
    AUTH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 토큰입니다."),
    OAUTH_FAILURE(HttpStatus.UNAUTHORIZED, "OAuth 인증에 실패했습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없는 요청입니다."),

    // 404 Not Found
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

    // 500 Internal Server Error
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB 에러가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // 502 Bad Gateway
    KAKAO_API_ERROR(HttpStatus.BAD_GATEWAY, "카카오 API 호출 실패"),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "외부 API 호출 실패");

    private final HttpStatus httpStatus;
    private final String message;
}
