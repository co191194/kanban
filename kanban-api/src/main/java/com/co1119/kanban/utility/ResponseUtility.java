package com.co1119.kanban.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.co1119.kanban.constant.ProcessResult;
import com.co1119.kanban.dto.response.AbstractKanbanResponse;

public class ResponseUtility {
    /**
     * 正常終了時のレスポンスを作成します。
     * 
     * @param <T>
     * @param response
     * @return
     */
    public static <T extends AbstractKanbanResponse> ResponseEntity<T> createSuccessResponse(T response) {
        response.setProcessResult(ProcessResult.SUCCESS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <T extends AbstractKanbanResponse> ResponseEntity<T> createBadRequestResponse(T response) {
        response.setProcessResult(ProcessResult.ERROR_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
