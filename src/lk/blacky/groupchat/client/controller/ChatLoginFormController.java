package lk.blacky.groupchat.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatLoginFormController {
    public AnchorPane pane;
    public JFXTextField txtUserName;

    public void btnLoginOnAction(ActionEvent actionEvent) {
        if (txtUserName.getText().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "User Name Invalid Or Empty !", ButtonType.OK).show();
            txtUserName.setFocusColor(Paint.valueOf("Red"));
            txtUserName.requestFocus();
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/lk/blacky/groupchat/client/view/ChatForm.fxml"));
        Parent load = null;
        try {
            load = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChatFormController controller = fxmlLoader.getController();
        controller.setUserName(txtUserName.getText());
        Stage stage = new Stage();
        stage.setTitle(txtUserName.getText());
        stage.setScene(new Scene(load));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
        txtUserName.clear();


    }
}
