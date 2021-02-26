import org.junit.jupiter.api.Assertions;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

class BlockingHashMapTest {
    BlockingHashMap<Integer, Integer> innerMap = new BlockingHashMap<>(1000);
    Random rnd = new Random(System.currentTimeMillis());
    int[] keys = new int[1000];
    boolean init = false;

    @org.junit.jupiter.api.Test
    void put() throws InterruptedException {
        if (!init) {
            initMaps();
        }
        for (int key : keys) {
            Assertions.assertNotEquals(innerMap.get(key), null);
        }
    }

    @org.junit.jupiter.api.Test
    void get() throws InterruptedException {
        if (!init) {
            initMaps();
        }
        for (int key : keys) {
            Assertions.assertNotEquals(innerMap.get(key), null);
        }
    }

    @org.junit.jupiter.api.Test
    void contains() throws InterruptedException {
        if (!init) {
            initMaps();
        }
        for (int key : keys) {
            boolean res = innerMap.contains(key);
            Assertions.assertTrue(res);
        }
    }

    void initMaps() throws InterruptedException {
        init = true;

        Thread thr1 = new Thread( () -> {
            for (int i = 0; i < 500; i++) {
                keys[i] = rnd.nextInt() % 1000;
                innerMap.put(keys[i], rnd.nextInt());
            }
        });

        Thread thr2 = new Thread( () -> {
            for (int i = 500; i < 1000; i++) {
                keys[i] = rnd.nextInt() % 1000;
                innerMap.put(keys[i], rnd.nextInt());
            }
        });

        thr1.start(); thr2.start();
        thr1.join(); thr2.join();
    }
}