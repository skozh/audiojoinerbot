/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package audiojoinerbot;

import java.io.File;

/**
 *
 * @author shyjuk
 */
public class FileController {
    
    public static void tempFileCleaner(){
        Config config = new Config();
        File tempdir = new File(config.tempdir);
        for(File file: tempdir.listFiles()){
            if (!file.isDirectory()){
                file.delete();
            }
        }
    }
}
