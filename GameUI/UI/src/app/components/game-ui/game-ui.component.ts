import { Component } from '@angular/core';
import { Game, PlayerResources } from 'src/app/models/game-model';
import { GameService } from 'src/app/services/game-service.service';
import { GamePlayService } from 'src/app/services/gameplay-service';

@Component({
  selector: 'app-game-ui',
  templateUrl: './game-ui.component.html',
  styleUrl: './game-ui.component.css'
})
export class GameUIComponent {
    game: Game = {} as Game;
    currentPlayerResources: PlayerResources['quantities'] = {
        WOOD: 0,
        CLAY: 0,
        STONE: 0,
        SHEEP: 0,
        WHEAT: 0,
        GOLD: 0
    };

    
    constructor(private gameService: GameService, private gamePlayService: GamePlayService) {}

    ngOnInit() {
        this.gameService.currentGame$.subscribe(game => {
            this.game = game;
            if (!this.game.id) {
                return;
            }
            const playerId = this.game.players.find(player => player.name === localStorage.getItem('username'))?.id;
            if (!playerId) {
                return;
            }
            this.gamePlayService.getPlayerResources(this.game.id, playerId).subscribe({
                next: resources => {
                    this.currentPlayerResources = resources.quantities;
                }
            });
        });
    }
}
