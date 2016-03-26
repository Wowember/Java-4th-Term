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

    public void connect(String ipAddress, int port) throws IOException {
        socket = new Socket(ipAddress, port);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream((socket.getOutputStream()));
    }

    public void disconnect() throws IOException {
        socket.close();
    }

    public List<FileInfo> executeList(String path) throws IOException {

        outputStream.writeInt(LIST_REQUEST);
        outputStream.writeUTF(path);
        outputStream.flush();

        List<FileInfo> files = new ArrayList<>();
        int size = inputStream.readInt();
        for (int i = 0; i < size; i++) {
            String fileName = inputStream.readUTF();
            boolean isDirectory = inputStream.readBoolean();
            files.add(new FileInfo(fileName, isDirectory));
        }
        return files;
    }

    public InputStream executeGet(String path) throws IOException {
        outputStream.writeInt(GET_REQUEST);
        outputStream.writeUTF(path);
        outputStream.flush();

        long fileSize = inputStream.readLong();
        byte [] buffer = new byte[(int)fileSize];
        inputStream.read(buffer);
        return new ByteArrayInputStream(buffer);
    }

}
