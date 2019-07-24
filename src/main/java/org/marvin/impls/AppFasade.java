package org.marvin.impls;

import org.marvin.config.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

/**
 * AppFasade {@code AppFasade} it class main class that calls the command
 * parser as well as the and also contains methods for listening to the input
 * stream in two modes. Firts starts LogFilter in simple mode. It mods this
 * mode is for a one-time start, if you pass -f of --follow command after filtering
 * is complet you will be expected to enter new data without without rebooting
 * App. Second mode is activated if no arguments were passed the process of waiting
 * for input is started.
 *
 */

public class AppFasade {

    /**
     * Declaration CommandParser attribute.
     * @see CommandParser for detail.
     *
     */
    private CommandParser commandParser;

    /**
     *
     * This method it implementation of simple mode.
     * On input String array with all argumnets.
     *
     * At the beginning, the arguments are validated.
     * Next e argument  transformed into a string and passed
     * to CommandParse instance in parseCommand method .
     *
     * At the end, the -c -stats and -f or --follow options are checked.
     *
     * @param args
     *        all arguments specified at startup
     *

     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    public void simpleMode(String... args) throws InvocationTargetException, IllegalAccessException {

        preCheck(args);

        StringBuilder builder = new StringBuilder();

        for (String string:args){
            builder.append(string).append(" ");
        }

        commandParser.parseCommands(builder.toString().trim());

        if(Configuration.getInstance().isPrintStatisticAfter())
            printStatistics(Configuration.getInstance().getLogData());

        if(Configuration.getInstance().isFollow())
            stdMode();

    }

    /**
     *
     * This method implemented stdMode
     * It works almost like the simp Mode method.
     * But waiting for input until -f or --follow
     * option equals true.
     *
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    public void stdMode() throws InvocationTargetException, IllegalAccessException {

        BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));

        do {
            Configuration.getInstance().returnAllOptionsToDefaults();

            String line = listenInput(buf);
            if(line == null || line.equals("")) continue;

            String[] query =  line.trim().split("\\s");
            preCheck(query);

            commandParser.parseCommands(line);

            if(Configuration.getInstance().isPrintStatisticAfter())
                printStatistics(Configuration.getInstance().getLogData());

        }while (Configuration.getInstance().isFollow());
    }

    /**
     * This method returns all input data in string
     *
     * @param bufferedReader
     *         buffered reader for read lines from keyboard
     *
     * @return first read line
     */

    private String listenInput(BufferedReader bufferedReader)  {
        System.out.println("Enter commands:\n");
        String line;

        try {
            line = bufferedReader.readLine();

            if (line.length() == 0) {
                System.out.println("You must enter at least one command : --command <arg> <path to log>");
                System.out.println("Enter -help or -h for command list");
            }

            return line;

        }catch (IOException e) {
            System.out.println("I/O Exception :(");
            System.exit(1);
        }

        return null;
    }

    /**
     * This method validates simple situations,
     * it avoids problems in further processing.
     *
     */

    private void preCheck(String ... args){

        if(args[0].equals("--help") || args[0].equals("-h")) {
            System.out.println(Configuration.getInstance().getHelpText());
            return;
        }

        if(!args[0].startsWith("-")){
            System.out.println("Commands must start with '-' symbol press -h or --help for command list ");
            System.exit(-1);
        }

        if(args.length < 2) {
            System.out.println("Min two args must be in query");
            System.exit(-1);
        }

        String logData = new String(commandParser.getFileSource().getLog(args[args.length - 1]));

        if( logData.equals("") || logData == null){

            System.out.println("Log is empty");
            System.exit(0);

        } else Configuration.getInstance().setLogData(logData);

        Configuration.getInstance().parseOptions(args);

        Configuration.getInstance().setInLogPath(args[args.length - 1]);
    }

    /**
     *
     * The method print input logs statistic.
     * Method initialize LogAnaliztor
     * @see LogAnalizator and calls getStatistics()
     * methods which returns statistic in string
     *
     */

    private void printStatistics(String log){
        LogAnalizator logAnalizator = new LogAnalizator();
        System.out.println(logAnalizator.getStatistic(log));
    }

    /**
     * CommandParser getter
     * @return commadParse instance
     */

    public CommandParser getCommandParser() {
        return commandParser;
    }

    /**
     * CommandParser setter for initializing
     * command parse in this class
     */

    public void setCommandParser(CommandParser commandParser) {
        this.commandParser = commandParser;
    }
}
