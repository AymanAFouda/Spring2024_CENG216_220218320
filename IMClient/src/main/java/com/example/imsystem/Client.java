package com.example.imsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Client {

    private Socket socket;
    private Scanner keyboard = new Scanner(System.in);
    private String name;
    private Controller controller;


    public Client(Controller controller) {
        this.controller = controller;
    }

    public void init(String ip, String port, String name) {
        try {
            this.name = name;
            int intPort = Integer.parseInt(port);
            socket = new Socket(ip, intPort);
            sendConnectMessage();

            Thread thread = new Thread(() -> {
                while(!socket.isClosed()) {
                    try {
                        readMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message msg) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString;

        try {
            jsonString = mapper.writeValueAsString(msg);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(jsonString);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void sendConnectMessage() {
        Message msg = new Message();
        msg.setType("connect");
        msg.setName(name);
        sendMessage(msg);
    }

    public void sendTextMessage(String to, String text) {
        Message msg = new Message();
        msg.setType("text");
        msg.setFrom(name);
        msg.setTo(to);
        msg.setText(text);
        sendMessage(msg);
    }

    public void sendChatMessage(String to) {
        Message msg = new Message();
        msg.setType("chat");
        msg.setFrom(this.name);
        msg.setTo(to);
        sendMessage(msg);
    }

    public void sendDisconnectMessage() {
        Message msg = new Message();
        msg.setType("disconnect");
        sendMessage(msg);
        closeAll();
    }

    public void readMessage() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();

        try {
            for (int chr = reader.read(); reader.ready(); chr = reader.read()) {
                stringBuilder.append((char) chr);
            }
        } catch (SocketException se) {
            //do nothing
        }
        String msgText = stringBuilder.toString();
        if (!msgText.equals("")) {
            System.out.println(msgText);
            Message msg = mapper.readValue(msgText, Message.class);
            messageHandler(msg);
        }
    }

    private void messageHandler(Message msg) {
        String msgType = msg.getType();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                switch(msgType) {
                    case "users" :
                        controller.displayActiveUsers(msg.getActiveUsers());
                        break;
                    case "chat" :
                        controller.displayChatStage(msg.getFrom());
                        break;
                    case "text" :
                        if(msg.getTo().equals(msg.getFrom())) {
                            return;
                        } else {
                            controller.displayText(msg.getFrom(), msg.getText());
                        }
                        break;
                    default:
                        System.out.println("Invalid Message Type");
                }
            }
        });
    }

    public boolean isClosed() {
        if (!socket.isClosed()) {
            return false;
        } else {
           return true;
        }
    }

    public void closeAll() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
