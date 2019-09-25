package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;

    // You're encouraged to add extra fields (and helper methods) though!
    private int size;
    private int sizeOfArray;

    public ArrayDictionary() {
        this.pairs = makeArrayOfPairs(10);
        this.size = 0;
        this.sizeOfArray = 10;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }
    
  
    @Override
    public V get(K key) {
        for (int i = 0; i < this.size; i++) {
            if (key == null) {
                if (this.pairs[i].key == null) {
                    return this.pairs[i].value;
                }
            } else if (key.equals(this.pairs[i].key)) {
                return this.pairs[i].value;
            }
        } 

        throw new NoSuchKeyException();
    }
    

    @Override
    public void put(K key, V value) {
        Pair<K, V> newPair = new Pair<K, V>(key, value);

        // 1st case: if key already exists, update value
        for (int i = 0; i < this.size; i++) {
            if (key == null) {
                if (this.pairs[i].key == null) {
                    this.pairs[i].value = value;
                    return;
                }
            } else if (key.equals(this.pairs[i].key)) {
                this.pairs[i].value = value;
                return;
            }
        }

        // 2nd case: if array is full, double the size, and then add
        if (this.size == this.sizeOfArray) {
            // double the size and make a new pair 'doublePair' that has double the size
            this.sizeOfArray = this.sizeOfArray*2;
            Pair<K, V>[] doublePairs = this.makeArrayOfPairs(sizeOfArray);

            // copy all the elements over from the old array to the new one
            for (int i = 0; i < this.size; i++) {
                doublePairs[i] = this.pairs[i];
            }

            this.pairs = doublePairs;
        }

        // 3rd case: regular adding to the back
        this.pairs[size] = newPair;
        this.size++;
    }

    @Override
    public V remove(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchKeyException();
        }
        
        int index = -1;
        
        for (int i = 0; i < this.size; i++) {
            if (key == null) {
                if (this.pairs[i].key == null) {
                    index = i;
                }
            } else if (key.equals(this.pairs[i].key)) {
                index = i;
            }
        } 

        V removed = this.pairs[index].value;
        this.pairs[index] = this.pairs[this.size - 1];
        this.pairs[this.size - 1] = null;
        this.size--;
        return removed;
    }

    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < this.size; i++) {
            if (key == null) {
                if (this.pairs[i].key == null) {
                    return true;
                }
            } else if (key.equals(this.pairs[i].key)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<>(this.pairs, this.size);
    }

    private class ArrayDictionaryIterator<T> implements Iterator<KVPair<K, V>> {
        private Pair<K, V>[] pairs;
        private int currentIndex = 0;
        private int size;

        public ArrayDictionaryIterator(Pair<K, V>[] pairs, int size) {
            this.pairs = pairs;
            this.size = size;
        }

        public boolean hasNext() {
            return this.currentIndex < this.size;
        }

        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            Pair<K, V> currentPair = this.pairs[currentIndex];
            KVPair<K, V> newPair = new KVPair<>(currentPair.key, currentPair.value);
            this.currentIndex += 1;
            return newPair;
        }
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
