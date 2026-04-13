package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {

    GamePanel gp;

    public Config(GamePanel gp) {
        this.gp = gp;
    }

    public void saveConfig() {
        try { 
            BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt")); 
            
            //Save fullscreen setting
            if (gp.fullScreenOn == true) { bw.write("On"); }
            if (gp.fullScreenOn == false) { bw.write("Off"); }
            bw.newLine();

            //Save music volume setting
            bw.write(String.valueOf(gp.music.volumeScale));
            bw.newLine();

            //Save sound effect volume setting
            bw.write(String.valueOf(gp.soundEffect.volumeScale));
            bw.newLine();

            bw.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void loadConfig() {
        try {
            InputStream is = getClass().getResourceAsStream("/config.txt");
            if (is == null) { is = getClass().getResourceAsStream("config.txt"); }
            if (is == null) {
                java.io.File f = new java.io.File("config.txt");
                if (f.exists()) { is = new java.io.FileInputStream(f); }
            }
            
            if (is == null) {
                System.err.println("Config file not found, using defaults");
                return;
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String s = br.readLine();

            //Load fullscreen setting
            if (s.equals("On")) { gp.fullScreenOn = true; }
            if (s.equals("Off")) { gp.fullScreenOn = false; }

            //Load music volume setting
            s = br.readLine();
            gp.music.volumeScale = Integer.parseInt(s);

            //Load sound effect volume setting
            s = br.readLine();
            gp.soundEffect.volumeScale = Integer.parseInt(s);

            br.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
