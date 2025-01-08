export enum BuildingType {
    Settlement,
    Town,
    Castle
}
    export interface ServerBuilding {
        id: number;
        playerId: number;
        type: string;
        x: number;
        y: number;
        production: ProductionData[];
    }
  export interface ProductionData {
    resourceType: 'WOOD' | 'CLAY' | 'STONE' | 'SHEEP' | 'WHEAT' | 'GOLD';
    productionRate: number;
    diceValue: number;
}
export interface Building {
    type: BuildingType;
    image: string;
}

export interface BuildingSpot{
    buildingId: number,
    playerId: string | null;
    playerIndex: number;
    x: number;
    y: number;
    building: Building | null;
    playerColor: string;
}

export interface ValidSpot {
    x: number;
    y: number;
    radius: number;
    color: string;
}
