/*
`* Linh Tran ltran18@u.rochester.edu
 * Partner: Sungwon Yoon syoon19@u.rochester.edu
 * Lab Section: MW 9:00-10:15am
 * Assignment: Project 3
 */

//import packages as needed
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.*;

/**this class reads a txt file, builds a graph from that file,
 * & removes nodes as needed from the graph. */

public class StopContagion {

    public static Graph graph = new Graph();

    /**Graph class -- sets up graph*/
    static class Graph {
        int vertex;
        int degree;
        ArrayList<LinkedList<Integer>> nodes;
        ArrayList<Integer> degreeList = new ArrayList<Integer>();

        //constructor
        public Graph() {
            vertex = 0;
            degree=0;
            nodes = new ArrayList<>();
            nodes.add(new LinkedList<>());
        }

        /**
         * adds new LinkedList<>() to nodes list
         * @param int size
         */
        public void addUp(int size) {
            while (nodes.size() < size+1) {
                nodes.add(new LinkedList<>());
                vertex++;
            }
        }

        /**
         * forms adjacency list; contains connected vertices
         * @param int node
         * @param int neighbor
         */
        public void addNeighbor(int node, int neighbor) {
            if (nodes.size() < node+1) addUp(node);
            nodes.get(node).addLast(neighbor);
            if (nodes.size() < neighbor+1) addUp(neighbor);
            nodes.get(neighbor).addLast(node);
        }

        /**
         * method removes node from graph
         * @param int node
         * @return updated list w/o node
         */
        public LinkedList<Integer> removeNode(int node) {
            for (int neighbor : nodes.get(node))
                nodes.get(neighbor).remove((Integer) node);
            LinkedList<Integer> neighbors = (LinkedList<Integer>) nodes.get(node).clone();
            nodes.get(node).clear();
            return neighbors;
        }

    }

    /**
     * method to calculate collective influence
     * @param node
     * @param radius
     * @return
     */
    public static int collInt(int node, int radius) {
        if (radius == 0)
            return 0;

        // create visiting queue
        Queue<Integer> q = new LinkedList<>();
        boolean[] visited = new boolean[graph.vertex+1];
        for (boolean visit : visited)
            visit = false;
        q.add(node);
        visited[node] = true;
        int sum = 0;

        // analyze visiting queue
        int deq = 1;
        while (!q.isEmpty() & radius > 0) {
            // count number of nodes needed to be dequeued for next round
            int nextdeq = 0;
            for (int i = deq; i > 0; i--) {
                int curr = q.poll();
                // add current node's neighbors to the queue for next round
                for (int neighbor : graph.nodes.get(curr)) {
                    // add unvisited nodes to visiting list
                    if (!visited[neighbor]) {
                        q.add(neighbor);
                        visited[neighbor] = true;
                        sum += graph.nodes.get(neighbor).size() - 1;
                        nextdeq++;
                    }
                }
            }
            deq = nextdeq;
            radius--; // end the count when the radius limit is reached
        }

        // calculate collective influence
        sum = sum * (graph.nodes.get(node).size() - 1);

        return sum;
    }

    /**
     * method to calculate exact collective influence
     * @param node
     * @param radius
     * @return
     */
    public static int collIntExact(int node, int radius) {
        if (radius == 0)
            return 0;

        /**BFS ALGORITHM*/

        // create visiting queue
        Queue<Integer> q = new LinkedList<>(graph.nodes.get(node));
        boolean[] visited = new boolean[graph.vertex+1];
        for (boolean visit : visited) {
            visit = false;
        }
        visited[node] = true;
        for (int neighbor : graph.nodes.get(node))
            visited[neighbor] = true;
        int sum = 0;

        // analyze visiting queue
        int deq = graph.nodes.get(node).size();
        //System.out.println("Root node has " + deq + " neighbors");
        while (radius > 1) {
            // count number of nodes needed to be dequeued for next round
            int nextdeq = 0;
            for (int i = deq; i > 0; i--) {
                int curr = q.poll();
                // add current node's neighbors to the queue for next round
                for (int neighbor : graph.nodes.get(curr)) {
                    // add unvisited nodes to visiting list
                    if (!visited[neighbor]) {
                        q.add(neighbor);
                        visited[neighbor] = true;
                        //System.out.println(curr + " add " + neighbor);
                        nextdeq++;
                    }
                }
            }
            deq = nextdeq;
            radius--; // end the count when the radius limit is reached
        }

        while (!q.isEmpty()) {
            int neighbor = q.poll();
            sum += graph.nodes.get(neighbor).size() - 1;
            //System.out.println(neighbor + " coll " + sum);
        }

        // calculate collective influence
        sum = sum * (graph.nodes.get(node).size() - 1);

        return sum;
    }

    /**
     * method to print graph
     */
    public static void printGraph() {
        for (int i = 1; i <= graph.vertex; i++) {
            System.out.print(i + " : ");
            for (int neighbor : graph.nodes.get(i))
                System.out.print(neighbor + " ");
            System.out.println();
        }
        System.out.println();
    }

    /**main method*/
    public static void main(String[] args) throws IOException {

        // draws graphics. calls RunImage() class
        if(args[0].equals("-t")) {
            RunImage.s.txtFile=args[args.length-1];
            new RunImage().setVisible(true);
        }

        // get number of nodes to remove
        String numOfNodesStr = args[args.length-2];
        int numOfNodes = Integer.parseInt(numOfNodesStr);
        //System.out.println("num of nodes: "+numOfNodes);

        // get file name
        String file = args[args.length-1];
        Scanner scanner = new Scanner(Paths.get(file));

        // check degree & radius
        boolean degree = false;
        int rad = 2;
        boolean execute = true;

        for (int i = 0; i < args.length-2; i++) {
            if (args[i].equals("-d")) {
                degree = true;
            }
            if (args[i].equals("-r")) {
                if (args.length == i + 4) {
                    rad = Integer.parseInt(args[i + 1]);
                    //System.out.println("radius " + rad);
                }
            }
        }

        if (execute) {
            // build graph
            do {
                int node = scanner.nextInt();
                int neighbor = scanner.nextInt();
                graph.addNeighbor(node, neighbor);
                //System.out.println(node + " " + neighbor);
            } while (scanner.hasNextInt());

            // test methods
            int[] coll = new int[graph.vertex+1];

            for (int j = numOfNodes; j > 0; j--) {
                // calculate collective influence & find maximum influence
                int maxInd = 0;
                for (int i = 1; i <= graph.vertex; i++) {
                    // check degree or collective influence
                    if (degree) coll[i] = graph.nodes.get(i).size();
                    else coll[i] = collIntExact(i, rad);

                    // find maximum
                    if (coll[i] > coll[maxInd])
                        maxInd = i;
                }

                // remove maximum influence node
                System.out.println(maxInd + " " + coll[maxInd]);
                graph.removeNode(maxInd);
            }

        } else {
            System.out.println("Problem when reading input");
        }

        scanner.close();
    }
}

/** this class calls the SGraphics class and has panel appear which
 * shows the graphics for the graph */

class RunImage extends JFrame {

    //calls SGraphics class
    static SGraphics s = new SGraphics();

    public RunImage() {
        this.setTitle("Graph");
        this.setSize(SGraphics.defaultWidth, SGraphics.defaultHeight);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        add(s);
    }
}