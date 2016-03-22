package ru.spbau.mit.wowember;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private static final int LIST_REQUEST = 1;
    private static final int GET_REQUEST = 2;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public void connect(String ipAddress, int port) {
        try {
            socket = new Socket(ipAddress, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream((socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<FileInfo> executeList(String path) {
        List<FileInfo> files = new ArrayList<>();
        try {
            outputStream.writeInt(LIST_REQUEST);
            outputStream.writeUTF(path);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return files;
        }

        try {
            int size = inputStream.readInt();
            for (int i = 0; i < size; i++) {
                String fileName = inputStream.readUTF();
                boolean isDirectory = inputStream.readBoolean();
                files.add(new FileInfo(fileName, isDirectory));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    public InputStream executeGet(String path) {
        byte[] buffer = new byte[0];
        try {
            outputStream.writeInt(GET_REQUEST);
            outputStream.writeUTF(path);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return new ByteArrayInputStream(buffer);
        }

        try {
            long fileSize = inputStream.readLong();
            buffer = new byte[(int)fileSize];
            inputStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(buffer);
    }

}
