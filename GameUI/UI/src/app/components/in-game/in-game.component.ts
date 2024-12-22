import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { GameService } from 'src/app/services/game-service.service';
import { Game } from 'src/app/models/game-model';
import { UserService } from 'src/app/services/user-service.service';
import { WebSocketService } from 'src/app/services/websocket.service';
@Component({
  selector: 'app-in-game',
  templateUrl: './in-game.component.html',
  styleUrl: './in-game.component.css'
})
export class InGameComponent {
  @Input() playerName: string = '';
  gameId: number = -1;
  game: Game = {} as Game;
  IsGameStarted: boolean = true;
  constructor(
    private route: ActivatedRoute,
    private gameService : GameService,
    private userService: UserService,
    private router: Router,
    private WebSocketService: WebSocketService

  ) { }
  private wsUrl = 'ws://localhost:8080/lobby';
  ngOnInit() {
    
    const gameId = this.route.snapshot.paramMap.get('gameId');
    console.log('Game ID:', gameId);
    this.gameId = Number(gameId);
    console.log('Game ID:', this.gameId);
    this.WebSocketService.connect(this.wsUrl + '/' + gameId).subscribe({
      
      next: (message: any) => {
        console.log('Message received:', message);
        if(message.data === 'Player Joined' || message.data === 'Player Left') {
         this.gameService.getGameInfo(this.gameId).subscribe({
            next: response => {
              this.game = response as Game;
              console.log('Game:', this.game);
            }
          });
        }
      },
      error: error => {
        console.error('Error:', error);
      },
      complete: () => {
        console.log('Connection closed');
      }
    });
      
      
    this.userService.playerName$.subscribe({
      next: playerName => {
        this.playerName = playerName;
      }
    });

    if(this.playerName === '')
      this.playerName = localStorage.getItem('username') ?? '';
    else
      localStorage.setItem('username', this.playerName);
    
    this.gameService.joinGame(this.gameId, this.playerName).subscribe({
      next: response => {
        this.game = response as Game;
      },
      error: error => {
        console.error(error);
      }
    });
  }

  leaveGame() {
    this.gameService.leaveGame(this.gameId, this.playerName).subscribe({
      next: response => {
        this.router.navigate(['/home']);
        console.log(response);
      },
      error: error => {
        console.error(error);
      }
    });
  }
  startGame() {
    this.IsGameStarted = true;
  }


}
