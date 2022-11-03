
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;

public class MainWindow extends JFrame {

    private final JCheckBox[][] checkBoxes = new JCheckBox[16][16];
    private final String[] instrumentsStr = {"Bass Drum", "Closed Hi-Hat",
        "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal",
        "Hand Clap", "High Tom", "Hi Bongo", "Maracas",
        "Whistle", "Low Conga", "Cowbell", "Vibraslap",
        "Low-mid Tom", "High Agogo", "Open Hi Conga"};
    private final int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60,
        70, 72, 64, 56, 68, 47, 67, 63};
    private final JLabel bpmLabel;
    private final String[] buttons = {"Start", "Stop", "Tempo Up", "Tempo Down"};
    private final Music music = new Music();
    private final Music musicPreview;
    private final JTextField loopTextFld;

    public MainWindow() {

        musicPreview = new Music();

        JPanel background = new JPanel(new BorderLayout());
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


        //  buttons

        Box buttonBox = new Box(BoxLayout.Y_AXIS);
        ButtonsListener buttonsListener = new ButtonsListener();
        for (String but : buttons) {
            JButton jBut = new JButton(but);
            jBut.addActionListener(buttonsListener);
            jBut.setPreferredSize(new Dimension(130, 20));
            buttonBox.add(jBut);
            buttonBox.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        bpmLabel = new JLabel("BPM: " + music.changeBPM(-1));
        buttonBox.add(bpmLabel);

        //  buttons other

        JPanel loopPanel = new JPanel(new FlowLayout());
        loopPanel.add(new JLabel("Loops: "));
        loopTextFld = new JTextField("0");
        loopTextFld.setPreferredSize(new Dimension(60, 20));
        loopPanel.add(loopTextFld);
        JButton loopButton = new JButton("OK");
        loopButton.setPreferredSize(new Dimension(130, 20));
        loopButton.addActionListener(buttonsListener);
        buttonBox.add(loopButton);
        buttonBox.add(loopPanel);

        //  instruments

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (String instr : instrumentsStr)
            nameBox.add(new Label(instr));

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);
        getContentPane().add(background);

        //  check boxes

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        JPanel checkBoxPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, checkBoxPanel);

        for (int i = 0; i < checkBoxes.length; i++) {
            for (int j = 0; j < checkBoxes[0].length; j++) {
                checkBoxes[i][j] = new JCheckBox();
                checkBoxes[i][j].addItemListener(new CheckBoxListener());
                checkBoxPanel.add(checkBoxes[i][j]);
            }
        }

        //  menu

        JMenuBar jMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem resetMenuItem = new JMenuItem("Reset");
        JMenuItem quitMenuItem = new JMenuItem("Quit");

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(resetMenuItem);
        fileMenu.add(quitMenuItem);
        jMenuBar.add(fileMenu);
        setJMenuBar(jMenuBar);

        JFileChooser jFileChooser = new JFileChooser("D:/JavaProjects/jaba3kurs/BeatBoxMusic");

        openMenuItem.addActionListener(e -> {
            jFileChooser.showOpenDialog(this);
            File file = jFileChooser.getSelectedFile();
            try {
                music.stop();
                restoreConfig(file);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.toString(),
                        "Ошибка открытия файла", JOptionPane.ERROR_MESSAGE);
            }
        });
        saveMenuItem.addActionListener(e -> {
            jFileChooser.showSaveDialog(this);
            File file = jFileChooser.getSelectedFile();
            try {
                music.stop();
                serializeConfig(file);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.toString(),
                        "Ошибка сохранения файла", JOptionPane.ERROR_MESSAGE);
            }
        });
        resetMenuItem.addActionListener(e -> {
            for (int i = 0; i < checkBoxes.length; i++) {
                for (int j = 0; j < checkBoxes[0].length; j++) {
                    checkBoxes[i][j].setSelected(false);
                }
            }
            bpmLabel.setText("BPM: " + 150.0f);
            music.setCurrentBPM(150.0f);
            loopTextFld.setText("0");
            music.setLoopCount(0);
            music.stop();
        });
        quitMenuItem.addActionListener(e ->
                System.exit(0));

        setTitle("Beat Box Music!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 50);
        setSize(700, 450);
        setVisible(true);
    }

    private class ButtonsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton but = (JButton)e.getSource();
            switch (but.getText()) {
                case "Start" -> {

                    for (int i = 0; i < checkBoxes.length; i++) {
                        for (int j = 0; j < checkBoxes[0].length; j++) {
                            if (checkBoxes[i][j].isSelected()) {
                                int note = j * 3 + 30;
                                music.createNode(note, instruments[i]);
                            }
                        }
                    }
                    music.play();
                    music.resetTrack();
                }
                case "Stop" ->
                    music.stop();

                case "Tempo Up" ->
                    bpmLabel.setText("BPM: " + music.changeBPM(Music.BPM_UP));

                case "Tempo Down" ->
                    bpmLabel.setText("BPM: " + music.changeBPM(Music.BPM_DOWN));

                case "OK" ->
                        music.setLoopCount(Integer.parseInt(loopTextFld.getText()));

                default -> { }
            }
        }
    }

    private class CheckBoxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            for (int i = 0; i < checkBoxes.length; i++) {
                for (int j = 0; j < checkBoxes[0].length; j++) {
                    if (e.getSource().equals(checkBoxes[i][j])) {
                        int note = j * 3 + 30;
                        musicPreview.createNode(note, instruments[i]);
                        musicPreview.play();
                        musicPreview.resetTrack();
                    }
                }
            }
        }
    }

    private void serializeConfig(File file) throws Exception {

        boolean[][] backupCheckBoxes = new boolean[checkBoxes.length][checkBoxes[0].length];
        for (int i = 0; i < backupCheckBoxes.length; i++) {
            for (int j = 0; j < backupCheckBoxes[0].length; j++) {
                backupCheckBoxes[i][j] = checkBoxes[i][j].isSelected();
            }
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream(file));
        objectOutputStream.writeObject(backupCheckBoxes);
        objectOutputStream.writeFloat(music.getCurrentBPM());
        objectOutputStream.writeInt(music.getLoop());
        objectOutputStream.close();
    }

    private void restoreConfig(File file) throws Exception {

        ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(file));
        boolean[][] backupCheckBoxes = (boolean[][]) objectInputStream.readObject();
        float bpm = objectInputStream.readFloat();
        int loops = objectInputStream.readInt();
        objectInputStream.close();

        music.setCurrentBPM(bpm);
        bpmLabel.setText("BPM: " + bpm);
        music.setLoopCount(loops);
        loopTextFld.setText(loops + "");
        for (int i = 0; i < backupCheckBoxes.length; i++) {
            for (int j = 0; j < backupCheckBoxes[0].length; j++) {
                checkBoxes[i][j].setSelected(backupCheckBoxes[i][j]);
            }
        }
    }

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
    }
}
