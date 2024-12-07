import { Component, Input } from '@angular/core';
import { Game } from 'src/app/models/game-model';
import { Router } from '@angular/router';
import { GameService } from 'src/app/services/game-service.service';
import { UserService } from 'src/app/services/user-service.service';
import { User } from 'src/app/models/user.model';
@Component({
  selector: 'game-list',
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.css']
})
export class GameListComponent {
  @Input() games: Game[] = [];
  @Input() playerName: string = '';
  user: User | undefined;
  constructor(
    private router: Router,
    private gameService: GameService,
  ) {}

  isJoinButtonDisabled(){
    return this.playerName === '';
  }

  joinGame(game: Game) {
    this.gameService.joinGame(game.id, this.playerName).subscribe({
      next: response => {
        console.log(response);
      },
      error: error => {
        console.error(error);
      }
    });

    this.router.navigate([`/game/${game.id}`]);
    console.log(`Joining game: ${game.id}`);

    const playerId = '';
  }
}
