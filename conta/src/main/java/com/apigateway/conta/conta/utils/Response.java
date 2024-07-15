package com.apigateway.conta.conta.utils;

import io.swagger.v3.oas.annotations.media.Schema;

public class Response {
    @Schema(example = "false")
    private Boolean success;
    private String message;

    private Object data;

    public Response(Boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}