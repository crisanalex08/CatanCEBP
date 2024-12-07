import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  url = 'http://localhost:8080';

  checkUsername(userName: string) {
    var request_url = this.url + '/api/users/check?username=' + userName;
    console.log('Requesting user data:', request_url);
    return this.http.get(request_url);
  }
}
