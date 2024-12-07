import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Route } from '@angular/router';

@Component({
  selector: 'app-in-game',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './in-game.component.html',
  styleUrl: './in-game.component.css'
})
export class InGameComponent {
  gameId: string = '';
  constructor(private route: ActivatedRoute) { }
  ngOnInit() {
    this.route.params.subscribe(params => {
      this.gameId = params['gameId'];
      console.log(this.gameId);
    });
  }
}
