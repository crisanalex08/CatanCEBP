import { Injectable } from '@angular/core';
import { delayWhen, Observable, retry, retryWhen, Subject, timer } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private subject: Subject<MessageEvent> | undefined;
  private readonly RETRY_SECONDS = 10;
  private readonly RETRY_LIMIT = 3;

  private ChatMessageQueue: String[] = [];

  constructor() { }

  public connect(url: string): Subject<MessageEvent> {
    this.subject = this.create(url);
    console.log('Successfully connected: ' + url);
    return this.subject;
  }

  private create(url: string): Subject<MessageEvent> {
    const ws = new WebSocket(url);
    let retryCount = 0;

    const observable = new Observable<MessageEvent>(observer => {
      ws.onmessage = (event) => {
        console.log('Received data:', event.data);
        observer.next(event);
      };
      
      ws.onerror = (event) => {
        console.error(`WebSocket connection, attempt ${retryCount + 1}/${this.RETRY_LIMIT} failed:`, event);
        if(retryCount < this.RETRY_LIMIT) {
          retryCount++;
          setTimeout(() => this.connect(url), this.RETRY_SECONDS * 1000);
        }
      };

      return () => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.close();
        }
      };
    });

    const observer = {
      next: (data: any) => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.send(JSON.stringify(data));
          this.ChatMessageQueue.push(data);

          
        }
      }
    };

    return Subject.create(observer, observable);
  }

  public sendMessage(message: any, gameId: number): void {
    if (!this.subject) {
      console.error('No WebSocket connection');
      return;
    }

    const data = {
      gameId: gameId,
      message: message
    };
   
    this.subject.next(data.message); 
    console.log('Sent data:', data);
  }
}
