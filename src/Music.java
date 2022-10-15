import javax.sound.midi.*;
import java.util.ArrayList;

public class Music {
    private Sequencer sequencer;
    private static final int
            NOTE_START = 144,
            NOTE_END = 128,
            CHANGE_INSTR = 192,
            CONTROLLER_EVENT = 176,
            TICK_DELTA = 2,
            BPM_DELTA = 10;
    public static final int
            BPM_DOWN = 0,
            BPM_UP = 1,
            BPM_GET = -1;
    private ArrayList<MidiEvent> midiEventsList = new ArrayList<>();
    private int tickCounter = 0;
    private int currentInstrument = 0;
    private float currentBPM = 150;
    private int loop = 0;

    public void createNode(int note, int instrument) {
        try {
            if (currentInstrument != instrument) {
                midiEventsList.add(createMidiEvent(CHANGE_INSTR, 1, instrument, 0, tickCounter));
                currentInstrument = instrument;
            }
            midiEventsList.add(createMidiEvent(NOTE_START, 1, note, 100, tickCounter));
            tickCounter += TICK_DELTA;
            midiEventsList.add(createMidiEvent(NOTE_END, 1, note, 100, tickCounter));
            tickCounter += TICK_DELTA;

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public float changeBPM(int action) {
        if (action == 0 && currentBPM > 120)
            currentBPM -= BPM_DELTA;
        else if (action == 1 && currentBPM < 320)
            currentBPM += BPM_DELTA;
        if (sequencer != null)
            sequencer.setTempoInBPM(currentBPM);
        return currentBPM;
    }

    public void setLoopCount(int loop) {
        this.loop = loop;
    }

    public void resetTrack() {
        midiEventsList.clear();
        tickCounter = 0;
    }

    public void play() {
        try {
            if (sequencer == null) {
                sequencer = MidiSystem.getSequencer();
                sequencer.open();
            }
            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();

            for (var midiEvent : midiEventsList) {
                track.add(midiEvent);
            }
            sequencer.setLoopCount(loop);
            sequencer.setSequence(seq);
            sequencer.setTempoInBPM(currentBPM);
            sequencer.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        if (sequencer != null) {
            sequencer.stop();
        }
    }

    private MidiEvent createMidiEvent(int command,
                              int channel, int data,
                              int volume, int tick) throws InvalidMidiDataException{
         ShortMessage a = new ShortMessage();
         a.setMessage(command, channel, data, volume);
         return new MidiEvent(a, tick);
    }
}