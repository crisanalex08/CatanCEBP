import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Game, GameCreateDetails } from '../models/game-model';
import { UserService } from './user-service.service';
import { User } from '../models/user.model';
import { BehaviorSubject, tap } from 'rxjs';
import { Building, BuildingSpot, BuildingType } from '../models/building-model';
import { GameService } from './game-service.service';
import { PlayersColor } from '../enums/PlayersColor';
import { GamePlayService } from './gameplay-service';

@Injectable({
  providedIn: 'root',
})
export class GameBoardService {
  buildings: Building[] = [];
  private readonly validSpots = [
    //ORANGE
    { x: 39, y: 80, radius: 10, color: 'ORANGE' }, // Bottom left corner (Water,Stone) png
    { x: 58, y: 78, radius: 10, color: 'ORANGE' }, // Bottom right corner (Clay) png
    { x: 83, y: 65, radius: 10, color: 'ORANGE' }, // Middle-Down right corner (Ore) png
    //BLUE
    { x: 18, y: 63, radius: 10, color: 'BLUE' }, // Middle-Down left corner (Stone) png
    { x: 39, y: 52, radius: 10, color: 'BLUE' }, // Middle left (Wood,Stone) png
    { x: 18, y: 34, radius: 10, color: 'BLUE' }, // Middle-Up left corner (Wood) png
    //YELLOW
    { x: 59, y: 50, radius: 10, color: 'YELLOW' }, // Middle right (Clay,Stone) png
    { x: 58, y: 36, radius: 10, color: 'YELLOW' }, // Middle-Up right corner (Wood,Stone) png
    { x: 86, y: 27, radius: 10, color: 'YELLOW' }, // Top right corner (Water,Stone) png
    //RED
    { x: 41, y: 23, radius: 10, color: 'RED' }, // Top left corner (Wood,Water) png
    { x: 19, y: 9, radius: 10, color: 'RED' }, // Extreme top left corner (Stone) png
    { x: 66, y: 9, radius: 10, color: 'RED' }, // Extreme top right corner (Stone,Wood) png
  ];
  constructor(
    private userService: UserService,
    private gameService: GameService,
    private gamePlayService: GamePlayService
  ) {
    this.gameService.currentGame$.subscribe((game) => {
      this.currentGame = game;
    });

    this.buildings = [
      {
        type: BuildingType.Settlement,
        image:
          'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/Settlement.png',
      },
      {
        type: BuildingType.Town,
        image:
          'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/Town.png',
      },
      {
        type: BuildingType.Castle,
        image:
          'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/Castle.png',
      },
    ];
  }

  currentGame!: Game;
  buildingSpots: BuildingSpot[] = [];
  playersColor = PlayersColor;

  $buildingSpots = new BehaviorSubject<BuildingSpot[]>(this.buildingSpots);
  currentPlayer: string | null = null;

  buildSettlement(playerName: string) {
    let playerIndex = -1;
    if (this.currentGame) {
      playerIndex = this.currentGame.players.findIndex(
        (player) => player.name === playerName
      );
    }

    if (playerIndex === -1) {
      return;
    }

    const availableSpot = this.validSpots.find(
      (spot) =>
        spot.color === this.playersColor[playerIndex] &&
        !this.buildingSpots.some((bs) => bs.x === spot.x && bs.y === spot.y)
    );

    if (availableSpot) {
      const playerId = this.currentGame.players[playerIndex].id;
      this.gamePlayService
        .buildSettlement(this.currentGame.id, playerId)
        .subscribe({
          next: () => {
            const newSpot: BuildingSpot = {
              playerId: playerId,
              playerIndex: playerIndex,
              x: availableSpot.x,
              y: availableSpot.y,
              playerColor: this.playersColor[playerIndex],
              building: this.buildings[0],
            };
            this.buildingSpots.push(newSpot);
            this.$buildingSpots.next(this.buildingSpots);
            console.log('Building spot added');
          },
          error: (error) => {
            console.error('Error building settlement:', error.getMessage());
          },
        });
    }
  }
}
