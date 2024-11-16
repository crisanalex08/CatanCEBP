package com.example.gameserver.models;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Requirments {
    @ElementCollection
    private List<ResourceRequirment> resourceRequirments;
    @ElementCollection
    private List<BuildingRequirment> buildingRequirments;
    
    
}
