import { Component } from '@angular/core';
import { Game } from 'src/app/models/game-model';
import { GameService } from 'src/app/services/game-service.service';

@Component({
  selector: 'app-game-ui',
  templateUrl: './game-ui.component.html',
  styleUrl: './game-ui.component.css'
})
export class GameUIComponent {
    game: Game = {} as Game;
    constructor(private gameService: GameService) {
    }

    ngOnInit() {
        this.gameService.currentGame$.subscribe(game => {
            this.game = game;
        });
    }
}
