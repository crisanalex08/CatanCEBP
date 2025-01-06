import { Component, Input } from '@angular/core';
import { Building, BuildingSpot, BuildingType } from 'src/app/models/building-model';
import { Game } from 'src/app/models/game-model';
import { GameBoardService } from 'src/app/services/game-board.service';
import { GameService } from 'src/app/services/game-service.service';
import { GamePlayService } from 'src/app/services/gameplay-service';
import { UserService } from 'src/app/services/user-service.service';

@Component({
  selector: 'app-game-board',
  templateUrl: './game-board.component.html',
  styleUrl: './game-board.component.css'
})
export class GameBoardComponent {
  @Input() game!: Game;
  buildings: Building[] = [];
  buildingSpots: BuildingSpot[] = [];
  currentPlayer: string | null = null;
  constructor(private userService: UserService,
    private gameBoardService: GameBoardService,
    private gamePlayService: GamePlayService
  ) { }

  ngOnInit() {

    this.gameBoardService.$buildingSpots.subscribe(spots => {
      this.buildingSpots = spots;
    });

    this.gameBoardService.updateAllBuildings();

    this.userService.playerName$.subscribe(playerName => {
      this.currentPlayer = playerName;
    });
  }
  
  getCursorPosition(event: MouseEvent) {
    const rect = (event.target as HTMLElement).getBoundingClientRect();
    const x = ((event.clientX - rect.left) / rect.width) * 100;
    const y = ((event.clientY - rect.top) / rect.height) * 100;

    console.log(`x: ${x}, y: ${y}`);
    
    // Check if the spot is valid and add the building spot
    // if (this.gameBoardService.isValidBuildingSpot(x, y)) {
    //     const playerName = this.currentPlayer;
        
    //     if (playerName) {
    //         this.gameBoardService.buildSettlement(playerName);
    //     }
    // }
  }
  // addBuildingSpot(relativeX: number, relativeY: number) {
  //   // Check if spot is valid for building (you'll need to define valid spots)
  //   if (this.isValidBuildingLocation(relativeX, relativeY)) {
  //     const newSpot: BuildingSpot = {
  //       playerId: null,
  //       playerIndex: 0,
  //       x: relativeX,
  //       y: relativeY,
  //       building: {
  //         type: BuildingType.Settlement,
  //         image: 'path/to/building/image.png'
  //       }
  //     };
  //     this.buildingSpots.push(newSpot);
  //   }
  // }


}
