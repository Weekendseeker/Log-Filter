package org.marvin.impls;

import org.marvin.api.Filter;
import org.marvin.api.LogSource;
import org.marvin.config.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * CommandParser it class for parse queries and execute filters methods
 * and write result to file.
 *
 */

public class CommandParser {

    private Filter filter;
    private LogSource fileSource;

    /**
     * This is the main method of this class. Method checks
     * the request and gets the necessary arguments from it
     * by the regex pattern.Patterns are defined in HashMap
     * in the SimpleLogFilter class.

     * In the for cycle:
     *
     * If pattern was finded in query,- The method will be taken
     * from mapand called at the current cycles iteration. After
     * filteredlog data will bi written to our or in file.
     *
     * @see SimpleLogFilter
     *
     * @param query line
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    public void parseCommands(String query) throws InvocationTargetException, IllegalAccessException {

        Map<Method, String> methodMap = ((SimpleLogFilter)filter).getMethodMap() ;

        for (Map.Entry<Method, String> entry : methodMap.entrySet()) {

            String regex = entry.getValue();
            Pattern pattern = Pattern.compile(entry.getValue());
            Matcher matcher = pattern.matcher(query);

            if (matcher.find()) {

                Method method = entry.getKey();
                String[] options = query.replaceAll(regex + "\\s", "").split("\\s");
                Configuration.getInstance().parseOptions(options);

                String parametrs = matcher.group().replaceAll("((-\\w+)\\s)", "");

                String filteredQuery = (String)method.invoke(filter, (Object[]) buildParamets(parametrs));

                writeLog(filteredQuery);

                break;
            }
        }
    }

    /**
     * This help method filters the parameters,
     * cleans them of unnecessary characters
     * and builds a new line.
     *
     * @param parametrs line
     *
     * @return filter parametrs in array list
     */

    private String[] buildParamets(String parametrs){

        StringBuilder paramBuilder = new StringBuilder();
        paramBuilder.append(parametrs.replaceAll("\"\\s","\n").replaceAll("\"",""));

        List<String> list = new ArrayList<>(Arrays.asList(paramBuilder.toString().split("\n")));
        list.add(Configuration.getInstance().getLogData());

        return list.toArray(new String[list.size()]);
    }

    /**
     * This method write filtered log to output.
     * If -o or --out not initialize. The record
     * will be made by default to the input log
     * file.
     * @param log
     *      log file
     */

    private void writeLog (String log) {

        if (log == null || log.equals("") || log.length() == 0) {
            System.out.println("Empty log cancle write");
            System.exit(0);
        }

        String outLogPath = Configuration.getInstance().getOutLogPath();

        if(outLogPath.equals("") || outLogPath == null)
                outLogPath = Configuration.getInstance().getInLogPath();

        fileSource.saveLog(log, outLogPath);
    }

    /**
     * Get fileter instance
     * @see SimpleLogFilter
     * @return fileSource
     */

    public Filter getFilter () {
        return filter;
    }

    /**
     * Initialize filtr
     * @see SimpleLogFilter
     *
     * @param filter
     */

    public void setFilter (SimpleLogFilter filter){
        this.filter = filter;
    }

    /**
     * Get LogSource instance
     * @see FileSource
     * @return fileSource
     */

    public LogSource getFileSource () {
        return fileSource;
    }

    /**
     * Setter LogSource instance
     * @see FileSource
     * @return fileSource
     */

    public void setFileSource (LogSource fileSource){
        this.fileSource = fileSource;
    }

}
