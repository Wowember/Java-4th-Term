package ru.spbau.mit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class TrackerProtocol {
    public static final int LIST_QUERY = 1;
    public static final int UPLOAD_QUERY = 2;
    public static final int SOURCES_QUERY = 3;
    public static final int UPDATE_QUERY = 4;
    public static final int SERVER_PORT = 8081;
    public static final int PORT_COUNT = 1 << 16;
    public static final int HASH_NUMBER = 31;

    public static final int TIME_BETWEEN_UPDATE_QUERIES = 60 * 1000;
    private static final int IP_LENGTH_IN_BYTES = 4;

    public static class TrackerFileEntry {

        private final String fileName;
        private int id;
        private final long size;

        TrackerFileEntry(int id, String fileName, long size) {
            this.fileName = fileName;
            this.id = id;
            this.size = size;
        }

        public String getFileName() {
            return fileName;
        }

        public int getId() {
            return id;
        }

        public long getSize() {
            return size;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TrackerFileEntry && ((TrackerFileEntry) obj).id == id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    public static class ClientEntry {

        private byte[] ip = new byte[IP_LENGTH_IN_BYTES];
        private int port;
        private long lastUpdateQueryTime = System.currentTimeMillis();

        ClientEntry(Socket socket) {
            this.ip = socket.getInetAddress().getAddress();
        }

        ClientEntry(byte[] ip, int port) {
            if (ip.length != IP_LENGTH_IN_BYTES) {
                throw new IllegalArgumentException("Incorrect ip: " + Arrays.toString(ip));
            }
            if (port < 0) {
                port = PORT_COUNT + port;
            }
            this.ip = ip;
            this.port = port;
        }

        public byte[] getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public long getLastUpdateQueryTime() {
            return lastUpdateQueryTime;
        }

        public void setIp(byte[] ip) {
            this.ip = ip;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public void setLastUpdateQueryTime(long lastUpdateQueryTime) {
            this.lastUpdateQueryTime = lastUpdateQueryTime;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(ip) * HASH_NUMBER + port;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ClientEntry) {
                ClientEntry entry = (ClientEntry) obj;
                return Arrays.equals(ip, entry.ip) && port == entry.port;
            }
            return false;
        }
    }

    private TrackerProtocol() {

    }


    public static int getQueryType(DataInputStream inputStream) throws IOException {
        return inputStream.readByte();
    }

    //UPLOAD QUERY
    public static TrackerFileEntry getUploadQueryData(DataInputStream inputStream) throws IOException {
        return new TrackerFileEntry(-1, inputStream.readUTF(), inputStream.readLong());
    }

    public static void uploadQueryResponse(DataOutputStream outputStream, int id) throws IOException {
        outputStream.writeInt(id);
        outputStream.flush();
    }

    public static int makeUploadQuery(DataInputStream inputStream, DataOutputStream outputStream,
                                      String fileName, long size) throws IOException {
        outputStream.writeByte(UPLOAD_QUERY);
        outputStream.writeUTF(fileName);
        outputStream.writeLong(size);
        outputStream.flush();
        return inputStream.readInt();
    }

    //SOURCES QUERY
    public static int getSourcesQueryFileId(DataInputStream inputStream) throws IOException {
        return inputStream.readInt();
    }

    public static void sourcesQueryResponse(DataOutputStream outputStream,
                                            Set<ClientEntry> clients) throws IOException {
        outputStream.writeInt(clients.size());
        for (ClientEntry client : clients) {
            for (int i = 0; i < IP_LENGTH_IN_BYTES; i++) {
                outputStream.writeByte(client.ip[i]);
            }
            outputStream.writeShort(client.port);
        }
        outputStream.flush();
    }

    public static List<ClientEntry> makeSourcesQuery(DataInputStream inputStream, DataOutputStream outputStream,
                                                     int fileId) throws IOException {
        outputStream.writeByte(SOURCES_QUERY);
        outputStream.writeInt(fileId);
        outputStream.flush();
        int count = inputStream.readInt();
        List<ClientEntry> entries = new ArrayList<>();
        byte[] ip = new byte[IP_LENGTH_IN_BYTES];

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < IP_LENGTH_IN_BYTES; j++) {
                ip[j] = inputStream.readByte();
            }
            entries.add(new ClientEntry(Arrays.copyOf(ip, IP_LENGTH_IN_BYTES), inputStream.readShort()));
        }
        return entries;
    }

    //UPDATE QUERY
    public static UpdateQueryData getUpdateQueryData(DataInputStream inputStream) throws IOException {
        short port = inputStream.readShort();
        int count = inputStream.readInt();
        int[] ids = new int[count];
        for (int i = 0; i < count; i++) {
            ids[i] = inputStream.readInt();
        }
        return new UpdateQueryData(port, ids);
    }

    public static void updateQueryResponse(DataOutputStream outputStream, boolean b) throws IOException {
        outputStream.writeBoolean(b);
        outputStream.flush();
    }

    public static boolean makeUpdateQuery(DataInputStream inputStream, DataOutputStream outputStream,
                                          int port, int[] fileIds) throws IOException {
        outputStream.writeByte(UPDATE_QUERY);
        outputStream.writeShort(port);
        outputStream.writeInt(fileIds.length);
        for (int id : fileIds) {
            outputStream.writeInt(id);
        }
        outputStream.flush();
        return inputStream.readBoolean();
    }

    //LIST QUERY
    public static void listQueryResponse(DataOutputStream outputStream,
                                         Set<TrackerFileEntry> files) throws IOException {
        outputStream.writeInt(files.size());
        for (TrackerFileEntry entry : files) {
            outputStream.writeInt(entry.id);
            outputStream.writeUTF(entry.fileName);
            outputStream.writeLong(entry.size);
        }
        outputStream.flush();
    }

    public static List<TrackerFileEntry> makeListQuery(DataInputStream inputStream,
                                                DataOutputStream outputStream) throws IOException {
        outputStream.writeByte(LIST_QUERY);
        outputStream.flush();
        int count = inputStream.readInt();
        List<TrackerFileEntry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entries.add(new TrackerFileEntry(inputStream.readInt(), inputStream.readUTF(),
                    inputStream.readLong()));
        }
        return entries;
    }

    public static class UpdateQueryData {

        private final int port;
        private final int[] fileIds;

        public UpdateQueryData(int port, int[] ids) {
            if (port < 0) {
                this.port = PORT_COUNT + port;
            } else {
                this.port = port;
            }
            this.fileIds = ids;
        }

        public int getPort() {
            return port;
        }

        public int[] getFileIds() {
            return fileIds;
        }
    }
}
