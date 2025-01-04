import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Game, GameCreateDetails } from '../models/game-model';
import { UserService } from './user-service.service';
import { User } from '../models/user.model';
import { BehaviorSubject, tap } from 'rxjs';
import { Building, BuildingSpot, BuildingType } from '../models/building-model';
import { GameService } from './game-service.service';

@Injectable({
    providedIn: 'root'
})
export class GameBoardService {
    buildings: Building[] = [];
    constructor(
        private userService: UserService,
        private gameService: GameService
    ) {

        this.gameService.currentGame$.subscribe(game => {
            this.currentGame = game;
        });

        this.buildingSpots.push({
            playerId: null,
            playerIndex: 0,
            x: 691,
            y: 691,
            building: null
        });

        this.buildingSpots.push({
            playerId: null,
            playerIndex: 0,
            x: 830,
            y: 311,
            building: null
        });

        this.buildingSpots.push({
            playerId: null,
            playerIndex: 0,
            x: 690,
            y: 377,
            building: null
        });

        this.buildingSpots.push({
            playerId: null,
            playerIndex: 1,
            x: 1061,
            y: 184,
            building: null
        });

        this.buildingSpots.push({
            playerId: null,
            playerIndex: 1,
            x: 1239,
            y: 330,
            building: null
        });

        this.buildingSpots.push({
            playerId: null,
            playerIndex: 1,
            x: 1052,
            y: 372,
            building: null
        });
        this.buildingSpots.push({
            playerId: null,
            playerIndex: 2,
            x: 877,
            y: 517,
            building: null
        });

        this.buildingSpots.push({
            playerId: null,
            playerIndex: 2,
            x: 670,
            y: 626,
            building: null
        });

        this.buildingSpots.push({
            playerId: null,
            playerIndex: 2,
            x: 823,
            y: 748,
            building: null
        });
        this.buildingSpots.push({
            playerId: null,
            playerIndex: 3,
            x: 1029,
            y: 544,
            building: null
        });

        this.buildingSpots.push({
            playerId: null,
            playerIndex: 3,
            x: 1204,
            y: 646,
            building: null
        });

        this.buildingSpots.push({
            playerId: null,
            playerIndex: 3,
            x: 1022,
            y: 755,
            building: null
        });
        this.$buildingSpots.next(this.buildingSpots);

        this.buildings = [
            {
                type: BuildingType.Settlement,
                image: 'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/Settlement.png'
            },
            {
                type: BuildingType.Town,
                image: 'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/Town.png'
            },
            {
                type: BuildingType.Castle,
                image: 'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/Castle.png'
            }
        ]
    }

    currentGame: Game | undefined;
    buildingSpots: BuildingSpot[] = [];

    $buildingSpots = new BehaviorSubject<BuildingSpot[]>(this.buildingSpots);
    currentPlayer: string | null = null;

    buildSettlement(playerName: string) {
        let playerIndex = -1;
        if (this.currentGame) {
            playerIndex = this.currentGame.players.findIndex(player => player.name === playerName);
        }

        this.buildingSpots.forEach(spot => {
            if (spot.playerId === null && spot.playerIndex === playerIndex && spot.building === null) {
                spot.playerId = playerName;
                spot.building = this.buildings[0]
                console.log(spot);
                this.$buildingSpots.next(this.buildingSpots);
                return;
            }
        });
    }
}
