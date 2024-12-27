package com.example.gameserver.models;

import com.example.gameserver.enums.ResourceType;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Embeddable
public class ProductionData {

    @NotNull
    private ResourceType resourceType;

    @Min(1)
    @NotNull
    private int productionRate;
    
    @Min(1)
    @Max(6)
    private int diceValue;
}
