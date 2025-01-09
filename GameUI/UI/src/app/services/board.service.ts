import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, catchError, Observable, of, tap, map } from 'rxjs';
import { Game } from '../models/game-model';
import { Building, BuildingSpot, BuildingType, ValidSpot, ServerBuilding } from '../models/building-model';
import { UserService } from './user.service';
import { GameService } from './game-service.service';
import { GamePlayService } from './gameplay-service';
import { ConfigService } from './config.service';
import { PlayersColor } from '../enums/PlayersColor';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root',
})
export class GameBoardService {
  private readonly validSpots: ValidSpot[] = [
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
  private playerBuildings: ServerBuilding[] = [];
  private playerBuildings$ = new BehaviorSubject<ServerBuilding[]>(this.playerBuildings);

  constructor(
    private userService: UserService,
    private gameService: GameService,
    private messageService: MessageService,
    private gamePlayService: GamePlayService,
    private config: ConfigService
  ) {
    this.gameService.currentGame$.subscribe(game => {
      this.currentGame = game;
      if (game?.id) {
        this.updateAllBuildings();
      }
    });
  }

  buildSettlement(playerId: string): void {
    if (!this.currentGame) {
      console.error('No active game');
      return;
    }

    this.gamePlayService.buildSettlement(this.currentGame.id, playerId)
      .pipe(
        catchError(error => {
          console.error('Error building settlement:', error);
          this.messageService.add({ severity: 'error', summary: 'Error building settlement', detail: error} );
          return of(null);
        })
      )
      .subscribe(response => {
        if (response) {
          this.updateAllBuildings();
        }
      });
  }

  public upgradeBuilding(playerId: string, gameId: number, building: any) {
    if (!this.currentGame) {
      console.error('No active game');
      return;
    }

    this.gamePlayService.upgradeBuilding(this.currentGame.id, playerId, building)
      .pipe(
        catchError(error => {
          console.error('Error upgrading building:', error);
          return of(null);
        })
      )
      .subscribe(response => {
        if (response) {
          this.updateAllBuildings();
        }
      });
  }

  updateAllBuildings(): void {
    if (!this.currentGame) return;

    this.gamePlayService.getAllBuildings(this.currentGame.id)
      .subscribe(buildings => {
        this.playerBuildings = buildings;
        this.playerBuildings$.next(this.playerBuildings);
      });
  }

  getPlayerBuildings(): Observable<ServerBuilding[]> {
    return this.playerBuildings$.asObservable();
  }

  getBuildingsForDisplay(): Observable<ServerBuilding[]> {
    return this.playerBuildings$.asObservable().pipe(
      map(buildings => {
        // Group buildings by player ID to handle multiple buildings per player
        const buildingsByPlayer = new Map<number, ServerBuilding[]>();
        
        buildings.forEach(building => {
          if (!buildingsByPlayer.has(building.playerId)) {
            buildingsByPlayer.set(building.playerId, []);
          }
          buildingsByPlayer.get(building.playerId)!.push(building);
        });

        // Process each player's buildings and assign unique coordinates
        return buildings.map(building => {
          const playerColor = this.getPlayerColor(building.playerId);
          const playerSpots = this.validSpots.filter(spot => spot.color === playerColor);
          
          // Get all buildings for this player
          const playerBuildings = buildingsByPlayer.get(building.playerId) || [];
          // Find index of current building in player's building list
          const buildingIndex = playerBuildings.findIndex(b => b.id === building.id);
          
          // Get corresponding spot (use modulo in case there are more buildings than spots)
          const spot = playerSpots[buildingIndex % playerSpots.length];
          
          return {
            ...building,
            x: spot?.x ?? 0,
            y: spot?.y ?? 0
          };
        });
      })
    );
  }

  private getPlayerColor(playerId: number): string {
    const playerIndex = this.currentGame?.players.findIndex(
      p => parseInt(p.id) === playerId
    ) ?? -1;
    return PlayersColor[playerIndex] || '';
  }
}