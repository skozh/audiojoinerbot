/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package audiojoinerbot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author shyjuk
 */
public class AudioBot extends TelegramLongPollingBot  {

    public AudioBot() {
        
        this.audiolist = new ArrayList<>();
        this.config = new Config();
    }
    
    private static final String default_message = "What shall we do today? If you want to compress audio, send /compress. If you want to merge audio, send /merge";
    private static int flag = 1;
    private List<String> audiolist;
    private Config config;
    private final static Logger logger = LoggerFactory.getLogger(AudioBot.class);
        
    @Override
    public void onUpdateReceived(Update update) {
        
        SendMessage sendMessage = new SendMessage();
        if (update!= null && update.hasMessage()) {
            String messageText = null;
            try{
                messageText = update.getMessage().getText();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            if (messageText != null){
                logger.info(messageText);
                if (messageText.equals("/merge")){
                    flag = 2;
                    audiolist.clear();
                    sendMessage.setText("OK, let's merge audio. Please send me the audio files. After you send all the files to merge in order, send /end.");
                    try {
                        sendMessage.setChatId(update.getMessage().getChatId().toString());
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    } 
                }    
                else if (messageText.equals("/compress")){
                    flag = 1;
                    audiolist.clear();
                    sendMessage.setText("Ok, let's compress audio. Please send me the audio files (<20MB). After you send all the files to compress, send /end");
                    try {
                        sendMessage.setChatId(update.getMessage().getChatId().toString());
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    } 
                }
                
                else if (messageText.equals("/end")){
                    String msgTxt;
                    if (audiolist.isEmpty()){
                        msgTxt = "No files received.";
                        try {
                            sendMessage.setText(msgTxt);
                            sendMessage.setChatId(update.getMessage().getChatId().toString());
                            execute(sendMessage);
                            
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        } 
                    }
                    else{
                        msgTxt = "Please wait for your files.";
                        sendMessage.setText(msgTxt);
                        try {
                            sendMessage.setChatId(update.getMessage().getChatId().toString());
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        AudioManager am = new AudioManager(audiolist);
                        if (flag == 2){
                            //Merge Audio
                            am.audioJoiner();
                        }
                        else{
                            //Compress Audio
                            am.audioSizeReducer();
                            
                        }
                        if (!am.compressedAudioList.isEmpty()){
                            for (String audiopath: am.compressedAudioList){
                                SendDocument sendDocument = new SendDocument();
                                sendDocument.setChatId(update.getMessage().getChatId().toString());
                                InputFile inputFile = new InputFile(new File(audiopath));
                                sendDocument.setDocument(inputFile);
                                try {
                                    execute(sendDocument);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                } 
                            }
                        }
                        else{
                            msgTxt = "Sorry, compression failed.";
                            sendMessage.setText(msgTxt);
                            try {
                                sendMessage.setChatId(update.getMessage().getChatId().toString());
                                execute(sendMessage);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                        FileController.tempFileCleaner();
                        audiolist.clear();
                    }
                    
                }
                else
                {
                    audiolist.clear();
                    sendMessage.setText("Hi "+ update.getMessage().getFrom().getFirstName()+",\n"+default_message);
                    try {
                        sendMessage.setChatId(update.getMessage().getChatId().toString());
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    } 
                }
            }
            else {
                if (update.getMessage().hasAudio()){   
                    logger.info("Audio received!");
                    Audio audiofile = update.getMessage().getAudio();
                    String audiofilename = audiofile.getFileName();
                    String audiofileid = audiofile.getFileId();
                    
                    String filename_path = config.tempdir+"/"+audiofilename.replaceAll("[^a-zA-Z0-9.]", "_");
                    GetFile getFile = new GetFile();
                    getFile.setFileId(audiofileid);
                    try {
                        org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                        downloadFile(file, new File(filename_path));
                        audiolist.add(filename_path);
                        
                    } catch (TelegramApiException ex) {
                        logger.error(ex.getMessage());
                    }
                }
                else if (update.getMessage().hasDocument()){    
                    logger.info("Document received!");
                    Document audiofile = update.getMessage().getDocument();
                    String audiofilename = audiofile.getFileName();
                    String audiofileid = audiofile.getFileId();
                    String filename_path = config.tempdir+"/"+audiofilename.replaceAll("[^a-zA-Z0-9.]", "_");
                    GetFile getFile = new GetFile();
                    getFile.setFileId(audiofileid);
                    try {
                        org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                        downloadFile(file, new File(filename_path));
                        audiolist.add(filename_path);
                        
                    } catch (TelegramApiException ex) {
                        logger.error(ex.getMessage());
                    }
                    
                }
                else
                {
                    audiolist.clear();
                    sendMessage.setText("Hi "+ update.getMessage().getFrom().getFirstName()+",\n"+default_message);
                    try {
                        sendMessage.setChatId(update.getMessage().getChatId().toString());
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    } 
                }
            }
        }
    }


    @Override
    public String getBotToken() {
        return config.bot_token;
    }

    @Override
    public String getBotUsername() {
        return config.bot_user;
    }

}
