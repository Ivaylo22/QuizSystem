import { Client } from '@stomp/stompjs';

function connect() {
    const stompClient = new Client({
        brokerURL: 'http://localhost:8090/ws',
        connectHeaders: {
            // headers if needed (e.g., authentication token)
        },
        debug: function (str) {
            console.log('STOMP: ' + str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = function (frame) {
        stompClient.subscribe('/user/queue/notifications', function (message) {
            const notification = JSON.parse(message.body);
            console.log('Received notification:', notification);
        });
    };

    stompClient.onStompError = function (frame) {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };

    stompClient.activate();
}

// Call connect when the component mounts or in appropriate lifecycle method
connect();
