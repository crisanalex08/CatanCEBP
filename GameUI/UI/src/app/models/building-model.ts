export enum BuildingType {
    Settlement,
    Town,
    Castle
}

export interface Building {
    type: BuildingType;
    image: string;
}

export interface BuildingSpot{
    playerId: string | null;
    playerIndex: number;
    x: number;
    y: number;
    building: Building | null;
    playerColor: string;
}