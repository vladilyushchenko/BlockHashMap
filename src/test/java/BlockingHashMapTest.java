import org.junit.jupiter.api.Assertions;

import java.util.*;

class BlockingHashMapTest {

    @org.junit.jupiter.api.Test
    void testPutByMultipleThreadsDoesNotLooseNodesInBucket() throws InterruptedException {
        BlockingHashMap<Integer, Integer> innerMap = new BlockingHashMap<>(1);
        Random rnd = new Random(System.currentTimeMillis());
        int[] keys = new int[15000];

        Thread thr1 = new Thread( () -> {
            for (int i = 0; i < 5000; i++) {
                keys[i] = rnd.nextInt();
                int value = rnd.nextInt();
                innerMap.put(keys[i], value);
            }
        });

        Thread thr2 = new Thread( () -> {
            for (int i = 5000; i < 10000; i++) {
                keys[i] = rnd.nextInt();
                int value = rnd.nextInt();
                innerMap.put(keys[i], value);
            }
        });

        Thread thr3 = new Thread( () -> {
            for (int i = 10000; i < 15000; i++) {
                keys[i] = rnd.nextInt();
                int value = rnd.nextInt();
                innerMap.put(keys[i], value);
            }
        });

        thr1.start(); thr2.start(); thr3.start();
        thr1.join(); thr2.join(); thr3.join();

        for (int key : keys) {
            Assertions.assertTrue(innerMap.contains(key));
        }
    }

    @org.junit.jupiter.api.Test
    void testGetReturnsWhatWePutBySingleThread() {
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
    void testMapRewritesValueInExistingKey() {
        BlockingHashMap<Integer, Integer> map = new BlockingHashMap<>(10);
        map.put(0, 1);
        map.put(0, 0);
        Assertions.assertEquals(map.get(0), 0);
    }

    @org.junit.jupiter.api.Test
    void testMapHasTrueSize() throws InterruptedException {
        BlockingHashMap<Integer, Integer> map = new BlockingHashMap<>(10);

        BlockingHashMap<Integer, Integer> innerMap = new BlockingHashMap<>(1);
        Random rnd = new Random(System.currentTimeMillis());
        int[] keys = new int[15000];

        Thread thr1 = new Thread( () -> {
            for (int i = 0; i < 5000; i++) {
                keys[i] = rnd.nextInt() % 5000;
                int value = rnd.nextInt();
                innerMap.put(keys[i], value);
            }
        });

        Thread thr2 = new Thread( () -> {
            for (int i = 5000; i < 10000; i++) {
                keys[i] = rnd.nextInt() % 5000;
                int value = rnd.nextInt();
                innerMap.put(keys[i], value);
            }
        });

        Thread thr3 = new Thread( () -> {
            for (int i = 10000; i < 15000; i++) {
                keys[i] = rnd.nextInt() % 5000;
                int value = rnd.nextInt();
                innerMap.put(keys[i], value);
            }
        });

        thr1.start(); thr2.start(); thr3.start();
        thr1.join(); thr2.join(); thr3.join();

        List<Integer> list =  arrayToList(keys);
        int keySize = list.parallelStream().distinct().toArray().length;

        BlockingHashMap.MapEntry<Integer, Integer>[] table = innerMap.getTableCopy();

        int mapSize = 0;
        for (BlockingHashMap.MapEntry<Integer, Integer> entry : table) {
            while (entry != null)  {
                entry = entry.next;
                mapSize++;
            }
        }

        Assertions.assertEquals(mapSize, keySize);
    }

    private List<Integer> arrayToList(int[] arr) {
        List<Integer> list = new ArrayList<>();
        for (int elem : arr) {
            list.add(elem);
        }
        return list;
    }
}
