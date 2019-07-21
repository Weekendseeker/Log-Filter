import org.marvin.config.Configuration;
import org.marvin.impls.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args){
        new Main();
    }

    private Main(String ... args) {
        initStartsConfiguration();
        try {
            startFiltr(args);

        } catch (InvocationTargetException | IllegalAccessException e) {
            System.out.println("Internal error , try again");
            System.exit(1);
        }
    }

    private void startFiltr(String ... args) throws InvocationTargetException, IllegalAccessException {

        AppFasade fasad = new AppFasade();

        CommandParser parser = new CommandParser();

        SimpleLogFilter filter = new SimpleLogFilter();
        filter.setLogPattern(Configuration.getInstance().getLogFormatRegex());
        filter.setTimePattern(Configuration.getInstance().getLogTimeFormat());

        parser.setFileSource(new FileSource());
        parser.setFilter(filter);

        fasad.setCommandParser(parser);

        if(args.length == 0 || args == null){
            fasad.stdMode();
        }

        else fasad.simpleMode(args);

    }

    private void initStartsConfiguration(){

        ClassLoader loader = this.getClass().getClassLoader();

        InputStream inputStream = loader.getResourceAsStream("helpText");
        if (inputStream == null){
            Configuration.getInstance().setHelpText("");
            System.exit(1);
        }

        InputStreamReader inreader = new InputStreamReader(inputStream);
        StringBuilder sb = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(inreader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

        } catch (IOException e) {
                    System.out.println("I/O Error with read help");
                    System.exit(1);
        }

        String helpText = sb.toString();

        Configuration.getInstance().setHelpText(helpText);
        Configuration.getInstance().setLogFormatRegex("^(\\d{2,4}-\\d{2}-\\d{2}.*Z)\\s(INFO|DEBUG|TRACE|WARN|ERROR)\\s\\[\\s{5}(.*)]\\s(.*)$");
        Configuration.getInstance().setLogTimeFormat("yy-MM-dd'T'HH:mm:ss.SSS'Z'");

    }
}



