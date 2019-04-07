/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gnc_test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Dimension;
import java.awt.BasicStroke;

import org.openide.util.Exceptions;

/**
 *
 * @author Hello
 */
public class DPQ {

    public static int[][] matrix;
    static int edgeCount_Directed = 0;

    JFrame frame = new JFrame("Permuatations And Combinations");

    static class MyNode implements Comparable<MyNode> {

        //static int edgeCount = 0;   // This works with the inner MyEdge class
        final String value;
        String weight = "INF";
        Edge[] adjacencies;
        double shortestDistance = Double.POSITIVE_INFINITY;
        MyNode parent;

        public MyNode(String val) {
            this.value = val;
        }

        public void set_Node_Weight(String w) {
            this.weight = w;
        }

        public String Node_Weight() {
            return weight;
        }

        public String Node_Property() {
            String node_prop = value;
            return (node_prop);
        }

        public boolean equals(String i) {
            if (this.value.equals(i)) {
                return true;
            }
            return false;
        }

        public String toString() {
            return this.value;
        }

        public int compareTo(MyNode other) {
            return Double.compare(shortestDistance, other.shortestDistance);
        }
    }

    static class Edge {

        public final MyNode target;
        public final double weight;
        String Label;
        int id;

        public Edge(MyNode targetNode, double weight, String Label) {
            this.id = edgeCount_Directed++;
            this.target = targetNode;
            this.weight = weight;
            this.Label = Label;
        }

        public Edge(double weight, String Label) {
            this.id = edgeCount_Directed++;
            this.target = null;
            this.weight = weight;
            this.Label = Label;
        }

        public String toString() {
            return "E" + id;
        }

        public String Link_Property() {
            String Link_prop = Label;
            return (Link_prop);
        }

        public String Link_Property_wt() {
            String Link_prop_wt = "" + weight;
            return (Link_prop_wt);
        }
    }

    public static List<MyNode> getShortestPathTo(MyNode target) {

        //trace path from target to source
        List<MyNode> path = new ArrayList<MyNode>();
        for (MyNode node = target; node != null; node = node.parent) {
            path.add(node);
        }

        //reverse the order such that it will be from source to target
        Collections.reverse(path);

        return path;
    }

    public static void computePaths(MyNode source) {
        source.shortestDistance = 0;

        //implement a priority queue
        PriorityQueue<MyNode> queue = new PriorityQueue<MyNode>();
        queue.add(source);

        while (!queue.isEmpty()) {
            MyNode u = queue.poll();

            /*visit the adjacencies, starting from 
			the nearest node(smallest shortestDistance)*/
            for (Edge e : u.adjacencies) {

                MyNode v = e.target;
                double weight = e.weight;

                //relax(u,v,weight)
                double distanceFromU = u.shortestDistance + weight;
                if (distanceFromU < v.shortestDistance) {

                    /*remove v from queue for updating 
					the shortestDistance value*/
                    queue.remove(v);
                    v.shortestDistance = distanceFromU;
                    v.parent = u;
                    queue.add(v);

                }
            }
        }
    }

    Graph<MyNode, Edge> g = (Graph<MyNode, Edge>) new DirectedSparseGraph<DPQ.MyNode, DPQ.Edge>();

