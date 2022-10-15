
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    private final JCheckBox[][] checkBoxes = new JCheckBox[16][16];
    private final JLabel[] labels = new JLabel[16];
    private final String[] instrumentsStr = {"Bass Drum", "Closed Hi-Hat",
        "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal",
        "Hand Clap", "High Tom", "Hi Bongo", "Maracas",
        "Whistle", "Low Conga", "Cowbell", "Vibraslap",
        "Low-mid Tom", "High Agogo", "Open Hi Conga"};
    private final int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60,
        70, 72, 64, 56, 68, 47, 67, 63};
    private final JPanel buttonsPanel, centerPanel;
    private final JLabel bpmLabel;
    private final JPanel[] linesJPanels = new JPanel[16];
    private final ArrayList<JButton> buttonsList = new ArrayList<>();
    private final String[] buttons = {"Start", "Stop", "Tempo Up", "Tempo Down"};
    private final Music music = new Music();
    private final Music musicPreview;
    private final JTextField loopTextFld;

    public MainWindow() {

        musicPreview = new Music();

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        for (int i = 0; i < linesJPanels.length; i++) {
            linesJPanels[i] = new JPanel();
            //linesComponents[i].setLayout(new FlowLayout(FlowLayout.LEFT));
            labels[i] = new JLabel(instrumentsStr[i], SwingConstants.LEFT);
            labels[i].setPreferredSize(new Dimension(200, 15));
            linesJPanels[i].add(labels[i]);
            for (int j = 0; j < checkBoxes.length; j++) {
                checkBoxes[i][j] = new JCheckBox();
                checkBoxes[i][j].addItemListener(new CheckBoxListener());
                checkBoxes[i][j].setPreferredSize(new Dimension(17, 15));
                linesJPanels[i].add(checkBoxes[i][j]);
            }
            linesJPanels[i].setPreferredSize(new Dimension(800, 20));
            centerPanel.add(linesJPanels[i]);
        }
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        //buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        for (String but : buttons)
            buttonsList.add(new JButton(but));
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        ButtonsListener buttonsListener = new ButtonsListener();
        for (JButton jBut : buttonsList) {
            jBut.addActionListener(buttonsListener);
            jBut.setPreferredSize(new Dimension(130, 20));
            buttonsPanel.add(jBut);
            buttonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        bpmLabel = new JLabel("BPM: " + music.changeBPM(-1));
        buttonsPanel.add(bpmLabel);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JLabel loopLabel = new JLabel("Loops:");
        loopTextFld = new JTextField("0");
        loopTextFld.setPreferredSize(new Dimension(70, 20));
        JButton loopButton = new JButton("OK");
        JPanel loopPanel = new JPanel();
        loopPanel.add(loopLabel);
        loopPanel.add(loopTextFld);
        loopButton.setPreferredSize(new Dimension(130, 20));
        loopButton.addActionListener(buttonsListener);
        buttonsPanel.add(loopButton);
        buttonsPanel.add(loopPanel);
        getContentPane().add(buttonsPanel, BorderLayout.EAST);

        setTitle("Beat Box Music!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 50);
        setSize(750, 450);
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

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
    }
}
