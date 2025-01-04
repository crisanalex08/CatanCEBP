import { Component, Input } from '@angular/core';
import { Game } from 'src/app/models/game-model';
import { GameBoardService } from 'src/app/services/game-board.service';
import { GameService } from 'src/app/services/game-service.service';

@Component({
    selector: 'app-game-ui',
    templateUrl: './game-ui.component.html',
    styleUrl: './game-ui.component.css'
})
export class GameUIComponent {
    @Input() game: Game = {} as Game;
    currentPlayer: string | null = null;
    constructor(private gameService: GameService, private gameBoardService: GameBoardService) {
    }

    ngOnInit() {
        this.gameService.currentGame$.subscribe(game => {
            this.game = game;
        });
    }

    buildSettlement() {
        console.log("Building a settlement");
        const userName = localStorage.getItem('username');
        if (userName)
            this.gameBoardService.buildSettlement(userName);
    }
}
