
import java.lang.Thread;
import javax.sound.midi.Sequencer;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.MidiMessage;
import java.io.File;
import java.util.ArrayList;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiDevice;

public class PianoTeacher {

	public static void main( String[] args ) throws Exception {
		Receiver receiver = getMidiDeviceReceiver("Port1");

		ArrayList<MidiEvent> events = null;
		try {
			 events = getMidiEvents("happy_birthday.midi");
		}
		catch(InvalidMidiDataException e) {
			print("Cannot process midifile: " + e.toString());
		}
		catch(IOException e) {
			print("Cannot read midifile: " + e.toString());	
		}

		//Visualizer
		long previousTick = 0;
		/*for(MidiEvent event : events) {
			if(previousTick != event.getTick()) {
				Thread.sleep(event.getTick() - previousTick);
			}
			print(event.getMessage() + ": " + event.getTick());
			previousTick = event.getTick();
		}*/

		//Play
		previousTick = 0;
		if(receiver != null) {
			for(MidiEvent event : events) {			
				if(previousTick != event.getTick()) {
					Thread.sleep(event.getTick() - previousTick);
				}

				try {
					receiver.send(event.getMessage(), event.getTick());
				}
				catch(Exception e) {
					print(e.toString());
				}

				previousTick = event.getTick();
			}
		}

		/*try {
			
			ShortMessage myMsg = new ShortMessage();
			myMsg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);

			receiver.send(myMsg, -1);
		}
		catch(Exception e) {

		}*/
	}

	private static Receiver getMidiDeviceReceiver(String name) {
        MidiDevice device = null;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for(MidiDevice.Info info : infos) {
        	if(info.getName().startsWith(name)) {
         		try {
         			device = MidiSystem.getMidiDevice(info);
         		}
         		catch(MidiUnavailableException e) {
         			print("Error!");
                   return null;
         		}
         	}
         }

        if (device != null && !device.isOpen()) {
			try {
				device.open();
				print("Type: " + device.getClass().toString());
				//print("" + Boolean.valueOf(device instanceof Sequencer));
				return device.getReceiver();
			} catch (MidiUnavailableException e) {
				print("Error!");
				return null;
			}
		}
		return null;
    }

    private static ArrayList<MidiEvent> getMidiEvents(String file) throws InvalidMidiDataException, IOException {
    	ArrayList<MidiEvent> events = new ArrayList<MidiEvent>();

    	Sequence sequence = MidiSystem.getSequence(new File(file));
    	print("Sequence loaded, found " + sequence.getTracks().length + " tracks");
    	
    	Track track = sequence.getTracks()[1];
		for (int i = 0; i < track.size(); i++) {
			MidiEvent event = track.get(i);
			events.add(event);
		}

		print("Read events: " + events.size());
		return events;
    }

    private static void print(String message) {
    	System.out.println(message);
    }
}