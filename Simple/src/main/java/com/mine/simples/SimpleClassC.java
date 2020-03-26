package com.mine.simples;

import com.ap.InterceptorChainImpl;
import com.ap.Request;
import com.ap.RequestImpl;
import com.ap.Result;
import com.ap.annotation.BackupInterceptor;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

@BackupInterceptor(index = 3)
public class SimpleClassC extends BaseInterceptor {

    public SimpleClassC() {}


    @Override
    void handle(Chain chain) {
        new Thread(() -> {
            try {
                lock.lock();
                System.out.println("do some work C");
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
        System.out.println("simple C success");
        if (chain.hasNext()) {
            System.out.println("simple C has nest ");
            RequestImpl.Builder builder = new RequestImpl.Builder();
            Request newRequest = builder
                    .from(SimpleClassC.class.getName())
                    .param(chain.request().getParam())
                    .build();
            return chain.proceed(newRequest);
        } else {
            Result.Builder builder = new Result.Builder();

            return builder.isSuccess(true)
                    .message("last simple C failed")
                    .build();
        }
    }

    @Override
    Result onBackupFailed(InterceptorChainImpl chain) {
        System.out.println("simple c failed");
        Result.Builder builder = new Result.Builder();

        return builder.isSuccess(false)
                .message("simple c failed")
                .build();
    }
}
