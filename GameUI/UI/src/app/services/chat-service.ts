import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ChatMessage } from '../models/message-model';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private messagesSubject = new BehaviorSubject<ChatMessage[]>([]);
  messages$ = this.messagesSubject.asObservable();

  constructor() { }

  addMessage(message: ChatMessage) {
    console.log('Adding message:', message);
    const currentMessages = this.messagesSubject.value;
    currentMessages.push(message);
  }

  addSystemMessage(content: string) {
    const systemMessage: ChatMessage = 
    {
      gameId: -1,
      sender: 'System',
      content: content,
      timestamp: new Date(),
      isSystem: true
    };
    this.addMessage(systemMessage);
  }

  clearMessages() {
    this.messagesSubject.next([]);
  }
}
