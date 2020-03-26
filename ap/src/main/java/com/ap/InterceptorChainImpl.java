package com.ap;

import java.util.List;

public class InterceptorChainImpl implements Interceptor.Chain {

    private final List<Interceptor> interceptors;
    private final int index;
    private final Request request;

    InterceptorChainImpl(List<Interceptor> interceptors, int index, Request request) {
        this.interceptors = interceptors;
        this.index = index;
        this.request = request;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean hasNext() {
        return index + 1 < interceptors.size();
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Result proceed(Request request) {

        if (index >= interceptors.size()) throw new AssertionError();

        InterceptorChainImpl next = new InterceptorChainImpl(interceptors, index + 1, request);
        Interceptor interceptor = interceptors.get(index);

        return interceptor.intercept(next);
    }
}

