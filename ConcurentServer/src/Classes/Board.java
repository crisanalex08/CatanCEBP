package Classes;

import java.util.Dictionary;
import java.lang.Math;

import Enums.TileType;

public class Board {
    TileType[] tile_layout;
    int[] number_layout;
    int[] ports[];
    int robber;

    public Board() {
        this.initMockBoard();
    }

    private void initMockBoard() {
        this.robber = -1;
        this.tile_layout = new TileType[]{
                TileType.WOOD,
                TileType.WOOD,
                TileType.WOOD,
                TileType.WOOD,
                TileType.CLAY,
                TileType.CLAY,
                TileType.CLAY,
                TileType.STONE,
                TileType.STONE,
                TileType.STONE,
                TileType.SHEEP,
                TileType.SHEEP,
                TileType.SHEEP,
                TileType.WHEAT,
                TileType.WHEAT,
                TileType.WHEAT,
                TileType.WHEAT,
                TileType.SHEEP,
                TileType.SAND
        };

        this.number_layout = new int[]{
            2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12
        };

    }
}