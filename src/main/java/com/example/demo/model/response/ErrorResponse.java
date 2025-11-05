package com.example.demo.model.response;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    private HttpStatus status;
    private Object message;

    public ErrorResponse() {}

    public ErrorResponse(HttpStatus status, Object message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HttpStatus status;
        private Object message;

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder message(Object message) {
            this.message = message;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(status, message);
        }
    }
}
