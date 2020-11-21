package com.example.mapcasestudy.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import libs.mjn.prettydialog.PrettyDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mapcasestudy.R;
import com.example.mapcasestudy.http.RestService;
import com.example.mapcasestudy.models.BookingResponse;
import com.example.mapcasestudy.models.StationInfoList;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    /**
     * Map
     */
    private GoogleMap m_Map;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationProviderClient;

    /**
     * Represents a geographical location.
     */
    Location currentLocation;
    /**
     * Request code
     */
    private static final int REQUEST_CODE = 101;
    /**
     * Map marker
     */
    Marker stationMarker;
    /**
     * List to add Lat/Lng
     */
    List<LatLng> polylines = null;

    private LatLng origin = null;

    private LatLng destination = null;

    MarkerOptions markerOptionsStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        initialize();
    }

    private void initialize() {
        polylines = new ArrayList<>();
        RestService.initializeSyncHttpService();
        RestService.initializeHttpService();
        fetchLocation();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = mFusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    assert mapFragment != null;
                    mapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
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
        if (currentLocation!=null) {
            googleMap.setOnMarkerClickListener(this);
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.oval);
            markerOptions.icon(icon);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            googleMap.addMarker(markerOptions);
            m_Map = googleMap;

            showStationsOnMap(googleMap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    /*private void getLocationPermission() {
     *//*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     *//*
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }*/

    private void showStationsOnMap(GoogleMap googleMap) {
        Call<List<StationInfoList>> stationInfoResponseCall = RestService.getHttpService().getStationInfo();

        stationInfoResponseCall.enqueue(new Callback<List<StationInfoList>>() {
            @Override
            public void onResponse(@NotNull Call<List<StationInfoList>> call, @NotNull Response<List<StationInfoList>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    for (int i=0; i<response.body().size(); i++) {
                        String[] stringSplit = response.body().get(i).getCenter_coordinates().split(",");

                        LatLng latLng = new LatLng(Double.parseDouble(stringSplit[0]), Double.parseDouble(stringSplit[1]));
                        markerOptionsStation  = new MarkerOptions().position(latLng);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.group_12);
                        markerOptionsStation .icon(icon);

                        stationMarker = googleMap.addMarker(markerOptionsStation);
                        stationMarker.setTag(response.body().get(i).getId());
                        stationMarker.setTitle(response.body().get(i).getName());
                        stationMarker.setSnippet(response.body().get(i).getCenter_coordinates());

                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<StationInfoList>> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String id = "", name = "", coordinates = "";
        if (marker.getTag()!=null && marker.getTitle()!=null && marker.getSnippet()!=null) {
            id = Objects.requireNonNull(marker.getTag()).toString();
            name = marker.getTitle();
            coordinates = marker.getSnippet();
        }

        showTripConfirmationDialog(name, id);

        return true;
    }

    private void sendBooking(String id) {
        Call<BookingResponse> bookingRequestCall = RestService.getHttpService().bookingRequest(id);

        bookingRequestCall.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(@NotNull Call<BookingResponse> call, @NotNull Response<BookingResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    String[] polyLineArray = response.body().getPolyline();

                    String[] latlong = response.body().getOrigin_coordinates().split(",");
                    double latitude = Double.parseDouble(latlong[0]);
                    double longitude = Double.parseDouble(latlong[1]);
                    LatLng location = new LatLng(latitude, longitude);
                    origin = location;
                    polylines.add(location);

                    for (String s : polyLineArray) {
                        latlong = s.split(",");
                        latitude = Double.parseDouble(latlong[0]);
                        longitude = Double.parseDouble(latlong[1]);
                        location = new LatLng(latitude, longitude);
                        polylines.add(location);

                    }

                    latlong = response.body().getDestination_coordinates().split(",");
                    latitude = Double.parseDouble(latlong[0]);
                    longitude = Double.parseDouble(latlong[1]);
                    location = new LatLng(latitude, longitude);
                    destination = location;
                    polylines.add(location);
                    Log.e("Poly ",""+polylines);

                    drawPolyLine(m_Map, polylines);
                    showReservationCompletedDialog();
                } else {
                    showErrorDialog();
                }

            }

            @Override
            public void onFailure(@NotNull Call<BookingResponse> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawPolyLine(GoogleMap googleMap, List<LatLng> polylines) {

        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .color(ContextCompat.getColor(this, R.color.color_green))
                .addAll(polylines));

        MarkerOptions markerOptions = new MarkerOptions().position(origin);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.oval_copy);
        markerOptions.icon(icon);
        googleMap.addMarker(markerOptions);

        markerOptions = new MarkerOptions().position(destination);
        icon = BitmapDescriptorFactory.fromResource(R.mipmap.oval_copy_2);
        markerOptions.icon(icon);
        googleMap.addMarker(markerOptions);
    }

    private void showTripConfirmationDialog(String stationName, String id) {

        PrettyDialog pDialog = new PrettyDialog(this);
        pDialog
                .setTitle(stationName)
                .setMessage(getString(R.string.text_message_confirmation))
                .addButton(
                        getString(R.string.text_button_evet),
                        R.color.pdlg_color_white,
                        R.color.color_orange,
                        () -> {
                            sendBooking(id);
                            pDialog.dismiss();
                        }
                )
                .addButton(
                        getString(R.string.text_button_hayır),
                        R.color.black,
                        R.color.color_lightGrey,
                        () -> pDialog.dismiss()
                )
                .setIcon(
                        R.drawable.ic_help,
                        R.color.color_yellow,
                        () -> {
                            //
                        })
                .setAnimationEnabled(true)
                .show();
    }

    private void showReservationCompletedDialog() {

        PrettyDialog pDialog = new PrettyDialog(this);
        pDialog
                .setTitle(getString(R.string.title_message_rezervasyon))
                .setMessage(getString(R.string.text_message_rezervasyon))
                .addButton(
                        getString(R.string.text_button_tamam),
                        R.color.pdlg_color_white,
                        R.color.color_orange,
                        () -> pDialog.dismiss()
                )
                .setIcon(
                        R.drawable.ic_check,
                        R.color.color_green,
                        () -> {
                            //
                        })
                .setAnimationEnabled(true)
                .show();

    }

    private void showErrorDialog() {

        PrettyDialog pDialog = new PrettyDialog(this);
        pDialog
                .setTitle(getString(R.string.title_message_error))
                .setMessage(getString(R.string.text_message_show_error))
                .addButton(
                        getString(R.string.text_button_tekrar),
                        R.color.pdlg_color_white,
                        R.color.color_orange,
                        () -> pDialog.dismiss()
                )
                .addButton(
                        getString(R.string.text_button_vazgeç),
                        R.color.black,
                        R.color.color_lightGrey,
                        () -> pDialog.dismiss()
                )
                .setIcon(
                        R.drawable.ic_error,
                        R.color.color_red,
                        () -> {
                            //
                        })
                .setAnimationEnabled(true)
                .show();
    }
}