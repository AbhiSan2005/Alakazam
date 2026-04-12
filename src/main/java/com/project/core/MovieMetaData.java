package com.project.core;

public class MovieMetaData {
    public String title;
    public String genre;
    public int duration;
    public int yearOfRelease;

    public MovieMetaData(String title, String genre, int duration, int yearOfRelease) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.yearOfRelease = yearOfRelease;
    }
}