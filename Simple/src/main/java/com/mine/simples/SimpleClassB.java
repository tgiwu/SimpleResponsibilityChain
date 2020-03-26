package com.mine.simples;

import com.ap.*;
import com.ap.annotation.BackupInterceptor;

@BackupInterceptor(index = 2)
public class SimpleClassB extends BaseInterceptor {

    public SimpleClassB() {}


    @Override
    void handle(Chain chain) {
        new Thread(() -> {
            try {
                lock.lock();
                System.out.println("do some work B");
                Thread.sleep(5 * 1000);
                isSuccess.set(Utils.shouldSuccess(System.currentTimeMillis()));

                condition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
    }

    @Override
    Result onBackupSuccess(InterceptorChainImpl chain) {
        System.out.println("simple B success");
        if (chain.hasNext()) {
            System.out.println("simple B has next");
            RequestImpl.Builder builder = new RequestImpl.Builder();
            Request newRequest = builder
                    .from(SimpleClassC.class.getName())
                    .param(chain.request().getParam())
                    .build();
            return chain.proceed(newRequest);
        } else {
            Result.Builder builder = new Result.Builder();

            return builder.isSuccess(true)
                    .message("last one simple B success")
                    .build();
        }
    }

    @Override
    Result onBackupFailed(InterceptorChainImpl chain) {
        System.out.println("simple B failed");
        Result.Builder builder = new Result.Builder();

        return builder.isSuccess(false)
                .message("simple B failed")
                .build();
    }
}
