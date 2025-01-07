import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { GameService } from 'src/app/services/game-service.service';
import { Game } from 'src/app/models/game-model';
import { UserService } from 'src/app/services/user-service.service';
import { WebSocketService } from 'src/app/services/websocket.service';
import { GamePlayService } from 'src/app/services/gameplay-service';
import { Subscription } from 'rxjs';
import { ChatMessage } from 'src/app/models/message-model';
import { ChatService } from 'src/app/services/chat-service';
import { GameBoardService } from 'src/app/services/game-board.service';

@Component({
  selector: 'app-in-game',
  templateUrl: './in-game.component.html',
  styleUrl: './in-game.component.css',
})
export class InGameComponent implements OnInit, OnDestroy {
  @Input() playerName: string = '';
  gameId: number = -1;
  game: Game = {} as Game;
  IsGameStarted: boolean = false;
  IsHost: boolean = false;
  IsStarting: boolean = false;
  private wsSubscription: Subscription | undefined;

  constructor(
    private route: ActivatedRoute,
    private gameService: GameService,
    private userService: UserService,
    private chatService: ChatService,
    private gamePlayService: GamePlayService,
    private gameBoardService: GameBoardService,
    private router: Router,
    private WebSocketService: WebSocketService
  ) { }

  private wsUrl = 'ws://localhost:8080/lobby';

  public waitingForPlayersMessage = 'Waiting for players...';
  public waitingForHostMessage = 'Waiting for host to start the game...';

  ngOnInit() {
    const gameId = this.route.snapshot.paramMap.get('gameId');
    this.gameId = Number(gameId);

    this.gameService.getGameInfo(this.gameId).subscribe({
      next: (game) => {
        this.game = game as Game;
        this.gameService.currentGame.next(this.game);

        this.gameService.currentGame$.subscribe({
          next: (game) => {
            this.game = game;
          },
        });

        this.setupWebSocketConnection();
        this.IsGameStarted = this.game.status === 'IN_PROGRESS' ? true : false;
        this.IsHost =
          this.game.players?.find((player) => player.name === this.playerName)?.host ?? false;
      },
      error: (error) => {
        console.error('Error fetching game:', error);
      },
    });

    this.gameService.currentGame$.subscribe({
      next: (game) => {
        this.game = game;
      },
    });

    this.userService.playerName$.subscribe({
      next: (playerName) => {
        this.playerName = playerName;
      },
    });

    if (this.playerName === '')
      this.playerName = localStorage.getItem('username') ?? '';
    else localStorage.setItem('username', this.playerName);

    this.gameService.joinGame(this.gameId, this.playerName).subscribe({
      next: (response) => {
        this.game = response as Game;
        this.gameService.currentGame.next(this.game);
      },
      error: (error) => {
        console.error(error);
      },
    });
  }

  private setupWebSocketConnection() {
    this.wsSubscription = this.WebSocketService.connect(
      this.wsUrl + '/' + this.gameId
    ).subscribe({
      next: (message: any) => {
        console.log('WebSocket message received:', message);

        if (
          message.data === 'Player Joined' ||
          message.data === 'Player Left'
        ) {
          this.updateGameInfo();
        }

        if (message.data === 'Game Started') {
          this.IsGameStarted = true;
          this.updateGameInfo();
        }

        if (message.data.includes('content')) {
          let chatMessage: ChatMessage = JSON.parse(message.data);
          if (chatMessage.sender === 'System') {
              if(chatMessage.content.includes('SETTLEMENT')) {
                  this.gameBoardService.updateAllBuildings();
              }
          
            this.updateGameInfo();
          }
          this.chatService.addMessage(chatMessage);
        }
      },
      error: (error) => {
        console.error('WebSocket error:', error);
      },
      complete: () => {
        console.log('WebSocket connection closed');
      },
    });
  }

  startGame() {
    this.IsStarting = true;
    this.gamePlayService.startGame(this.gameId).subscribe({
      next: (response) => {
       
        this.IsGameStarted = true;
        this.WebSocketService.connect(this.wsUrl + '/' + this.gameId).next(
          new MessageEvent('GameStarted')
        );
      },
      error: (error) => {
        console.error('Error starting game:', error);
        this.IsStarting = false;
      },
      complete: () => {
        this.IsStarting = false;
      },
    });
  }
  sendMessage(message: ChatMessage) {
    if (!message.content.trim()) return;

    if (this.gameId === null) {
      console.error('No game ID');
      return;
    }

    this.WebSocketService.sendMessage(message, this.gameId);
  }

  ngOnDestroy() {
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
    this.chatService.clearMessages();
    this.IsGameStarted = false;
    this.IsHost = false;
    this.IsStarting = false;
  }

  private updateGameInfo() {
    this.gameService.getGameInfo(this.gameId).subscribe({
      next: (response) => {
        this.game = response as Game;
        this.gameService.currentGame.next(this.game);
        this.IsHost =
          this.game.players?.find((player) => player.host === true)?.name ===
          this.playerName;
      },
    });
  }

  leaveGame() {
    this.gameService.leaveGame(this.gameId, this.playerName).subscribe({
      next: (response) => {
        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error(error);
      },
    });
  }
}
