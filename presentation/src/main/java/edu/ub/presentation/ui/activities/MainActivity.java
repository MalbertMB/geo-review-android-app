package edu.ub.presentation.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Objects;

import edu.ub.presentation.MyApplication;
import edu.ub.presentation.Permission;
import edu.ub.presentation.R;
import edu.ub.presentation.databinding.ActivityMainBinding;
import edu.ub.presentation.ui.fragments.MapsFragment;
import edu.ub.presentation.ui.fragments.PropersFragment;
import edu.ub.presentation.viewmodel.MapsFragmentViewModel;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Location currentLocation;
    private static final int FINE_PERMISSION_CODE = 1;
    private MapsFragmentViewModel mapsFragmentViewModel;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       //El cloudinary s'inicialitza a MyApplication per evitar errrors de init

        //No titol
        Objects.requireNonNull(getSupportActionBar()).hide();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mapsFragmentViewModel = new ViewModelProvider(
                this,
                ((MyApplication) getApplication()).getViewModelFactory()
        ).get(MapsFragmentViewModel.class);


        //default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, new MapsFragment())
                    .commit();
        }
        bottomNavSelected();
    }

    public <T> void observeOnce(LiveData<T> liveData, Observer<T> observer) {
        liveData.observe(this, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                observer.onChanged(t);
                liveData.removeObserver(this);
            }
        });
    }
    public void bottomNavSelected() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                binding.fab.setVisibility(View.VISIBLE);
                replaceFragment(new MapsFragment());
            } else if (item.getItemId() == R.id.nav_propers){
                binding.fab.setVisibility(View.GONE);
                //actualitzem localització
                getLastLocation();
                gotoLabavosPropers();
            }
            return true;
        });
    }

    private void gotoLabavosPropers() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Permission.requestPermission(this, FINE_PERMISSION_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                mapsFragmentViewModel.checkLoggedInClient();
                observeOnce(mapsFragmentViewModel.getClientActual(), clientId -> {
                    if (clientId != null) {
                        // Usuari no loggejat
                        Fragment propersFragment = PropersFragment.newInstance(location.getLatitude(), location.getLongitude());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.flFragment, propersFragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                        startActivity(intent);
                    }
                });

            } else {
                Toast.makeText(this, "No s'ha pogut obtenir la localització actual", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Permission.requestPermission(this, FINE_PERMISSION_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
            return; // Return si no tenim els permissos
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                // Navigate to PropersFragment utilitzan location
                mapsFragmentViewModel.checkLoggedInClient();
                observeOnce(mapsFragmentViewModel.getClientActual(), clientId -> {
                    if (clientId != null) {
                        PropersFragment fragment = PropersFragment.newInstance(
                                currentLocation.getLatitude(), currentLocation.getLongitude());
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flFragment, fragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        startActivity(new Intent(this, LogInActivity.class));
                    }
                });
            } else {
                Toast.makeText(this, "Ubicació no trobada", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void replaceFragment(Fragment frahment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, frahment);
        fragmentTransaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permís concebut
                Toast.makeText(this, "Permís d'ubicació CONCEBUT", Toast.LENGTH_SHORT).show();
                // Obtenir l'ubicació l'acceptar permisos
                MapsFragment mapsFragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.flFragment);
                if (mapsFragment != null) {
                    mapsFragment.getLastLocation();
                    mapsFragment.enableMyLocation();
                } else {
                    Toast.makeText(this, "No se encontró MapsFragment", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Permis denegat
                Toast.makeText(this, "Permís d'ubicació DENEGAT", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showFab(){
        binding.fab.setVisibility(View.VISIBLE);

    }

}