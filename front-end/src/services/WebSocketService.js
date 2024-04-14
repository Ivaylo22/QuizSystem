import { Client } from '@stomp/stompjs';

const WebSocketService = (() => {
    let client = null;
    
    const connect = (onConnectCallback) => {
        client = new Client({
            brokerURL: 'ws://localhost:8090/ws',
            connectHeaders: {
              login: 'user',
              passcode: 'password',
            },
            debug: function (str) {
              console.log('WebSocket: ' + str);
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: frame => {
                console.log('Connected: ' + frame);
                client.subscribe('/user/topic/notifications', message => {
                    onConnectCallback(JSON.parse(message.body));
                });
            },
        });

        client.activate();
    };

    const disconnect = () => {
        if (client !== null) {
            client.deactivate();
            console.log("Disconnected");
        }
    };

    return {
        connect,
        disconnect
    };
})();

export default WebSocketService;