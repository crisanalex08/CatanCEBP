package com.example.gameserver.websocket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class GamesWebSocketHandler extends TextWebSocketHandler {

    
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<WebSocketSession>> lobbySessions = new ConcurrentHashMap<>();

    @Override   
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uri = session.getUri().toString();

        if(uri.contains("lobby")) {
            String lobbyId = uri.substring(uri.lastIndexOf('/') + 1);
            System.out.println("Lobby ID: " + lobbyId);
            addToLobby(lobbyId, session);
            System.out.println("New Connection: " + session.getId());
            System.out.println("Lobby Sessions: " + lobbySessions.get(lobbyId));
        } else {
            sessions.add(session);
        }
    }
    public void addToLobby(String lobbyId, WebSocketSession session) {
        if(lobbySessions.containsKey(lobbyId)) {
            lobbySessions.get(lobbyId).add(session);
        } else {
            CopyOnWriteArrayList<WebSocketSession> newLobby = new CopyOnWriteArrayList<>();
            newLobby.add(session);
            lobbySessions.put(lobbyId, newLobby);
        }
    }
   
    public void removeFromLobby(String lobbyId, WebSocketSession session) {
        if(lobbySessions.containsKey(lobbyId)) {
            lobbySessions.get(lobbyId).remove(session);
            if(lobbySessions.get(lobbyId).isEmpty()) {
                lobbySessions.remove(lobbyId);
            }
        }
    }
    public void broadcastToLobby(String lobbyId, TextMessage message) {

        if(lobbySessions.containsKey(lobbyId)) {

            for (WebSocketSession webSocketSession : lobbySessions.get(lobbyId)) {
                try {
                    webSocketSession.sendMessage(message);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String uri = session.getUri().toString();
        if(uri.contains("lobby")) {
            String lobbyId = uri.substring(uri.lastIndexOf('/') + 1);
            removeFromLobby(lobbyId, session);
        } else {
            sessions.remove(session);
        }

    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        for (WebSocketSession webSocketSession : sessions) {
            webSocketSession.sendMessage(message);
        }
    }

    public void broadcastGameList() {
        for (WebSocketSession webSocketSession : sessions) {
            try {
                webSocketSession.sendMessage(new TextMessage("Game List Updated"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    

    
}
