package org.marvin.impls;

import org.marvin.config.Configuration;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *  The {@code LogAnalizator} class that analyzes log data and
 *  return statistics in following format:
 *
    Lines : 200
    Mean time : PT3M34S
    Levels : TRACE 20, DEBUG 40, INFO 80, WARN 50, ERROR 10
    Levels % : TRACE 10, DEBUG 20, INFO 40, WARN 25, ERROR 5
    Threads : 12
    Most thread : main-thread
    Least thread : worker-5
 *
 *
*/

public class LogAnalizator {

    /**
     *
     * Fasade method which invoke others methods to
     * receive:
     *
     * 1)Log layers count
     * 2)Length log
     * 3)Period beetween start and end dates of log or
     * length log at time
     * 4)Сalculation log layers count at percent
     * 5)Threads count
     * 6)Most Thread
     * 7)And LeastThread
     *
     * @param log
     *        input log
     *
     * @return input log statistic in string
     */

    public String getStatistic(String log){

        Map<String,Integer> threadCount = getThreadsCounts(log);
        int[] logLayersCounts = getLogLayerCounts(log);

        String lines = Integer.toString(getLogSize(log));
        String meanTime = getPeriodAllLog(log);
        String logLayersCountsInPercents = logLayersCountsAtPercents(logLayersCounts.clone());

        String levels = String.format(
                "INFO %d DEBUG %d WARN %d ERROR %d TRACE %d",
                logLayersCounts[0],logLayersCounts[1],logLayersCounts[2],logLayersCounts[3],logLayersCounts[4]
        );

        String threadsCount = Integer.toString(threadCount.size());
        String mostThread = getMostThread(threadCount);
        String leastThread = getLeastThread(threadCount);

        return String.format(
                "Lines:          %s\n" +
                "Mean time:      %s\n" +
                "Levels:         %s\n" +
                "Levels%%:        %s\n" +
                "Threads:        %s\n" +
                "Most thread:    %s\n" +
                "Least Thread:   %s\n" , lines, meanTime,levels,logLayersCountsInPercents,threadsCount,mostThread,leastThread);

    }

    /**
     *
     * Method for calculate period beetween
     * start and end dates of log.*
     *
     * @param log
     *        input log
     *
     * @return Period pattern
     *
     *
     */

    private String getPeriodAllLog(String log){

        String logPattern = Configuration.getInstance().getLogFormatRegex();
        Pattern pattern = Pattern.compile(logPattern);
        List<String> listLogLines = log.lines().collect(Collectors.toList());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Configuration.getInstance().getLogTimeFormat());

        LocalDate startDate = null;
        LocalDate endDate = null;

        for(int start = 0, end = listLogLines.size()-1; start < listLogLines.size(); start++, end--){

            Matcher startMatch = pattern.matcher(listLogLines.get(start));
            Matcher endMatch = pattern.matcher(listLogLines.get(end));

            if(startDate == null && startMatch.matches()){
                    startDate = LocalDate.parse(startMatch.group(1),dateTimeFormatter);
            }

            if(endDate == null && endMatch.matches()){
                endDate = LocalDate.parse(endMatch.group(1),dateTimeFormatter);
            }

            if((endDate != null) && (startDate != null)) break;
        }

        if(startDate == null && endDate == null) return "-";

        Period period = Period.between(startDate,endDate);

        return period.toString();
    }

    /**
     *
     * Method for calculate log layers count in percent
     *
     * @param logLayersCounts
     *        it int array which contains count of log laya
     *
     * @return formated string with all calculations
     *
     */

    private String logLayersCountsAtPercents(int [] logLayersCounts){

        int summAllLayers = 0;

        for (int logLayersCount : logLayersCounts) {
            summAllLayers += logLayersCount;
        }

        for (int i =0 ; i < logLayersCounts.length; i++){

            logLayersCounts[i] = ( logLayersCounts[i] * 100 ) / summAllLayers;

        }

       return String.format(
                "INFO %d DEBUG %d WARN %d ERROR %d TRACE %d",
               logLayersCounts[0],logLayersCounts[1],logLayersCounts[2],logLayersCounts[3],logLayersCounts[4]
       );
    }

    /**
     *
     * Method for quantities сount for
     * each searched thread
     *
     * @param log
     *      input log
     *
     * @return  map where key it thread
     * name and map value it quantity
     *
     */

    private Map<String,Integer> getThreadsCounts(String log){

        String logPattern = Configuration.getInstance().getLogFormatRegex();
        Pattern pattern = Pattern.compile(logPattern);
        List<String> listLogLines = log.lines().collect(Collectors.toList());

        Map<String, Integer> map = new HashMap<>();

        for(String line : listLogLines){

            Matcher matcher = pattern.matcher(line);

            if(matcher.matches()){

                String thread = matcher.group(3);
                map.putIfAbsent(thread,0);
                map.replace(thread, map.get(thread) + 1);
            }
        }

        return map;
    }

    /**
     *
     * Method returns the most common
     * threads name in log
     *
     * @param countsMap
     *      Map with thread counts
     *
     * @return  return most common threads name
     *
     */

    private String getMostThread(Map<String, Integer> countsMap){

        StringBuilder ma = new StringBuilder();

        int max = 0;
        for(Map.Entry<String, Integer> a : countsMap.entrySet()){

            int val = a.getValue();

            if(val >= max) {
                max = val;
                ma.delete(0,ma.length());
                ma.append(a.getKey());
            }
        }

        return ma.toString() ;
    }

    /**
     *
     * Method returns the least common
     * threads name in log
     *
     * @param countsMap
     *      Map with thread counts
     *
     * @return return leas common threads name
     *
     */

    private String getLeastThread(Map<String, Integer> countsMap){

        StringBuilder mi = new StringBuilder();

        int min = 0;
        for (Map.Entry<String, Integer> a : countsMap.entrySet()) {

            int val = a.getValue();
            if (min == 0) min = val;

            if (val <= min) {
                min = val;
                mi.delete(0, mi.length());
                mi.append(a.getKey());
            }
        }

        return mi.toString();
    }

    /**
     *
     * Method for counts log layers
     *
     * @param log
     *      input Log
     *
     * @return return int array with 5 elements where
     *
     * infoCount[0]  INFO   count
     * infoCount[1]  DEBUG  count
     * infoCount[2]  WARN   count
     * infoCount[3]  ERROR  count
     * infoCount[4]  TRACE  count
     *
     */

    private int[] getLogLayerCounts(String log) {

        int[] infoCount = new int[5];

        String logPattern = Configuration.getInstance().getLogFormatRegex();
        Pattern pattern = Pattern.compile(logPattern);
        List<String> listLogLines = log.lines().collect(Collectors.toList());

        for(String line : listLogLines){

            Matcher matcher = pattern.matcher(line);

            if(matcher.matches()){

                switch (matcher.group(2)){
                    case "INFO"  : infoCount[0] ++ ; break;
                    case "DEBUG" : infoCount[1] ++ ; break;
                    case "WARN"  : infoCount[2] ++ ; break;
                    case "ERROR" : infoCount[3] ++ ; break;
                    case "TRACE" : infoCount[4] ++ ; break;
                }
            }
        }

        return infoCount;
    }

    /**
     *
     * Method returns logs line size
     *
     * @param log
     *      Map with thread counts
     *
     * @return  return int size
     *
     */

    private int getLogSize(String log){
        List<String> listLogLines = log.lines().collect(Collectors.toList());
        return listLogLines.size();
    }

}
