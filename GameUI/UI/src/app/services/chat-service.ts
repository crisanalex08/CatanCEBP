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
    const currentMessages = this.messagesSubject.value;
    this.messagesSubject.next([...currentMessages, message]);
  }

  addSystemMessage(content: string) {
    const systemMessage: ChatMessage = {
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
