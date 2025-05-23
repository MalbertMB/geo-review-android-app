package edu.ub.presentation.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.util.Objects;
import edu.ub.presentation.MyApplication;
import edu.ub.presentation.R;
import edu.ub.presentation.databinding.ActivityAddvalorationBinding;
import edu.ub.presentation.pos.ToiletPO;
import edu.ub.presentation.viewmodel.AddValorationViewModel;

public class AddValorationActivity extends AppCompatActivity {
    private String toiletId,clientId,clientActual,clientActualString,nomLavaboString;
    private AddValorationViewModel addValorationViewModel;
    private TextView nomLavabo;
    private RatingBar newValoracio;
    private EditText newComentari;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    private ActivityAddvalorationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddvalorationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        if (getIntent() != null) {
            toiletId = getIntent().getStringExtra("toiletId");
            clientId = getIntent().getStringExtra("clientId");
            Log.d("ToiletInfoActivity2", "Toilet ID: " + toiletId);
        }

        initViewModel();

        nomLavabo = findViewById(R.id.toiletNameTextView);
        newValoracio = findViewById(R.id.ratingBar);
        newComentari = findViewById(R.id.commentEditText);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        binding.selectedImage.setImageURI(selectedImageUri);

                        addValorationViewModel.uploadImage(selectedImageUri);

                        //Barra de progrés
                        addValorationViewModel.getUploadingImage().observe(this, uploading -> {
                            if (uploading != null) {
                                binding.progressBar.setVisibility(uploading ? View.VISIBLE : View.GONE);
                                binding.btnUploadImage.setEnabled(!uploading);
                            }
                        });
                    }
                });

        addValorationViewModel.getUploadedImageUrl().observe(this, url -> {
            if (selectedImageUri != null) {
                // Si hay imagen seleccionada, solo habilitamos botón si ya hay URL subida
                binding.submitButton.setEnabled(url != null && !url.isEmpty());
            } else {
                // Si no hay imagen seleccionada, permitimos enviar sin esperar
                binding.submitButton.setEnabled(true);
            }
        });


        binding.btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        addValorationViewModel.loadToiletInfo(toiletId);

        // Observers
        addValorationViewModel.getToiletLiveData().observe(this, toilet -> {
            if (toilet != null) {
                nomLavaboString = toilet.getName();
                nomLavabo.setText(nomLavaboString);

                // Configura botón solo aquí para evitar múltiples listeners
                binding.submitButton.setOnClickListener(v -> {
                            submitValoration(toilet);
                        });
            } else {
                Log.e("ToiletInfoActivity", "Toilet not found");
            }
        });

        addValorationViewModel.getAddSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Valoració afegida correctament!", Toast.LENGTH_SHORT).show();

                // Tonrem a ValorationActivity
                Intent intent = new Intent(AddValorationActivity.this, ToiletInfoActivity.class);
                intent.putExtra("toiletId", toiletId);
                startActivity(intent);
                finish();
            }
        });

        addValorationViewModel.getAddError().observe(this, throwable -> {
            if (throwable != null) {
                Toast.makeText(this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitValoration(ToiletPO toilet) {
        String comment = newComentari.getText().toString();
        int rating = (int) newValoracio.getRating();

        if (addValorationViewModel.getClientActual().getValue() == null) {
            Toast.makeText(this, "Client no logejat", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si hay imagen seleccionada, asegúrate de tener su URL
        if (selectedImageUri != null) {
            String uploadedUrl = addValorationViewModel.getUploadedImageUrl().getValue();
            if (uploadedUrl == null || uploadedUrl.isEmpty()) {
                return;
            }
        }

        addValorationViewModel.addValoration(toilet, rating, comment);
    }


    private void initViewModel() {
        addValorationViewModel = new ViewModelProvider(
                this,
                ((MyApplication) getApplication()).getViewModelFactory()
        ).get(AddValorationViewModel.class);
    }
}