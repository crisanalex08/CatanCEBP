import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private subject: Subject<MessageEvent> | undefined;

  constructor() { }

  public connect(url: string): Subject<MessageEvent> {
    if (!this.subject) {
      this.subject = this.create(url);
      console.log('Successfully connected: ' + url);
    }
    return this.subject;
  }

  private create(url: string): Subject<MessageEvent> {
    const ws = new WebSocket(url);

    const observable = new Observable<MessageEvent>(observer => {
      ws.onmessage = (event) => {
        console.log('WebSocket message received:', event);
        observer.next(event);
      };
      ws.onerror = (event) => {
        console.error('WebSocket error:', event);
        observer.error(event);
      };
      ws.onclose = (event) => {
        console.log('WebSocket connection closed:', event);
        observer.complete();
      };
      ws.onopen = () => {
        console.log('WebSocket connection established');
      };

      return () => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.close();
        }
      };
    });

    const observer = {
      next: (data: Object) => {
        if (ws.readyState === WebSocket.OPEN) {
          console.log('Sending data:', data);
          ws.send(JSON.stringify(data));
        }
      }
    };

    return Subject.create(observer, observable);
  }
}