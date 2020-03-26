package com.ap;

import java.util.HashMap;
import java.util.Map;

public class RequestImpl extends Request {

    public RequestImpl(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.param = builder.param;
    }

    public static class Builder {
        private String from, to;
        private Map<String, String> param = new HashMap<>();

        public Builder() { }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder param(Map<String, String> param) {
            if (null != param)
                this.param = param;
            return this;
        }

        public Request build() {
            return new RequestImpl(this);
        }
    }

}
