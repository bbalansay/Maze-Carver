package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * See IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a 4-heap.
    private static final int NUM_CHILDREN = 4;
    private static final int INITIAL_CAPACITY = 100;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int heapSize;
    private int heapCapacity;

    // Feel free to add more fields and constants.
    public ArrayHeap() {
        this.heap = makeArrayOfT(INITIAL_CAPACITY);
        this.heapSize = 0;
        this.heapCapacity = INITIAL_CAPACITY;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int size) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[size]);
    }

    @Override
    public T removeMin() {
        if (this.heapSize == 0) {
            throw new EmptyContainerException();
        }
        
        T removed = this.heap[0];
        this.heap[0] = this.heap[this.heapSize - 1];
        this.heap[this.heapSize - 1] = null;
        this.heapSize--;
        moveDown(0);
        return removed;
    }

    @Override
    public T peekMin() {
        if (this.heapSize == 0) {
            throw new EmptyContainerException();
        }
        
        return this.heap[0];
    }
    
    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        
        if (this.heapSize >= this.heapCapacity) {
            this.resize();
        }

        this.heap[this.heapSize] = item;
        moveUp(this.heapSize);
        this.heapSize++;
    }
    
    // Helper method
    // Moves node up if of a lesser value than it's parent
    private void moveUp(int childIndex) {
        int parentIndex = findParent(childIndex);

        while (childIndex > 0 && this.heap[childIndex].compareTo(this.heap[parentIndex]) < 0) {
            nodeSwap(childIndex, parentIndex);
            childIndex = parentIndex;
            parentIndex = findParent(childIndex);
        }
    }
    
    // Helper method
    // Moves node down if of a greater value than it's child
    private void moveDown(int parentIndex) {
        int childIndex = findMinChild(parentIndex);

        while (childIndex > 0 && this.heap[childIndex].compareTo(this.heap[parentIndex]) < 0) {
            nodeSwap(childIndex, parentIndex);
            parentIndex = childIndex;
            childIndex = findMinChild(parentIndex);
        }
    }
    
    // Helper method
    // Swaps the positions of two nodes
    private void nodeSwap(int idxA, int idxB) {
        T valA = this.heap[idxA];
        T valB = this.heap[idxB];
        this.heap[idxA] = valB;
        this.heap[idxB] = valA;
    }
    
    // Helper method
    // Finds the parent of a given node
    private int findParent(int childIndex) {
        return (childIndex - 1) / NUM_CHILDREN;
    }
    
    // Helper method
    // Finds the child with least value of a given node
    private int findMinChild(int parentIndex) {
        int idxFirstChild = NUM_CHILDREN * parentIndex + 1;
        int idxLastChild = NUM_CHILDREN * parentIndex + NUM_CHILDREN;
        
        if (idxFirstChild >= this.heapSize) {
            return 0;
        } else {
            int idxMinChild = idxFirstChild;
            
            for (int i = idxFirstChild + 1; i <= idxLastChild; i++) {
                T newChild = this.heap[i];
                T minChild = this.heap[idxMinChild];
                
                if (newChild != null && newChild.compareTo(minChild) < 0) {
                    idxMinChild = i;
                }
            }
            
            return idxMinChild;
        }
    }
    
    // Helper method
    // Doubles the capacity of the heap
    private void resize() {
        T[] oldHeap = this.heap;
        this.heapCapacity *= 2;
        this.heap = makeArrayOfT(this.heapCapacity);
        
        for (int i = 0; i < this.heapSize; i++) {
            this.heap[i] = oldHeap[i];
        }
    }

    @Override
    public int size() {
        return this.heapSize;
    }
}