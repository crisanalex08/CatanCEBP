import { Component } from '@angular/core';
import { TradeService } from 'src/app/services/trade.service';
import { Trade } from 'src/app/models/trade-model';

@Component({
  selector: 'app-trade-list',
  templateUrl: './trade-list.component.html',
  styleUrl: './trade-list.component.css'
})
export class TradeListComponent {
  trades: Trade[] = [];
  constructor(
      // private router: Router,
      private tradeService: TradeService,
      // private userService: UserService
    ) {}

  ngOnInit() {
    this.tradeService.trades$.subscribe(trades => {
      this.trades = trades;
    });
  }

}
