package org.marvin.api;

import java.lang.reflect.InvocationTargetException;

public interface Filter {

    String filterByDate(String start, String end, String log);

    String filterByDurationFromStart(String start, String durationPattern, String log);

    String filterByLogLayer(String layer, String log);

    String filterByMessage(String mask, String log);

    String filterByThread(String threadName, String log);

    String filterByDurationFromEnd(String END, String durationPattern, String log);

}
