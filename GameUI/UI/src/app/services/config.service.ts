import { Injectable } from '@angular/core';
import { BuildingType } from '../models/building-model';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  public serverUrl = 'http://localhost:8080';
  constructor() { }

  public getServerUrl(): string {
    return this.serverUrl;
  }

  public getBuildingImage(buildingType: BuildingType): string {
    switch (buildingType) {
      case BuildingType.Settlement:
        return 'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/Settlement.png';
      case BuildingType.Town:
        return 'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/Town.png';
      case BuildingType.Castle:
        return 'https://raw.githubusercontent.com/crisanalex08/CatanCEBP/refs/heads/main/GameUI/UI/src/assets/images/Castle.png';
      default:
        return '';
    }
  }
}
