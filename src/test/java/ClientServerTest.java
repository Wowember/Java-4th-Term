import ru.spbau.mit.wowember.*;

import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ClientServerTest {

    private static final String TEST_DIRECTORY_PATH =
            "C:\\ABC\\Прога\\2015-2016\\AU-AU\\Java\\Java-4th-Term\\src\\test\\resources";

    @Test
    public void Test() {
        Server server = new Server(0);
        server.start();
        Client client = new Client();
        client.connect("localhost", server.getPort());
        List<FileInfo> list = client.executeList(TEST_DIRECTORY_PATH + "\\TestDirectory");
        assertEquals(list, Arrays.asList(new FileInfo("dir", true), new FileInfo("file", false)));
        client.disconnect();

        client.connect("127.0.0.1", server.getPort());
        File file = new File(TEST_DIRECTORY_PATH + "\\abc2");
        try {
            InputStream inputStream = client.executeGet(TEST_DIRECTORY_PATH + "\\abc");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            outputStream.write(buffer);
            outputStream.flush();
            assertEquals(Files.readAllLines(file.toPath()),
                    Files.readAllLines(Paths.get(TEST_DIRECTORY_PATH + "\\abc")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.disconnect();
        server.stop();
    }
}
