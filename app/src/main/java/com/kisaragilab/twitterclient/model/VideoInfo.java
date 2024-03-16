package com.kisaragilab.twitterclient.model;

import java.util.ArrayList;

public class VideoInfo {

    private int aspect_ratio[];
    private int duration_millis;
    private ArrayList<Variants> variants;

    public int[] getAspectRatio() {
        return aspect_ratio;
    }

    public int getDurationMillis() {
        return duration_millis;
    }

    public ArrayList<Variants> getVariants() {
        return variants;
    }

}
