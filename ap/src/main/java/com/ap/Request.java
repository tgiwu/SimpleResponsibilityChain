package com.ap;

import java.util.HashMap;
import java.util.Map;

public class Request {
    protected String from, to;
    protected Map<String, String> param = new HashMap<>();

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Map<String, String> getParam() {
        return param;
    }
}
