import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Random;

class BlockingHashMapTest {

    @org.junit.jupiter.api.Test
    void testPutByTwoThreads() throws InterruptedException {
        BlockingHashMap<Integer, Integer> innerMap = new BlockingHashMap<>(10);
        Random rnd = new Random(System.currentTimeMillis());
        int[] keys = new int[1000];

        Thread thr1 = new Thread( () -> {
            for (int i = 0; i < 500; i++) {
                keys[i] = rnd.nextInt() % 1000;
                int value = rnd.nextInt() % 1000;
                innerMap.put(keys[i], value);
            }
        });

        Thread thr2 = new Thread( () -> {
            for (int i = 500; i < 1000; i++) {
                keys[i] = rnd.nextInt() % 1000;
                int value = rnd.nextInt() % 1000;
                innerMap.put(keys[i], value);
            }
        });

        thr1.start(); thr2.start();
        thr1.join(); thr2.join();

        for (int key : keys) {
            innerMap.contains(key);
        }
    }

    @org.junit.jupiter.api.Test
    void testGetReturnsCorrect() {
        BlockingHashMap<Integer, Integer> innerMap = new BlockingHashMap<>(50);
        Random rnd = new Random(System.currentTimeMillis());
        int[] keys = new int[1000];
        HashMap<Integer, Integer> valueByKey = new HashMap<>();

        for (int i = 0; i < 1000; i++) {
            keys[i] = rnd.nextInt() % 1000;
            int value = rnd.nextInt() % 1000;
            innerMap.put(keys[i], value);
            valueByKey.put(keys[i], value);
        }

        for (int i = 0; i < 1000; i++) {
            Assertions.assertEquals(innerMap.get(keys[i]), valueByKey.get(keys[i]));
        }
    }

    @org.junit.jupiter.api.Test
    void testPutSameKeys() {
        BlockingHashMap<Integer, Integer> map = new BlockingHashMap<>(10);
        map.put(0, 1);
        map.put(0, 0);
        Assertions.assertEquals(map.get(0), 0);
    }
}
