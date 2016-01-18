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
    long movie_id;

    public Movies(String img, String name, String releaseDate, String userRating, String summary, long movie_id){
        this.image = img;
        this.name = name;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
        this.summary = summary;
        this.movie_id = movie_id;
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
    public void setMovieId(long id){
        this.movie_id = id;
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
    public long getMovieId(){
        return this.movie_id;
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
    private Movies(Parcel in){
//        String[] movieData = new String[5];
//        in.readStringArray(movieData);
//        this.image = movieData[0];
//        this.name = movieData[1];
//        this.releaseDate = movieData[2];
//        this.userRating = movieData[3];
//        this.summary = movieData[4];
        this.image = in.readString();
        this.name = in.readString();
        this.releaseDate = in.readString();
        this.userRating = in.readString();
        this.summary = in.readString();
        this.movie_id = in.readLong();

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
//        out.writeStringArray(new String[]{this.image,
//                this.name,
//                this.releaseDate,
//                this.userRating,
//                this.summary});
        out.writeString(this.image);
        out.writeString(this.name);
        out.writeString(this.releaseDate);
        out.writeString(this.userRating);
        out.writeString(this.summary);
        out.writeLong(this.movie_id);
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
