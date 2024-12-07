import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Game, GameCreateDetails } from '../models/game-model';
import { UserService } from './user-service.service';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(
    private http: HttpClient,
    private userService: UserService
  ) { }

  url = 'http://localhost:8080';
  user: User | undefined;
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

  create(gameDetails: GameCreateDetails) {
    var request_url = this.url + '/api/games/create';
    var request_body = {
      name: gameDetails.gameName,
      hostName: gameDetails.hostname,
      settings: {
        maxPlayers: gameDetails.maxPlayers,
      currentPlayersCount: 1
      }
    }
    console.log('Creating game with data:', request_body);
    return this.http.post<Game>(request_url, request_body);
  }

  joinGame(gameId: number, username: string) {
    this.userService.checkUsername(username).subscribe({
      next: response => {
        this.user = response as User;
        if (!this.user) {
          console.log("User not found");
          return;
        }
        console.log("uSer:", this.user);
      },
      error: error => {
        console.error(error);
        return;
      }
    });

    var request_url = this.url + '/api/games/' + gameId + '/join';
    console.log('Joining game:', request_url);
    var request_body = {
      playerId: this.user.id
    }
    return this.http.post(request_url, request_body);
  }
}
