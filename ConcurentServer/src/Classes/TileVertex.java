package Classes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Enums.ConstructionTypeVertex;

public class TileVertex {
     private String id;
     private Integer row;
     private Integer column;
     private Board board;
     private Player owner;
     private ConstructionTypeVertex constructionType;
     private List<TileEdge> edges;

    
    public TileVertex(Integer row, Integer column, Player owner,ConstructionTypeVertex constructionType, Board board) {
        this.row = row;
        this.column = column;
        this.board = board;
        this.owner = owner;
        this.constructionType = constructionType;
        this.edges = new ArrayList<>();
        board.setTileVertex(this);
    }
    public String getId() {
        return id;
    }

}
