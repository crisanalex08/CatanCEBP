import { User } from "./user-model";

export interface GameSettings {
    maxPlayers: number;
    currentPlayersCount: number;
}

export interface Game {
    id: number;
    name: string;
    hostId: number;
    players: User[];
    status: string;
    settings: GameSettings;
}

export interface GameCreateDetails{
    hostname: string;
    gameName: string;
    maxPlayers: number;
}


export interface PlayerResources {
    id: number;
    gameId: number;
    playerId: number;
    quantities: {
        WHEAT: number;
        GOLD: number;
        WOOD: number;
        STONE: number;
        CLAY: number;
        SHEEP: number;
    };
    createdAt: string;
    updatedAt: string;
    }


