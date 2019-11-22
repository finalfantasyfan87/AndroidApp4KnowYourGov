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

public class GoogleCivicAPI  extends AsyncTask<String, Void, String> {

    private static final String TAG = "GoogleCivicAPI";

    private static final String API_KEY = "AIzaSyBSwoiBNOd6mOPi8ThzbFEUd6B5ZVsduyw";

    private static final String GOOGLE_CIVIC = "https://www.googleapis.com/civicinfo/v2/representatives?key="+API_KEY+"&address=";

    private final String noData = "There is no available Data";

    private ArrayList<Official> officialResults = new ArrayList<Official>();
    private Object[] results = new Object[2];
    private String location;

    private MainActivity mainActivity;

    public GoogleCivicAPI(MainActivity activity) {
        this.mainActivity = activity;
    }


    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: ");

        if (s == null) {
            Toast.makeText(mainActivity, "Civic Info service is unavailable", Toast.LENGTH_SHORT).show();
            mainActivity.setOfficialList(null);
            return;

        } else if (s.isEmpty()) {
            Toast.makeText(mainActivity, "No data is available for the specified location", Toast.LENGTH_SHORT).show();
            mainActivity.setOfficialList(null);
            return;

        } else {
            parseJSON(s);
            results[0] = location;
            results[1] = officialResults;
            mainActivity.setOfficialList(results);
            return;
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

            JSONObject normalizedInputObject = jsonObject.getJSONObject("normalizedInput");
            String city = normalizedInputObject.getString("city");
            String state = normalizedInputObject.getString("state");
            String zip = normalizedInputObject.getString("zip");
            location = city + ", " + state + " " + zip;


            JSONArray officesArray = jsonObject.getJSONArray("offices");
            HashMap<String, String> officeIndexHashMap = new HashMap<>();
            for (int i = 0; i < officesArray.length(); i++) {
                JSONObject officeObject = (JSONObject) officesArray.get(i);
                String officialTitle = officeObject.getString("name");
                JSONArray officialIndices = officeObject.getJSONArray("officialIndices");

                for (int j = 0; j < officialIndices.length(); j++)
                    officeIndexHashMap.put(officialIndices.getString(j), officialTitle);
            }


            JSONArray officialsArray= jsonObject.getJSONArray("officials");
            for (int i = 0; i < officialsArray.length(); i++) {
                JSONObject officialObject = (JSONObject) officialsArray.get(i);

                String office = officeIndexHashMap.get(String.format("%d", i));
                String name = officialObject.getString("name");

                String address = "";
                if (officialObject.has("address")) {
                    JSONObject addressObject = (JSONObject) officialObject.getJSONArray("address").get(0);

                    if (addressObject.has("line1"))
                        address += addressObject.getString("line1") + "\n";
                    else if (addressObject.has("line2"))
                        address += ", " + addressObject.getString("line2") + "\n";
                    address += addressObject.get("city") + ", " + addressObject.get("state") + " " + addressObject.get("zip");

                } else {
                    address = noData;
                }

                String party;
                if (officialObject.has("party"))
                    party = officialObject.getString("party");
                else
                    party = "Unkown";

                String phone;
                if (officialObject.has("phones"))
                    phone = officialObject.getJSONArray("phones").get(0).toString();
                else
                    phone = noData;

                String url;
                if (officialObject.has("urls"))
                    url = officialObject.getJSONArray("urls").get(0).toString();
                else
                    url = noData;

                String email;
                if (officialObject.has("emails"))
                    email = officialObject.getJSONArray("emails").get(0).toString();
                else
                    email = noData;

                String photoUrl;
                if (officialObject.has("photoUrl"))
                    photoUrl = officialObject.getString("photoUrl");
                else
                    photoUrl = noData;

                String googlePlus = noData;
                String facebook = noData;
                String twitter = noData;
                String youTube = noData;
                if (officialObject.has("channels")) {
                    JSONArray channelsArray = officialObject.getJSONArray("channels");
                    for (int j = 0; j < channelsArray.length(); j++) {
                        JSONObject channelObject = (JSONObject) channelsArray.get(j);
                        if (channelObject.getString("type").equals("GooglePlus"))
                            googlePlus = channelObject.getString("id");

                        else if (channelObject.getString("type").equals("Facebook"))
                            facebook = channelObject.getString("id");

                        else if (channelObject.getString("type").equals("Twitter"))
                            twitter = channelObject.getString("id");

                        else if (channelObject.getString("type").equals("YouTube"))
                            youTube = channelObject.getString("id");
                    }
                }

                officialResults.add(new Official(office, name, party, address, phone, email, url, photoUrl, googlePlus, facebook, twitter, youTube));
            }
        } catch (JSONException e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

}


