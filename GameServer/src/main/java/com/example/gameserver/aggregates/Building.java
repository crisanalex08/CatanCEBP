package com.example.gameserver.aggregates;

import com.example.gameserver.enums.BuildingType;
import lombok.Data;

@Data
public class Building {
    private String id;
    private BuildingType type;
    private int level;
    private Position position;
    private Resources resources;

    public Building(String id, BuildingType type, int level, Position position, Resources resources) {
        this.id = id;
        this.type = type;
        this.level = level;
        this.position = position;
        this.resources = resources;
    }

    public String getId() {
        return id;
    }

    public BuildingType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public Position getPosition() {
        return position;
    }

    public Resources getResources() {
        return resources;
    }

    public void upgrade() {
        level++;
    }
}
