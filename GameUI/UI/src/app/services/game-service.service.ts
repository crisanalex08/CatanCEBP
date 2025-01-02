import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Game, GameCreateDetails } from '../models/game-model';
import { UserService } from './user-service.service';
import { User } from '../models/user.model';
import { BehaviorSubject, tap } from 'rxjs';
import { tick } from '@angular/core/testing';

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

  games = new BehaviorSubject<Game[]>([]);
  games$ = this.games.asObservable();
  currentGame = new BehaviorSubject<Game>({} as Game);
  currentGame$ = this.currentGame.asObservable();

  checkGameId(gameId: string) {
    var request_url = this.url + '/api/games/' + gameId;
    return this.http.get(request_url);
  }

  list() {
    var request_url = this.url + '/api/games/list';
    return this.http.get<Game[]>(request_url).pipe(
      tap((res: Game[]) => {
        this.games.next([...res]);
      })
    );
  }

  create(gameDetails: GameCreateDetails) {
    var request_url = this.url + '/api/games/create';
    var request_body = {
      name: gameDetails.gameName,
      hostName: gameDetails.hostname,
      maxPlayers: gameDetails.maxPlayers,
    }
    return this.http.post<Game>(request_url, request_body).pipe(
      tap((res: Game) => {
        const games = this.games.value;
        games.push(res);
        this.games.next([...games]);
        this.currentGame.next(res);
      })
    );
  }

  joinGame(gameId: number,name: string) {
    var request_url = this.url + '/api/games/' + gameId + '/join';
    console.log('Joining game:', request_url);
    var request_body = {
      playerName: name
    }
    return this.http.post(request_url, request_body).pipe(
      tap((response: any) => {
        this.currentGame.next(response as Game);
      })
    );
  }

  leaveGame(gameId: number, username: string) {
    var request_url = this.url + '/api/games/' + gameId + '/leave';
    console.log('Leaving game:', request_url);
    var request_body = {
      playerName: username
    }
    return this.http.put(request_url, request_body).pipe(
      tap((response: any) => {
        this.currentGame.next({} as Game);
      })
    );
  }
  
  getGameInfo(gameId: number) {
    var request_url = this.url + '/api/games/' + gameId;
    console.log('Requesting game info:', request_url);
    return this.http.get(request_url);
  }
  
}
