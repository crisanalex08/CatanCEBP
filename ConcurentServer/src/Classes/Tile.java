package Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Enums.TileType;
    

public class Tile {
     private String id;
     private Integer row;
     private Integer column;
     private TileType type;
     private int number;
     private List<TileEdge> edges;
     private List<TileVertex> vertices;


    public Tile(Integer row, Integer column, TileType type, int diceNumber, Board board) {
        this.id = row + "," + column;
        this.row = row;
        this.column = column;
        this.type = type;
        this.number = diceNumber;
        this.edges = new ArrayList<>();
        this.vertices = new ArrayList<>();
        setVertices(board);
        setEdges(board);
    }


    private void setEdges(Board board) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEdges'");
    }


    private void setVertices(Board board) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setVertices'");
    }
        
    


   


   
    




}
