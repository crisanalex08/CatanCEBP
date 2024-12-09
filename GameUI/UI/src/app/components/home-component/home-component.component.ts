import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Game, GameCreateDetails } from 'src/app/models/game-model';
import { GameService } from 'src/app/services/game-service.service';
import { UserService } from 'src/app/services/user-service.service';
import { WebSocketService } from 'src/app/services/websocket.service';


@Component({
  selector: 'app-home-component',
  templateUrl: './home-component.component.html',
  styleUrls: ['./home-component.component.css']
})
export class HomeComponent implements OnInit {
  playerName: any = '';
  gameName: any = '';
  gameId: any = '';
  gameStatusText: any = '';
  numberOfPlayers: number[] = [2, 3, 4];
  selectedNumberOfPlayers: number = 2;
  gameError: boolean = false;
  display: boolean = false;

  currentGame: Game | undefined;

  constructor(private router: Router,
    private gameService: GameService,
    private userService: UserService,
    private webSocketService: WebSocketService
  ) {
  }
  private wsUrl = 'ws://localhost:8080/ws';
  ngOnInit() {
    this.gameService.list().subscribe();
    
    this.webSocketService.connect(this.wsUrl).subscribe({
      next: (message: any) => {
        console.log('Message:', message);
        
      },
      error: error => {
        console.error('Error:', error);
      },
      complete: () => {
        console.log('Connection closed');
      }
    });
  }

  

  createGame() {
    this.display = false;
  
    let gameDetails = {
      hostname: this.playerName,
      gameName: this.gameName,
      maxPlayers: this.selectedNumberOfPlayers
    } as GameCreateDetails;
    this.gameService.create(gameDetails).subscribe({
      next: game => {
        this.userService.playerName.next(this.playerName);
        this.gameService.currentGame.next(game);
        this.router.navigate([`/game/${game.id}`]);
      },
      error: error => {
        console.error(error);
      }
    });
  }

  isButtonDisabled() {
    return this.playerName === '';
  }

  isCreateButtonDisabled() {
    return this.playerName === '' || this.gameName === '';
  }

  showDialog(event: any) {
    console.log('Showing dialog');
    this.display = true;
  }
}
