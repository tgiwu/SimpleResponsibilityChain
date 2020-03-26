package com.mine.simples;

import com.ap.Interceptor;
import com.ap.InterceptorChainImpl;
import com.ap.Result;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BaseInterceptor implements Interceptor {

    private final int TIME_OUT_DEFAULT = 20;
    private final TimeUnit TIME_OUT_UNIT_DEFAULT = TimeUnit.MINUTES;

    protected AtomicBoolean isSuccess = new AtomicBoolean(false);
    protected int timeout = TIME_OUT_DEFAULT;
    protected TimeUnit timeoutUnit = TIME_OUT_UNIT_DEFAULT;

    protected Lock lock = new ReentrantLock(true);
    protected Condition condition = lock.newCondition();
    @Override
    public Result intercept(Chain chain) {
        try {
            lock.lock();

            handle(chain);

            condition.await(timeout, timeoutUnit);

            if (isSuccess.get()) {
                return onBackupSuccess((InterceptorChainImpl) chain);
            } else {
                return onBackupFailed((InterceptorChainImpl) chain);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return null;
    }

    abstract void handle(Chain chain);

    abstract Result onBackupSuccess(InterceptorChainImpl chain);

    abstract Result onBackupFailed(InterceptorChainImpl chain);
}
