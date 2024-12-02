import { Component, Input } from '@angular/core';
import { Game } from 'src/app/models/game-model';

@Component({
  selector: 'game-list',
  templateUrl: './game-list.component.html',
  styleUrls: ['./game-list.component.css']
})
export class GameListComponent {
  @Input() games: Game[] = [];

  joinGame(game: Game) {
    console.log(`Joining game: ${game.id}`);
  }
}
