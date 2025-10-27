import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

// This class handles playing short, one-time sound effects.
public class SoundManager {

    // This method finds and plays a sound file once.
    public void playSfx(String sfxFilePath) {
        
        try {
            File sfxFile = new File(sfxFilePath);
            
            if (sfxFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(sfxFile);
                Clip sfxClip = AudioSystem.getClip();
                sfxClip.open(audioStream);
                sfxClip.start(); // Play the sound once
            } else {
                System.err.println("SFX file not found: " + sfxFilePath);
            }

        } catch (Exception e) {
            System.err.println("Error playing SFX: " + e.getMessage());
            e.printStackTrace();
        }
    }
}