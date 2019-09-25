package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        if (this.front == null && this.back == null && this.size == 0) {
            this.front = new Node<T>(item);
            this.back = this.front;
        } else {
            this.back.next = new Node<T>(this.back, item, null);
            this.back = this.back.next;
        }
        
        this.size++;
    }

    @Override
    public T remove() {
        if (this.front == null && this.back == null && this.size == 0) {
            throw new EmptyContainerException();
        } 
    
        Node<T> temp;
        
        if (this.size == 1) {
            temp = front;
            front = null;
            back = null;
        } else {
            temp = this.back;
            this.back = this.back.prev;
            this.back.next = null;
        }
        

        this.size--;
        return temp.data;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        } 
        
        return findNode(index).data;
    }

    @Override
    public void set(int index, T item) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        } 
        
        if (this.size == 1) {
            this.front = new Node<T>(item);
            this.back = this.front;
        } else if (index == 0) {
            this.front = new Node<T>(null, item, this.front.next);
            this.front.next.prev = this.front;
        } else if (index == (this.size - 1)) {
            this.back = new Node<T>(this.back.prev, item, null);
            this.back.prev.next = this.back;
        } else {
            Node<T> oldNode = findNode(index);
            Node<T> newNode = new Node<T>(oldNode.prev, item, oldNode.next);
            oldNode.prev.next = newNode;
            oldNode.next.prev = newNode;
        }
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index >= (this.size + 1)) {
            throw new IndexOutOfBoundsException();
        } 
        
        if (this.size == 0) {
            this.front = new Node<T>(item);
            this.back = this.front;
        } else if (index == 0) {
            this.front.prev = new Node<T>(null, item, this.front);
            this.front = this.front.prev;
        } else if (index == this.size) {
            this.back.next = new Node<T>(this.back, item, null);
            this.back = this.back.next;
        } else {
            Node<T> oldNode = findNode(index);
            Node<T> newNode = new Node<T>(oldNode.prev, item, oldNode);
            oldNode.prev.next = newNode;
            oldNode.prev = newNode;
        }
        
        this.size++;
    }

    @Override
    public T delete(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        } 
        
        Node<T> oldNode = findNode(index);
        
        if (this.size == 1) {
            this.front = null;
            this.back = null;
        } else if (index == 0) {
            this.front = this.front.next;
            this.front.prev = null;
        } else if (index == (this.size - 1)) {
            this.back = this.back.prev;
            this.back.next = null;
        } else {
            oldNode.prev.next = oldNode.next;
            oldNode.next.prev = oldNode.prev;
        }
        
        this.size--;
        return oldNode.data;
    }

    @Override
    public int indexOf(T item) {
        Node<T> temp = this.front;
        
        for (int i = 0; i < this.size; i++) {
            if (temp.data == null) {
                if (item == null) {
                    return i;
                }
            } else if (temp.data.equals(item)) {
                return i;
            }
            temp = temp.next;
        }
        
        return -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(T item) {
        return indexOf(item) != -1;
    }
    
    // This method takes an index as input and returns the node at said index
    private Node<T> findNode(int index) {
        Node<T> temp = null;
        
        if (index < this.size / 2) {
            temp = this.front;
            for (int i = 0; i < index; i++) {
                temp = temp.next;
            }
        } else {
            temp = this.back;
            for (int i = (this.size - 1); i > index; i--) {
                temp = temp.prev;
            }
        }
        
        return temp;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            
            T nextData = this.current.data;
            this.current = this.current.next;
            return nextData;
        }
    }
}
