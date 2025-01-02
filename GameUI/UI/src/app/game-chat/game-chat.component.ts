import { Component, OnInit, Input } from '@angular/core';
import { ChatService } from 'src/app/services/chat-service';
import { ChatMessage } from 'src/app/models/message-model';
import { WebSocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'app-game-chat',
  templateUrl: './game-chat.component.html',
  styleUrls: ['./game-chat.component.css']
})
export class GameChatComponent implements OnInit {
  @Input() gameId: number;
  @Input() playerName: string;
  messages: ChatMessage[] = [];
  newMessage: string = '';

  constructor(
    private chatService: ChatService,
    private webSocketService: WebSocketService
  ) { }

  ngOnInit() {
    this.chatService.messages$.subscribe(messages => {
      this.messages = messages;
    });
  }

  sendMessage() {
    if (!this.newMessage.trim()) return;

    const message: ChatMessage = {
      sender: this.playerName,
      content: this.newMessage,
      timestamp: new Date()
    };

    // Send via WebSocket
    this.webSocketService.connect(`ws://localhost:8080/lobby/${this.gameId}`).next(new MessageEvent('message', { data: JSON.stringify(message) }));
    this.chatService.addMessage(message);
    this.newMessage = '';
  }
}