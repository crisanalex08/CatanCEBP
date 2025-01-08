import { Component, EventEmitter, Input, Output, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Game, PlayerResources } from 'src/app/models/game-model';
import { ChatMessage } from 'src/app/models/message-model';
import { GameBoardService } from 'src/app/services/board.service';
import { GameService } from 'src/app/services/game-service.service';
import { GamePlayService } from 'src/app/services/gameplay-service';
import { WebSocketService } from 'src/app/services/websocket.service';
import { PlayersColor } from 'src/app/enums/PlayersColor';
import { ServerBuilding } from 'src/app/models/building-model';

@Component({
    selector: 'app-game-ui',
    templateUrl: './game-ui.component.html',
    styleUrl: './game-ui.component.css'
})
export class GameUIComponent implements OnInit, OnDestroy {
    merchantTradeDialogVisible = false;
    playerTradeDialogVisible = false;
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

    private readonly BASE_IMAGE_URL = 'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/';
    private subscriptions: Subscription[] = [];

    constructor(
        private route: ActivatedRoute,
        private gameService: GameService,
        private gamePlayService: GamePlayService,
        private WebSocketService: WebSocketService,
        private gameBoardService: GameBoardService

    ) { }
    @Input() game: Game = {} as Game;
    currentPlayer: string | null = null;
    currentPlayerId: number | null = null;
    playerBuildings: ServerBuilding[] = [];

    closeMerchantTradeDialog() {
        this.merchantTradeDialogVisible = false;
    }

    closePlayerTradeDialog() {
        this.playerTradeDialogVisible = false;
    }

    ngOnInit() {
        // Existing game subscription
        this.subscriptions.push(
            this.gameService.currentGame$.subscribe(game => {
                this.game = game;
                if (!this.game.id) {
                    return;
                }

                this.playerName = localStorage.getItem('username') ?? '';
                const playerId = this.game.players.find(player => 
                    player.name === localStorage.getItem('username')
                )?.id;
                
                if (!playerId) {
                    return;
                }
                this.currentPlayerId = parseInt(playerId.toString());
                this.game.status == "IN_PROGRESS" && this.gamePlayService.getPlayerResources(this.game.id, playerId).subscribe({
                    next: resources => {
                        this.currentPlayerResources = resources.quantities;
                    }
                });
            })
        );

        // Add subscription to player resources
        this.subscriptions.push(
            this.gamePlayService.playerResources$.subscribe(resources => {
                if (resources) {
                    this.currentPlayerResources = resources.quantities;
                }
            })
        );

        // Existing buildings subscription
        this.subscriptions.push(
            this.gameBoardService.getPlayerBuildings().subscribe(buildings => {
                this.playerBuildings = buildings;
            })
        );
    }

    ngOnDestroy() {
        this.subscriptions.forEach(sub => sub.unsubscribe());
    }

    showMerchantTradeDialog() {
        this.merchantTradeDialogVisible = true;
    }

    showPlayerTradeDialog() {
        this.playerTradeDialogVisible = true;
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
    getResourceImage(resourceType: string): string {
        switch (resourceType) {
            case 'WOOD':
                return `${this.BASE_IMAGE_URL}wood.png`;
            case 'CLAY':
                return `${this.BASE_IMAGE_URL}clay.png`;
            case 'STONE':
                return `${this.BASE_IMAGE_URL}stone.png`;
            case 'SHEEP':
                return `${this.BASE_IMAGE_URL}sheep.png`;
            case 'WHEAT':
                return `${this.BASE_IMAGE_URL}wheat.png`;
            case 'GOLD':
                return `${this.BASE_IMAGE_URL}gold.png`;
            default:
                return '';
        }
    }

}
