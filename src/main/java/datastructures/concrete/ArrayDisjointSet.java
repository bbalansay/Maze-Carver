package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.
    private static final int INITIAL_CAPACITY = 10;
    private int arrayLength;
    private int arraySize;
    private IDictionary<T, Integer> nodeInventory;

    public ArrayDisjointSet() {
        this.pointers = new int[INITIAL_CAPACITY];
        this.arrayLength = this.pointers.length;
        this.arraySize = 0;
        this.nodeInventory = new ChainedHashDictionary<T, Integer>();
    }

    @Override
    public void makeSet(T item) {
        if (this.nodeInventory.containsKey(item)) {
            throw new IllegalArgumentException();
        } else {
            int index = this.arraySize;
            this.arraySize++;
            
            if (this.arraySize > this.arrayLength) {
                this.resize();
            } 
                
            this.nodeInventory.put(item, index);
            this.pointers[index] = -1; // Rank 0 -> Value stored as `(rank * -1) - 1`
        }
    }
    
    // This method doubles the size of the current array
    // Copies everything from pointers into new array
    // Updates arrayLength to twice its previous value
    private void resize() {
        this.arrayLength *= 2;
        int[] resizedPointers = new int[this.arrayLength];
        
        
        // I'm pretty sure the iterator is faster than for loops?
        // We can switch this back to a for loop if we want to idk
        // I switched it to a for loop just to be safe
        for (int i = 0; i < this.pointers.length; i++) {
            resizedPointers[i] = this.pointers[i];
        }
        
        this.pointers = resizedPointers;
    }

    @Override
    public int findSet(T item) {
        if (!this.nodeInventory.containsKey(item)) {
            throw new IllegalArgumentException();
        } else {
            // Get the index of the item, then check its reference
            int index = this.nodeInventory.get(item);
            return findSetHelper(index);
        }
    }
    
    // Helps find the root >>>INDEX<<< of the set
    // If the reference ID is a rank, this is the root node so return its index
    // If the reference points to another index, follow the reference
    private int findSetHelper(int index) {
        // Either references a rank or an index
        int referenceID = this.pointers[index];
        
        // If it's a rank, return its index
        // If not, keep following the trail
        if (referenceID < 0) {
            return index;
        } else {
            return findSetHelper(referenceID);
        }
    }

    @Override
    public void union(T item1, T item2) {
        if (!this.nodeInventory.containsKey(item1) || !this.nodeInventory.containsKey(item2)) {
            throw new IllegalArgumentException();
        } 
        
        // findSet finds the >>>INDEX<<< of the set's root
        int rootIndexSet1 = findSet(item1);
        int rootIndexSet2 = findSet(item2);
        
        if (rootIndexSet1 == rootIndexSet2) {
            throw new IllegalArgumentException();
        }
        
        // REMINDER: Ranks are stored as `(rank * -1) - 1`
        int rankSet1 = this.pointers[rootIndexSet1];
        int rankSet2 = this.pointers[rootIndexSet2];
        
        // Since ranks are stored as negative numbers, comparisons are backwards
        // The rankSet of greater value is actually the smaller set
        // We move the smaller set to point to the root of the bigger set
        if (rankSet1 > rankSet2) {
            this.pointers[rootIndexSet1] = rootIndexSet2;
        } else if (rankSet1 < rankSet2) {
            this.pointers[rootIndexSet2] = rootIndexSet1;
        } else {
            this.pointers[rootIndexSet2] = rootIndexSet1;
            this.pointers[rootIndexSet1]--;
        }   
    }
}
