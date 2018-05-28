package com.loghandler;

import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class CustomHandler  extends Handler {
    //Impl eigenen Handler um Loggs zu fromatieren, aufzubereiten oder an einen anderen Service zu übermitteln


    private Date date;
    @Override
    public void publish(LogRecord logRecord) {
        StringBuilder sb = new StringBuilder();
        date = new Date(logRecord.getMillis());
        sb.append(date .toString())
                .append(" - ")
                .append(logRecord.getSourceClassName())
                .append("#")
                .append(logRecord.getSourceMethodName())
                .append(" - ")
                .append(logRecord.getMessage());
        System.out.println(sb.toString());

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
