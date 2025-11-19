package com.co1119.kanban.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.co1119.kanban.constant.ProcessResult;
import com.co1119.kanban.dto.response.AbstractResponse;
import com.co1119.kanban.dto.response.ErrorResponse;

public class ResponseUtility {

    public static <T extends AbstractResponse> ResponseEntity<T> createOkResponse(T response) {
        return createSuccessResponse(response, HttpStatus.OK);
    }

    public static <T extends AbstractResponse> ResponseEntity<T> createCreatedReponse(T response) {
        return createSuccessResponse(response, HttpStatus.CREATED);
    }

    public static ResponseEntity<ErrorResponse> createBadRequestResponse(Exception e) {
        return createErrorResponse(e, HttpStatus.BAD_REQUEST, ProcessResult.ERROR_BAD_REQUEST);
    }

    public static ResponseEntity<ErrorResponse> createForbbidenResponse(Exception e) {
        return createErrorResponse(e, HttpStatus.FORBIDDEN, ProcessResult.ERROR_ACCESS_DNEY);
    }

    public static ResponseEntity<ErrorResponse> createNotFoundResponse(Exception e) {
        return createErrorResponse(e, HttpStatus.NOT_FOUND, ProcessResult.ERROR_NOT_FOUND);
    }

    /**
     * 正常終了時のレスポンスを作成します。
     * 
     * @param <T>
     * @param response
     * @return
     */
    private static <T extends AbstractResponse> ResponseEntity<T> createSuccessResponse(T response, HttpStatus status) {
        response.setProcessResult(ProcessResult.SUCCESS);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * エラー終了時のレスポンスを作成します。
     * 
     * @param e
     * @param status
     * @param processResult
     * @return
     */
    private static ResponseEntity<ErrorResponse> createErrorResponse(Exception e, HttpStatus status,
            String processResult) {
        return ResponseEntity.status(status).body(new ErrorResponse(processResult, e.getMessage()));
    }
}
