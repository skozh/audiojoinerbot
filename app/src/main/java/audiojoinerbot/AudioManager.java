/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package audiojoinerbot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shyjuk
 */
public class AudioManager  {
    
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AudioManager.class);
    
    Config config;
    String extension;
    String outputfilewithext;
    List<String> audiolist;
    List<String> compressedAudioList;
    

    public AudioManager(List<String> audiolist) {
        this.config = new Config();
        
        this.audiolist = audiolist;
        this.compressedAudioList = new ArrayList<>();
        if (!audiolist.isEmpty()){
            String firstelement = audiolist.get(0);
            this.extension = firstelement.substring(firstelement.lastIndexOf(".")+1);
            this.outputfilewithext = config.tempdir+"/"+config.outputfile+"."+this.extension;
            
            File ofile = new File(this.outputfilewithext);
            if(ofile.exists() && ofile.isFile()){
                ofile.delete();
            }
        }
        File lfile = new File(config.listfile);
        if(lfile.exists() && lfile.isFile()){
            lfile.delete();
        }  
    }
    
    
    public void audioJoiner(){
        
        try {
            FileWriter lfileWriter = null;
            try {
                lfileWriter = new FileWriter(config.listfile);
                if (!audiolist.isEmpty()){
                    for (String audiofile: audiolist){
                        lfileWriter.write(String.format("file '%s'\n", audiofile));
                    }
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            } finally {
                try {
                    lfileWriter.close();
                } catch (IOException ex) {
                    logger.error(ex.getMessage());
                }
            }
            
            //Merging Audio
            String cmd = String.format("ffmpeg -f concat -safe 0 -i %s -c copy %s",config.listfile, outputfilewithext);
            logger.info(cmd);
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            
            this.audiolist.clear();
            this.audiolist.add(outputfilewithext);
            audioSizeReducer();
            
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        
    }
    
    
    public void audioSizeReducer(){
        
        try {
            
            
            for (String audioinput: this.audiolist){
                logger.info(String.format("Compressing %s", audioinput));
                String audiobasename = audioinput.substring(0,audioinput.lastIndexOf("."));
                String audiooutput = audiobasename+"_compressed.mp3";
                String cmd = String.format("ffmpeg -i %s -codec:a libmp3lame -qscale:a 9 %s",audioinput, audiooutput);
                logger.info(cmd);
                Process p = Runtime.getRuntime().exec(cmd);
                p.waitFor();
                this.compressedAudioList.add(audiooutput);            
            }
        }catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        
    }
    
    
    
}
