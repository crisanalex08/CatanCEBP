package com.example.gameserver.api.dto;

import com.example.gameserver.enums.BuildingType;
import lombok.Data;

@Data
public class BuildingCreateRequest {
    private BuildingType type;
}
