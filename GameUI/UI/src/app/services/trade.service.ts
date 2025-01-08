import { Injectable } from '@angular/core';
import { MerchantTrade, Trade } from '../models/trade-model';
import { ResourceType } from '../models/resource-model';
import { HttpClient } from '@angular/common/http';
import { ConfigService } from './config.service';
import { BehaviorSubject, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TradeService {

  // trades : Trade[] = [];
  trades = new BehaviorSubject<Trade[]>([]);
  trades$ = this.trades.asObservable();

  constructor(private http: HttpClient,
    private config: ConfigService
  ) { }

  createMerchantTrade(request: ResourceType, offer: ResourceType, gameId: number, playerId: number) {
    let trade: MerchantTrade = {
      gameId: gameId,
      playerId: playerId,
      offer: offer,
      request: request
    }

    return this.http.post(this.config.serverUrl + '/api/games/' + gameId + '/trades/merchant-trade', trade);
  }

  createPlayerTrade(request: ResourceType, offer: ResourceType, gameId: number, playerId: number) {
    let trade: MerchantTrade = {
      gameId: gameId,
      playerId: playerId,
      offer: offer,
      request: request
    }

    return this.http.post(this.config.serverUrl + '/api/games/' + gameId + '/trades/player-trade', trade);
  }

  getMyActiveTrades(gameId: number, playerId: number) {
    return this.http.get<Trade[]>(this.config.serverUrl + "/api/games/" + gameId + "/trades/" + playerId + "/trades").pipe(
      tap((res: Trade[]) => {
        console.log("Trades", res);
        this.trades.next([...res]);
      })
    );
  }

  acceptTrade(gameId: number, tradeId: number) {
    return this.http.post(this.config.serverUrl + "/api/games/" + gameId + "/trades/accept-trade/" + tradeId, {});
  }
}
