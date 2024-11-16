package com.example.gameserver.models;

import com.example.gameserver.enums.BuildingType;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable 
public class BuildingRequirment {
    private BuildingType buildingTypeRequired;
}
