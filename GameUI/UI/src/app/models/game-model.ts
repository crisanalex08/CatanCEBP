export interface GameSettings {
    maxPlayers: number;
}

export interface Game {
    id: number;
    hostId: number;
    players: any[];
    status: string;
    settings: GameSettings;
}