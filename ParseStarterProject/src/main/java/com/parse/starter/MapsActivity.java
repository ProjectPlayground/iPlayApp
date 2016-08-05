package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, android.location.LocationListener {

    private GoogleMap mMap;
    private FloatingActionButton searchPickAPlaceButton;
    int PLACE_PICKER_REQUEST = 1;
    Location usersLocation;
    private String selectedSport;
    private List<String> sportsList;
    private LocationManager mLocationManager;
    private String provider;
    private Location userLastLocation;
    private LatLng usersLastLocLatLng;
    ListView nearbyPlacesListView;
    List<Place> nearbyPlacesList;
    private ArrayList<Integer> placeTypesList;
    private NearbyPlacesListAdapter nearbyPlacesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchPickAPlaceButton = (FloatingActionButton) findViewById(R.id.searchAPlaceButton);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        searchPickAPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        //Get the sport selected from bundle
        selectedSport = getIntent().getStringExtra("sportSelected");
        Toast.makeText(getApplicationContext(), "Sport Selected " +
                selectedSport, Toast.LENGTH_SHORT).show();

        //Initialize Listview and list for the list of nearby places
        nearbyPlacesList= new ArrayList<>();
        nearbyPlacesListView = (ListView) findViewById(R.id.nearbyPlacesListView);
        nearbyPlacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Send the selected place to OpenGymLocationFragment to display this places info
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_map,new OpenGymLocationInfoFragment()).commit();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.i("Permissions Test", "Permissions are not set");
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION},32);

        }
        Log.i("Permissions Test","Permissions are set");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = mLocationManager.getBestProvider(new Criteria(), false);


        //Get the last know users location
        userLastLocation = mLocationManager.getLastKnownLocation(provider);
        if (userLastLocation == null) {
            mLocationManager.requestLocationUpdates(provider, 400, 1, this);
            //Get the last know users location
            userLastLocation = mLocationManager.getLastKnownLocation(provider);
            Log.i("lastknownLocation","Users last location is null");
        }
        else{
            Log.i("lastknownLocation","Users last loc"+ userLastLocation.toString());
            usersLastLocLatLng = new LatLng(userLastLocation.getLatitude(),userLastLocation.getLongitude());

        }


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Log.i("MarkerSelectedTest", "Markers title: " + marker.getTitle() +" markers snippett: " + marker.getSnippet());

                //Send the selected place to OpenGymLocationFragment to display this places info
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_map,new OpenGymLocationInfoFragment()).commit();

            }
        });
       if (userLastLocation != null){
           //Place marker on map for the users last location
           mMap.addMarker(new MarkerOptions().position(usersLastLocLatLng).title("Your location"));
           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usersLastLocLatLng,1));
           //Query the nearest place from the users last location
           ParseQuery<ParseObject> localPlaces = new ParseQuery<ParseObject>("Places");
           localPlaces.whereNear("placeLatLng",new ParseGeoPoint(userLastLocation.getLatitude(),userLastLocation.getLongitude()));
           localPlaces.findInBackground(new FindCallback<ParseObject>() {
               @Override
               public void done(List<ParseObject> objects, ParseException e) {
                   if(e== null){
                       if(objects.size()>0){
                           for(final ParseObject nearbyLocs: objects){
                               Log.i("nearbyLocs", "Query was succesfull " );
                               LatLng nearbyPlaceLatLng = new LatLng(nearbyLocs.getParseGeoPoint("placeLatLng").getLatitude(),nearbyLocs.getParseGeoPoint("placeLatLng").getLongitude());
                               mMap.addMarker(new MarkerOptions().position(nearbyPlaceLatLng).title(nearbyLocs.getString("placeName")).snippet(nearbyLocs.getString("placeAddress")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearbyPlaceLatLng,10));
                               nearbyPlacesList.add(new Place() {
                                   @Override
                                   public String getId() {
                                       return nearbyLocs.getString("placeId");
                                   }

                                   @Override
                                   public List<Integer> getPlaceTypes() {
                                       return nearbyLocs.getList("placeTypes");
                                   }

                                   @Override
                                   public CharSequence getAddress() {
                                       return nearbyLocs.getString("placeAddress");
                                   }

                                   @Override
                                   public Locale getLocale() {
                                       return null;
                                   }

                                   @Override
                                   public CharSequence getName() {
                                       return nearbyLocs.getString("placeName");
                                   }

                                   @Override
                                   public LatLng getLatLng() {
                                       return new LatLng(nearbyLocs.getParseGeoPoint("placeLatLng").getLatitude(),nearbyLocs.getParseGeoPoint("placeLatLng").getLongitude());
                                   }

                                   @Override
                                   public LatLngBounds getViewport() {
                                       return null;
                                   }

                                   @Override
                                   public Uri getWebsiteUri() {
                                       return Uri.parse(nearbyLocs.getString("placeWebsiteUri"));
                                   }

                                   @Override
                                   public CharSequence getPhoneNumber() {
                                       return nearbyLocs.getString("placePhoneNumber");
                                   }

                                   @Override
                                   public float getRating() {
                                       return (float) nearbyLocs.get("placeRating");
                                   }

                                   @Override
                                   public int getPriceLevel() {
                                       return nearbyLocs.getInt("placePriceLevel");
                                   }

                                   @Override
                                   public CharSequence getAttributions() {
                                       return null;
                                   }

                                   @Override
                                   public Place freeze() {
                                       return null;
                                   }

                                   @Override
                                   public boolean isDataValid() {
                                       return false;
                                   }
                               });

                           }
                           nearbyPlacesAdapter = new NearbyPlacesListAdapter(getApplicationContext(),R.layout.place_list_item,nearbyPlacesList);
                           nearbyPlacesListView.setAdapter(nearbyPlacesAdapter);

                           Log.i("nearbyLocs", "Place Query was succesfull " );
                       }
                       else{
                           Log.i("nearbyLocs", "Place Query returned 0 results " );
                       }
                   }
                   else{
                       Log.i("nearbyLocs", "Place Query was unsuccesful " + e.getMessage());
                   }

               }
           });

       }
        else {
           Log.i("nearbyLocs", "Users last location is empty");
       }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, MapsActivity.this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();

                //Query the places and see if the place picked is already in system
                ParseQuery<ParseObject> placeQuery = new ParseQuery<ParseObject>("Places");
                placeQuery.whereEqualTo("placeId", place.getId());
                placeQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if(e== null){

                            Log.i("placeQuery","Query was succesfull place already in system");
                            mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).snippet(place.getAddress().toString()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                            nearbyPlacesList.add(place);
                            Log.i("addPlaceToList", "Place was added to list placeName:" + place.getName() + " list size: " + nearbyPlacesList.size());
                        }
                        else{
                            Log.i("placeQuery","Query was unsuccesful " + e.getMessage() +" " +e.getCode());
                            if(e.getCode() == 101){
                                //Create a new place object to add to database
                                ParseObject newPlaceObject = new ParseObject("Places");
                                ParseACL defaultAcl = new ParseACL();
                                defaultAcl.setPublicReadAccess(true);
                                defaultAcl.setPublicWriteAccess(true);
                                newPlaceObject.setACL(defaultAcl);
                                newPlaceObject.put("placeId", place.getId());
                                newPlaceObject.put("placeName", place.getName());
                                newPlaceObject.put("placeAddress", place.getAddress());
                                if(place.getWebsiteUri()!= null){
                                    newPlaceObject.put("placeUrl", place.getWebsiteUri().toString());
                                }
                                newPlaceObject.put("placePhoneNumber", place.getPhoneNumber());
                                newPlaceObject.put("placeLatLng", new ParseGeoPoint(place.getLatLng().latitude,place.getLatLng().longitude));
                                sportsList = new ArrayList<String>();
                                sportsList.add(selectedSport);
                                newPlaceObject.put("placeSportPlayed",sportsList);
                                newPlaceObject.put("placeTypes", place.getPlaceTypes());
                                newPlaceObject.put("placePriceLevel", place.getPriceLevel());
                                newPlaceObject.put("placeRating", place.getRating());
                                newPlaceObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null){
                                            Log.i("PlaceSaved", "Place was saved succesfully");
                                            //add the searched marker into the map
                                            mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).snippet(place.getAddress().toString()));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                                        }
                                        else{
                                            Log.i("PlaceSaved","Place was not saved " + e.getMessage());
                                        }
                                    }
                                });

                                nearbyPlacesList.add(place);


                            }
                        }
                        nearbyPlacesAdapter.notifyDataSetChanged();
                    }
                });


            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        usersLocation = location;
        mMap.clear();
        LatLng userLocLatLng = new LatLng(usersLocation.getLatitude(),usersLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLocLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocLatLng));
        Log.i("usersLocChange", "Position changed to " + location.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        switch (requestCode){
            case (32):{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i("PermissionRequestTest", "Permissions Succesful added");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i("PermissionRequestTest","Permission was not added");
                }
            }

        }
    }

}
