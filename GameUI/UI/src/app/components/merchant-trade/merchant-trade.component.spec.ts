import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MerchantTradeComponent } from './merchant-trade.component';

describe('MerchantTradeComponent', () => {
  let component: MerchantTradeComponent;
  let fixture: ComponentFixture<MerchantTradeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MerchantTradeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MerchantTradeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
