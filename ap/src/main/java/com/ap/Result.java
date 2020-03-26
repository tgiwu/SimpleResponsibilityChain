package com.ap;

public class Result {
    private boolean isSuccess;
    private String from, to, message;

    private Result(boolean isSuccess, String from, String to, String message) {
        this.isSuccess = isSuccess;
        this.from = from;
        this.to = to;
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public static class Builder {
        boolean isSuccess;
        String from, to, message;

        public Builder() { }

        public Builder isSuccess(boolean isSuccess) {
            this.isSuccess = isSuccess;
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Result build() {
            return new Result(isSuccess, from, to, message);
        }
    }
}
