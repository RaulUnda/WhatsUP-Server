package com.example.whatsupserver;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Server(ServerSocket serverSocket) {
        try {
            this.serverSocket = serverSocket;
            this.socket = serverSocket.accept();
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){
            System.out.println("Error al crear el servidor");
            e.printStackTrace();
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }
    public void sendMsgToClient(String msgToClient, int Key, int Pub_Key){
        try{
            if(Key > 0 && Pub_Key == 0)
            {
                String encryptedToMsg = Encrypt(msgToClient, Key);
                bufferedWriter.write(encryptedToMsg);
            }
            else if(Pub_Key != 0)
            {
                String encryptedToMsg = Encrypt(msgToClient, Pub_Key);
                bufferedWriter.write(encryptedToMsg);
            }
            else {
                bufferedWriter.write(msgToClient);
            }
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Error al mandar el mensaje al cliente");
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receiveMsgFromClient(VBox vBox, int Key, int Pub_Key){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()){
                   try {
                       String msgFromClient  = bufferedReader.readLine();
                       if(Key > 0 && Pub_Key == 0)
                       {
                           String encryptedFromMsg = Encrypt(msgFromClient, Key);
                           Controller.addLabel(encryptedFromMsg, vBox);
                       }
                       else if(Pub_Key != 0)
                       {
                           String encryptedFromMsg = Encrypt(msgFromClient, Pub_Key);
                           Controller.addLabel(encryptedFromMsg, vBox);
                       }
                       else {
                           Controller.addLabel(msgFromClient, vBox);
                       }
                   }catch (IOException e){
                       e.printStackTrace();
                       System.out.println("Error al recibir el mensaje del cliente");
                       closeAll(socket, bufferedReader, bufferedWriter);
                       break;
                   }
                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String Encrypt(String msg, int key){
        String encrypted = "";
        for(int i=0; i<msg.length(); i++)
        {
            char letter = msg.charAt(i);
            if (letter >= 'a' && letter <= 'z'){
                letter = (char)(letter + key);
                if(letter > 'z') {
                    letter = (char)(letter - 'z' + 'a' - 1);
                }
                encrypted += letter;
            }
            else if (letter >= 'A' && letter <= 'Z'){
                letter = (char)(letter + key);
                if (letter > 'Z'){
                    letter = (char)(letter - 'Z' + 'A' -1);
                }
                encrypted += letter;
            }
            else{
                encrypted += letter;
            }
        }
        return encrypted;
    }
}
