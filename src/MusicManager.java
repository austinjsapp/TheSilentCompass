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

// This class handles playing, stopping, and looping music with fades.
public class MusicManager {

    private Clip currentClip;
    private Timer fadeTimer;

    // Increased fade time from 500ms to 1.5 seconds
    private final int FADE_DURATION = 1500; // <-- CHANGED

    private final int FADE_STEPS = 20;
    private final int FADE_DELAY = FADE_DURATION / FADE_STEPS;

    private final float GAIN_SILENT = -80.0f;
    private final float GAIN_FULL = 0.0f;
    private final float GAIN_RANGE = GAIN_FULL - GAIN_SILENT;

    // (Helper methods are unchanged)
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

    // (fadeIn is unchanged)
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
                float volume = (float) step[0] / FADE_STEPS;
                setVolume(currentClip, volume);

                if (step[0] >= FADE_STEPS) {
                    setVolume(currentClip, 1.0f);
                    ((Timer) e.getSource()).stop();
                }
            });
            fadeTimer.start();

        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    // (fadeOut is unchanged)
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
            float volume = 1.0f - ((float) step[0] / FADE_STEPS);
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

    // (stopMusic is unchanged)
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

    // (setMuted is unchanged)
    public void setMuted(boolean mute) {
        if (currentClip != null && currentClip.isControlSupported(BooleanControl.Type.MUTE)) {
            BooleanControl muteControl = (BooleanControl) currentClip.getControl(BooleanControl.Type.MUTE);
            muteControl.setValue(mute);
        }
    }
}