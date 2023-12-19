package me.vlad.banksimulation.util;

public class NumberUtils {
    public static double convertValueFromRangeToNewRange(double oldMin, double oldMax, double newMin, double newMax, double valueToConvert) {
        return (newMax + (valueToConvert - oldMin) * (newMax-newMin)/(oldMax-oldMin));
    }
}
