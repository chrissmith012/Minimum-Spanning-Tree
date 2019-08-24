package app;
import structures.Graph;

import java.io.IOException;
import java.util.ArrayList;

import structures.Arc;
import structures.PartialTree;
import structures.Vertex;
import structures.MinHeap;
/**
 * Class used for testing the MST
 */
public class Driver {

    public static void main(String[] args) {
        Graph graph = null;
        try {
            graph = new Graph("graph1.txt");
        }
        catch (IOException e) {
        	System.out.println("NOT HERE");
            e.printStackTrace();
        }
        //
        PartialTreeList partialTreeList = PartialTreeList.initialize(graph);        
        //
        ArrayList<Arc> arcArrayList = PartialTreeList.execute(partialTreeList);
        // System.out.println(arcArrayList.size()); 
       /* for(int i = 0; i < arcArrayList.size(); i++) {
            System.out.print(arcArrayList.get(i) + " ");
        }*/
    }
}
