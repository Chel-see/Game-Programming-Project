// for playing sound clips
import javax.sound.sampled.*;
import java.io.*;

import java.util.HashMap;				// for storing sound clips


public class SoundManager {				// a Singleton class
	HashMap<String, Clip> clips;

	private float volume;

	private static SoundManager instance = null;	// keeps track of Singleton instance

	private SoundManager () {
		Clip clip;
		clips = new HashMap<String, Clip>();
		
	

		clip = loadClip("sounds/nature-soundstropicaljunglebirds.wav");
		clips.put("background", clip);	
		
		clip = loadClip("sounds/KeyCollected.wav");
		clips.put("key", clip);

		clip = loadClip("sounds/RuneCollected.wav");
		clips.put("coin", clip);

		clip = loadClip("sounds/Growl.wav");
		clips.put("growl", clip);

		clip = loadClip("sounds/Splash.wav");
		clips.put("splash", clip);
		
		clip = loadClip("sounds/Slash.wav");
		clips.put("slice", clip);

	}


	public static SoundManager getInstance() {	// class method to get Singleton instance
		if (instance == null)
			instance = new SoundManager();
		
		return instance;
	}		


	public Clip getClip (String title) {

		return clips.get(title);		// gets a sound by supplying key
	}


    	public Clip loadClip (String fileName) {	// gets clip from the specified file
 		AudioInputStream audioIn;
		Clip clip = null;

		try {
    			File file = new File(fileName);
    			audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL()); 
    			clip = AudioSystem.getClip();
    			clip.open(audioIn);
		}
		catch (Exception e) {
 			System.out.println ("Error opening sound files: " + e);
		}
    		return clip;
    	}


    	public void playSound(String title, Boolean looping) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.setFramePosition(0);
			if (looping)
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			else
				clip.start();
		}
    	}


    	public void stopSound(String title) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.stop();
		}
    	}

		 public void setVolume (String title, float volume) {
        Clip clip = getClip(title);

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();

        gainControl.setValue(gain);
    }

}