package org.marvin.impls;

import org.marvin.api.Filter;
import org.marvin.config.Configuration;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 *
 *  The class {@code SimpleLogFilter} it simple implementation interface
 *  @see Filter for filtering logs by the specified parameters for examples:
 *
 * 1. Filter log lines which were reported during May 23rd, 2019:
 * log-filter -s "19-05-23T00:00:00.000Z" -e "19-05-23T23:59:59.999Z" example.log
 *
 * 2. Filter log lines which have DEBUG or WARN levels and were reported under threads:
 * log-filter -l "DEBUG,WARN" -t "worker-*" example.log
 *
 * 3. Filter log lines which were reported during a week since the start of May 2019:
 * log-filter -s "19-05-01T00:00:00.000Z" -p "P1W" example.log
 *
 * 4. Filter log lines which were reported from 8th to 10th May 2019:
 * log-filter -e "19-05-010T00:00:00.000Z" -p "P2D" example.log

 * 5. Filter log lines with a message containing failed as a substring:
 * log-filter -m "*failed*" example.log
 *
 * 6. Redirect a result to the filtered.log file instead of the standard output:
 * log-filter -o filtered.log example.log
 *
 * 7. Filter log lines starting with Task in a message part and ensure all the lines are processable:
 * log-filter -m "Task*" --strict example.log
 *
 * 8. Filter warning log lines and wait for new lines:
 * log-filter -l "WARN" -f example.log
 *
 * 9 Print out log statistics contained in example.log file:
 * log-filter -c example.log
 *
 *
 *
 */

public class SimpleLogFilter implements Filter {

    /**
     *
     * Initializes:
     *
     * methodMap it HashMap with Method type as key and String as value.
     * This stored filter methods and regex patterns to call them.
     *
     * readableLineNumber which counting log lines
     *
     */
    private Map<Method, String> methodMap = new HashMap<>();
    private int readableLineNumber = 0;
    private String logPattern;
    private String timePattern;

    /**
     *
     *
     * SimpleLogFilters constructor filled methodMap.
     * If method not is exist then will be generated
     * NoSuchMethodException and program process will
     * be interrupt.
     *
     *
     */

    public SimpleLogFilter() {

        try {

        methodMap.put(SimpleLogFilter.class.getDeclaredMethod("filterByDate",String.class,String.class,String.class),
                     "(-s|--start)\\s\"(\\d{2,4}-\\d{2}-\\d{2}.*Z)\"\\s(-e|--e)\\s\"(\\d{2,4}-\\d{2}-\\d{2}.*Z)\"");

        methodMap.put(SimpleLogFilter.class.getDeclaredMethod("filterByDurationFromStart",String.class,String.class,String.class),
                     "(-s|--start)\\s\"(\\d{2,4}-\\d{2}-\\d{2}.*Z)\"\\s-p|--period\\s\"(P\\d.*)\"");

        methodMap.put(SimpleLogFilter.class.getDeclaredMethod("filterByLogLayer",String.class,String.class),
                     "-l \"((?:(INFO|DEBUG|TRACE|WARN|ERROR),?)+)\"");

        methodMap.put(SimpleLogFilter.class.getDeclaredMethod("filterByMessage",String.class,String.class),
                     "-m \"(.*)\"\\s");

        methodMap.put(SimpleLogFilter.class.getDeclaredMethod("filterByThread",String.class,String.class),
                     "-t \"(.*)\"");

        } catch (NoSuchMethodException e) {

            System.out.println("Internal error, try again");
            System.exit(1);
        }
    }

    /**
     *
     * Method for filtering log by date range
     * from start date to end date.
     *
     * @param start
     *        Date from begin reading
     * @param end
     *        Date end reading
     *
     * @param log
     * @return filtered log in string
     */

    @Override
    public String filterByDate(String start, String end, String log){

        Pattern pattern = Pattern.compile(logPattern);

        DateTimeFormatter df = DateTimeFormatter.ofPattern(timePattern);

        LocalDate startz = LocalDate.parse(start, df);
        LocalDate endz = LocalDate.parse(end, df);

        List<String> listLogLines = getLines(log);
        StringBuilder sb = new StringBuilder();

        for (String line : listLogLines) {

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {

                LocalDate dateInLog = LocalDate.parse(matcher.group(1), df);

                if (dateInLog.isAfter(startz) && dateInLog.isBefore(endz) || dateInLog.equals(startz) || dateInLog.equals(endz)) {
                    sb.append("\n").append(line);
                }

                readableLineNumber++;

            } else if (Configuration.getInstance().isStrict()) printStrictAndExit(line);

        }
        return sb.toString().trim();
    }

