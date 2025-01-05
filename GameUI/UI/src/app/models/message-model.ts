export interface ChatMessage {
    gameId: number;
    sender: string;
    content: string;
    timestamp: Date;
    isSystem ?: boolean;
}