import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { ChatService } from 'src/app/services/chat-service';
import { ChatMessage } from 'src/app/models/message-model';
import { WebSocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'app-game-chat',
  templateUrl: './game-chat.component.html',
  styleUrls: ['./game-chat.component.css']
})
export class GameChatComponent implements OnInit {
  @Input() gameId!: number;
  @Input() playerName!: string;
  @Output() sendMessageEvent = new EventEmitter<ChatMessage>();
  messages: ChatMessage[] = [];
  newMessage: string = '';

  constructor(
    private chatService: ChatService,
  
  ) { }

  ngOnInit() {
    this.chatService.messages$.subscribe(messages => {
      this.messages = messages;
    });
    
  }

  sendMessage() {
    if (!this.newMessage.trim()) return;

    const message: ChatMessage = {
      gameId: this.gameId,
      sender: this.playerName,
      content: this.newMessage,
      timestamp: new Date()
    };

   this.sendMessageEvent.emit(message);
   this.newMessage = '';

  }
}