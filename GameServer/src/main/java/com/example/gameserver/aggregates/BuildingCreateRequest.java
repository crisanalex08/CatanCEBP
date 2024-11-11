package com.example.gameserver.aggregates;

import com.example.gameserver.enums.BuildingType;
import lombok.Data;

@Data
public class BuildingCreateRequest {
    private BuildingType type;
    private int x;
    private int y;
}
