package com.ap;

import java.util.List;

public class TaskImpl implements ITask {
    private BackupHelper mHelper;

    public TaskImpl(BackupHelper helper) {
        this.mHelper = helper;
    }

    @Override
    public Result execute() {
        Result result = getResultWithInterceptorChain();
        return result;
    }

    private Result getResultWithInterceptorChain() {
        List<Interceptor> interceptors = mHelper.getAllInterceptors();
        RequestImpl.Builder builder = new RequestImpl.Builder();
        Interceptor.Chain chain = new InterceptorChainImpl(interceptors, 0, builder.build());

        return chain.proceed(builder.build());
    }
}
