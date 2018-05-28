package com.service;

import com.api.CalcService;

import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LocalImpl implements CalcService {
    private static Logger LOGGER= LogManager.getLogManager().getLogger("");
    @Override
    public double calc(List<Double> numbers) {
        LOGGER.info("Got numbers and calc sum");
        double sum = 0;
        for(double n:numbers){
            sum+=n;
        }
        return sum;
    }
}
