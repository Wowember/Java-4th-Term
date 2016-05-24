package ru.spbau.mit;

import java.io.IOException;

public final class TorrentClientMain {

    private TorrentClientMain() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        TorrentClientFrame frame = new TorrentClientFrame();
        frame.pack();
        frame.setVisible(true);
    }
}
