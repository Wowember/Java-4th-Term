package ru.spbau.mit.wowember;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class Server {

    private static final int LIST_REQUEST = 1;
    private static final int GET_REQUEST = 2;

    private ServerSocket serverSocket;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(() -> {
            while(serverSocket != null && !serverSocket.isClosed()) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> handleConnection(socket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverSocket = null;
    }

    private void handleConnection(Socket socket) {
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            int requestType = inputStream.readInt();
            String path = inputStream.readUTF();
            if (requestType == LIST_REQUEST) {
                list(path, outputStream);
            } else if (requestType == GET_REQUEST){
                get(path, outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                outputStream.flush();
        } else {
            outputStream.writeInt(0);
        }
    }

    private void get(String path, DataOutputStream outputStream) throws IOException {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            outputStream.writeLong(Files.size(file.toPath()));
            Files.copy(file.toPath(), outputStream);
        } else {
            outputStream.writeLong(0);
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }
}
