package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Movies class for storing the JSON data like imageurl, moviename, releaseDate, userRating, and plot summary.
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

    /*
        *Parses the year of release from the releaseDate found in the Json.
        *Dates are formatted like so: YYYY-MM-DD so the year is located at the 0th index
     */
    public String getYear(){
        int yearPosition = 0;
        String delims = "[-]";
        String[] tokens = this.getReleaseDate().split(delims);
        return tokens[yearPosition];
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
