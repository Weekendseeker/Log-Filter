package org.marvin.impls;

import org.marvin.api.LogSource;
import java.io.*;

/**
 *
 * This class {@code FileSource} implementation LogSource
 * interface for reading and writings files.*
 *
 */

public class FileSource implements LogSource {


    /**
     * Overloaded method for save log by path to file.
     * If file doesn't exists, will created new file.
     *
     * @param log
     *        input log
     *
     * @param path
     *        path to save
     *
     */

    @Override
    public void saveLog(String log,String path)  {

        if (log == null || log.equals("")){
            System.out.println("Log is empty");
            return;
        }

        if(path == null || log.equals("")){
            System.out.println("Log is empty");
            return;

        }

        saveLog(log.getBytes(),path);
    }

    /**
     * Write bytes data to file
     *
     * @param log
     *        log in bytes array
     *
     * @param path
     *        save paths
     */

    @Override
    public void saveLog(byte[] log,String path) {

        if(path == null || path.equals("")){

            System.out.println("Path " + path + " is empty");
            System.exit(1);

        }

        if(log.length == 0) {

            System.out.println("Log is empty");
            System.exit(0);
        }

        File file = new File(path);

        if(!file.exists()) {
            try {
                if(file.createNewFile()){
                    System.out.println("File " + path + " created successful" );
                }

            } catch (IOException e) {
                System.out.println("Error with create file " + path +
                                   " Perhaps there is no access.");

                System.exit(1);
            }
        }

        try(FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(log);

        }catch (IOException e){
            System.out.println("I/O Error");
            System.exit(1);
        }
    }

    /**
     *
     *Method for read log data from path
     *
     * @param path
     *        input Log
     *
     * @return logs bytes data
     */

    private byte[] getLogFile(String path) {

        File file = new File(path);
        if(!fileIsExist(path)) {
            System.out.println("File " + path + " is not exists");
            System.exit(1);
        }

        byte [] data = null;

        try (FileInputStream fileInputStream = new FileInputStream(file)){

            int avalible = fileInputStream.available();

            if(avalible > 0) {
                data = new byte[fileInputStream.available()];
                fileInputStream.read(data);
            }

        }catch (IOException e){
            System.out.println("I/O Error");
            System.exit(1);
        }
        return data;
    }

    /**
     *
     * Help method for validate files before
     * reading or saving. This method checking
     * file on format, on existing.
     *
     *
     * @param path
     * @return boolean
     */

    private boolean fileIsExist (String path){

        if (!path.matches(".*\\.log")){

            System.out.println("Not correct log format");
            return false;
        }

        File file = new File(path);

        if(!file.exists()){
            System.out.println("File : " + path + "\n" +
                    "is not exists check your filepath");
            return false;
        }

        if(!file.isFile()){
            System.out.println("File : " + path + "\n" +
                    "should not be a directory");
            return false;
        }

        return true;
    }

    /**
     * Methods for getting log from input strim
     *
     * @param inputStream
     *
     * @return byte array with data from input source
     *
     */

    @Override
    public byte[] getLog(InputStream inputStream)  {

        try {
            byte[] data = new byte[inputStream.available()];

            inputStream.read(data);

            inputStream.close();

            return data;

        } catch (IOException e) {

            System.out.println("I/O Error");
            System.exit(1);

        }

        return null;
    }

    @Override
    public byte[] getLog(String path)  {
        return getLogFile(path);
    }

}
