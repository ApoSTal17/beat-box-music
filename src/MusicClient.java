
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MusicClient {

    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private final MainWindow mainWindow;
    private final Thread receiveMusicThread;

    protected MusicClient(MainWindow mainWindow) throws Exception {

        socket = new Socket("localhost", 4242);
        writer = new PrintWriter(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        this.mainWindow = mainWindow;
        System.out.printf("Network connection is established with server %s on port %s%n",
                socket.getInetAddress(), socket.getPort());

        writer.println(mainWindow.getUserName());

        receiveMusicThread = new Thread(new ReceiveMusic());
        receiveMusicThread.start();
    }

    protected void sendMessage(String message) {

        writer.println(message);
        writer.flush();
    }

    /*protected void disconnect() throws IOException {
        receiveMusicThread.interrupt();
        reader.close();
        socket.close();
        writer.close();
    }*/

    private class ReceiveMusic implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("(Debugging) Read: " + message);
                    mainWindow.setChatTextArea(message);
                }
            } catch (Exception ex) {
                mainWindow.showErrorMessage("Reading message error!", ex.getMessage());
            }
        }
    }
}