    /**
     * out the log lines that are reported before
     * the given time point
     *
     *
     * and return result:
     * @see #filterByDate
     *
     * @param start
     *        Date from begin reading
     * @param durationPattern
     *        Duration pattern for example
     *
     *      *   "P2Y"             -- Period.ofYears(2)
     *      *   "P3M"             -- Period.ofMonths(3)
     *      *   "P4W"             -- Period.ofWeeks(4)
     *      *   "P5D"             -- Period.ofDays(5)
     *      *   "P1Y2M3D"         -- Period.of(1, 2, 3)
     *      *   "P1Y2M3W4D"       -- Period.of(1, 2, 25)
     *      *   "P-1Y2M"          -- Period.of(-1, 2, 0)
     *      *   "-P1Y2M"          -- Period.of(-1, -2, 0)
     *
     * @param log
     *        Log data from source in string format
     *
     * @return filtered by duration log
     */

    @Override
    public String filterByDurationFromStart(String start, String durationPattern, String log) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern(timePattern);
        LocalDateTime starz= LocalDateTime.parse(start, df);
        LocalDateTime dateTime = starz.plus(Period.parse(durationPattern));

        return filterByDate(start,dateTime.format(df),log);
    }

    /**
     * out the log lines that are reported after
     * the given time point.
     * @see #filterByDate
     *
     */

    @Override
    public String filterByDurationFromEnd(String end, String durationPattern, String log) {

        DateTimeFormatter df = DateTimeFormatter.ofPattern(timePattern);
        LocalDateTime endz= LocalDateTime.parse(end, df);
        LocalDateTime dateTime = endz.minus(Period.parse(durationPattern));

        return filterByDate(dateTime.format(df),end,log);
    }

    /**
     * Method for filtering log by log layerPattern

     * @see #filterByDate
     *
     * @param layerPattern
     *        Layer pattern in format:
     *        WARN,TRACE,INFO
     *
     * @param log
     *        Log data from source in string format
     *
     * @return filtered by layerPattern log
     */

    @Override
    public String filterByLogLayer(String layerPattern, String log) {

        String[] layers = layerPattern.split(",");

        Pattern pattern = Pattern.compile(logPattern);

        List<String> listLogLines = getLines(log);
        StringBuilder sb = new StringBuilder();

        for(String line : listLogLines) {

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {

                for (String layer: layers){
                    if (matcher.group(2).trim().equals(layer)) {
                        sb.append("\n").append(line);
                }

                    readableLineNumber++;
                }

            }else if(Configuration.getInstance().isStrict()) printStrictAndExit(line);

        }
        return sb.toString().trim();
    }

    /**
     * Method for filtering log by message
     * This method supported regex
     *
     * @param mask
     *       Filter mask for example:
     *       ^Exceptrion.* -  begin 'Exception" word
     *
     *
     * @param log
     *        Log data from source in string format
     *
     * @return filterByDate result
     */

    @Override
    public String filterByMessage(String mask, String log) {

        Pattern pattern = Pattern.compile(logPattern);

        List<String> listLogLines = getLines(log);
        StringBuilder sb = new StringBuilder();

        for(String line : listLogLines) {

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                if (matcher.group(4).trim().matches(mask)) {
                    sb.append(line).append("\n");
                }

                readableLineNumber ++;

            }else if(Configuration.getInstance().isStrict()) printStrictAndExit(line);
        }
        return sb.toString().trim();
    }

    /**
     * Method for filtering log by thread description
     * This metod supported regex for example:
     *
     *
     * @param threadMask
     *         executor-[123] - filtering log where thread
     *         id equal 1 or 2 or 3
     *
     * @param log
     *        Log data from source in string format
     *
     * @return filterByDate result
     */

    @Override
    public String filterByThread(String threadMask, String log) {

        Pattern pattern = Pattern.compile(logPattern);

        List<String> listLogLines = getLines(log);
        StringBuilder sb = new StringBuilder();

        for(String line : listLogLines) {

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                if (matcher.group(3).trim().matches(threadMask)) {
                    sb.append(line).append("\n");
                }

                readableLineNumber++;

            }else if(Configuration.getInstance().isStrict()) printStrictAndExit(line);
        }
        return sb.toString().trim();
    }

    /**
     * Method terminated filters process if there was
     * an attempt to process a string that does not match the format.
     * This method works if --strict option has true flag.
     *
     * @param line
     *        Not format line
     */

    private void printStrictAndExit(String line){

        System.out.println(
                String.format(
                        "Process terminated because:" +
                                "Line number: %d\n" +
                                "Line: %s\n" +
                                "Not format...\n" ,readableLineNumber,line)
        );

        System.exit(1);
    }

    /**
     * Method of getting logs line as List
     *
     * @param log
     *        Log data
     *
     * @return List log lines
     */

    private List<String> getLines(String log){
        return log.lines().collect(Collectors.toList());
    }

    /**
     * Standart getter for methodMap
     *
     * @return methodMap
     */

    public Map<Method, String> getMethodMap() {
        return methodMap;
    }

    public void setLogPattern(String logPattern) {
        this.logPattern = logPattern;
    }

    public String getLogPattern(){
        return this.logPattern;
    }

    public String getTimePattern() {
        return timePattern;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }
}
