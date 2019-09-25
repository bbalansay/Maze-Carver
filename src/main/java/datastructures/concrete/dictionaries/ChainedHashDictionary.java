package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    private int load;
    private int capacity;

    public ChainedHashDictionary() {
        this.chains = makeArrayOfChains(10);
        this.load = 0;
        this.capacity = 10;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }
    
    // Gives correct hash code
    private int getHashCode(K key) {
        if (key != null) {
            return Math.abs(key.hashCode()) % this.capacity;
        } else {
            return 0;
        }
    }

    @Override
    public V get(K key) {
        int hash = getHashCode(key);
        // No key/dictionary exists for that hash
        if (this.chains[hash] == null || !this.chains[hash].containsKey(key)) {
            throw new NoSuchKeyException();
        }
        
        return this.chains[hash].get(key);
        
    }

    @Override
    public void put(K key, V value) {
        int hash = getHashCode(key);
        
        if (this.chains[hash] == null) {
            this.chains[hash] = new ArrayDictionary<K, V>();
        }
        
        // Need this if it overwrites
        int sizeBeforePut = this.chains[hash].size();
        this.chains[hash].put(key, value);
        int sizeAfterPut = this.chains[hash].size();
        // Either 0 or 1
        this.load += (sizeAfterPut - sizeBeforePut);
        
        double loadFactor = (double) this.load / (double) this.capacity;
        double loadResize = 2.0 / 3.0;
        
        if (loadFactor > loadResize) {
            this.capacity *= 2;
            IDictionary<K, V>[] tempChains = makeArrayOfChains(this.capacity);
            
            for (KVPair<K, V> pair : this) {
                hash = getHashCode(pair.getKey());
                
                if (tempChains[hash] == null) {
                    tempChains[hash] = new ArrayDictionary<K, V>();
                }
                
                tempChains[hash].put(pair.getKey(), pair.getValue());    
            }
            
            this.chains = tempChains;
        }
    }

    @Override
    public V remove(K key) {
        int hash = getHashCode(key);
        
        if (this.chains[hash] == null || !this.chains[hash].containsKey(key)) {
            throw new NoSuchKeyException();
        } else {
            this.load--;
            return this.chains[hash].remove(key);
        }
    }

    @Override
    public boolean containsKey(K key) {
        int hash = getHashCode(key);

        if (this.chains[hash] == null) {
            return false;
        } else {
            return this.chains[hash].containsKey(key);
        }    
    }

    @Override
    public int size() {
        return this.load;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int currentIndex;
        private int chainLength;
        private Iterator<KVPair<K, V>> innerIter;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.currentIndex = 0;
            this.chainLength = chains.length;
            this.innerIter = this.getIterator();
        }
        
        // Helper method to get iterator for inner dictionary
        private Iterator<KVPair<K, V>> getIterator() {
            if (this.chains[currentIndex] != null) {
                return this.chains[currentIndex].iterator();
            } else {
                return null;
            }
        }

        @Override
        public boolean hasNext() {
            while (this.currentIndex < this.chainLength) {
                // If not null and has next, return true
                if (innerIter != null && innerIter.hasNext()) {
                    return true;
                } 
                
                // Update current index to search again
                this.currentIndex++;
                if (this.currentIndex != this.chainLength) {
                    innerIter = this.getIterator();
                }
            }
            
            return false;
        }

        @Override
        public KVPair<K, V> next() {
            if (this.hasNext()) {
                return this.innerIter.next();
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
