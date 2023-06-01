package lk.blacky.groupchat.client.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatFormController {
    public String userName;
    public AnchorPane pane;
    public Label lblUserName;
    public Label lblDate;
    public JFXTextField txtMsg;
    public ScrollPane scrollpane;
    public VBox vBox;

    public Socket socket;
    public DataInputStream inputStream;
    public DataOutputStream outputStream;

    public File file;
    public ImageView img;
    public ImageView btnCancel;

    String type;
    public void initialize(){
        setDateAndTime();
        new Thread(()  ->{
            try {
                socket = new Socket("localhost", 2002);
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());

                while(socket.isConnected()){
                    type=inputStream.readUTF();
                    if (type.equalsIgnoreCase("text")){
                        setText();
                    }else {
                        setFile();
                    }
                }
                inputStream.close();
                outputStream.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }



        }).start();

        img.setVisible(false);
        btnCancel.setVisible(false);

        vBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scrollpane.setVvalue((Double) newValue);
            }
        });


    }

    private void setFile() {
        try{
            String userName = inputStream.readUTF();
            byte[] sizeAr = new byte[4];
            inputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr = new byte[size];
            inputStream.read(imageAr);

            HBox hBox = new HBox();

            if (userName.equalsIgnoreCase(this.userName)) {
                hBox.setAlignment(Pos.CENTER_RIGHT);
            } else {
                hBox.setAlignment(Pos.CENTER_LEFT);
            }

            hBox.setPadding(new Insets(5, 5, 5, 10));


            Image image1 = new Image(new ByteArrayInputStream(imageAr));

            ImageView imageView = new ImageView(image1);

            hBox.getChildren().add(imageView);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vBox.getChildren().add(hBox);
                }
            });



        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void setText(){
        try{
            String userName=inputStream.readUTF();
            String message=inputStream.readUTF();
            if (!message.isEmpty()){
                HBox hBox=new HBox();

                Text text=new Text(message);
                TextFlow textFlow=new TextFlow(text);
                if (userName.equalsIgnoreCase(this.userName)){
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5,5,5,10));

                    textFlow.setStyle("-fx-color : rgb(239, 242, 255);" +
                            "-fx-background-color: rgb(15, 125, 242);" +
                            "-fx-background-radius: 20px");

                    text.setFill(Color.color(0.934, 0.945, 0.996));




                }else {
                    text.setText(userName + " : " + message);

                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);" +
                            "-fx-background-radius: 20px");
                }
                text.setStyle("-fx-font-size: 20px;");
                textFlow.setPadding(new Insets(5, 10, 8, 10));
                hBox.getChildren().add(textFlow);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        vBox.getChildren().add(hBox);
                    }
                });


            }


        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    public void setUserName(String userName) {
        this.userName=userName;
        lblUserName.setText(this.userName);
    }
    private void setDateAndTime() {

        Timeline time = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss ");
                    lblDate.setText(LocalDateTime.now().format(formatter));
                }), new KeyFrame(Duration.seconds(1)));
        time.setCycleCount(Animation.INDEFINITE);
        time.play();

    }


    public void btnSendImgOnClickAction(MouseEvent mouseEvent) {
        try{
            if (!txtMsg.getText().isEmpty()){
                outputStream.writeUTF("text".trim());
                outputStream.writeUTF(this.userName.trim());
                outputStream.writeUTF(txtMsg.getText().trim());
                outputStream.flush();
                txtMsg.clear();
            }else if (null != this.file){
                BufferedImage bufferedImage = ImageIO.read(this.file);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();


                outputStream.writeUTF("picture".trim());
                outputStream.writeUTF(this.userName.trim());
                outputStream.write(size);
                outputStream.write(byteArrayOutputStream.toByteArray());

                outputStream.flush();

                txtMsg.setVisible(true);
                btnCancel.setVisible(true);
                img.setVisible(false);
                btnCancel.setVisible(false);
                file = null;


                System.out.println("Flushed: " + System.currentTimeMillis());

                System.out.println("Closing: " + System.currentTimeMillis());

            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }
        
    }

    public void btnAtachFileOnClickAction(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) lblUserName.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        this.file = fileChooser.showOpenDialog(stage);

        if (null != this.file) {


            BufferedImage bufferedImage = ImageIO.read(this.file);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

            byte[] array = byteArrayOutputStream.toByteArray();
            Image image = new Image(new ByteArrayInputStream(array));
            img.setImage(image);
            txtMsg.setVisible(false);
            img.setVisible(true);
            btnCancel.setVisible(true);
        }



    }

    public void btnCancelOnClickAction(MouseEvent mouseEvent) {
       txtMsg.clear();
       btnCancel.setVisible(false);
    }
}
