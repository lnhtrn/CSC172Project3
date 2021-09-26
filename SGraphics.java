/*
 * Linh Tran ltran18@u.rochester.edu
 * Partner: Sungwon Yoon syoon19@u.rochester.edu
 * Lab Section: MW 9:00-10:15am
 * Assignment: Project 3
 */

//import packages as needed
import java.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

/**this class draws graphics of the graph made accordingly
 * by reading the txt file */

public class SGraphics extends JPanel {
    static int vertex;
    static LinkedList<Node> nodes;
    static LinkedList<Edge> edges;
    ArrayList<Node> newNodes=new ArrayList<Node>();
    public String txtFile;

    //sets size of panel
    public final static int defaultWidth=650;
    public final static int defaultHeight=650;

    // constructor
    public SGraphics() {
        vertex = 0;
        nodes = new LinkedList<>();
        edges = new LinkedList<>();
    }

    /**Node class*/
    class Node {
        int xCoord;
        int yCoord;
        int data;
        Node node;

        //constructors
        Node(){}

        //stores x+y coordinates of each node
        Node(int val, int x, int y){
            xCoord=x;
            yCoord=y;
            data=val;
        }

        //getters+setters
        public int getX() {
            return this.xCoord;
        }

        public int getY() {
            return this.yCoord;
        }

        void setX(int x) {
            xCoord=x;
        }

        void setY(int y) {
            yCoord=y;
        }

        public int getVal() {
            return this.data;
        }

        void setVal(int newVal) {
            data=newVal;
        }
    }

    /**Edge class*/
    class Edge {
        Node node1;
        Node node2;

        //constructor b/w nodes node1+node2
        Edge(Node n1, Node n2){
            node1=n1;
            node2=n2;
        }

        //getters
        public Node getN1() {
            return this.node1;
        }
        public Node getN2() {
            return this.node2;
        }
    }

    /**
     * method to draw circle/nodes w/ val in middle
     * @param graphics g
     * @param int x-coord
     * @param int y-coord
     * @param int diam of node
     * @param int val of node
     */
    public void drawOval(Graphics g, int x, int y, int diam, int i) {
        g.drawOval(x, y, diam, diam);
        g.setColor(Color.BLACK);
        g.drawString(Integer.toString(i), x+diam/2-5, y+diam/2+3);
    }

    /**paint method*/
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D gr=(Graphics2D)g;

        File f = new File(txtFile);
        Scanner scan;
        Random rng = new Random();
        int count;
        int x;
        int y;

        try {

            //scans txt file. builds graph
            scan = new Scanner(f);
            while(scan.hasNextInt()) {

                int node = scan.nextInt();

                //random x+y coordinates
                if (node < 10) {
                    x = x = rng.nextInt(250);
                    y = rng.nextInt(250);
                } else if (node < 40) {
                    x = rng.nextInt(400);
                    y = rng.nextInt(400);
                } else {
                    x = rng.nextInt(550);
                    y = rng.nextInt(550);
                }

                //initializes new node
                Node n = new Node(node, x, y);
                newNodes.add(n);

                //loop through nodes list. if node is alr there, dont add to nodes list
                if(nodes.size()==0) {
                    //0th index
                    nodes.add(n);
                } else if(nodes.size()==1) {
                    //1st index
                    if(nodes.get(0).getVal()!=node) {
                        nodes.add(n);
                    }
                } else {
                    count=0;
                    for(int i=0; i<nodes.size(); i++) {
                        if(n.getVal()==nodes.get(i).getVal()) {
                            count=1;
                            break;
                        }
                    }

                    if(count==0) {
                        nodes.add(n);
                    }
                }
            }

            //loops through newNodes list.
            //if 2 nodes are the same -- for instance, (0,1), (0,2)
            //then set coordinates to be the same as prev one
            //(so the 2nd 0 would have the same X,Y coords as 1st 0)
            for(int i=0; i<newNodes.size(); i++) {
                for(int j=0; j<newNodes.size(); j++) {
                    if(newNodes.get(i).getVal()==newNodes.get(j).getVal()) {
                        newNodes.get(j).setX(newNodes.get(i).getX());
                        newNodes.get(j).setY(newNodes.get(i).getY());
                    }
                }
            }

            //loops through newNodes list. adds edges
            for(int i=0; i<newNodes.size(); i+=2) {
                int k=i+1;
                if(k<newNodes.size()) {
                    Edge e = new Edge(newNodes.get(i),newNodes.get(k));
                    edges.add(e);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found. Error: " + e.getMessage());
            e.printStackTrace();
        }

        //prints linkedlist values
//		for(int i=0; i<nodes.size(); i++) {
//			System.out.print(nodes.get(i).getVal());
//		}

        //draws edges
        for(int j=0; j<edges.size(); j++) {
//			System.out.println("COORDS: " + edges.get(j).getN1().getVal() + ":"
//			+ edges.get(j).getN1().getX() + " " + edges.get(j).getN1().getY()
//			+ "\n" + edges.get(j).getN2().getVal() + ":"
//			+ edges.get(j).getN2().getX() + " " + edges.get(j).getN2().getY());

            g.drawLine(edges.get(j).getN1().getX()+20, edges.get(j).getN1().getY()+20,
                    edges.get(j).getN2().getX()+20, edges.get(j).getN2().getY()+20);
        }

        //draws nodes
        for(int i=0; i<nodes.size(); i++) {
            g.setColor(Color.MAGENTA);
            g.fillOval(nodes.get(i).getX(), nodes.get(i).getY(), 30, 30);
            gr.setStroke(new BasicStroke(1));
            gr.setColor(Color.BLACK);
            drawOval(g,nodes.get(i).getX(),nodes.get(i).getY(),30,nodes.get(i).getVal());
//			System.out.println("X: " +nodes.get(i).getX()
//					+ "Y: "+nodes.get(i).getY());
        }
    }
}
