package com.xie.song.XUNET.test;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class Test4 {
    public static void main(String[] args) {
        PriorityBlockingQueue priorityBlockingQueue = new PriorityBlockingQueue<User>();
        priorityBlockingQueue.put(new User(10));
        priorityBlockingQueue.put(new User(12));
        priorityBlockingQueue.put(new User(32));
        priorityBlockingQueue.put(new User(4));
        priorityBlockingQueue.put(new User(52));
        priorityBlockingQueue.put(new User(6));

        try {
            System.out.println(priorityBlockingQueue.take().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class User implements Comparable {

     int index;

    public User(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(Object o) {
        User user = (User)o;
        int i = this.index - user.index;
        return i;
    }

    @Override
    public String toString() {
        return "User{" +
                "index=" + index +
                '}';
    }
}
