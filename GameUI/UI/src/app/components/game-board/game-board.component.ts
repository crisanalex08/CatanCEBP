import { Component, Input, OnInit } from '@angular/core';
import { Building, BuildingSpot, BuildingType } from 'src/app/models/building-model';
import { Game } from 'src/app/models/game-model';
import { GameBoardService } from 'src/app/services/board.service';
import { GameService } from 'src/app/services/game-service.service';
import { GamePlayService } from 'src/app/services/gameplay-service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-game-board',
  templateUrl: './game-board.component.html',
  styleUrl: './game-board.component.css'
})
export class GameBoardComponent implements OnInit {
  @Input() game!: Game;
  buildings: Building[] = [];
  buildingSpots: BuildingSpot[] = [];
  currentPlayer: string | null = null;

  constructor(
    private userService: UserService,
    private gameBoardService: GameBoardService,
    private gamePlayService: GamePlayService
  ) { }
   private readonly BASE_IMAGE_URL = 'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/';
  ngOnInit() {
    // Subscribe to building spots updates
    this.gameBoardService.getBuildingSpots().subscribe(spots => {
      this.buildingSpots = spots;
      console.log('Building spots updated', this.buildingSpots);
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
  }

  getBuildingImage(buildingType: string): string {
    switch (buildingType) {
        case 'SETTLEMENT':
            return `${this.BASE_IMAGE_URL}Settlement.png`;
        case 'TOWN':
            return `${this.BASE_IMAGE_URL}Town.png`;
        case 'CASTLE':
            return `${this.BASE_IMAGE_URL}Castle.png`;
        default:
            return '';
    }
}
      
}
