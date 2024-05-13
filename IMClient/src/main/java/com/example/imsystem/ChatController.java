package com.example.imsystem;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    private String name;
    private Client client;

    @FXML
    private Label welcomeText;

    @FXML
    private Button sendButton;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    @FXML
    private ScrollPane sp_main;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double) newValue );
            }
        });
    }

    public void sendButtonOnClick(ActionEvent e) {
        String  messageToSend = tf_message.getText();
        if(!messageToSend.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5, 5, 5, 10));
            Text text = new Text(messageToSend);
            TextFlow textflow = new TextFlow(text);
            textflow.setStyle("-fx-color:rgb(239,242,255);" +
                    "-fx-background-color:rgb(15,125,242);" +
                    "-fx-background-radius:20px;");
            textflow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(0.934, 0.945, 0.996));
            hBox.getChildren().add(textflow);
            vbox_messages.getChildren().add(hBox);

            client.sendTextMessage(name, messageToSend);
            tf_message.clear();
        }
    }

    public void addLabel(String msgFromServer){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(msgFromServer);
        TextFlow textflow = new TextFlow(text);
        textflow.setStyle("-fx-background-color:rgb(233,233,235);"
                +"-fx-background-radius:20px;");
        textflow.setPadding(new Insets(5,10,5,10));
        hBox.getChildren().add(textflow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox_messages.getChildren().add(hBox);
            }
        });
    }

    public void setName(String name) {
        this.name = name;
        welcomeText.setText("Chat with " + name + ":");
    }

    public String getName() {
        return this.name;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
