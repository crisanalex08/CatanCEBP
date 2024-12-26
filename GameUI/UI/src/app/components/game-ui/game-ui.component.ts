import { Component } from '@angular/core';
import { Game } from 'src/app/models/game-model';

@Component({
  selector: 'app-game-ui',
  standalone: true,
  imports: [],
  templateUrl: './game-ui.component.html',
  styleUrl: './game-ui.component.css'
})
export class GameUIComponent {
    game: Game = {} as Game;
  constructor() { }

  ngOnInit() {
  }


}
