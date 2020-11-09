package com.larko.speedruntimer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {
    public static String formatTimer(Long time) {
        return (new SimpleDateFormat("mm:ss:SSS")).format(new Date(time));
    }

    public static String colorDimension(String dimension) {
        if(dimension == "Overworld") {
            return "639C2F";
        } else if(dimension == "Nether") {
            return "E64C19";
        } else {
            return "FBFFD9";
        }
    }

    public static String readableStep(String step) {
        if(step == "enterNether") {
            return "Entered Nether";
        } else if(step == "exitNether") {
            return "Left Nether";
        } else {
            return "Entered End";
        }
    }
}
