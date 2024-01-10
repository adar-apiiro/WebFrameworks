package com.example;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.SimpleWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.event.WebSocketBinaryPayload;
import org.apache.wicket.protocol.ws.api.event.WebSocketPayload;
import org.apache.wicket.protocol.ws.api.message.BinaryMessage;
import org.apache.wicket.protocol.ws.api.message.StringMessage;
import org.apache.wicket.protocol.ws.api.registry.IKey;

public class MyWebSocketPage extends WebPage {

    public MyWebSocketPage() {
        // Add WebSocket behavior to the page
        add(new WebSocketBehavior() {
            @Override
            protected void onConnect(WebSocketRequestHandler handler) {
                super.onConnect(handler);
                // Handle WebSocket connection establishment
            }

            @Override
            protected void onMessage(WebSocketRequestHandler handler, WebSocketPayload message) {
                super.onMessage(handler, message);

                if (message instanceof StringMessage) {
                    // Handle String message
                    String text = ((StringMessage) message).getText();
                    System.out.println("Received String: " + text);
                } else if (message instanceof BinaryMessage) {
                    // Handle Binary message
                    byte[] data = ((BinaryMessage) message).getBytes();
                    System.out.println("Received Binary data with length: " + data.length);
                }
            }

            @Override
            protected void onPush(WebSocketRequestHandler handler, IWebSocketConnectionRegistry registry) {
                super.onPush(handler, registry);
                // Push data to connected clients
                handler.push("Hello, WebSocket Clients!");
            }

            @Override
            protected void onClose(WebSocketRequestHandler handler, IWebSocketConnectionRegistry registry, int code,
                                   String reason) {
                super.onClose(handler, registry, code, reason);
                // Handle WebSocket connection closure
            }
        });
    }
}
