package org.marvin;
import org.marvin.impls.FileSource;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LogGenerator {


    private String[] logThread ={

            "[     main]",
            "[     worker-1]",
            "[     worker-2]",
            "[     worker-3]",
            "[     executor-1]",
            "[     task-5]",

    };

    private String[] logLayers = {

            "INFO",
            "DEBUG",
            "WARN",
            "TRACE",
            "ERROR"
    };


    private String[] logTexts = {

            "Starting up an application",
            "Run task #1241",
            "Run task #717",
            "Connecting to storage:3121",
            "Application started successfully",
            "DB health-check failed",
            "Task #1241 has been completed"

    };



    private String logData =

                    "19-05-23T20:57:20.564Z INFO [     main] Starting up an application\n" +
                    "19-05-24T20:57:30.032Z INFO [     worker-1] Run task #1241\n" +
                    "19-05-25T20:57:32.421Z INFO [     worker-2] Run task #717\n" +
                    "19-05-26T20:57:33.843Z INFO [     main] Connecting to storage:3121\n" +
                    "19-05-27T20:57:58.931Z INFO [     worker-2] Task #717 has been completed\n" +
                    "19-05-28T20:57:59.001Z INFO [     main] Connection established\n" +
                    "19-05-29T20:58:14.315Z WARN [     worker-3] Run task #467\n" +
                    "19-05-30T20:58:15.917Z WARN [     worker-3] Task #467 is not configured\n" +
                    "19-06-01T20:58:34.143Z INFO [     worker-1] Task #1241 has been completed\n" +
                    "19-05-02T20:58:40.719Z INFO [     main] Application started successfully\n" +
                    "19-05-23T20:58:44.242Z INFO [     worker-2] Run task #937\n" +
                    "19-05-23T20:58:56.964Z ERROR [     worker-3] Task #467 finished with fail\n" +
                    "19-05-23T20:58:57.234Z WARN [     worker-3] Task #467 cannot be rescheduled\n" +
                    "19-05-23T20:59:21.622Z INFO [     main] DB health-check succeded\n" +
                    "19-05-23T20:59:32.421Z ERROR [     worker-1] Task #8272 crashed during startup\n" +
                    "19-05-23T20:59:33.642Z WARN [     worker-2] Task #937 has been completed\n" +
                    "19-05-23T20:59:33.186Z DEBUG [     worker-1] Task #8272 will be restarted\n" +
                    "19-05-23T20:59:39.037Z INFO [     worker-3] Run task #819\n" +
                    "19-05-23T20:59:43.146Z INFO [     worker-2] Run task #2911\n" +
                    "19-05-23T21:00:19.620Z INFO [     worker-3] Task #819 has been completed\n" +
                    "19-05-23T21:00:20.821Z ERROR [     main] DB health-check failed\n" +
                    "19-05-23T21:00:23.422Z INFO [     worker-2] Task #2911 has been completed";


    public String generateLogByDate(int logLinesSize ) throws ParseException {

        StringBuilder sb = new StringBuilder();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTime dateTime = LocalDateTime.now();

        while (logLinesSize > 0){


            sb.append(dateTime.format(df)).append(" ");
            sb.append(logLayers[new Random().nextInt(logLayers.length)]).append(" ");
            sb.append(logThread[new Random().nextInt(logThread.length)]).append(" ");
            sb.append(logTexts[new Random().nextInt(logTexts.length)]).append(" ");
            sb.append("\n");

            logLinesSize --;
            dateTime = dateTime.plus(Period.parse("P1D"));
        }

        return sb.toString();
    }

    public void saveLog(String log, String path){
        FileSource fileSource = new FileSource();
        fileSource.saveLog(log, path);
    }

    private String getSimpleLog(){
        return logData;
    }

    public String[] getLogTexts() {
        return logTexts;
    }

    public void setLogTexts(String[] logTexts) {
        this.logTexts = logTexts;
    }

    public String getLogData() {
        return logData;
    }

    public void setLogData(String logData) {
        this.logData = logData;
    }
}
