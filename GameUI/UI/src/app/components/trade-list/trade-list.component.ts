import { Component, Input } from '@angular/core';
import { TradeService } from 'src/app/services/trade.service';
import { Trade } from 'src/app/models/trade-model';

@Component({
  selector: 'app-trade-list',
  templateUrl: './trade-list.component.html',
  styleUrl: './trade-list.component.css'
})
export class TradeListComponent {
  @Input() playerId: number;
  @Input() gameId: number;

  trades: Trade[] = [];

  constructor(
    // private router: Router,
    private tradeService: TradeService,
    // private userService: UserService
  ) { }

  ngOnInit() {
    this.tradeService.getMyActiveTrades(this.gameId, this.playerId).subscribe();
    this.tradeService.trades$.subscribe(trades => {
      this.trades = trades;
    });
  }

  acceptTrade(gameId : number, tradeId : number) {
    this.tradeService.acceptTrade(gameId, tradeId).subscribe({
        next: () => {
          this.trades = this.trades.filter(trade => trade.tradeId !== tradeId);
        }
    });
  }

  declineTrade(gameId : number, tradeId : number) {
    this.tradeService.declineTrade(gameId, tradeId).subscribe({
      next: () => {
        this.trades = this.trades.filter(trade => trade.tradeId !== tradeId);
      }
    });
  }
}
