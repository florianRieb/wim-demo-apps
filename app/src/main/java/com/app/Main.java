package com.app;

import com.api.CalcService;
import com.loghandler.CustomHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    public static void main(String... args){
        LogManager.getLogManager().reset();
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.addHandler(new CustomHandler());

        rootLogger.info("test message");

        if(args.length<=0)
            throw new RuntimeException("No numbers in args[]");

        List<Double> numbers = new ArrayList<>();
        for(String arg:args){
            numbers.add(Double.parseDouble(arg));
        }
        rootLogger.info("numbers:" + numbers);


        ServiceLoader<CalcService> services = ServiceLoader.load(CalcService.class);


        if(!services.iterator().hasNext()){
            throw new RuntimeException("No Service providers found");
        }
        for(CalcService service:services){
            rootLogger.info("result: " +service.calc(numbers));
        }






    }



}
