package com.example.imsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {

    private Client client = new Client(this);

    @FXML
    private Button connectButton;

    @FXML
    private TextField ipInput;

    @FXML
    private TextField nameInput;

    @FXML
    private TextField portInput;

    @FXML
    private VBox usersLayout;

    ArrayList<ChatController> chatControllers = new ArrayList<>();


    public void connectButtonOnClick(ActionEvent e) {
        String name = nameInput.getText();
        String IP = ipInput.getText();
        String port = portInput.getText();
        if (connectButton.getText().equals("Connect")) {
            try {
                client.init(IP, port, name);
                connectButton.setText("Disconnect");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            client.sendDisconnectMessage();
            connectButton.setText("Connect");
            displayActiveUsers(new String[]{});
        }
    }

    public void displayActiveUsers(String[] usersArray) {
        usersLayout.getChildren().clear();
        for(int i=0; i<usersArray.length; i++) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("user-item.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                UserItemController uic = fxmlLoader.getController();
                uic.setData(usersArray[i], client, this);
                usersLayout.getChildren().add(hbox);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void displayText(String from, String text) {
        for(int i=0; i<chatControllers.size(); i++) {
            if(chatControllers.get(i).getName().equals(from)) {
                chatControllers.get(i).addLabel(text);
            }
        }
    }

    public void addChatController(ChatController cc) {
       chatControllers.add(cc);
    }

    public void removeChatController(ChatController cc) {
        for(int i=0; i<chatControllers.size(); i++) {
            if (chatControllers.get(i) == cc) {
                chatControllers.remove(i);
            }
        }
    }

    public void displayChatStage(String from) {
        if (nameInput.getText().equals(from)) {
            return;
        }
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("chat-view.fxml"));
        Scene scene = null;

        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ChatController cc = fxmlLoader.getController();
        cc.setName(from);
        cc.setClient(client);
        stage.setTitle("Chat");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            removeChatController(cc);
            stage.close();
        });

        addChatController(cc);
    }

    public void closeRequestHandler() {
        if(!(client == null)) {
            return;
        } else if (!client.isClosed()) {
            client.sendDisconnectMessage();
        }
    }
}
