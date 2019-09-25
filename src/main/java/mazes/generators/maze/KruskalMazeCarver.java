package mazes.generators.maze;

import datastructures.concrete.ChainedHashSet;
import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;
import java.util.Random;
import misc.graphs.Graph;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {
        // Note: make sure that the input maze remains unmodified after this method is over.
        //
        // In particular, if you call 'wall.setDistance()' at any point, make sure to
        // call 'wall.resetDistanceToOriginal()' on the same wall before returning.

        Random rand = new Random();
        ISet<Wall> toRemove = new ChainedHashSet<>();

        // creates edges with random weight
        for (Wall wall : maze.getWalls()) {
            double randDistance = rand.nextInt(200);
            wall.setDistance(randDistance);
        }

        // use the random weight edges to create a graph and run MST
        Graph<Room, Wall> graph = new Graph<>(maze.getRooms(), maze.getWalls());
        toRemove = graph.findMinimumSpanningTree();

        // reset the edges to the original distance
        for (Wall wall : maze.getWalls()) {
            wall.resetDistanceToOriginal();
        }

        // return to remove any wall that was part of the MST to create maze
        return toRemove;
    }
}
