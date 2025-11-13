import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.File;

/**
 * Manages short sound effects with proper resource cleanup.
 */
public class SoundManager {

    /**
     * Plays a sound effect once and automatically releases resources.
     * @param sfxFilePath The full path to the sound effect file
     */
    public void playSfx(String sfxFilePath) {
        if (sfxFilePath == null) {
            System.err.println("SFX file path is null");
            return;
        }

        // Use a separate thread to avoid blocking the UI
        new Thread(() -> {
            Clip sfxClip = null;
            AudioInputStream audioStream = null;

            try {
                File sfxFile = new File(sfxFilePath);

                if (!sfxFile.exists()) {
                    System.err.println("SFX file not found: " + sfxFilePath);
                    return;
                }

                audioStream = AudioSystem.getAudioInputStream(sfxFile);
                sfxClip = AudioSystem.getClip();
                sfxClip.open(audioStream);

                // Add listener to clean up resources after sound finishes
                final Clip finalClip = sfxClip;
                final AudioInputStream finalStream = audioStream;

                sfxClip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        finalClip.close();
                        try {
                            if (finalStream != null) {
                                finalStream.close();
                            }
                        } catch (Exception e) {
                            // Ignore cleanup errors
                        }
                    }
                });

                sfxClip.start();

            } catch (Exception e) {
                System.err.println("Error playing SFX: " + e.getMessage());

                // Clean up on error
                if (sfxClip != null) {
                    sfxClip.close();
                }
                if (audioStream != null) {
                    try {
                        audioStream.close();
                    } catch (Exception ex) {
                        // Ignore
                    }
                }
            }
        }).start();
    }
}