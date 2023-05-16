// This is an open source non-commercial project. Dear PVS-Studio, please check it.

// PVS-Studio Static Code Analyzer for C, C++, C#, and Java: https://pvs-studio.com

import java.io.*;
import java.net.Socket;

public class MusicClient {

    private final Socket socket;
    private final ObjectOutputStream objectWriter;
    private final ObjectInputStream objectReader;
    private final MainWindow mainWindow;
    private final Thread receiveMusicThread;
    public static final int PROTOCOL_SEND = 0,
                PROTOCOL_REQUEST = 1;


    protected MusicClient(MainWindow mainWindow) throws Exception {



        socket = new Socket("localhost", 4242);
        objectWriter = new ObjectOutputStream(socket.getOutputStream());
        objectReader = new ObjectInputStream(socket.getInputStream());

        this.mainWindow = mainWindow;
        System.out.printf("Network connection is established with server %s on port %s%n",
                socket.getInetAddress(), socket.getPort());

        objectWriter.writeObject(mainWindow.getUserName());

        receiveMusicThread = new Thread(new ReceiveMusic());
        receiveMusicThread.start();

        var v1 = new String("toLowerCase");
        var v2 = "toLowerCase";
        v1.toLowerCase();
        v1 = v1;
        System.out.println(v1);
        System.out.println(v1.equals(v2));
    }

    protected void sendMusicToServer(String nameMusic, boolean[][] backupCheckBoxes) throws Exception {

        objectWriter.writeInt(PROTOCOL_SEND);
        objectWriter.writeObject(nameMusic);
        objectWriter.writeObject(backupCheckBoxes);
        objectWriter.flush();
    }

    protected void requestMusic(String nameMusic) throws Exception {

        objectWriter.writeInt(PROTOCOL_REQUEST);
        objectWriter.writeObject(mainWindow.getUserName());
        objectWriter.writeObject(nameMusic);
        objectWriter.flush();
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
                Object objectMessage;
                while ((objectMessage = objectReader.readObject()) != null) {
                    if (objectMessage instanceof String) {
                        System.out.println("(Debugging) Read: " + objectMessage);
                        mainWindow.setChatTextArea(objectMessage.toString());
                    } else if (objectMessage instanceof boolean[][]) {
                        System.out.println("(Debugging) Read: boolean[][] array");
                        mainWindow.setChatTextArea("bool array!");
                    }
                }
            } catch (Exception ex) {
                mainWindow.showErrorMessage("Reading message error!", ex.getMessage());
            }
        }
    }
}
