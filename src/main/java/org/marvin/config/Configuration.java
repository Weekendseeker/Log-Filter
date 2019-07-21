package org.marvin.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private Map<String,Method> optionsMap = new HashMap<>();

    private static Configuration instance;

    private boolean strict;
    private boolean printStatisticAfter;
    private boolean follow;
    private String outLogPath;
    private String inLogPath;
    private String logData;
    private String logFormatRegex;
    private String logTimeFormat;
    private String helpText;

    private Configuration() {

        try {

            optionsMap.put("-c", this.getClass().getMethod("setPrintStatisticAfter",boolean.class));
            optionsMap.put("--stats", this.getClass().getMethod("setPrintStatisticAfter",boolean.class));
            optionsMap.put("-f", this.getClass().getMethod("setFollow",boolean.class));
            optionsMap.put("-follow", this.getClass().getMethod("setFollow",boolean.class));
            optionsMap.put("--strict", this.getClass().getMethod("setStrict",boolean.class));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static Configuration getInstance() {
        if (instance == null)
            instance = new Configuration();

        return instance;
    }

    public void parseOptions(String... options){

        for (int i = 0; i < options.length ; i++) {

            String arg = options[i];
            if (optionsMap.containsKey(arg)){
                Method method = optionsMap.get(arg);
                try {

                    method.invoke(instance, true);

                } catch (IllegalAccessException | InvocationTargetException ignored) {

                    System.out.println("Internal error, try again");
                    System.exit(-1);

                }
            }

            if (arg.equals("-o") || arg.equals("--o")) {
                if (!options[i + 1].startsWith("-")) {
                    this.outLogPath = options[i + 1];
                }
            }
        }

        if(this.outLogPath == null || this.outLogPath.equals(""))
            this.outLogPath = this.inLogPath;
    }

    public void returnAllOptionsToDefaults(){

        strict = false;
        printStatisticAfter = false;
        follow = false;
        outLogPath = null;
    }

    public String getOutLogPath() {
        return outLogPath;
    }

    public void setOutLogPath(String outLogPath) {
        this.outLogPath = outLogPath;
    }

    public void setLogFormatRegex(String logFormatRegex) {
        this.logFormatRegex = logFormatRegex;
    }

    public String getLogTimeFormat() {
        return logTimeFormat;
    }

    public void setLogTimeFormat(String logTimeFormat) {
        this.logTimeFormat = logTimeFormat;
    }

    public String getLogFormatRegex() {
        return logFormatRegex;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict){
        this.strict = strict;
    }

    public boolean isPrintStatisticAfter() {
        return printStatisticAfter;
    }

    public void setPrintStatisticAfter(boolean printStatisticAfter) {
        this.printStatisticAfter = printStatisticAfter;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public Map<String, Method> getOptionsMap() {
        return optionsMap;
    }

    public void setOptionsMap(Map<String, Method> optionsMap) {
        this.optionsMap = optionsMap;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public String getInLogPath() {
        return inLogPath;
    }

    public void setInLogPath(String inLogPath) {
        this.inLogPath = inLogPath;
    }

    public String getLogData() {
        return logData;
    }

    public void setLogData(String logData) {
        this.logData = logData;
    }

    @Override
    public String toString() {
        return "Configuration{\n" +
                ", optionsMap =" + optionsMap + "\n" +
                ", strict =" + strict + "\n" +
                ", printStatisticAfter =" + printStatisticAfter+"\n" +
                ", follow =" + follow + "\n" +
                ", outLogPath ='" + outLogPath + '\''+"\n" +
                ", logFormatRegex ='" + logFormatRegex + '\'' + "\n" +
                ", logTimeFormat ='" + logTimeFormat + '\'' + "\n" +
                '}';

    }
}
