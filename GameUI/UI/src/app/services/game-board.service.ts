import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, catchError, Observable, of, tap } from 'rxjs';
import { Game } from '../models/game-model';
import { Building, BuildingSpot, BuildingType, ValidSpot } from '../models/building-model';
import { UserService } from './user-service.service';
import { GameService } from './game-service.service';
import { GamePlayService } from './gameplay-service';
import { ConfigService } from './config-service.service';
import { PlayersColor } from '../enums/PlayersColor';


@Injectable({
  providedIn: 'root',
})
export class GameBoardService {
  private readonly validSpots:ValidSpot[] = [
    // ORANGE
    { x: 39, y: 80, radius: 10, color: 'ORANGE' },
    { x: 58, y: 78, radius: 10, color: 'ORANGE' },
    { x: 83, y: 65, radius: 10, color: 'ORANGE' },
    // BLUE
    { x: 18, y: 63, radius: 10, color: 'BLUE' },
    { x: 39, y: 52, radius: 10, color: 'BLUE' },
    { x: 18, y: 34, radius: 10, color: 'BLUE' },
    // YELLOW
    { x: 59, y: 50, radius: 10, color: 'YELLOW' },
    { x: 58, y: 36, radius: 10, color: 'YELLOW' },
    { x: 86, y: 27, radius: 10, color: 'YELLOW' },
    // RED
    { x: 41, y: 23, radius: 10, color: 'RED' },
    { x: 19, y: 9, radius: 10, color: 'RED' },
    { x: 66, y: 9, radius: 10, color: 'RED' },
  ];

  private buildings: Building[] = [
    {
      type: BuildingType.Settlement,
      image: 'assets/images/Settlement.png',
    },
    {
      type: BuildingType.Town,
      image: 'assets/images/Town.png',
    },
    {
      type: BuildingType.Castle,
      image: 'assets/images/Castle.png',
    },
  ];

  private currentGame?: Game;
  private buildingSpots: BuildingSpot[] = [];
  private buildingSpots$ = new BehaviorSubject<BuildingSpot[]>(this.buildingSpots);

  constructor(
    private userService: UserService,
    private gameService: GameService,
    private gamePlayService: GamePlayService,
    private config: ConfigService
  ) {
    this.gameService.currentGame$.subscribe(game => {
      this.currentGame = game;
    });
  }

  getBuildingSpots(): Observable<BuildingSpot[]> {
    return this.buildingSpots$.asObservable();
  }

  buildSettlement(playerName: string, buildingExists = false): void {
    if (!this.currentGame) {
      console.error('No active game');
      return;
    }

    const playerIndex = this.currentGame.players.findIndex(
      player => player.name === playerName
    );

    if (playerIndex === -1) {
      console.error('Player not found');
      return;
    }

    const availableSpot = this.findAvailableSpot(playerIndex);
    if (!availableSpot) {
      console.error('No available spots');
      return;
    }

    if (!buildingExists) {
      this.handleNewSettlement(playerIndex, availableSpot);
    } else {
      this.handleExistingSettlement(playerIndex, availableSpot);
    }
  }

  private findAvailableSpot(playerIndex: number): ValidSpot | undefined {
    return this.validSpots.find(
      spot =>
        spot.color === PlayersColor[playerIndex] &&
        !this.buildingSpots.some(bs => bs.x === spot.x && bs.y === spot.y)
    );
  }

  private handleNewSettlement(playerIndex: number, spot: ValidSpot): void {
    const playerId = this.currentGame!.players[playerIndex].id;
    
    this.gamePlayService.buildSettlement(this.currentGame!.id, playerId)
      .pipe(
        catchError(error => {
          console.error('Error building settlement:', error);
          return of(null);
        })
      )
      .subscribe(response => {
        if (response) {
          this.addBuildingSpot(playerIndex, spot, playerId);
        }
      });
  }

  private handleExistingSettlement(playerIndex: number, spot: ValidSpot): void {
    const playerId = this.currentGame!.players[playerIndex].id;
    this.addBuildingSpot(playerIndex, spot, playerId);
  }

  private addBuildingSpot(playerIndex: number, spot: ValidSpot, playerId: string): void {
    const newSpot: BuildingSpot = {
      buildingId: Date.now(), // Temporary ID until server provides one
      playerId,
      playerIndex,
      x: spot.x,
      y: spot.y,
      playerColor: PlayersColor[playerIndex],
      building: this.buildings[0],
    };

    this.buildingSpots.push(newSpot);
    this.buildingSpots$.next(this.buildingSpots);
  }

  updateAllBuildings(): void {
    if (!this.currentGame) return;

    this.gamePlayService.getAllBuildings(this.currentGame.id)
      .pipe(
        catchError(error => {
          console.error('Error fetching buildings:', error);
          return of([]);
        })
      )
      .subscribe(buildings => {
        this.syncBuildings(buildings);
      });
  }

  private syncBuildings(serverBuildings: any[]): void {
    // Remove buildings that no longer exist on server
    this.buildingSpots = this.buildingSpots.filter(spot =>
      serverBuildings.some(b => b.id === spot.buildingId)
    );

    // Add new buildings from server
    serverBuildings.forEach(building => {
      const exists = this.buildingSpots.some(spot => spot.buildingId === building.id);
      if (!exists) {
        const playerName = this.currentGame!.players.find(
          player => player.id === building.playerId
        )?.name;
        
        if (playerName) {
          this.buildSettlement(playerName, true);
        }
      }
    });

    this.buildingSpots$.next(this.buildingSpots);
  }
}
