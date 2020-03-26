package com.mine.simples;

import com.ap.*;
import com.ap.annotation.BackupInterceptor;

@BackupInterceptor(index = 1)
public class SimpleClassA extends BaseInterceptor {

    public SimpleClassA() {}

    @Override
    void handle(Chain chain) {
        new Thread(() -> {
            try {
                lock.lock();
                System.out.println("do some work A");
                Thread.sleep(20 * 1000);
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
        System.out.println("simple A success");
        if (chain.hasNext()) {
            System.out.println("simple A has nest ");
            RequestImpl.Builder builder = new RequestImpl.Builder();
            Request newRequest = builder
                    .from(SimpleClassC.class.getName())
                    .param(chain.request().getParam())
                    .build();
            return chain.proceed(newRequest);
        } else {
            Result.Builder builder = new Result.Builder();

            return builder.isSuccess(true)
                    .message("simple A failed")
                    .build();
        }
    }

    @Override
    Result onBackupFailed(InterceptorChainImpl chain) {
        System.out.println("simple A failed");
        Result.Builder builder = new Result.Builder();

        return builder.isSuccess(false)
                .message("simple A failed")
                .build();
    }
}
