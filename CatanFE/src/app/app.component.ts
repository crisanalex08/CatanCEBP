import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  playerName: string = '';
  serverCode: string = '';

  constructor(private router:Router) {}

  onSubmit() {
    if (true) {
      console.log('Starting game for player:', this.playerName);
      this.router.navigate(['/game', this.serverCode, this.playerName]);
      // Here you would typically navigate to the game component or start the game logic
    }
  }
}