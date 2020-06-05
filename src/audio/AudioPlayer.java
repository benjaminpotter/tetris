package audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

// statically available method playAudio, plays an audio track
// only one track can be played at a time
public class AudioPlayer {

    static String filepath;

    static Clip clip;
    static boolean isPlaying;

    /*** playAudio ****************************************
     * start an audio clip                                *
     ******************************************************/
    // plays an audio file
    public static void playAudio(String filepath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioPlayer.filepath = filepath;

        // turn file into stream
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( new File(filepath).getAbsoluteFile());

        // clip ref
        clip = AudioSystem.getClip();

        // pass the clip ref the audio stream
        clip.open(audioInputStream);

        // set the clip to loop
        clip.loop(Clip.LOOP_CONTINUOUSLY);

        // play the clip
        clip.start();
        isPlaying = true;
    }

    /*** stopAudio ****************************************
     * stop an audio clip                                *
     ******************************************************/
    public static void stopAudio() {
        if (isPlaying) {
            clip.stop();
            clip.close();
            isPlaying = false;
        }
    }
}
