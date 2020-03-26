package com.mine;


import com.ap.BackupHelper;
import com.ap.Result;

public class AAA {
    public static void main(String[] args) {
        System.out.println("print com.mine.AAA");
        BackupHelper.Builder builder = BackupHelper.Builder.newBuilder();
        BackupHelper.create(builder);

        Thread work = new Thread(() -> {
            try {
                Result result = BackupHelper.newTask().execute();
                System.out.println("success : " + result.isSuccess() + "; message : " + result.getMessage() );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        work.start();
        try {
            work.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
