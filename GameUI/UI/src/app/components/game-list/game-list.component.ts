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
  @Input() playerName: string = '';
  user: User | undefined;
  games: Game[] = [];
  constructor(
    private router: Router,
    private gameService: GameService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.gameService.games$.subscribe(games => {
      this.games = games;
    });
  }

  isJoinButtonDisabled(){
    return this.playerName === '';
  }

  joinGame(game: Game) {
    this.userService.checkUsername(this.playerName).subscribe({
      next: () => {
        this.gameService.joinGame(game.id, this.playerName).subscribe({
          next: response => {
            console.log(response);
            this.router.navigate([`/game/${game.id}`]);
          },
          error: error => {
            console.error(error);
          }
        });
      },
      error: error => {
        console.error('Error setting username:', error);
      }
    });
  }
}
