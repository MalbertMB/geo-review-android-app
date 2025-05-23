package edu.ub.presentation.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import edu.ub.presentation.MyApplication;
import edu.ub.presentation.R;
import edu.ub.presentation.databinding.FragmentConfigBinding;
import edu.ub.presentation.viewmodel.ConfigFragmentViewModel;

public class ConfigFragment extends Fragment {

    private FragmentConfigBinding binding;
    private ConfigFragmentViewModel configFragmentViewModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri = null;
    private String uploadedImageUrl = null;

    public ConfigFragment() {
        // Constructor públic buit necessari
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentConfigBinding.inflate(inflater, container, false);
        initViewModel();
        mostrarUI();
        return binding.getRoot();
    }

    private void initViewModel() {
        Activity activity = requireActivity();

        configFragmentViewModel = new ViewModelProvider(
                this,
                ((MyApplication) activity.getApplication()).getViewModelFactory()
        ).get(ConfigFragmentViewModel.class);
    }

    private void mostrarUI() {
        // Mostrar la foto de perfil actual
        configFragmentViewModel.getCurrentClient().observe(getViewLifecycleOwner(), clientPO -> {
            if (clientPO != null && clientPO.getPhotoUrl() != null) {
                Glide.with(requireContext())
                        .load(clientPO.getPhotoUrl())
                        .into(binding.profileImage);
                uploadedImageUrl = clientPO.getPhotoUrl();
            }
        });

        binding.img8.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        selectedImageUri = uri;
                        binding.profileImage.setImageURI(uri);

                        // Pujar imatge immediatament al seleccionar-la
                        configFragmentViewModel.uploadImage(selectedImageUri);

                        configFragmentViewModel.getUploadedImageUrl().observe(getViewLifecycleOwner(), url -> {
                            if (url != null) {
                                uploadedImageUrl = url;
                                configFragmentViewModel.updateUserPhotoUrl(url);
                            }
                        });

                        // observem la progressBar
                        configFragmentViewModel.getUploadingImage().observe(getViewLifecycleOwner(), uploading -> {
                            if (uploading != null) {
                                binding.progressBar.setVisibility(uploading ? View.VISIBLE : View.GONE);
                                binding.btnGuardarCanvis.setEnabled(!uploading);
                            }
                        });

                    }
                });

        // Clicar una imatge predefinida
        int[] imageButtons = {
                R.id.img1, R.id.img2, R.id.img3, R.id.img4,
                R.id.img5, R.id.img6, R.id.img7
        };
        String[] cloudinaryUrls = {
        "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_caca_ep0yxc.png",
        "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_jesus_lfonnn.png",
        "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_cartman_fr5cwb.png",
        "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_kenny_ceuz7d.png",
        "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605757/predef_kyle_d2y0ae.png",
        "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_stan_lm4h8b.png",
        "https://res.cloudinary.com/do3m0wgbl/image/upload/v1746605756/predef_towelie_ygsqc0.png"};

        for (int i = 0; i < imageButtons.length; i++) {
            final int index = i;
            binding.getRoot().findViewById(imageButtons[i]).setOnClickListener(v -> {
                selectedImageUri = Uri.parse(cloudinaryUrls[index]);
                Glide.with(requireContext())
                        .load(uploadedImageUrl)
                        .into(binding.profileImage);

                configFragmentViewModel.updateUserPhotoUrl(String.valueOf(selectedImageUri));
            });
        }

        binding.btnGuardarCanvis.setOnClickListener(v -> {
            String nouEmail = binding.nouNom.getText().toString().trim();
            boolean emailValid = !nouEmail.isEmpty();
            boolean imageChanged = (selectedImageUri != null || uploadedImageUrl != null);

            if (!emailValid && !imageChanged) {
                Toast.makeText(requireContext(), "Cap canvi detectat", Toast.LENGTH_SHORT).show();
                return;
            }

            if (emailValid) {
                configFragmentViewModel.updateUserEmail(nouEmail);
            }

            if (selectedImageUri != null){
                configFragmentViewModel.updateUserPhotoUrl(uploadedImageUrl);
            }

            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.nav_home);
        });
    }

}
