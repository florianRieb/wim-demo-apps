package com.api;

import java.io.IOException;
import java.util.List;

public interface CalcService {
    double calc(List<Double> n) throws IOException;

}
