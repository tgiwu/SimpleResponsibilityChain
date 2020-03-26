package com.ap;

import java.util.ArrayList;
import java.util.List;

public class BackupHelper {

    private List<Interceptor> mInterceptors;
    private static BackupHelper instance;
    private IInterceptorFactory factory;

    public static void create(Builder builder) {
        if (null == instance) {
            instance = builder.build();
        } else {
            System.out.println("alright init !!!!! ");
        }
    }

    private BackupHelper(List<Interceptor> interceptors, IInterceptorFactory factory) {
        this.factory = factory;
        this.mInterceptors = new ArrayList<>();
        if (null != factory) this.mInterceptors.addAll(factory.getInterceptors());
        if (null != interceptors) this.mInterceptors.addAll(interceptors);
    }

    public static boolean isInit() {
        return null != instance;
    }

    public static ITask newTask() throws Exception {
        if (null == instance) {
            throw new Exception("BackupHelper has not been init");
        }
        return new TaskImpl(BackupHelper.instance);
    }

    public List<Interceptor> getAllInterceptors() {
        return mInterceptors;
    }

    public static class Builder {

        private List<Interceptor> interceptors = new ArrayList<>();
        private IInterceptorFactory factory;
        private Builder() {}

        public static Builder newBuilder() {

            Builder builder = new Builder();
            try {
                Class factoryClass = Class.forName("com.ap.InterceptorsFactory");
                if (null != factoryClass) {
                    builder.setInterceptorFactory((IInterceptorFactory)factoryClass.newInstance());
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }

            return builder;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (null != interceptor) interceptors.add(interceptor);
            return this;
        }

        public Builder addInterceptors(List<Interceptor> interceptors) {
            this.interceptors.addAll(interceptors);
            return this;
        }

        public Builder setInterceptorFactory(IInterceptorFactory factory) {
            this.factory = factory;
            return this;
        }

        public BackupHelper build() {
            return new BackupHelper(interceptors, factory);
        }

    }



}
