export interface Resource{
    responseType: ResourceType;
    quantity: number;
}

export enum ResourceType{
    WHEAT = 'WHEAT',
    GOLD = 'GOLD',
    WOOD = 'WOOD',
    STONE = 'STONE',
    CLAY = 'CLAY',
    SHEEP = 'SHEEP'
}