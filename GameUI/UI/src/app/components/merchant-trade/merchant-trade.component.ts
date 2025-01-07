import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ResourceType } from 'src/app/models/resource-model';
import { GameService } from 'src/app/services/game-service.service';
import { TradeService } from 'src/app/services/trade.service';

@Component({
  selector: 'app-merchant-trade',
  templateUrl: './merchant-trade.component.html',
  styleUrls: ['./merchant-trade.component.css']
})
export class MerchantTradeComponent {
  @Output() closeDialogEvent = new EventEmitter<void>();
  @Input() gameId: number;
  @Input() playerId: number;

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
    private gameService: GameService) {
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
    this.tradeService.createMerchantTrade(this.selectedRequest, this.selectedOffer, this.gameId, this.playerId).subscribe();
    console.log(`Offer: ${this.selectedOffer}, Request: ${this.selectedRequest}`);
  }

  closeDialog() {
    this.closeDialogEvent.emit();
  }
}