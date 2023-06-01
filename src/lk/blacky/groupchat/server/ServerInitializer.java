package lk.blacky.groupchat.server;

import lk.blacky.groupchat.server.controller.LocalSocketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerInitializer {
    public static void main(String[] args) {
        List<LocalSocketHandler> localSocketHandlerList=new ArrayList<>();
        ServerSocket serverSocket;
        Socket socket;
        try {
            serverSocket=new ServerSocket(2002);

            while (!serverSocket.isClosed()){
                System.out.println("Server is Waiting !!");
                socket = serverSocket.accept();
                System.out.println("A New Client Has Login!!");

                LocalSocketHandler localSocketHandler=new LocalSocketHandler(socket,localSocketHandlerList);
                localSocketHandlerList.add(localSocketHandler);

                Thread thread=new Thread(localSocketHandler);
                System.out.println("Thread Startd!!");
                thread.start();
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }
}
