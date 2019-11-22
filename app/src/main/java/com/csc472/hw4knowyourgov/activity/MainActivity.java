package com.csc472.hw4knowyourgov.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csc472.hw4knowyourgov.R;
import com.csc472.hw4knowyourgov.adapter.OfficialAdapter;
import com.csc472.hw4knowyourgov.model.GoogleCivicAPI;
import com.csc472.hw4knowyourgov.model.Official;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private List<Official> listOfOfficials;
    private OfficialAdapter officialAdapter;
    private TextView location;
    private Locator locator;
    public static int LOCATION_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        location = findViewById(R.id.location);
        locator = new Locator(this);
        if(!amIConnected()){
            showNoNetworkDialog();
        }

        listOfOfficials = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        officialAdapter = new OfficialAdapter(listOfOfficials, this);

        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Official official = listOfOfficials.get(pos);
        Intent officialActivity = new Intent(this, OfficialActivity.class);
        officialActivity.putExtra("heading", location.getText());
        officialActivity.putExtra("official", official);
        startActivity(officialActivity);
    }

    @Override
    public boolean onLongClick(View view) {
        onClick(view);
        return true;
    }




    private void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText dialogText = new EditText(this);
        final MainActivity currentActivity = this;
        dialogText.setInputType(InputType.TYPE_CLASS_TEXT);
        dialogText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        dialogText.setGravity(Gravity.CENTER_HORIZONTAL);

        builder.setView(dialogText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new GoogleCivicAPI(currentActivity).execute(dialogText.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setMessage("Enter a City, State or a Zip Code: ");
        builder.setTitle("Government Location");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateOfficalList(Object[] jsonResults) {
        if(jsonResults != null) {
            listOfOfficials.clear();
            ArrayList<Official> officialsParsed = (ArrayList<Official>) jsonResults[1];
            listOfOfficials.addAll(officialsParsed);
            officialAdapter.notifyDataSetChanged();
            location.setText( (String) jsonResults[0]);
        } else {
            showNoNetworkDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_CODE) {
            int i = 0;
            if (i < permissions.length) {
                do {
                    if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                        if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                            locator.setUpLocationManager();
                            locator.findLocation();
                            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                        } else {
                            Toast.makeText(this, "Location permission was denied", Toast.LENGTH_LONG).show();
                        }
                    }
                    i++;
                } while (i < permissions.length);
            }
        }
    }

    public void doLocationWork(double latitude, double longitude) {
        List<Address> locations;
        try {
            locations = new Geocoder(this, Locale.getDefault()).getFromLocation(latitude, longitude, 1);
            new GoogleCivicAPI(this).execute(locations.get(0).getPostalCode());
        } catch (IOException e) {
            Toast.makeText(this, "Location Service cannot find results for the address", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean amIConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null) != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void showNoNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot Be accessed/loaded \nwithout an internet connection.");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showNoLocationToast() {
        Toast.makeText(this, "Location is unavailable", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.zipCode:
                displayDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }
}
