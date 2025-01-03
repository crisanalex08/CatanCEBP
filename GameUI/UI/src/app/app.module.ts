import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home-component/home-component.component';
import { GameService } from './services/game-service.service';
import { HttpClientModule } from '@angular/common/http';
import { GameListComponent } from './components/game-list/game-list.component';
import { InGameComponent } from './components/in-game/in-game.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import {ButtonModule} from 'primeng/button';
import {DialogModule} from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { CardModule } from 'primeng/card';
import { ToolbarModule } from 'primeng/toolbar';
import { ChipModule } from 'primeng/chip';

import { WebSocketService } from './services/websocket.service';
import { GameUIComponent } from "./components/game-ui/game-ui.component";

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    GameListComponent,
    InGameComponent,
    GameUIComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    TableModule,
    InputTextModule,
    ButtonModule,
    DialogModule,
    DropdownModule,
    InputNumberModule,
    CardModule,
    ToolbarModule,
    ChipModule
],
  providers: [GameService, WebSocketService],
  bootstrap: [AppComponent]
})
export class AppModule { }
