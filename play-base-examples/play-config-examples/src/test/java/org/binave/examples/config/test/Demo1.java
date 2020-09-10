package org.binave.examples.config.test;

import org.binave.common.util.CodecUtil;
import org.junit.Test;
import redis.clients.util.MurmurHash;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * @author by bin jin on 2017/2/18.
 */
public class Demo1 {

    @Test
    public void Test1() throws UnsupportedEncodingException {

        Random r = new Random();

        for (int j = 0; j < 100; j++) {
            String i = "" +
                    (char) r.nextInt() + (char) r.nextInt() + (char) r.nextInt() + (char) r.nextInt() +
                    (char) r.nextInt() + (char) r.nextInt() + (char) r.nextInt();

            byte[] bb = i.getBytes("UTF-8");
            System.out.println(j + ",+ " + CodecUtil.ConsistentHash.MURMUR3.hash(bb, 0, bb.length));

            MurmurHash murmurHash = new MurmurHash();
            System.out.println(j + ",= " + murmurHash.hash(bb));
            System.out.println();
        }

////        CountDownLatch downLatch = new CountDownLatch(10);
//
//        Semaphore semaphore = new Semaphore(100, true);
//
////        try {
////            semaphore.acquire(80);
////        } catch (InterruptedException ignored) {
////
////        }
//        // 清空
//        semaphore.drainPermits();
//        semaphore.release();
//
//        System.out.println("== ==");
//        for (int i = 0; i < 21; i++) {
//            try {
//
//                System.out.println("+");
//                System.out.print(i + " - ");
//                semaphore.acquire(); // 卡住
//                System.out.println("->");
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            System.out.print(i + " = ");
//            System.out.print(semaphore.availablePermits());
//            System.out.println(" =>");
//        }
//
////        try {
////
////            Field field = RegRun.class.getDeclaredField("str");
////            field.setAccessible(true);
////
////            System.out.println("is static: " + Modifier.isStatic(field.getModifiers()));
////
////            field.set(null, "ss");
////
////            RegRun.doOneThing();
////
////        } catch (NoSuchFieldException | IllegalAccessException e) {
////            e.printStackTrace();
////        }
    }

}
