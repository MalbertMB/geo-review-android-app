package edu.ub.presentation.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import edu.ub.presentation.MyApplication;
import edu.ub.presentation.R;
import edu.ub.presentation.databinding.FragmentPropersBinding;
import edu.ub.presentation.pos.ToiletPO;
import edu.ub.presentation.ui.activities.ToiletInfoActivity;
import edu.ub.presentation.ui.adapters.PropersAdapter;
import edu.ub.presentation.viewmodel.PropersFragmentViewModel;

public class PropersFragment extends Fragment {
    private static final String TAG = "PropersFragment";
    private FragmentPropersBinding binding;
    private RecyclerView recyclerView;
    private ImageView animationView;
    private PropersAdapter adapter;
    private PropersFragmentViewModel viewModel;
    private static String ARG_LAT = "arg_lat";
    private static String ARG_LNG = "arg_lng";
    FusedLocationProviderClient fusedLocationProviderClient;
    private double latitud;
    private double longitud;

    //Constructor
    public static PropersFragment newInstance(double lat, double lng) {
        PropersFragment fragment = new PropersFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LNG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitud = getArguments().getDouble(ARG_LAT);
            longitud = getArguments().getDouble(ARG_LNG);
            initViewModel();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPropersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(
                this,
                ((MyApplication) requireActivity().getApplication()).getViewModelFactory()
        ).get(PropersFragmentViewModel.class);
        viewModel.setCoords(this.latitud, this.longitud);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View.OnClickListener onItemClickListener = view1 -> {
            ToiletPO clickedToilet = (ToiletPO) view1.getTag();
            Intent intent = new Intent(requireContext(), ToiletInfoActivity.class);
            intent.putExtra("toiletId", String.valueOf(clickedToilet.getToiletUid()));
            startActivity(intent);
        };

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.flFragment);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewLavabos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        animationView = view.findViewById(R.id.not_found_animation);
        loadGifAnimation();

        // Set up adapter
        adapter = new PropersAdapter(getContext(), new ArrayList<>(), onItemClickListener, latitud, longitud);
        recyclerView.setAdapter(adapter);

        // Observe del ViewModel
        viewModel.getNearbyToilets().observe(getViewLifecycleOwner(), lavabos -> {
            if (lavabos.isEmpty()) {
                showNotFoundAnimation();
            } else {
                showToiletsList(lavabos);
            }
        });
    }

    private void loadGifAnimation() {
        try {
            // Load and cache the GIF using Glide
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(this)
                    .asGif()
                    .load(R.raw.not_found) // Replace with your GIF resource
                    .apply(options)
                    .into(animationView);

            Log.d(TAG, "GIF animation loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error loading GIF animation", e);
            Toast.makeText(requireContext(), "Error loading animation: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotFoundAnimation() {
        Log.d(TAG, "No toilets found, showing animation");
        recyclerView.setVisibility(View.GONE);
        animationView.setVisibility(View.VISIBLE);
    }

    private void showToiletsList(List<ToiletPO> lavabos) {
        Log.d(TAG, "Found " + lavabos.size() + " toilets, hiding animation");
        animationView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.updateData(lavabos);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (animationView != null) {
            Glide.with(this).clear(animationView);
        }
    }
}