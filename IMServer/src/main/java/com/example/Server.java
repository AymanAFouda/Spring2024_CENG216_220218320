package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {
    private static ServerSocket server;
    private static ArrayList<ClientThread> threads = new ArrayList<>();

    public void init() {
        try {
            server = new ServerSocket(2000);
            System.out.println("Server is running");

            while(true) {
                Socket socket = server.accept();
                System.out.println("A new client has connected");
                ClientThread thread = new ClientThread(socket);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void addThread(ClientThread thread) {
        threads.add(thread);
        notifyThreads();
        System.out.println(thread.getClientName());
    }

    public synchronized void removeThread(ClientThread thread) {
        for(int i=0 ; i<threads.size() ; i++) {
            if(threads.get(i) == thread) {
                threads.remove(i);
            }
        }
        notifyThreads();
    }

    public synchronized String[] getActiveUsers() {
        String[] activeUsers = new String[threads.size()];

        for(int i=0; i<activeUsers.length; i++) {
            activeUsers[i] = threads.get(i).getClientName();
        }

        return activeUsers;
    }

    public void notifyThreads() {
        for(ClientThread thread : threads) {
            thread.updateActiveUsers();
        }
    }

    public synchronized void forward(Message msg) {
        String to = msg.getTo();
        for(int i=0; i<threads.size(); i++) {
            if (threads.get(i).getClientName().equals(to)) {
                threads.get(i).sendMessage(msg);
            }
        }
    }

    public void close() {
        System.out.println("Server is closed");
        if(server != null)
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private class ClientThread extends Thread {

        private Socket socket;
        private String clientName;
        private PrintWriter writer;
        private BufferedReader reader;
        private ObjectMapper mapper = new ObjectMapper();


        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                while(!socket.isClosed()) {
                    readMessage();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        private void readMessage() throws IOException {
            StringBuilder stringBuilder = new StringBuilder();

            for(int chr = reader.read(); reader.ready() ; chr = reader.read()) {
                stringBuilder.append((char) chr);
            }

            String msgText = stringBuilder.toString();
            System.out.println(msgText);

            Message msg = mapper.readValue(msgText, Message.class);
            messageHandler(msg);
        }

        private void messageHandler(Message msg) {
            String msgType = msg.getType();
            switch (msgType) {
                case "connect":
                    this.clientName = msg.getName();
                    addThread(this);
                    break;
                case "chat":
                case "text":
                    forward(msg);
                    break;
                case "disconnect":
                    removeThread(this);
                    closeAll();
                    break;
            }
        }

        private void sendMessage(Message msg) {
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(msg);
                writer.println(jsonString);

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        public void updateActiveUsers() {
            Message msg = new Message();
            msg.setType("users");
            msg.activeUsers = getActiveUsers();
            sendMessage(msg);
        }

        public String getClientName() {
            return clientName;
        }

        public void closeAll() {
            try {
                if (socket != null) {socket.close();}
                if (reader != null) {reader.close();}
                if (writer != null) {writer.close();}
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.init();
        } catch(Exception e) {
            server.close();
            System.out.println("An error occured");
        }
    }
}
