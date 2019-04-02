package com.example.lameater;

public class MeaterData {

    private static MeaterData instance;

    private final TemperatureFetcher fetcher;

    private MeaterData() {
        fetcher = new TemperatureFetcher();
    }

    public static MeaterData getInstance() {
        if (instance == null)
            instance = new MeaterData();
        return instance;
    }

    public TemperatureFetcher getFetcher() {
        return fetcher;
    }
}
