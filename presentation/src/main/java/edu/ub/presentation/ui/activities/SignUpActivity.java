package edu.ub.presentation.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import java.util.Objects;
import edu.ub.presentation.MyApplication;
import edu.ub.presentation.databinding.ActivitySignUpBinding;
import edu.ub.presentation.viewmodel.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {

  private ActivitySignUpBinding binding;
  private SignUpViewModel signUpViewModel;
  private static final int PICK_IMAGE_REQUEST = 1;
  private Uri selectedImageUri;
  private CheckBox selectImageCheckBox;
  private ImageView profileImage;
  private ActivityResultLauncher<Intent> imagePickerLauncher;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivitySignUpBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    initViewModel();
    initWidgetListeners();
    Objects.requireNonNull(getSupportActionBar()).hide();


    //botó de retorn
    binding.btnReturn.setOnClickListener(v -> {
      setResult(Activity.RESULT_CANCELED);
      finish();
    });
    //Pujar imatges
    imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
              if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                binding.profileImage.setImageURI(selectedImageUri);
              }

              signUpViewModel.uploadImage(selectedImageUri);

              signUpViewModel.getUploadingImage().observe(this, uploading -> {
                if (uploading != null) {
                  binding.progressBar.setVisibility(uploading ? View.VISIBLE : View.GONE);
                  binding.btnSignUp.setEnabled(!uploading);
                }
              });
            });

    binding.btnPujarFotoPerfil.setOnClickListener(v -> {
      Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      imagePickerLauncher.launch(intent);
    });

  }

  private void initWidgetListeners() {
    binding.btnSignUp.setOnClickListener(view -> {

      if(selectedImageUri!=null){

        //signUpViewModel.uploadImage(selectedImageUri);

        signUpViewModel.getUploadedImageUrl().observe(this, url -> {
          if (url != null) {
            signUpViewModel.signUp(
                    binding.etSignupUsername.getText().toString(),
                    binding.etSignUpEmail.getText().toString(),
                    binding.etSignupPassword.getText().toString(),
                    binding.etSignupPasswordConfirmation.getText().toString(),
                    selectedImageUri
            );
          }});
        }else{
        // No hay imagen, simplemente registramos
        signUpViewModel.signUp(
                binding.etSignupUsername.getText().toString(),
                binding.etSignUpEmail.getText().toString(),
                binding.etSignupPassword.getText().toString(),
                binding.etSignupPasswordConfirmation.getText().toString(), null);
      }
    });
  }

  /*genera un numero entre 1 i 5*/
  private void initViewModel() {
    signUpViewModel = new ViewModelProvider(
            this,
            ((MyApplication) getApplication()).getViewModelFactory()
    ).get(SignUpViewModel.class);

    signUpViewModel.getSignUpState().observe(this, state -> {
      switch (state.getStatus()) {
        case SUCCESS:
          Toast.makeText(this, "Usuari creat correctament!" , Toast.LENGTH_SHORT).show();
          startActivity(new Intent(this, LogInActivity.class));
          finish(); // Torna enrere a LogInActivity
          break;
        case ERROR:
          Toast.makeText(this, state.getError().getMessage(), Toast.LENGTH_SHORT).show();
          break;
      }
    });

  }

  /*Desplegable revisar*/
  private void selectorDimatge() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
      selectedImageUri = data.getData();
      profileImage.setImageURI(selectedImageUri);
      selectImageCheckBox.setChecked(false); // opcional: desmarcar el checkbox
    }
  }

}
