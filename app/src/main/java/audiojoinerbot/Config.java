/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package audiojoinerbot;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shyjuk
 */
public class Config {
    
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Config.class);    
    String tempdir;
    String listfile;
    String outputfile;
    String bot_token;
    String bot_user;

    public Config() {
        
        try {
            Properties prop = new Properties();
            prop.load(Config.class.getClassLoader().getResourceAsStream("application.properties"));
            this.tempdir = prop.getProperty("TEMPDIR");
            this.listfile = prop.getProperty("LISTFILE");
            this.outputfile = prop.getProperty("OUTPUTFILE");
            this.bot_token = prop.getProperty("BOTTOKEN");
            this.bot_user = prop.getProperty("BOTUSER");
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
        
    }    
}
