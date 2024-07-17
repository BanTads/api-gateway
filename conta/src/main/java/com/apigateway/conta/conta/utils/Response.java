package com.apigateway.conta.conta.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private Boolean success;
    private Integer code; // Changed to Integer to allow null values
    private String message;
    private Object data;

    // Constructor with all parameters
    public Response(Boolean success, String message, Object data, Integer code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    // Overloaded constructor without code parameter
    public Response(Boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = null; // Default value for code
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
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
