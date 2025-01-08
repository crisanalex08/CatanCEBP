import { from } from "rxjs";
import { Resource, ResourceType } from "./resource-model";

export interface Trade{
    tradeId: number;
    gameId: number;
    fromPlayerId: number;
    toPlayerId: number;
    offer: ResourceType;
    request: ResourceType;
    status: TradeStatus;
}

export interface MerchantTrade{
    gameId: number;
    playerId: number;
    offer: ResourceType;
    request: ResourceType;
}

export enum TradeStatus{
    ACTIVE = 'ACTIVE',
    COMPLETED = 'COMPLETED',
    CANCELLED = 'CANCELLED'
}