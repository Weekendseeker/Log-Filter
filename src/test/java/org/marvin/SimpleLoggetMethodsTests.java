package org.marvin;

import org.junit.Assert;
import org.junit.Test;
import org.marvin.impls.FileSource;
import org.marvin.impls.SimpleLogFilter;

import java.text.ParseException;

public class SimpleLoggetMethodsTests {


    private String filterBylog =
            "19-05-23T20:57:20.564Z INFO [     main] Starting up an application\n" +
            "19-05-24T20:57:25.872Z INFO [     main] Initializing a worker pool\n" +
            "19-05-25T20:57:30.032Z INFO [     worker-1] Run task #1241\n" +
            "19-05-26T20:57:32.421Z INFO [     worker-2] Run task #717\n" +
            "19-05-27T20:57:33.843Z INFO [     main] Connecting to storage:3121\n" +
            "19-05-28T20:57:58.931Z INFO [     worker-2] Task #717 has been completed\n" +
            "19-05-29T20:57:59.001Z INFO [     main] Connection established\n" +
            "19-05-30T20:58:14.315Z WARN [     worker-3] Run task #467\n" +
            "19-05-31T20:58:14.315Z WARN [     worker-3] Run task #467\n" +
            "19-06-01T20:58:14.315Z WARN [     worker-3] Run task #467";


    private String filterByDatesResult =

            "19-05-23T20:57:20.564Z INFO [     main] Starting up an application\n" +
            "19-05-24T20:57:25.872Z INFO [     main] Initializing a worker pool\n" +
            "19-05-25T20:57:30.032Z INFO [     worker-1] Run task #1241\n" +
            "19-05-26T20:57:32.421Z INFO [     worker-2] Run task #717\n" +
            "19-05-27T20:57:33.843Z INFO [     main] Connecting to storage:3121\n" +
            "19-05-28T20:57:58.931Z INFO [     worker-2] Task #717 has been completed\n" +
            "19-05-29T20:57:59.001Z INFO [     main] Connection established";


    private String filterBylogLayerLog =

            "19-07-21T10:14:41.592Z DEBUG [     worker-3] Run task #717\n" +
            "19-07-22T10:14:41.592Z TRACE [     worker-3] Run task #717\n" +
            "19-07-23T10:14:41.592Z WARN [     main] Application started successfully\n" +
            "19-07-24T10:14:41.592Z ERROR [     task-5] DB health-check failed\n" +
            "19-07-25T10:14:41.592Z DEBUG [     main] Run task #717\n" +
            "19-07-26T10:14:41.592Z INFO [     worker-3] Application started successfully\n" +
            "19-07-27T10:14:41.592Z INFO [     worker-3] Task #1241 has been completed\n" +
            "19-07-28T10:14:41.592Z WARN [     executor-1] Run task #1241\n" +
            "19-07-29T10:14:41.592Z INFO [     worker-1] Run task #717\n" +
            "19-07-30T10:14:41.592Z ERROR [     worker-3] Task #1241 has been completed\n" +
            "19-07-31T10:14:41.592Z WARN [     worker-2] Run task #1241\n" +
            "19-08-01T10:14:41.592Z WARN [     worker-3] Run task #717\n" +
            "19-08-02T10:14:41.592Z TRACE [     executor-1] Task #1241 has been completed\n" +
            "19-08-03T10:14:41.592Z TRACE [     worker-1] Connecting to storage:3121\n" +
            "19-08-04T10:14:41.592Z TRACE [     worker-2] Task #1241 has been completed\n" +
            "19-08-05T10:14:41.592Z DEBUG [     worker-3] Task #1241 has been completed\n" +
            "19-08-06T10:14:41.592Z WARN [     worker-3] Run task #1241\n" +
            "19-08-07T10:14:41.592Z ERROR [     main] Connecting to storage:3121\n" +
            "19-08-08T10:14:41.592Z WARN [     worker-1] Connecting to storage:31";

     private String filterByLogLayerResult =

             "19-07-22T10:14:41.592Z TRACE [     worker-3] Run task #717\n" +
             "19-07-23T10:14:41.592Z WARN [     main] Application started successfully\n" +
             "19-07-24T10:14:41.592Z ERROR [     task-5] DB health-check failed\n" +
             "19-07-28T10:14:41.592Z WARN [     executor-1] Run task #1241\n" +
             "19-07-30T10:14:41.592Z ERROR [     worker-3] Task #1241 has been completed\n" +
             "19-07-31T10:14:41.592Z WARN [     worker-2] Run task #1241\n" +
             "19-08-01T10:14:41.592Z WARN [     worker-3] Run task #717\n" +
             "19-08-02T10:14:41.592Z TRACE [     executor-1] Task #1241 has been completed\n" +
             "19-08-03T10:14:41.592Z TRACE [     worker-1] Connecting to storage:3121\n" +
             "19-08-04T10:14:41.592Z TRACE [     worker-2] Task #1241 has been completed\n"+
             "19-08-06T10:14:41.592Z WARN [     worker-3] Run task #1241\n" +
             "19-08-07T10:14:41.592Z ERROR [     main] Connecting to storage:3121\n" +
             "19-08-08T10:14:41.592Z WARN [     worker-1] Connecting to storage:31";

     private String filterByMessageLog =

