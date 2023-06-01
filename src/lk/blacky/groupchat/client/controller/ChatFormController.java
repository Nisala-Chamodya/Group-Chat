package lk.blacky.groupchat.client.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatFormController {
    public String userName;
    public AnchorPane pane;
    public Label lblUserName;
    public Label lblDate;

    public void initialize(){
        setDateAndTime();
    }
    public void setUserName(String userName) {
        this.userName=userName;
        lblUserName.setText(this.userName);
    }
    private void setDateAndTime() {

        Timeline time = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss ");
                    lblDate.setText(LocalDateTime.now().format(formatter));
                }), new KeyFrame(Duration.seconds(1)));
        time.setCycleCount(Animation.INDEFINITE);
        time.play();

    }




}
