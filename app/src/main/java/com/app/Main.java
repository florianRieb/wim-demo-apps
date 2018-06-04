package com.app;

import com.api.CalcService;
import com.loghandler.CustomHandler;

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    public static void main(String... args) throws InterruptedException {
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


        Set<ModuleDescriptor> serviceModules = new HashSet<>();

        // create new Layer
        ModuleFinder.of(Paths.get(System.getProperty("user.dir")+ "/mlib")).findAll().stream().filter((ModuleReference modref) -> modref.descriptor().name().startsWith("service")).forEach((ModuleReference modref) -> serviceModules.add(modref.descriptor()));

        ModuleLayer.boot().modules().forEach(System.out::println);

        ModuleFinder finder = ModuleFinder.of(Paths.get(System.getProperty("user.dir")+ "/mlib"));
        ModuleLayer bootLayer = ModuleLayer.boot();
        Configuration config = bootLayer.configuration().resolveAndBind(finder,ModuleFinder.of(), Set.of("service.proxy"));
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        ModuleLayer newLayer = bootLayer.defineModulesWithOneLoader(config,scl);
        rootLogger.info("--------------Modules in newLayer-------------------");
        newLayer.modules().forEach((Module mod) -> rootLogger.info( mod.getDescriptor().name()) );




        ServiceLoader<CalcService> services = ServiceLoader.load(newLayer, CalcService.class);


        if(!services.iterator().hasNext()){
            throw new RuntimeException("No Service providers found");
        }

        while (!Thread.currentThread().isInterrupted()){
            for(CalcService service:services){
                try {
                    rootLogger.info("result: " +service.calc(numbers));
                } catch (IOException e) {
                    rootLogger.warning(e.getMessage());
                }

            }
            Thread.sleep(20000);


        }







    }



}
