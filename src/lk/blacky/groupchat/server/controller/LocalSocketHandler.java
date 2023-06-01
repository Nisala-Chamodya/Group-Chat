package lk.blacky.groupchat.server.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;

public class LocalSocketHandler implements Runnable{
    public Socket socket;
    public List<LocalSocketHandler> localSocketHandlerList;
    public DataInputStream inputStream;
    public DataOutputStream outputStream;
    public String type;

    public LocalSocketHandler(Socket socket,List<LocalSocketHandler> localSocketHandlerList){
        this.socket=socket;
        this.localSocketHandlerList=localSocketHandlerList;
    }

    @Override
    public void run() {
        try {
            inputStream=new DataInputStream(socket.getInputStream());
            outputStream=new DataOutputStream(socket.getOutputStream());

            while (socket.isConnected()){
               type= inputStream.readUTF();
               if (type.equalsIgnoreCase("text")){
                   sendText();
               }else {
                   sendFile();
               }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void sendFile() {
        try {
            String userName=inputStream.readUTF();

            byte[]sizeAr =new byte[4];
            inputStream.read(sizeAr);
            int size= ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr=new byte[size];
            inputStream.read(imageAr);

            for (LocalSocketHandler localSocketHandler :localSocketHandlerList){
                localSocketHandler.outputStream.writeUTF(type);
                localSocketHandler.outputStream.writeUTF(userName);
                localSocketHandler.outputStream.write(sizeAr);
                localSocketHandler.outputStream.write(imageAr);
                localSocketHandler.outputStream.flush();
            }


        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void sendText() {
        try{
            String userName=inputStream.readUTF();
            String message=inputStream.readUTF();
            System.out.println(userName);
            System.out.println(message);

            for (LocalSocketHandler localSocketHandler :localSocketHandlerList){
                localSocketHandler.outputStream.writeUTF(type);
                localSocketHandler.outputStream.writeUTF(userName);
                localSocketHandler.outputStream.writeUTF(message);
                localSocketHandler.outputStream.flush();
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
