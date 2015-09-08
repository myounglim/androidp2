package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by student on 9/1/15.
 */
public class Movies implements Parcelable, Comparable<Movies> {
    String image;
    String name;
    String releaseDate;
    String userRating;
    String summary;

    public Movies(String img, String name, String releaseDate, String userRating, String summary){
        this.image = img;
        this.name = name;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
        this.summary = summary;
    }

    public void setImage(String img){
        this.image = img;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDate(String releaseDate){
        this.releaseDate = releaseDate;
    }
    public void setRating(String userRating){
        this.userRating = userRating;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }

    public void setAll(String img, String name, String releaseDate, String userRating, String summary){
        this.setImage(img);
        this.setName(name);
        this.setDate(releaseDate);
        this.setRating(userRating);
        this.setSummary(summary);
    }

    public String getImageUrl(){
        return this.image;
    }
    public String getMovieName(){
        return this.name;
    }
    public String getReleaseDate(){
        return this.releaseDate;
    }
    public String getUserRating(){
        return this.userRating;
    }
    public String getMovieSummary(){
        return this.summary;
    }
    public String getYear(){
        String delims = "[-]";
        String[] tokens = this.getReleaseDate().split(delims);
        return tokens[0];
    }

    @Override
    public int compareTo(Movies another) {
        double result =  (10 * (Double.parseDouble(another.getUserRating()) - Double.parseDouble(this.userRating)));
        return (int) result;
    }

    /* everything below here is for implementing Parcelable */
    public Movies(Parcel in){
        String[] movieData = new String[5];
        in.readStringArray(movieData);
        this.image = movieData[0];
        this.name = movieData[1];
        this.releaseDate = movieData[2];
        this.userRating = movieData[3];
        this.summary = movieData[4];

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[]{this.image,
                this.name,
                this.releaseDate,
                this.userRating,
                this.summary});
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel source) {
            return new Movies(source);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };


}
