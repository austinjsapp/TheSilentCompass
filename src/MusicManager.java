import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import javax.sound.sampled.BooleanControl;

/**
 * Manages music playback with volume control, fading, and muting.
 */
public class MusicManager {

    private Clip currentClip;
    private Timer fadeTimer;
    private float currentVolume = 1.0f;

    private final int FADE_DURATION = 1500;
    private final int FADE_STEPS = 20;
    private final int FADE_DELAY = FADE_DURATION / FADE_STEPS;

    private final float GAIN_SILENT = -80.0f;
    private final float GAIN_FULL = 0.0f;
    private final float GAIN_RANGE = GAIN_FULL - GAIN_SILENT;

    private FloatControl getVolumeControl(Clip clip) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        }
        return null;
    }

    private void setVolume(Clip clip, float linearVolume) {
        FloatControl volumeControl = getVolumeControl(clip);
        if (volumeControl != null) {
            float dB = (linearVolume * GAIN_RANGE) + GAIN_SILENT;
            dB = Math.max(dB, volumeControl.getMinimum());
            dB = Math.min(dB, volumeControl.getMaximum());
            volumeControl.setValue(dB);
        }
    }

    /**
     * Set the overall volume (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        this.currentVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentClip != null) {
            setVolume(currentClip, currentVolume);
        }
    }

    public void fadeIn(String musicFilePath) {
        stopMusic();

        try {
            File musicFile = new File(musicFilePath);
            if (!musicFile.exists()) {
                System.err.println("Music file not found: " + musicFilePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);

            setVolume(currentClip, 0.0f);
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentClip.start();

            if (fadeTimer != null && fadeTimer.isRunning()) {
                fadeTimer.stop();
            }

            final int[] step = {0};

            fadeTimer = new Timer(FADE_DELAY, e -> {
                step[0]++;
                float volume = (float) step[0] / FADE_STEPS * currentVolume;
                setVolume(currentClip, volume);

                if (step[0] >= FADE_STEPS) {
                    setVolume(currentClip, currentVolume);
                    ((Timer) e.getSource()).stop();
                }
            });
            fadeTimer.start();

        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    public void fadeOut(Runnable onFadeComplete) {
        if (currentClip == null) {
            onFadeComplete.run();
            return;
        }

        if (fadeTimer != null && fadeTimer.isRunning()) {
            fadeTimer.stop();
        }

        final int[] step = {0};
        final Clip clipToFade = currentClip;
        currentClip = null;

        fadeTimer = new Timer(FADE_DELAY, e -> {
            step[0]++;
            float volume = currentVolume * (1.0f - ((float) step[0] / FADE_STEPS));
            setVolume(clipToFade, volume);

            if (step[0] >= FADE_STEPS) {
                ((Timer) e.getSource()).stop();
                clipToFade.stop();
                clipToFade.close();
                onFadeComplete.run();
            }
        });
        fadeTimer.start();
    }

    public void stopMusic() {
        if (fadeTimer != null && fadeTimer.isRunning()) {
            fadeTimer.stop();
        }
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
    }

    public void setMuted(boolean mute) {
        if (currentClip != null && currentClip.isControlSupported(BooleanControl.Type.MUTE)) {
            BooleanControl muteControl = (BooleanControl) currentClip.getControl(BooleanControl.Type.MUTE);
            muteControl.setValue(mute);
        }
    }
}