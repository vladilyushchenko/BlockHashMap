public class BlockingHashMap<K, V> {

    private static class MapEntry<K, V> {
        protected final K key;
        protected volatile V value;
        protected final int hash;
        protected MapEntry<K, V> next;

        MapEntry(int hash, K key, V value, MapEntry<K, V> next) {
            this.value = value;
            this.hash = hash;
            this.key = key;
            this.next = next;
        }
    }

    private final MapEntry<K, V>[] table;

    public BlockingHashMap(int initialSize) {
        this.table = newTable(initialSize);
    }

    protected MapEntry<K, V>[] newTable(int size) {
        @SuppressWarnings("unchecked")
        MapEntry<K, V>[] nt = (MapEntry<K, V>[]) new MapEntry<?, ?>[size];
        return nt;
    }

    public synchronized void put(K key, V value) {
        int hash = hashCode(key);
        int index = hash & table.length - 1;
        MapEntry<K, V> curr = table[index];
        for (; curr != null && curr.next != null; curr = curr.next) {
            if ((curr.hash == hash) && (key.equals(curr.key))) {
                curr.value = value;
                return;
            }
        }
        if (curr == null)
            table[index] = new MapEntry<>(hash, key, value, null);
        else
            curr.next = new MapEntry<>(hash, key, value, null);

    }

    public synchronized V get(K key) {
        int hash = hashCode(key);
        int index = hash & table.length - 1;

        for (MapEntry<K, V> curr = table[index]; curr != null; curr = curr.next) {
            if (curr.hash == hash && key.equals(curr.key)) {
                return curr.value;
            }
        }

        return null;
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public int hashCode(K x) {
        int h = x.hashCode();
        return (h << 7) - h + (h >>> 9) + (h >>> 17);
    }
}
