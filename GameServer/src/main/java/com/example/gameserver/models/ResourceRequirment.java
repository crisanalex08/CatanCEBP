package com.example.gameserver.models;

import com.example.gameserver.enums.ResourceType;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ResourceRequirment {
    private ResourceType resourceType;
    private int amountRequired;
}
