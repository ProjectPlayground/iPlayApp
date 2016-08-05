package com.parse.starter;
/**
 * Created by chris on 5/18/16.
 */
public class SearchLocationListItem {

    String parkName;
    int peopleCurrentlyThere;


    public SearchLocationListItem(String parkName2, int peopleCount){
        parkName = parkName2;
        peopleCurrentlyThere = peopleCount;
    }


    public int getPeopleCurrentlyThere() {
        return peopleCurrentlyThere;
    }

    public void setPeopleCurrentlyThere(int peopleCurrentlyThere) {
        this.peopleCurrentlyThere = peopleCurrentlyThere;
    }

    public String getParkName() {

        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }
}
