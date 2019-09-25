//   Represents an undirected, weighted graph
//   Possibly contains self-loops, parallel edges, and unconnected nodes

package misc.graphs;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.exceptions.NoPathExistsException;
import static misc.Searcher.topKSort;

public class Graph<V, E extends Edge<V> & Comparable<E>> {
    private IDictionary<V, IList<E>> adjacencyList;
    private IDisjointSet<V> forestOfMSTs;
    
    private IList<V> vertexInventory;
    private IList<E> edgeInventory;
    
    private int numOfVertices;
    private int numOfEdges;
    

    // @throws IllegalArgumentException:
    //   if any of the edges have a negative weight
    //   if one of the edges connects to a vertex not present in the 'vertices' list
    public Graph(IList<V> vertices, IList<E> edges) {
        this.adjacencyList = new ChainedHashDictionary<V, IList<E>>(); // Relations for all edges and vertices
        this.forestOfMSTs = new ArrayDisjointSet<V>();
        
        this.vertexInventory = new DoubleLinkedList<V>();
        this.edgeInventory = new DoubleLinkedList<E>();
        
        this.numOfVertices = 0;
        this.numOfEdges = 0;
        
        // Add <V, emptyList> to the adjacencyList
        // Update vertexInventory and numOfVertices
        for (V vertex : vertices) {
            this.adjacencyList.put(vertex, new DoubleLinkedList<>());
            this.vertexInventory.add(vertex);
            this.numOfVertices++;
        }
        
        // Import edges into the adjacencyList
        // Update edgeInventory
        // Check for illegal edges
        for (E edge : edges) {
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }
            
            V vertex1 = edge.getVertex1();
            V vertex2 = edge.getVertex2();
            
            if (!vertices.contains(vertex1) || !vertices.contains(vertex2)) {
                throw new IllegalArgumentException();
            }
            
            // Add edges to inventory and update number of edges
            this.edgeInventory.add(edge);
            this.numOfEdges++;
            
            // Adds the edge to adjacencyList for both vertices
            this.adjacencyList.get(vertex1).add(edge);
            this.adjacencyList.get(vertex2).add(edge);
        }
    }

    public Graph(ISet<V> vertices, ISet<E> edges) {
        this(setToList(vertices), setToList(edges));
    }

    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    // Returns number of vertices within the graph
    public int numVertices() {
        return this.numOfVertices;
    }

    // Returns number of edges within the graph
    public int numEdges() {
        return this.numOfEdges;
    }

    
    // Returns the set of all edges that make up the minimum spanning tree of this graph
    //   If there exists multiple valid MSTs, return any one of them
    //   Assume the graph does not contain any unconnected components
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> mst = new ChainedHashSet<>();
        
        for (V vertex : this.vertexInventory) {
            this.forestOfMSTs.makeSet(vertex);
        }
        
        IList<E> sortedEdges = topKSort(this.numOfEdges, this.edgeInventory);
        
        for (E edge : sortedEdges) {
            V vertex1 = edge.getVertex1();
            V vertex2 = edge.getVertex2();
            
            int indexRoot1 = this.forestOfMSTs.findSet(vertex1);
            int indexRoot2 = this.forestOfMSTs.findSet(vertex2);
            
            // Don't add edge if the two vertices are already grouped
            if (indexRoot1 != indexRoot2) {
                this.forestOfMSTs.union(vertex1, vertex2);
                mst.add(edge);
            }
        }
        
        return mst;
    }

    // Returns the edges that make up the shortest path from the start to the end
    // 
    // The first edge in the output list should be the edge leading out
    // of the starting node; the last edge in the output list should be
    // the edge connecting to the end node.
    // 
    // Return an empty list if the start and end vertices are the same.
    // 
    // @throws NoPathExistsException  
    //   if there does not exist a path from the start to the end
    public IList<E> findShortestPathBetween(V start, V end) {
        // VDPair is Comparable by distance, so use that to make the heap
        IPriorityQueue<VDPair> vdHeap = new ArrayHeap<VDPair>();
        
        IDictionary<V, VDPair> distancesFromStart = new ChainedHashDictionary<V, VDPair>();
        IDictionary<V, E> pathEdges = new ChainedHashDictionary<V, E>();
        
        ISet<V> unprocessedVertices = new ChainedHashSet<V>();
        
        IList<E> shortestPath = new DoubleLinkedList<E>();
        
        if (start.equals(end)) {
            return shortestPath;
        }
        
        // The following is a variance of Dijkstra's algorithm
        
        // This for loop:
        // - Sets all vertices to have a distance of infinity (except start)
        // - Collects all distances
        // - Inserts them into a min heap
        for (V vertex : this.vertexInventory) {
            VDPair vd = new VDPair(vertex, Double.POSITIVE_INFINITY);
            
            if (vertex.equals(start)) {
                vd.setD(0.0);
            }
            
            if (!unprocessedVertices.contains(vertex)) {
                vdHeap.insert(vd);
                distancesFromStart.put(vertex, vd);
                unprocessedVertices.add(vertex);
            }
        }
        
        // This while loop:
        // - Processes the closest unprocessed vertex
        // - Sets distances for all neighboring edges
        // NOTE: while loop depends on unprocessedVertices and NOT vdHeap
        //   This is because we add duplicates to the heap
        while (!unprocessedVertices.isEmpty()) {
            VDPair vd = vdHeap.removeMin();
            V vertex = vd.getV();
  
            double distance = vd.getD();
            
            if (distance == Double.POSITIVE_INFINITY) {
                break;
            }
            
            if (unprocessedVertices.contains(vertex)) {
                unprocessedVertices.remove(vertex);
                
                if (vertex.equals(end)) {
                    while (!vertex.equals(start)) {
                        E edge = pathEdges.get(vertex);
                        shortestPath.insert(0, edge);
                        vertex = edge.getOtherVertex(vertex);
                    }
                    
                    return shortestPath;
                }
                
                IList<E> edges = this.adjacencyList.get(vertex);
                
                for (E edge : edges) {
                    V neighborVertex = edge.getOtherVertex(vertex);
                    VDPair neighborVD = distancesFromStart.get(neighborVertex);
                    
                    // Since edges are undirected, need to specify unprocessed vertices
                    if (unprocessedVertices.contains(neighborVertex)) {
                        double neighborDistanceCurrent = neighborVD.getD();
                        double neighborDistanceNew = distance + edge.getWeight();
                        
                        if ((neighborDistanceNew - neighborDistanceCurrent) < 0) {
                            VDPair bestVDPair = new VDPair(neighborVertex, neighborDistanceNew);
                            vdHeap.insert(bestVDPair);
                            distancesFromStart.put(neighborVertex, bestVDPair);
                            pathEdges.put(neighborVertex, edge);
                        }
                    }
                }
            }
        }
        
        throw new NoPathExistsException();
    }
    
    // Objects used to keep track of a vertex's distance from the start
    // Set all distances to infinity
    // As you process vertices, set the distance accordingly
    private class VDPair implements Comparable<VDPair> {
        private V vertex;
        private double distance;
        
        public VDPair(V vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
        
        public V getV() {
            return this.vertex;
        }
        
        public double getD() {
            return this.distance;
        }
        
        public void setD(double d) {
            this.distance = d;
        }
        
        @Override
        public int compareTo(VDPair vdObject) {
            return (int) (this.distance - vdObject.distance);
        }
    }
}
