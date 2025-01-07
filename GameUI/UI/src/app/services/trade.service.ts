import { Injectable } from '@angular/core';
import { MerchantTrade, Trade } from '../models/trade-model';
import { ResourceType } from '../models/resource-model';
import { HttpClient } from '@angular/common/http';
import { ConfigService } from './config.service';

@Injectable({
  providedIn: 'root'
})
export class TradeService {

  trades : Trade[] = [];

  constructor(private http: HttpClient,
    private config: ConfigService
  ) { }

  createMerchantTrade(request: string, offer: string, gameId: number, playerId: number){
    let trade: MerchantTrade = {
      gameId: gameId,
      playerId: playerId,
      offer: offer,
      request: request
    }

    // let request_body = JSON.stringify(trade);
    // let test_body = {
    //   gameId : 2002,
    //   playerId : 2102,
    //   offer : "CLAY",
    //   request : "WOOD"
    // }
    return this.http.post(this.config.serverUrl + '/api/games/' + gameId + '/trades/merchant-trade', trade);
  }
}
