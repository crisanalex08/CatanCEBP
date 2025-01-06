import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, take, tap } from 'rxjs';
import { ConfigService } from './config-service.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient,
    private config: ConfigService
  ) { 
    const username = localStorage.getItem('username');
    if (username) {
      this.playerName.next(username);
    }
  }
  url = this.config.serverUrl;
  
  playerName = new BehaviorSubject<string>('');
  playerName$ = this.playerName.asObservable();
  
  checkUsername(userName: string) {
    var request_url = this.url + '/api/users/check?username=' + userName;
    return this.http.get(request_url).pipe(
      tap((response: any) => {
        console.log('Response:', response);
        if (response) {
          localStorage.setItem('username', userName);
          this.playerName.next(userName);
      }})
    );
  }

}
