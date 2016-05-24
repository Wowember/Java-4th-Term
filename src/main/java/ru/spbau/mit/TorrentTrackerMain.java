package ru.spbau.mit;

import java.io.IOException;

public final class TorrentTrackerMain {
    private TorrentTrackerMain() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        TorrentServer server = new TorrentServer();
        server.start();
        server.join();
    }
}
