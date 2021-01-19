package com.xie.song.XUNET.test;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Test5 {
    private static  ReentrantLock reentrantLock = new ReentrantLock();

    //记录
    private static Condition condition = reentrantLock.newCondition();

    public static void main(String[] args) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    reentrantLock.lock();
//                    System.out.println("kaishi");
//                    condition.await();
//                    System.out.println("jieshu");
//
//                    reentrantLock.unlock();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("解锁");
//        reentrantLock.lock();
//        condition.signal();
//        reentrantLock.unlock();

//        ByteBuffer allocate = ByteBuffer.allocate(1024);
//        allocate.put("qwe".getBytes());
//        System.out.println(allocate.array().length);

        String s = "xiesongxu";
        ByteBuffer wrap = ByteBuffer.wrap(s.getBytes());
        System.out.println(wrap.position()+":"+wrap.limit());


    }
}
