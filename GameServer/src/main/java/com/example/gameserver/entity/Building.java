package com.example.gameserver.entity;

import java.util.ArrayList;
import java.util.List;


import com.example.gameserver.enums.BuildingStatus;
import com.example.gameserver.enums.BuildingType;
import com.example.gameserver.models.ProductionData;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data

public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @Enumerated(EnumType.STRING)
    private BuildingType type;
    @Enumerated(EnumType.STRING)
    private BuildingStatus status;
    @ElementCollection
    private List<ProductionData> production = new ArrayList<>();
    @ElementCollection
    private Requriments requriments;

}
