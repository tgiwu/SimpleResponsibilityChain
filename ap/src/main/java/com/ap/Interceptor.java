package com.ap;

public interface Interceptor {

    Result intercept(Chain chain);

    interface Chain {

        Request request();

        Result proceed(Request request);

        boolean hasNext();
    }
}
