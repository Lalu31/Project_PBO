import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class SoundManager {
    public static void playSound(String soundFilePath) {
        try {
            System.out.println("Memutar suara dari path: " + soundFilePath); // Debugging
            File soundFile = new File(soundFilePath);
            if (soundFile.exists()) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start(); // Memulai pemutaran suara
            } else {
                System.out.println("File suara tidak ditemukan di path: " + soundFilePath); // Debugging
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}