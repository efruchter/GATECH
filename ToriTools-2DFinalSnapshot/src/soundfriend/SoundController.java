package soundfriend;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundController {
	
	public static enum Sounds {
		HAPPY("happy.wav"),
		HAPPY2("happy2.wav"),
		HUNGRY("hungry.wav"),
		SLEEPY("sleepy.wav"),
		GRUMBLE("grumble.wav");
		
		private String filename;
		
		Sounds(final String filename) {
			this.filename = filename;
		}
		
		public String filepath() {
			return "tamodatchi" + File.separator + "sounds" + File.separator + filename;
		}
	}

	private static Clip clip;

    // play the MP3 file to the sound card
    public static void play(Sounds sound, float energyLevel) {
    	String filepath = sound.filepath();
    	if (clip != null && clip.isActive()) {
    		return;
    	}
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
					new File(filepath));
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			FloatControl gainControl = 
					(FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(gainControl.getMaximum() * energyLevel);
			clip.start();
			
			
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

    }
    
}
