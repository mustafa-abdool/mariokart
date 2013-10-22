
//Midi Class I took from somewhere
//http://blog.taragana.com/index.php/archive/how-to-play-a-midi-file-from-a-java-application/


import javax.sound.midi.*;
import java.io.*;

/** Plays a midi file provided on command line */
public class MidiPlayer {
    public static void main(String args[]) {
        // Argument check
        if(args.length == 0) {
        	System.out.println("OK");
            helpAndExit();
        }
        String file = args[0];
        if(!file.endsWith(".mid")) {
        	System.out.println(file);
            helpAndExit();
        }
        File midiFile = new File(file);
        // Play once
        try {
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.setSequence(MidiSystem.getSequence(midiFile));
            sequencer.open();
            sequencer.start();
            while(true) {
                if(sequencer.isRunning()) {
                    try {
                        Thread.sleep(1000); // Check every second
                    } catch(InterruptedException ignore) {
                        break;
                    }
                } else {
                    break;
                }
            }
            // Close the MidiDevice & free resources
            sequencer.stop();
            sequencer.close();
        } catch(MidiUnavailableException mue) {
            System.out.println("Midi device unavailable!");
        } catch(InvalidMidiDataException imde) {
            System.out.println("Invalid Midi data!");
        } catch(IOException ioe) {
            System.out.println("I/O Error!");
        } 

    }  

    /** Provides help message and exits the program */
    private static void helpAndExit() {
        System.out.println("Usage: java MidiPlayer midifile.mid");
        System.exit(1);
    }
}
