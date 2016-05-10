package ru.spbau.mit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ClientProtocol {
    public static final int STAT_QUERY = 1;
    public static final int GET_QUERY = 2;
    public static final int PART_SIZE = 10 * 1024 * 1024;
    private ClientProtocol() {

    }

    public static int getQueryType(DataInputStream inputStream) throws IOException {
        return inputStream.readByte();
    }

    //GET QUERY
    public static GetQueryData getGetQueryData(DataInputStream inputStream) throws IOException {
        return new GetQueryData(inputStream.readInt(), inputStream.readInt());
    }

    public static void getGetQueryResponse(DataOutputStream outputStream, byte[] part) throws IOException {
        outputStream.write(part);
        outputStream.flush();
    }

    public static byte[] makeGetQuery(DataInputStream inputStream, DataOutputStream outputStream, int fileId,
                                      int partId, int partSize) throws IOException {
        outputStream.writeByte(GET_QUERY);
        outputStream.writeInt(fileId);
        outputStream.writeInt(partId);
        outputStream.flush();
        byte[] buffer = new byte[partSize];
        int readBytes = 0;
        while (readBytes < partSize) {
            readBytes += inputStream.read(buffer, readBytes, partSize - readBytes);
        }
        return buffer;
    }

    //STAT QUERY
    public static int getStatQueryId(DataInputStream inputStream) throws IOException {
        return inputStream.readInt();
    }

    public static void statQueryResponse(DataOutputStream outputStream,
                                         List<Integer> availableParts) throws IOException {
        outputStream.writeInt(availableParts.size());
        for (int id : availableParts) {
            outputStream.writeInt(id);
        }
        outputStream.flush();
    }

    public static List<Integer> makeStatQuery(DataInputStream inputStream, DataOutputStream outputStream,
                                              int fileId) throws IOException {
        outputStream.writeByte(STAT_QUERY);
        outputStream.writeInt(fileId);
        outputStream.flush();
        int count = inputStream.readInt();
        List<Integer> parts = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            parts.add(inputStream.readInt());
        }
        return parts;
    }

    public static class GetQueryData {
        private final int id;
        private final int part;

        GetQueryData(int id, int part) {
            this.id = id;
            this.part = part;
        }

        public int getId() {
            return id;
        }

        public int getPart() {
            return part;
        }
    }
}
