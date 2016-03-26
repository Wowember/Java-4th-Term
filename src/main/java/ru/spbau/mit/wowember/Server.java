package ru.spbau.mit.wowember;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class Server {

    private static final int LIST_REQUEST = 1;
    private static final int GET_REQUEST = 2;

    private ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        new Thread(() -> {
            while(serverSocket != null && !serverSocket.isClosed()) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> {
                        try {
                            handleConnection(socket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private void handleConnection(Socket socket) throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        int requestType = inputStream.readInt();
        String path = inputStream.readUTF();
        if (requestType == LIST_REQUEST) {
            list(path, outputStream);
        } else if (requestType == GET_REQUEST){
            get(path, outputStream);
        }
    }

    private void list(String path, DataOutputStream outputStream) throws IOException {
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            File[] listFiles = directory.listFiles();
            if (listFiles == null) {
                outputStream.writeInt(0);
                return;
            }
            outputStream.writeInt(listFiles.length);
            for (File file: listFiles) {
                outputStream.writeUTF(file.getName());
                outputStream.writeBoolean(file.isDirectory());
            }
        } else {
            outputStream.writeInt(0);
        }
        outputStream.flush();
    }

    private void get(String path, DataOutputStream outputStream) throws IOException {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            outputStream.writeLong(Files.size(file.toPath()));
            Files.copy(file.toPath(), outputStream);
        } else {
            outputStream.writeLong(0);
        }
        outputStream.flush();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }
}
