import { Component, Input } from '@angular/core';
import { Building, BuildingSpot, BuildingType } from 'src/app/models/building-model';
import { Game } from 'src/app/models/game-model';
import { GameBoardService } from 'src/app/services/game-board.service';
import { GameService } from 'src/app/services/game-service.service';
import { UserService } from 'src/app/services/user-service.service';

@Component({
  selector: 'app-game-board',
  templateUrl: './game-board.component.html',
  styleUrl: './game-board.component.css'
})
export class GameBoardComponent {
  @Input() game: Game | undefined;
  buildings: Building[] = [];
  buildingSpots: BuildingSpot[] = [];
  currentPlayer: string | null = null;
  constructor(private userService: UserService,
    private gameBoardService: GameBoardService
  ) { }

  ngOnInit() {

    // this.gameBoardService.$buildingSpots.subscribe(spots => {
    //   this.buildingSpots = spots;
    // });
    
    this.userService.playerName$.subscribe(playerName => {
      this.currentPlayer = playerName;
    });
  }

  getCursorPosition(event: any) {
    const x = event.clientX;
    const y = event.clientY;

    this.buildingSpots.push({
      playerId: null,
      playerIndex: 0,
      x: x,
      y: y,
      building:  {
        type: BuildingType.Settlement,
        image: 'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/Settlement.png'
    },
    });

    console.log(x, y);
  }
}


