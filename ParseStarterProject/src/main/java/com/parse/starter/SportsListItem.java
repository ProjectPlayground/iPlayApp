package com.parse.starter;
/**
 * Created by chris on 5/14/16.
 */
public class SportsListItem {

     String sportsName;
     int imageId;

    public SportsListItem(String name, int id){
        sportsName = name;
        imageId = id;
    }

    public void setSportsName(String name){
        sportsName = name;
    }
    public String getSportsName(){
        return sportsName;
    }
    public void setImageId(int id){
        imageId = id;
    }
    public int getImageId(){
        return imageId;
    }
}
