/**
 * Class presenting thread-safety hash map
 * @param <K> type of key in KEY-VALUE pair
 * @param <V> type of value in KEY-VALUE pair
 */
public class BlockingHashMap<K, V> {

    private final int INIT_SIZE = 16;

    public static class MapEntry<K, V> {
        protected final K key;
        protected volatile V value;
        protected final int hash;
        protected MapEntry<K, V> next;

        MapEntry(int hash, K key, V value) {
            this.value = value;
            this.hash = hash;
            this.key = key;
        }
    }

    private final MapEntry<K, V>[] table;

    /**
     * Class constructor specifying number of buckets to create
     * @param initialSize a number of buckets in map
     */
    public BlockingHashMap(int initialSize) {
        this.table = newTable(initialSize);
    }

    /**
     * Empty class constructor
     */
    public BlockingHashMap() {
        this.table = newTable(INIT_SIZE);
    }

    private MapEntry<K, V>[] newTable(int size) {
        @SuppressWarnings("unchecked")
        MapEntry<K, V>[] nt = (MapEntry<K, V>[]) new MapEntry<?, ?>[size];
        return nt;
    }

    /**
     *
     * <p>
     *     this method adds key-value pair in map
     * </p>
     * @param key a key
     * @param value a value
     */
    public synchronized void put(K key, V value) {
        int hash = hashCode(key);
        int index = hash & table.length - 1;
        MapEntry<K, V> curr = table[index];
        for (; curr != null; curr = curr.next) {
            if ((curr.hash == hash) && (key.equals(curr.key))) {
                curr.value = value;
                return;
            }
            if (curr.next == null) {
                break;
            }
        }
        if (curr == null) {
            table[index] = new MapEntry<>(hash, key, value);
        }
        else {
            curr.next = new MapEntry<>(hash, key, value);
        }

    }

    /**
     * this methods tries to find an object with a given key in map
     * @param key key of KEY-VALUE pair
     * @return found object, otherwise null
     */
    public V get(K key) {
        int hash = hashCode(key);
        int index = hash & table.length - 1;

        for (MapEntry<K, V> curr = table[index]; curr != null; curr = curr.next) {
            if (curr.hash == hash && key.equals(curr.key)) {
                return curr.value;
            }
        }

        return null;
    }

    /**
     * this method check if map contains KEY-VALUE pair with a given key
     * @param key key of KEY-VALUE pair
     * @return true - if map contains this key, false otherwise
     */
    public boolean contains(K key) {
        int hash = hashCode(key);
        int index = hash & table.length - 1;

        for (MapEntry<K, V> curr = table[index]; curr != null; curr = curr.next) {
            if (curr.hash == hash && key.equals(curr.key)) {
                return true;
            }
        }

        return false;
    }


    private int hashCode(K x) {
        int h = x.hashCode();
        return (h << 7) - h + (h >>> 9) + (h >>> 17);
    }

    public MapEntry<K, V>[] getTableCopy() {
        MapEntry<K, V>[] tableCopy = newTable(table.length);
        System.arraycopy(table, 0, tableCopy, 0, table.length);
        return tableCopy;
    }
}
