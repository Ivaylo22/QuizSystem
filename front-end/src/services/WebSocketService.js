import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const WebSocketService = (() => {
    let client = null;
    let isConnected = false;

    const connect = (onConnectCallback, onErrorCallback) => {
        const socket = new SockJS('http://localhost:8090/ws');
        client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => {
                isConnected = true;
                console.log('Connected to WebSocket server.');
                client.subscribe('/user/topic/notifications', message => {
                    console.log('Notification received:', message.body);
                    onConnectCallback(JSON.parse(message.body));
                });
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
                onErrorCallback(frame.headers['message']);
            },
            onWebSocketError: (evt) => {
                isConnected = false;
                console.error('WebSocket connection error:', evt);
                onErrorCallback('WebSocket connection error');
            },
            onWebSocketClose: () => {
                isConnected = false;
                console.log('WebSocket connection closed');
            }
        });

        client.activate();
    };

    const disconnect = () => {
        if (client !== null) {
            client.deactivate();
            isConnected = false;
            console.log("Disconnected from WebSocket server.");
        }
    };

    const isConnectionActive = () => isConnected;

    return {
        connect,
        disconnect,
        isConnectionActive
    };
})();

export default WebSocketService;
