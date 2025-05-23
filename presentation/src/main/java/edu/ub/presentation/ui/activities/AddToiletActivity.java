package edu.ub.presentation.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.util.Objects;
import edu.ub.presentation.MyApplication;
import edu.ub.presentation.databinding.ActivityAddtoiletBinding;
import edu.ub.presentation.viewmodel.AddToiletViewModel;

public class AddToiletActivity extends AppCompatActivity {
    private ActivityAddtoiletBinding binding;
    private String coord;
    private String selectedImageUri;
    private AddToiletViewModel addToiletViewModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String usuariActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddtoiletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();

        Objects.requireNonNull(getSupportActionBar()).hide();

        // Recuperar las coordenadas del Intent
        if (getIntent() != null) {
            double latitude = getIntent().getDoubleExtra("latitude", 0.0);
            double longitude = getIntent().getDoubleExtra("longitude", 0.0);
            // Unificar las coordenades en un sol string
            coord = String.valueOf(latitude) + "," + String.valueOf(longitude);
        }

        addToiletViewModel.getAddError().observe(this, throwable -> {
            if (throwable != null) {
                Toast.makeText(this, "Error adding toilet", Toast.LENGTH_SHORT).show();
            }
        });


        //No podem instanciar la clase ClientID aqui, utilitzem el viewModel
        addToiletViewModel.getUsuariActual().observe(this, userId -> {
            usuariActual = String.valueOf(userId);
        });

        addToiletViewModel.checkLoggedInUser();

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        selectedImageUri = String.valueOf(uri);
                        binding.selectedImage.setImageURI(uri);

                        // Pujar imatge immediatament al seleccionar-la
                        addToiletViewModel.uploadImage(Uri.parse(selectedImageUri));

                        // observem la progressBar
                        addToiletViewModel.getUploadingImage().observe(this, uploading -> {
                            if (uploading != null) {
                                binding.progressBar.setVisibility(uploading ? View.VISIBLE : View.GONE);
                                binding.btnAfegir.setEnabled(!uploading);
                            }
                        });

                    }
                });


        binding.btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        binding.btnAfegir.setOnClickListener(v -> {
            String nomText = binding.etNom.getText().toString();
            String descText = binding.etDesc.getText().toString();
            boolean men = binding.checkMen.isChecked();
            boolean women = binding.checkWomen.isChecked();
            boolean unisex = binding.checkUnisex.isChecked();
            boolean handicap = binding.checkHandicap.isChecked();
            boolean baby = binding.checkBabyChanging.isChecked();
            boolean free = binding.checkFree.isChecked();
            float ratingValue = binding.ratingBar.getRating();
            String ratingText = binding.etRatingComment.getText().toString();

            if (nomText.isEmpty() || descText.isEmpty()) {
                Toast.makeText(this, "Omple tots els forats", Toast.LENGTH_SHORT).show();
                return;
            }

            if (unisex && (men || women)) {
                Toast.makeText(this, "Dades de gènere contradictòries", Toast.LENGTH_SHORT).show();
                return;
            }

            String uploadedUrl = addToiletViewModel.getUploadedImageUrl().getValue();
            if (selectedImageUri != null && uploadedUrl == null) {
                return;
            }

            if (ratingValue == 0 && !ratingText.isEmpty()){
                Toast.makeText(this, "Atenció: no pots comentar sense valorar!", Toast.LENGTH_SHORT).show();
                return;
            }

            String imageToSend = (uploadedUrl != null) ? uploadedUrl : "default";

            addToiletViewModel.addToilet(
                    nomText,
                    usuariActual,
                    descText,
                    coord,
                    (int) ratingValue,
                    ratingText,
                    imageToSend,
                    0,
                    men, women, unisex, handicap, free, baby
            );
        });



        // Observar el resulta del ViewModel
        addToiletViewModel.getAddSucces().observe(this, success -> {
            if (success) {
                Toast toast = Toast.makeText(this, "Lavabo afegit correctament", Toast.LENGTH_SHORT);
                // Configurar posició (gravity, offsetX, offsetY)
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
                toast.show();

                // Tanquem l'activitat després de la confirmació
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_toilet_added", true);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        addToiletViewModel.getAddError().observe(this, error -> {
            Toast.makeText(this, "Error adding toilet: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });

        binding.btnReturn.setOnClickListener( v-> {
            finish();
        });

    }

    private void initViewModel() {
        /* Init viewmodel */
        addToiletViewModel = new ViewModelProvider(
                this,
                ((MyApplication) getApplication()).getViewModelFactory()
        ).get(AddToiletViewModel.class);
    }

}

