import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Game, PlayerResources } from 'src/app/models/game-model';
import { ChatMessage } from 'src/app/models/message-model';
import { GameBoardService } from 'src/app/services/game-board.service';
import { GameService } from 'src/app/services/game-service.service';
import { GamePlayService } from 'src/app/services/gameplay-service';
import { WebSocketService } from 'src/app/services/websocket.service';
import { PlayersColor } from 'src/app/enums/PlayersColor';

@Component({
    selector: 'app-game-ui',
    templateUrl: './game-ui.component.html',
    styleUrl: './game-ui.component.css'
})
export class GameUIComponent {
    
    @Output() sendMessageEvent = new EventEmitter<ChatMessage>();

    gameId: number = -1;
    playerName: string = '';
    currentPlayerResources: PlayerResources['quantities'] = {
        WOOD: 0,
        CLAY: 0,
        STONE: 0,
        SHEEP: 0,
        WHEAT: 0,
        GOLD: 0
    };

    playersColor = PlayersColor;

    
    constructor(
        private route: ActivatedRoute,
        private gameService: GameService, 
        private gamePlayService: GamePlayService,
        private WebSocketService: WebSocketService,
        private gameBoardService: GameBoardService

    ) {}
    @Input() game: Game = {} as Game;
    currentPlayer: string | null = null;

   
     
    ngOnInit() {
        
         this.gameService.currentGame$.subscribe(game => {
            this.game = game;
            if (!this.game.id) {
                return;
            }
        
            this.playerName = localStorage.getItem('username') ?? ''; 
            const playerId = this.game.players.find(player => player.name === localStorage.getItem('username'))?.id; 
            if (!playerId) {
                return;
            }
            
            this.game.status == "IN_PROGRESS" && this.gamePlayService.getPlayerResources(this.game.id, playerId).subscribe({
                next: resources => {
                    this.currentPlayerResources = resources.quantities;
                }
            });
        });
    }
    rollDice() {
        const playerId = this.game.players.find(player => player.name === localStorage.getItem('username'))?.id;
        if (!playerId) {
            return;
        }
        this.gamePlayService.rollDice(this.game.id, playerId).subscribe();
    }

    buildSettlement() {
        const playerId = this.game.players.find(player => player.name === localStorage.getItem('username'))?.id;
        if (!playerId) {
            return;
        }
        this.gameBoardService.buildSettlement(this.playerName);
    }
    

    sendMessage(message: ChatMessage) {
        this.sendMessageEvent.emit(message);
    }

   
}