    public void draw_Path(LinkedList<String> Source_vertex, LinkedList<String> Target_vertex) {
        CircleLayout<MyNode, Edge> layout1 = new CircleLayout<MyNode, Edge>(g);
        layout1.setSize(new Dimension(600, 600));
        BasicVisualizationServer<MyNode, Edge> viz = new BasicVisualizationServer<MyNode, Edge>(layout1);
        viz.setPreferredSize(new Dimension(600, 600));
        frame.getContentPane().add(viz);
        Transformer<Edge, Paint> colorTransformer = new Transformer<Edge, Paint>() {
            public Paint transform(Edge e) {
                final MyNode s = g.getSource(e);
                final MyNode d = g.getDest(e);
                int i = 0;
                if (s.equals(Source_vertex.get(i)) && d.equals(Target_vertex.get(i))) {
                    return Color.GREEN;
                }
                return Color.BLACK;

            }
        };
        viz.getRenderContext().setArrowFillPaintTransformer(colorTransformer);
        viz.getRenderContext().setArrowDrawPaintTransformer(colorTransformer);
        viz.getRenderContext().setEdgeDrawPaintTransformer(colorTransformer);

        frame.getContentPane().add(viz);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void Visualize_Directed_Graph(LinkedList<String> Distinct_nodes, LinkedList<String> source_vertex, LinkedList<String> target_vertex, LinkedList<Double> Edge_Weight, LinkedList<String> Edge_Label) {
        //CREATING weighted directed graph
        //create node objects

        LinkedList<MyNode> Graph_Nodes_Only = new LinkedList<DPQ.MyNode>();
        Hashtable<String, MyNode> Graph_Nodes = new Hashtable<String, DPQ.MyNode>();
        LinkedList<MyNode> Source_Node = new LinkedList<DPQ.MyNode>();
        LinkedList<MyNode> Target_Node = new LinkedList<DPQ.MyNode>();

        //LinkedList<Edge> Graph_Links = new LinkedList<Graph_Algos.Edge>();
        //create graph nodes
        for (int i = 0; i < Distinct_nodes.size(); i++) {
            String node_name = Distinct_nodes.get(i);
            MyNode data = new MyNode(node_name);
            Graph_Nodes.put(node_name, data);
            Graph_Nodes_Only.add(data);
        }
        //Now convert all source and target nodes into objects
        for (int t = 0; t < source_vertex.size(); t++) {
            Source_Node.add(Graph_Nodes.get(source_vertex.get(t)));
            Target_Node.add(Graph_Nodes.get(target_vertex.get(t)));
        }
        //Now add nodes and edges to the graph

        for (int i = 0; i < source_vertex.size(); i++) {
            g.addEdge(new Edge(Edge_Weight.get(i), Edge_Label.get(i)), Source_Node.get(i), Target_Node.get(i), EdgeType.DIRECTED);
            CircleLayout<MyNode, Edge> layout1 = new CircleLayout<MyNode, Edge>(g);
            layout1.setSize(new Dimension(600, 600));
            BasicVisualizationServer<MyNode, Edge> viz = new BasicVisualizationServer<MyNode, Edge>(layout1);
            viz.setPreferredSize(new Dimension(600, 600));
            frame.getContentPane().add(viz);

            Transformer<MyNode, Paint> vertexColor = new Transformer<MyNode, Paint>() {
                public Paint transform(MyNode vertex) {
                    return Color.BLUE;
                }
            };

            Transformer<MyNode, String> vertexLabelTransformer = new Transformer<MyNode, String>() {
                public String transform(MyNode vertex) {
                    return (String) vertex.Node_Property() + " = " + vertex.Node_Weight();
                }
            };

            Transformer<Edge, Paint> colorTransformer = new Transformer<Edge, Paint>() {
                public Paint transform(Edge edge) {
                    return Color.BLACK;
                }
            };

            Transformer<Edge, Stroke> edgeStroke = new Transformer<Edge, Stroke>() {
                public Stroke transform(Edge edge) {
                    return new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
                }
            };

            Transformer<Edge, Paint> edgePaint = new Transformer<Edge, Paint>() {
                public Paint transform(Edge edge) {
                    return Color.BLACK;
                }
            };

            Transformer<Edge, String> edgeLabelTransformer = new Transformer<Edge, String>() {
                public String transform(Edge edge) {
                    return "[ " + edge.Link_Property() + " ]: Wt = " + edge.Link_Property_wt();
                }
            };

            //EDGE SETUP
            viz.getRenderContext()
                    .setEdgeLabelTransformer(edgeLabelTransformer);
            viz.getRenderContext()
                    .setEdgeDrawPaintTransformer(edgePaint);
            viz.getRenderContext()
                    .setEdgeStrokeTransformer(edgeStroke);
            //ARROW SETUP
            viz.getRenderContext()
                    .setArrowFillPaintTransformer(colorTransformer);
            viz.getRenderContext()
                    .setArrowDrawPaintTransformer(colorTransformer);

            //VERTEX SETUP
            viz.getRenderContext()
                    .setVertexLabelTransformer(vertexLabelTransformer);
            viz.getRenderContext()
                    .setVertexFillPaintTransformer(vertexColor);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.getContentPane().add(viz);
            frame.pack();

            frame.setVisible(true);
            //SLEEP FOR 1000 MILISECS.
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

    }

    public static void main(String[] args) throws IOException {

        LinkedList<String> Distinct_Vertex = new LinkedList<String>();//used to enter vertexes
        LinkedList<String> Source_Vertex = new LinkedList<String>();
        LinkedList<String> Target_Vertex = new LinkedList<String>();
        LinkedList<Double> Edge_Weight = new LinkedList<Double>();//used to enter edge weight
        LinkedList<String> Edge_Label = new LinkedList<String>(); //used to enter edge levels
        DPQ pr = new DPQ();

        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Hello\\Documents\\NetBeansProjects\\GnC\\src\\Gnc_test\\Dijkstra.txt"));
        String[] nameArr = br.readLine().split(", ");
        MyNode[] nodeArr = new MyNode[nameArr.length];
        for (int i = 0; i < nameArr.length; i++) {
            nodeArr[i] = new MyNode(nameArr[i]);
            Distinct_Vertex.add(nameArr[i]);
        }
        int edge = Integer.parseInt(br.readLine());
        matrix = new int[edge][edge];
        String currLine;
        int j = 0;
        while ((currLine = br.readLine()) != null) {
            String[] str = currLine.split(" ");
            for (int i = 0; i < edge; i++) {
                matrix[j][i] = Integer.parseInt(str[i]);
            }
            j++;
        }
        for (int i = 0; i < edge; i++) {
            for (j = 0; j < edge; j++) {
                if (matrix[i][j] != 0) {
                    Source_Vertex.add(nameArr[i]);
                    Target_Vertex.add(nameArr[j]);
                    Edge_Weight.add((double) matrix[i][j]);
                    Edge_Label.add(nameArr[i] + " " + nameArr[j]);

                    nodeArr[i].adjacencies = new Edge[]{
                        new Edge(nodeArr[j], matrix[i][j], " "),};
                }
            }
        }
        computePaths(nodeArr[0]);
        pr.Visualize_Directed_Graph(Distinct_Vertex, Source_Vertex, Target_Vertex, Edge_Weight, Edge_Label);

        //print shortest paths
        for (MyNode n : nodeArr) {
            System.out.println("Distance to "
                    + n + ": " + n.shortestDistance);
            List<MyNode> path = getShortestPathTo(n);
            LinkedList<String> Source_Vertex_2 = new LinkedList<String>();
            LinkedList<String> Target_Vertex_2 = new LinkedList<String>();
            System.out.println("path arr = " + path.size());

            if (path.size() != 1 && path.size() != 0) {
                for (j = 0; j < path.size() - 1; j++) {
                    Source_Vertex_2.add(path.get(j).toString());
                    Target_Vertex_2.add(path.get(j + 1).toString());

                }
            } else {
                Source_Vertex_2.add(path.get(0).toString());
                Target_Vertex_2.add(path.get(0).toString());
            }
            System.out.println("Path: " + path);
            pr.draw_Path(Source_Vertex_2, Target_Vertex_2);

        }
    }
}
