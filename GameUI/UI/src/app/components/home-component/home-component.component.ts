import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Game } from 'src/app/models/game-model';
import { GameService } from 'src/app/services/game-service.service';


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
  games: Game[] = [];
  games$: Observable<Game[]>;

  constructor(private router: Router,
    private gameService: GameService
  ) {
    this.games$ = this.gameService.list();
  }

  ngOnInit() {
    this.games$.subscribe(games => {
      console.log('Games:', games);
      this.games = games;
    });
  }

  createGame() {
    this.display = false;
    this.gameService.create(this.playerName, this.gameName, this.selectedNumberOfPlayers).subscribe((game: Game) => {
      console.log('Game created:', game);
      this.games.push(game);
      // this.router.navigate(['/game', game.id]);
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
