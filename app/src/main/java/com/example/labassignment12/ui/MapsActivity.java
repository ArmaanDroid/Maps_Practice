package com.example.labassignment12.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.labassignment12.GetDirectionData;
import com.example.labassignment12.GetNearbyPlaceData;
import com.example.labassignment12.R;
import com.example.labassignment12.db.DataBaseClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 1;

    // latitude, longitude
    double latitude, longitude;
    double dest_lat, dest_lng;
    int rowNumber;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    Marker baseMarker;
    Marker destMarker;
    final int RADIUS = 1500;
    List<Marker> markers = new ArrayList<>();
    List<Marker> destMarkers = new ArrayList<>();
    DataBaseClass dataBase;

    static boolean directionRequested;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        dataBase = new DataBaseClass(this);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if (grantResults.length >0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }




    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude()+" "+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment)
                            getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.manu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId())
        {

            case R.id.none:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void btnClick(View view) {

        Object[] dataTransfer;
        String url;
        GetNearbyPlaceData getNearbyPlaceData = new GetNearbyPlaceData();

        switch (view.getId()) {
            case R.id.btn_restaurant:
                // get the url from place api
                url = getUrl(latitude, longitude, "restaurant");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlaceData.execute(dataTransfer);
                Toast.makeText(this, "Restaurants", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_museum:
                // get the url from place api
                url = getUrl(latitude, longitude, "museum");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlaceData.execute(dataTransfer);
                Toast.makeText(this, "Museums", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cafe:
                // get the url from place api
                url = getUrl(latitude, longitude, "cafe");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlaceData.execute(dataTransfer);
                Toast.makeText(this, "Cafe", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_distance:
            case R.id.btn_direction:
                url = getDirectionUrl();
                Log.i("MapsActivity", url);
                dataTransfer = new Object[3];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(dest_lat, dest_lng);

                GetDirectionData getDirectionData = new GetDirectionData();
                //execute asynchronously
                getDirectionData.execute(dataTransfer);
                if (view.getId() == R.id.btn_direction)
                    directionRequested = true;
                else
                    directionRequested = false;
                break;


        }
    }


    @Override
    public void onMapReady( GoogleMap googleMap) {

        mMap = googleMap;

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
        mMap.addMarker(markerOptions);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Location location = new Location("Your Destination");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                dest_lat = latLng.latitude;
                dest_lng = latLng.longitude;

                //set marker
                setBaseMarker(location);
            }

        });

//        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//            @Override
//            public void onMarkerDragStart(Marker marker) {
//
//            }
//
//            @Override
//            public void onMarkerDrag(Marker marker) {
//
//            }
//
//            @Override
//            public void onMarkerDragEnd(Marker marker) {
//                String  newAddress = "";
//                LatLng draggedPos = marker.getPosition();
//
//                dest_lat = draggedPos.latitude;
//                dest_lng = draggedPos.longitude;
//
//                newAddress = geoCoder(draggedPos);
//                if (!MainActivity.dataBase.contains(newAddress)){
//                    Boolean inserted = db.updateData(MainActivity.dataBase.get(rowNum) , String.valueOf(marker.getPosition().latitude) , String.valueOf(marker.getPosition().longitude) , newAddress , "False");
//                    if (inserted){
//                        lat.add(String.valueOf(destLat));
//                        lng.add(String.valueOf(destLng));
//                        MainActivity.dataBase.set(rowNumber , newAddress);
//                        MainActivity.arrayAdapter.notifyDataSetChanged();
//                    }else{
//
//                        Toast.makeText(MapsActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
//                    }
//
//                }else{
//                    Toast.makeText(MapsActivity.this, "Location already saved", Toast.LENGTH_SHORT).show();
//
//
//            }
//        });

    }

    private void setBaseMarker(Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        //this part wasnt working with location reference
        latitude = location.getLatitude();
        longitude = location.getLongitude();


        MarkerOptions options = new MarkerOptions().position(userLatLng);
        baseMarker = mMap.addMarker(options);
        markers.add(baseMarker);

        if (markers.size() >= 2){
            for (Marker marker:markers){
                marker.remove();
            }markers.clear();

        }
        baseMarker = mMap.addMarker(options);
        markers.add(baseMarker);
//                .title("Your Destination")
//                .snippet("You're Going There")
//                .draggable(true);
//        mMap.addMarker(options);
        if (rowNumber == -1) {
            cameraAnimantion(userLatLng);
        }
    }

    private void setFavouriteMarker(LatLng latLng) {

        dest_lat = latLng.latitude;
        dest_lng = latLng.longitude;

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));


        if (rowNumber >= 0) {
            options.draggable(true);
        }


        destMarker = mMap.addMarker(options);
        destMarkers.add(destMarker);
        cameraAnimantion(latLng);
    }

    private void cameraAnimantion(LatLng latLng) {


        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latLng)
                .zoom(15)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }


    private String getDirectionUrl() {
        StringBuilder directionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        directionUrl.append("origin="+latitude+","+longitude);
        directionUrl.append("&destination="+dest_lat+","+dest_lng);
        directionUrl.append("&key="+getString(R.string.api_key_places));
        System.out.println(directionUrl);
        return directionUrl.toString();
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeUrl.append("location="+latitude+","+longitude);
        placeUrl.append("&radius="+RADIUS);
        placeUrl.append("&type="+nearbyPlace);
        placeUrl.append("&key="+getString(R.string.api_key_places));
        return placeUrl.toString();
    }
}
