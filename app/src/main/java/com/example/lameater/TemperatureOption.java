package com.example.lameater;

public class TemperatureOption {

    private String tempName;
    private int temp;

    public TemperatureOption(String tempName, int temp) {
        this.tempName = tempName;
        this.temp = temp;
    }

    public String toString() {
        return tempName;
    }

    public int getTemp() {
        return temp;
    }

}