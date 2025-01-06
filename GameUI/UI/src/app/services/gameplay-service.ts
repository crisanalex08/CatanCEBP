import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Game, GameCreateDetails } from '../models/game-model';
import { UserService } from './user-service.service';
import { User } from '../models/user.model';
import { BehaviorSubject, catchError, tap, throwError } from 'rxjs';
import { ConfigService } from './config-service.service';


@Injectable({
  providedIn: 'root'
})

export class GamePlayService{
    constructor(
        private http: HttpClient,
        private userService: UserService,
        private config: ConfigService
    ) { }

    url = this.config.serverUrl;
    user: User | undefined;

    private playerResources = new BehaviorSubject<any>(null);
    playerResources$ = this.playerResources.asObservable();

    private playerBuildings = new BehaviorSubject<any>(null);
    playerBuildings$ = this.playerBuildings.asObservable();

    private handleError(error: HttpErrorResponse) {
      let errorMessage = 'An error occurred';
      
      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = error.error.message;
      } else {
        // Server-side error
        errorMessage = error.error?.message || `Error Code: ${error.status}`;
      }
  
      console.error(errorMessage);
  
      return throwError(() => errorMessage);
    }
    //Add method for starting game & initializing resources for players
    startGame(gameId: number) {
      const request_url = `${this.url}/api/gameplay/${gameId}/start`;
      return this.http.post(request_url, {}).pipe(
        tap(() => {
          console.log('Game started');
        }),
        catchError((error) => {
          console.error('Error:', error);
          return error} )
      );
    }

    // Add method to fetch resources
    getPlayerResources(gameId: number, playerId: string) {
      const request_url = `${this.url}/api/games/${gameId}/resources/${playerId}`;
      return this.http.get(request_url).pipe(
        tap((resources: any) => {
          this.playerResources.next(resources);
          console.log('Player resources:', resources);
        }),
        catchError((error) => {
          console.error('Error:', error);
          return error
        })
      );
    }
    //Add method for rolling dice
    rollDice(gameId: number, playerId: string) {
      const request_url = `${this.url}/api/gameplay/${gameId}/roll/${playerId}`;
      return this.http.post(request_url, {}).pipe(
      tap(() => {
          console.log('Dice rolled');

        }),
        catchError((error) => {
          console.error('Error:', error);
          return error
        })
      );
    }
    buildSettlement(gameId: number, playerId: string)
    {
      const request_url = `${this.url}/api/gameplay/${gameId}/construct/${playerId}`;
      return this.http.post(request_url, {}).pipe(
        tap(() => {
          
          console.log('Settlement built');
        }),
        catchError(this.handleError.bind(this))
      );
    }

    getAllBuildings(gameId: number) {
      const request_url = `${this.url}/api/gameplay/${gameId}/buildings`;
      return this.http.get(request_url).pipe(
        tap((buildings: any) => {
          this.playerBuildings.next(buildings);
          console.log('Player buildings:', buildings);
        }),
        catchError((error) => {
          console.error('Error:', error);
          return error
        })
      );  

    }

  }