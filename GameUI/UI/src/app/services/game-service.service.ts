import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Game } from '../models/game-model';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(private http: HttpClient) { }

  url = 'http://localhost:8080';

  checkGameId(gameId: string) {
    var request_url = this.url + '/api/games/' + gameId;
    console.log('Requesting game data:', request_url);
    return this.http.get(request_url);
  }

  list() {
    var request_url = this.url + '/api/games/list';
    console.log('Requesting game list:', request_url);
    return this.http.get<Game[]>(request_url);
  }

  create(hostName: string, gameName: string, numberOfPlayers: number) {
    var request_url = this.url + '/api/games/create';
    var request_body = {
      name: gameName,
      hostName: hostName,
      settings: {
      numberOfPlayers: numberOfPlayers
      }
    }
    console.log('Creating game with data:', request_body);
    return this.http.post<Game>(request_url, request_body);
  }
}