             "19-08-04T10:14:41.592Z TRACE [     worker-2] Task #1241 has been completed\n"+
             "19-08-06T10:14:41.592Z WARN [     worker-3] Run task #1241\n" +
             "19-08-07T10:14:41.592Z ERROR [     main] Connecting to storage:3121\n" +
             "19-08-08T10:14:41.592Z WARN [     worker-1] Connecting to storage:31";

     private String filterByMessageLogResult =
             "19-08-07T10:14:41.592Z ERROR [     main] Connecting to storage:3121\n" +
             "19-08-08T10:14:41.592Z WARN [     worker-1] Connecting to storage:31";


    private String filterByThreadLog =

                    "19-07-23T10:14:41.592Z WARN [     main] Application started successfully\n" +
                    "19-07-24T10:14:41.592Z ERROR [     task-5] DB health-check failed\n" +
                    "19-07-28T10:14:41.592Z WARN [     executor-1] Run task #1241\n" +
                    "19-07-30T10:14:41.592Z ERROR [     worker-3] Task #1241 has been completed\n" +
                    "19-07-31T10:14:41.592Z WARN [     worker-2] Run task #1241\n" +
                    "19-08-01T10:14:41.592Z WARN [     worker-3] Run task #717\n" +
                    "19-08-02T10:14:41.592Z TRACE [     executor-1] Task #1241 has been completed\n" +
                    "19-08-03T10:14:41.592Z TRACE [     worker-1] Connecting to storage:3121\n" +
                    "19-08-04T10:14:41.592Z TRACE [     worker-2] Task #1241 has been completed";

    private String filterByThreadLogResult =
                    "19-07-30T10:14:41.592Z ERROR [     worker-3] Task #1241 has been completed\n" +
                    "19-07-31T10:14:41.592Z WARN [     worker-2] Run task #1241\n" +
                    "19-08-01T10:14:41.592Z WARN [     worker-3] Run task #717\n" +
                    "19-08-04T10:14:41.592Z TRACE [     worker-2] Task #1241 has been completed";


    @Test
    public void filterByDate(){


        SimpleLogFilter logFilter = new SimpleLogFilter();
        logFilter.setLogPattern("^(\\d{2,4}-\\d{2}-\\d{2}.*Z)\\s(INFO|DEBUG|TRACE|WARN|ERROR)\\s\\[\\s{5}(.*)]\\s(.*)$");
        logFilter.setTimePattern("yy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String filterResult = logFilter.filterByDate("19-05-23T20:57:20.564Z", "19-05-29T20:57:59.001Z", filterBylog);

        Assert.assertEquals(filterByDatesResult,filterResult);

    }

    @Test
    public void filterByDateDurationFromStart(){
        SimpleLogFilter logFilter = new SimpleLogFilter();
        logFilter.setLogPattern("^(\\d{2,4}-\\d{2}-\\d{2}.*Z)\\s(INFO|DEBUG|TRACE|WARN|ERROR)\\s\\[\\s{5}(.*)]\\s(.*)$");
        logFilter.setTimePattern("yy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String filterResult = logFilter.filterByDurationFromStart("19-05-23T20:57:20.564Z", "P6D", filterBylog);

        Assert.assertEquals(filterByDatesResult,filterResult);

    }

    @Test
    public void filterByLogLayer(){

        SimpleLogFilter logFilter = new SimpleLogFilter();
        logFilter.setLogPattern("^(\\d{2,4}-\\d{2}-\\d{2}.*Z)\\s(INFO|DEBUG|TRACE|WARN|ERROR)\\s\\[\\s{5}(.*)]\\s(.*)$");
        logFilter.setTimePattern("yy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String filterResult = logFilter.filterByLogLayer("TRACE,WARN,ERROR", filterBylogLayerLog);

        Assert.assertEquals(filterByLogLayerResult,filterResult);

    }

    @Test
    public void setFilterByMessage(){

        SimpleLogFilter logFilter = new SimpleLogFilter();
        logFilter.setLogPattern("^(\\d{2,4}-\\d{2}-\\d{2}.*Z)\\s(INFO|DEBUG|TRACE|WARN|ERROR)\\s\\[\\s{5}(.*)]\\s(.*)$");
        logFilter.setTimePattern("yy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String filterResult = logFilter.filterByMessage("Connecting to.*", filterByMessageLog);

        Assert.assertEquals(filterByMessageLogResult,filterResult);

    }


    @Test
    public void setFilterByThreadLog(){

        SimpleLogFilter logFilter = new SimpleLogFilter();
        logFilter.setLogPattern("^(\\d{2,4}-\\d{2}-\\d{2}.*Z)\\s(INFO|DEBUG|TRACE|WARN|ERROR)\\s\\[\\s{5}(.*)]\\s(.*)$");
        logFilter.setTimePattern("yy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String filterResult = logFilter.filterByThread("worker-[23]", filterByThreadLog);

        Assert.assertEquals(filterByThreadLogResult,filterResult);

    }

    @Test
    public void gere(){


        //((-\w+)\s)

//        LogGenerator generator = new LogGenerator();
//        try {
//
//            FileSource fileSource = new FileSource();
//            fileSource.saveLog(generator.generateLogByDate(30),"D:\\Loooog.log");
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

    }
}

