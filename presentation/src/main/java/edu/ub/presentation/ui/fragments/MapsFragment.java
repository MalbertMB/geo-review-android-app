package edu.ub.presentation.ui.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import edu.ub.presentation.MyApplication;
import edu.ub.presentation.Permission;
import edu.ub.presentation.R;
import edu.ub.presentation.databinding.FragmentMapsBinding;
import edu.ub.presentation.pos.ToiletPO;
import edu.ub.presentation.ui.activities.AddToiletActivity;
import edu.ub.presentation.ui.activities.LogInActivity;
import edu.ub.presentation.ui.activities.MainActivity;
import edu.ub.presentation.ui.activities.ToiletInfoActivity;
import edu.ub.presentation.ui.adapters.CustomInfoWindowAdapter;
import edu.ub.presentation.viewmodel.MapsFragmentViewModel;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private FragmentMapsBinding binding;
    private Marker currentMarker;
    private MapsFragmentViewModel mapsFragmentViewModel;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Retornar la vista correcta
    }

    private final ActivityResultLauncher<Intent> addToiletLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    //Tornem afegir tots els lavabos al crear un nou (refresc)
                    //Canviar en un futur per un getToiletById o algo aixi i no haver de der un altre getAll()
                    mapsFragmentViewModel.loadToiletCoordinates();

                    mapsFragmentViewModel.getToilets().observe(getViewLifecycleOwner(), toilets -> {
                        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(requireContext()));
                        for (ToiletPO toilet : toilets) {
                            String[] parts = toilet.getCoord().split(",");
                            if (parts.length != 2) continue;
                            double lat = Double.parseDouble(parts[0]);
                            double lng = Double.parseDouble(parts[1]);

                            LatLng location = new LatLng(lat, lng);
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(location)
                                    .title(toilet.getName())
                                    .snippet(toilet.getDescription())
                                    .icon(resizeBitmap(requireContext(), R.drawable.logo_cagaub, 80, 120));

                            Marker marker = mMap.addMarker(markerOptions);
                            marker.setTag(toilet); // Associem l’objecte Toilet per recuperar-lo al InfoWindowAdapter
                        }
                    });
                    }else if (result.getResultCode() == Activity.RESULT_CANCELED){
                    if(currentMarker != null){
                        currentMarker.remove();
                        currentMarker = null; //Netegem referencia
                    }
                }
            });

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.flFragment);
        initViewModel();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        getLastLocation();
        binding.btnPerfil.setOnClickListener(v -> {
            PerfilFragment perfilFragment = new PerfilFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, perfilFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.btnInfo.setOnClickListener( v -> {
            mostrarInfo();
        });
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    private void initViewModel() {
        /* Init viewmodel */
        mapsFragmentViewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(MapsFragmentViewModel.class);
    }

    public void getLastLocation(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Permission.requestPermission((MainActivity) requireContext(),FINE_PERMISSION_CODE,Manifest.permission.ACCESS_FINE_LOCATION,true );
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
                    if (mapFragment != null){
                        mapFragment.getMapAsync(MapsFragment.this);
                    }
                }
            }
        });
    }

    //PROVA
    @SuppressWarnings("unused")
    public void getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        // Mover la cámara a la ubicación actual
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                    } else {
                        Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        // Mover la cámara a la ubicación actual
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                    } else {
                        Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                    }
                });

        //Versió utilitzant el ViewModel, ens subscrivim a addMarker(), si canvia aquest mètode s'executa automàticament
        mapsFragmentViewModel.getAddMarker().observe(getViewLifecycleOwner(), isAdding -> {
            if (mMap == null) return;

            mapsFragmentViewModel.checkLoggedInClient();

            mapsFragmentViewModel.getClientActual().observe(getViewLifecycleOwner(), clientId -> {
                boolean isLoggedIn = clientId != null;

                if (isAdding) {
                    if (isLoggedIn) {
                        //Toast.makeText(getContext(), "Afegir lavabo activat", Toast.LENGTH_SHORT).show();
                        setCardViewBorder(binding.cardViewBorder, Color.BLUE, 8);

                        mMap.setOnMapClickListener(latLng -> {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(latLng)
                                    .title(latLng.latitude + " : " + latLng.longitude)
                                    .icon(resizeBitmap(requireContext(), R.drawable.logo_cagaub, 80, 120));

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                            currentMarker = mMap.addMarker(markerOptions);

                            Intent intent = new Intent(requireContext(), AddToiletActivity.class);
                            intent.putExtra("latitude", latLng.latitude);
                            intent.putExtra("longitude", latLng.longitude);
                            addToiletLauncher.launch(intent);
                        });

                    } else {
                        Toast.makeText(requireContext(), "Aquesta funcionalitat està reservada per a usuaris registrats.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isLoggedIn) {
                        setCardViewBorder(binding.cardViewBorder, Color.TRANSPARENT, 8);
                    }
                    mMap.setOnMapClickListener(null);
                }
            });
        });

        /*Part encarregada del click del popup*/
        mMap.setOnInfoWindowClickListener(marker -> {
            ToiletPO toilet = (ToiletPO) marker.getTag();
            if (toilet != null) {
                String toiletId = String.valueOf(toilet.getToiletUid());
                Intent intent = new Intent(requireContext(), ToiletInfoActivity.class);
                Log.d("ToiletInfoActivity2", "Toilet ID: " + toiletId);
                intent.putExtra("toiletId", toiletId);
                startActivity(intent);
            }
        });
        binding.btnMiBoton.setOnClickListener(v -> mapsFragmentViewModel.addMarker());

        mapsFragmentViewModel.getToilets().observe(getViewLifecycleOwner(), toilets -> {
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(requireContext()));
            for (ToiletPO toilet : toilets) {
                String[] parts = toilet.getCoord().split(",");
                if (parts.length != 2) continue;
                double lat = Double.parseDouble(parts[0]);
                double lng = Double.parseDouble(parts[1]);

                LatLng location = new LatLng(lat, lng);
            ;

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location)
                        .title(toilet.getName())
                        .snippet(toilet.getDescription())
                        //canviar per get Img_url
                        .icon(resizeBitmap(requireContext(), R.drawable.logo_cagaub, 80, 120));

                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(toilet); // Associem l’objecte Toilet per recuperar-lo al InfoWindowAdapter
            }
        });
    }

    public void enableMyLocation () {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            Permission.requestPermission((MainActivity) requireContext(), FINE_PERMISSION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
    }

    //Funció per canviar les dimensions de la icona del marcador
    private BitmapDescriptor resizeBitmap(Context context, int resId, int width, int height) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(context, resId);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    //Funció per canviar el color del border del fragment
    public void setCardViewBorder(CardView cardView, int color, int width) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(0);  //Border sense corner
        shape.setStroke(width, color);
        shape.setColor(Color.TRANSPARENT);  // Fons tranparent
        cardView.setBackground(shape);
    }

    //Métode per mostrar la pestanya de info al fragment
    public void mostrarInfo(){
        String missatge =
                "<b>1. Crea un compte</b>: " + getString(R.string.infoMessage1) + "<br><br>" +
                "<b>2. Inicia sessió</b>: " + getString(R.string.infoMessage2) + "<br><br>" +
                "<b>3. Afegeix lavabos</b>: " + getString(R.string.infoMessage3);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.benvingut))
                .setMessage(Html.fromHtml(missatge, Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton(getString(R.string.button), (dialog, which) -> dialog.dismiss())
                .setIcon(R.drawable.info_logo)
                .show();
    }
}