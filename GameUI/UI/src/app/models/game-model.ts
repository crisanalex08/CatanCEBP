export interface GameSettings {
    maxPlayers: number;
    currentPlayersCount: number;
}

export interface Game {
    id: number;
    name: string;
    hostId: number;
    players: any[];
    status: string;
    settings: GameSettings;
}

export interface GameCreateDetails{
    hostname: string;
    gameName: string;
    maxPlayers: number;
}