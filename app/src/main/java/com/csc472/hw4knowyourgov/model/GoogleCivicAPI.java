package com.csc472.hw4knowyourgov.model;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.csc472.hw4knowyourgov.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.String.format;

public class GoogleCivicAPI  extends AsyncTask<String, Void, String> {

    private static final String TAG = "GoogleCivicAPI";
    private final String noDataFound = "There is no available Data";

    private static final String API_KEY = "AIzaSyBSwoiBNOd6mOPi8ThzbFEUd6B5ZVsduyw";

    private static final String GOOGLE_CIVIC = "https://www.googleapis.com/civicinfo/v2/representatives?key="+API_KEY+"&address=";



    private ArrayList<Official> officialResults = new ArrayList<Official>();
    private Object[] civicResults = new Object[2];
    private String location;

    private MainActivity mainActivity;

    public GoogleCivicAPI(MainActivity activity) {
        this.mainActivity = activity;
    }


    @Override
    protected void onPostExecute(String dataToSend) {
        Log.d(TAG, "onPostExecute: ");

        if (dataToSend == null) {
            Toast.makeText(mainActivity, "Civic Info service is unavailable", Toast.LENGTH_SHORT).show();
            mainActivity.updateOfficalList(null);

        } else if (dataToSend.isEmpty()) {
            Toast.makeText(mainActivity, "No data is available for the specified location", Toast.LENGTH_SHORT).show();
            mainActivity.updateOfficalList(null);

        } else {
            parseJSON(dataToSend);
            civicResults[0] = location;
            civicResults[1] = officialResults;
            mainActivity.updateOfficalList(civicResults);
        }
    }
    @Override
    protected String doInBackground(String... strings) {
        String civicsURL = GOOGLE_CIVIC + strings[0];
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(civicsURL, String.class);
    }


    private void parseJSON(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject normalizedInput = jsonObject.getJSONObject("normalizedInput");
            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");
            location = city + ", " + state + " " + zip;
            JSONArray officesArray = jsonObject.getJSONArray("offices");
            HashMap<String, String> officialHashMap = new HashMap<>();
            {
                int i = 0;
                while (i < officesArray.length()) {
                    JSONObject officeObject = (JSONObject) officesArray.get(i);
                    String officialTitle = officeObject.getString("name");
                    JSONArray officialIndices = officeObject.getJSONArray("officialIndices");

                    int j = 0;
                    while (j < officialIndices.length()) {
                        officialHashMap.put(officialIndices.getString(j), officialTitle);
                        j++;
                    }
                    i++;
                }
            }
            mapOfficialData(jsonObject, officialHashMap);
        } catch (JSONException e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mapOfficialData(JSONObject jsonObject, HashMap<String, String> officialHashMap) throws JSONException {
        JSONArray officialsArray= jsonObject.getJSONArray("officials");
        int i = 0;
        while (i < officialsArray.length()) {
            JSONObject official = (JSONObject) officialsArray.get(i);

            String office = officialHashMap.get(format("%d", i));
            String name = official.getString("name");

            String officialAddress = "";
            if (official.has("officialAddress")) {
                JSONObject addressObject = (JSONObject) official.getJSONArray("officialAddress").get(0);

                if (addressObject.has("line1")) {
                    officialAddress.concat(addressObject.getString("line1") + "\n");
                } else if (addressObject.has("line2"))
                    officialAddress.concat(", " + addressObject.getString("line2") + "\n");
                officialAddress.concat( addressObject.get("city") + ", " + addressObject.get("state") + " " + addressObject.get("zip"));

            } else {
                officialAddress = noDataFound;
            }

            String party;
            if  (official.has("party"))
                party = official.getString("party");
            else
                party = noDataFound;

            String phone;
            if (official.has("phones"))
                phone = official.getJSONArray("phones").get(0).toString();
            else
                phone = noDataFound;

            String url;
            if (official.has("urls"))
                url = official.getJSONArray("urls").get(0).toString();
            else
                url = noDataFound;

            String email;
            if (official.has("emails"))
                email = official.getJSONArray("emails").get(0).toString();
            else
                email = noDataFound;

            String photoUrl;
            if (official.has("photoUrl"))
                photoUrl = official.getString("photoUrl");
            else
                photoUrl = noDataFound;

            String googlePlus = noDataFound;
            String facebook = noDataFound;
            String twitter = noDataFound;
            String youTube = noDataFound;
            if (official.has("channels")) {
                JSONArray channelsArray = official.getJSONArray("channels");
                int j = 0;
                while (j < channelsArray.length()) {
                    JSONObject channelObject = (JSONObject) channelsArray.get(j);
                    if (channelObject.getString("type").equals("GooglePlus"))
                        googlePlus = channelObject.getString("id");

                    else if (channelObject.getString("type").equals("Facebook"))
                        facebook = channelObject.getString("id");

                    else if (channelObject.getString("type").equals("Twitter"))
                        twitter = channelObject.getString("id");

                    else if (channelObject.getString("type").equals("YouTube"))
                        youTube = channelObject.getString("id");
                    j++;
                }
            }

            officialResults.add(new Official(office, name, party, officialAddress, phone, email, url, photoUrl, googlePlus, facebook, twitter, youTube));
            i++;
        }
    }

}


