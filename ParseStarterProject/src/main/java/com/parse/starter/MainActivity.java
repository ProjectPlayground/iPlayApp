/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //Permissions for facebook, can be null
    //by default read permission is on
    List<String> permissions;
    ImageView mProfileImage;
    Button testButton;
    Button facebookLoginButton;
    private String email;
    private String name;
    private ParseUser parseUser;
    private ParseFile parseFile;
    public Bitmap bitmap;

    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};

    @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

        mProfileImage = (ImageView) findViewById(R.id.mProfileImage);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        if(ParseUser.getCurrentUser()!= null){
            Log.i("UserTest",ParseUser.getCurrentUser().getEmail()+ " is currently logged in");
           // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainPageFragment()).commit();
            ParseUser.logOut();
        }
;
        facebookLoginButton = (Button) findViewById(R.id.facebookLogInButton);
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            Log.d("MyApp", "Logged in user info " + user.getUsername());
                            //Get the new data from facebook and add to parse
                            getUserDetailsFromFB();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MainPageFragment()).commit();
                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                            //Query the user from parse
                            getUserDetailsFromParse();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MainPageFragment()).commit();
                        }
                    }
                });
            }
        });




  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

    private void getUserDetailsFromFB() {
        // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture");
        //Method that grabs data from facebook
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
         /* handle the result */
                        try {
                            Log.i("facebookTest", response.toString());
                            email = response.getJSONObject().getString("email");
                            name = response.getJSONObject().getString("name");
                            Log.i("nameTest","Check the value of name " + name);
                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");
                            //  Returns a 50x50 profile picture
                            String pictureUrl = data.getString("url");
                            new ProfilePhotoAsync(pictureUrl).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
    class ProfilePhotoAsync extends AsyncTask<String, String, String> {
        String url;
        public ProfilePhotoAsync(String url) {
            this.url = url;
        }
        @Override
        protected String doInBackground(String... params) {
            // Fetching data from URI and storing in bitmap
            bitmap = DownloadImageBitmap(url);
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            saveNewUser();
        }
    }
    private void saveNewUser() {
        parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(name);
        parseUser.setEmail(email);
    //        Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] data = stream.toByteArray();
        String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
        parseFile = new ParseFile(thumbName + "_thumb.jpg", data);
        parseUser.put("profileThumb", parseFile);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(MainActivity.this, "New user:" + name + " Signed up", Toast.LENGTH_SHORT).show();
            }
        });


    }
    public static Bitmap DownloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            //Create Url
            URL aURL = new URL(url);
            //Create connectionf or the URl
            URLConnection conn = aURL.openConnection();
            conn.connect();
            //Get the data stream from connection
            InputStream is = conn.getInputStream();
            //Buffer the inputstream
            BufferedInputStream bis = new BufferedInputStream(is);
            //Decode the stream and store in bitmap
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }

    private void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();
//    //Fetch profile photo
//        try {
//            ParseFile parseFile = parseUser.getParseFile("profileThumb");
//            byte[] data = parseFile.getData();
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            mProfileImage.setImageBitmap(bitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //mEmailID.setText(parseUser.getEmail());
        //mUsername.setText(parseUser.getUsername());
        Toast.makeText(MainActivity.this, "Welcome back " + parseUser.getUsername(), Toast.LENGTH_SHORT).show();
    }

}
