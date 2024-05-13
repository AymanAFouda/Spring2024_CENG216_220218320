package com.example.imsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserItemController implements Initializable {

    @FXML
    private Label activeUserName;

    @FXML
    private Button chatButton;

    private String name;
    private Client client;
    private Controller mainController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setData(String name, Client client, Controller mainController) {
        this.name = name;
        this.client = client;
        this.mainController = mainController;
        activeUserName.setText(name);
    }

    public void setButtonOnClick(ActionEvent e) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("chat-view.fxml"));
        Scene scene = null;

        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        ChatController cc = fxmlLoader.getController();
        cc.setName(name);
        cc.setClient(client);
        stage.setTitle("Chat");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            mainController.removeChatController(cc);
            stage.close();
        });

        client.sendChatMessage(name);

        mainController.addChatController(cc);
    }
}
