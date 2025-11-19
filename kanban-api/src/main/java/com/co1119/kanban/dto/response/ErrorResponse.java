package com.co1119.kanban.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ErrorResponse extends AbstractResponse {

    public ErrorResponse(String processResult, String message) {
        super();
        super.setProcessResult(processResult);
        super.setMessage(message);
    }
}
