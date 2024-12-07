import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home-component/home-component.component';
import { InGameComponent } from './components/in-game/in-game.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'game/:gameId', component: InGameComponent },
  { path: '**', redirectTo: '/home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule {
    
    constructor() { }
 }
