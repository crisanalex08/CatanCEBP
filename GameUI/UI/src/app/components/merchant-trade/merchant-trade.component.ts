import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ResourceType } from 'src/app/models/resource-model';
import { GameService } from 'src/app/services/game-service.service';
import { GamePlayService } from 'src/app/services/gameplay-service';
import { TradeService } from 'src/app/services/trade.service';
import { switchMap } from 'rxjs/operators';
import { MessageService } from 'primeng/api';
@Component({
  selector: 'app-merchant-trade',
  templateUrl: './merchant-trade.component.html',
  styleUrls: ['./merchant-trade.component.css']
})
export class MerchantTradeComponent {
  @Output() closeDialogEvent = new EventEmitter<void>();
  @Input() gameId!: number;
  @Input() playerId!: number;

  ResourceType = [
    { label: 'Wood', value: 'WOOD' },
    { label: 'Clay', value: 'CLAY' },
    { label: 'Stone', value: 'STONE' },
    { label: 'Sheep', value: 'SHEEP' },
    { label: 'Wheat', value: 'WHEAT' },
    { label: 'Gold', value: 'GOLD' }
  ];

  selectedOffer: string;
  selectedRequest: string;

  constructor(private tradeService: TradeService,
    private gamePlayerService: GamePlayService,
    private messageService: MessageService) {
    this.selectedOffer = '';
    this.selectedRequest = '';
  }

  mapResourceType(resourceType: string) {
    switch (resourceType) {
      case 'WOOD':
        return ResourceType.WOOD;
      case 'CLAY':
        return ResourceType.CLAY;
      case 'STONE':
        return ResourceType.STONE;
      case 'SHEEP':
        return ResourceType.SHEEP;
      case 'WHEAT':
        return ResourceType.WHEAT;
      case 'GOLD':
        return ResourceType.GOLD;
      default:
        return ResourceType.WOOD;
    }
  }

  tradeResources() {
    this.closeDialogEvent.emit();
    
    this.tradeService.createMerchantTrade(
      this.mapResourceType(this.selectedRequest), 
      this.mapResourceType(this.selectedOffer), 
      this.gameId, 
      this.playerId
    ).pipe(
      // Chain getPlayerResources after successful trade
      switchMap(() => this.gamePlayerService.getPlayerResources(this.gameId, this.playerId.toString()))
    ).subscribe({
      next: (resources) => {
        
        console.log('Trade completed and resources updated:', resources);
        this.messageService.add({ severity: 'success', summary: 'Trade successful', detail: 'Trade completed successfully' });
      },
      error: (err) => {
        console.error('Error trading resources:', err);
        this.messageService.add({ severity: 'error', summary: 'Trade failed', detail: err.error });

      }
    });
  }

  closeDialog() {
    this.closeDialogEvent.emit();
  }
}