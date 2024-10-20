package Classes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import Enums.TileType;
import Enums.PortType;

public class Board {
    //Maybe add initial data for the board

    private HashMap<String, Tile> tiles;
    // private HashMap<String, Port> ports;
    private HashMap<String, TileVertex> vertices;
    private HashMap<String, TileEdge> edges;
    private String robberTileId;
    private ArrayList<String> orderVertices;
    private ArrayList<String> orderedVertices;
    private Object vertexColumnsMap;

    public Board() {
        this.tiles = new HashMap<>();
        // this.ports = new HashMap<>();
        this.vertices = new HashMap<>();
        this.edges = new HashMap<>();
        this.robberTileId = null;

        this.orderedVertices = new ArrayList<String>(Arrays.asList(
            "1,2",
            "1,1",
            "1,3",
            "1,4",
            "1,5",
            "1,6",
            "1,7",
            "2,1",
            "2,2",
            "2,3",
            "2,4",
            "2,5",
            "2,6",
            "2,7",
            "2,8",
            "2,9",
            "3,1",
            "3,2",
            "3,3",
            "3,4",
            "3,5",
            "3,6",
            "3,7",
            "3,8",
            "3,9",
            "3,10",
            "3,11",
            "4,1",
            "4,2",
            "4,3",
            "4,4",
            "4,5",
            "4,6",
            "4,7",
            "4,8",
            "4,9",
            "4,10",
            "4,11",
            "5,1",
            "5,2",
            "5,3",
            "5,4",
            "5,5",
            "5,6",
            "5,7",
            "5,8",
            "5,9",
            "6,1",
            "6,2",
            "6,3",
            "6,4",
            "6,5",
            "6,6",
            "6,7"
        )
        );
        this.vertexColumnsMap = new HashMap<>(){{
            put(1, 7);
            put(2, 9);
            put(3, 11);
            put(4, 11);
            put(5, 9);
            put(6, 7);
        }};
        edgesFromVertices();
      
    }

    public void setTileVertex(TileVertex vertex) {
        
        vertices.put(vertex.getId(), vertex);
    }

    private void edgesFromVertices() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'edgesFromVertices'");
    }

}


            
        
        
    

  

