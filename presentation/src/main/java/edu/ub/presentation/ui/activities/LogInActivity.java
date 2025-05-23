package edu.ub.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import java.util.Objects;

import edu.ub.presentation.MyApplication;
import edu.ub.presentation.databinding.ActivityLogInBinding;
import edu.ub.presentation.viewmodel.LogInViewModel;

public class LogInActivity extends AppCompatActivity {

  private ActivityLogInBinding binding;
  private LogInViewModel logInViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityLogInBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    //Amagem la barra lila de LogInActivity
    Objects.requireNonNull(getSupportActionBar()).hide();

    initViewModel();
    initWidgetListeners();
  }

  private void initWidgetListeners() {
    binding.btnLogIn.setOnClickListener(view -> {
      String enteredUsername = binding.etLoginUsername.getText().toString();
      String enteredPassword= binding.etLoginPassword.getText().toString();
      enteredUsername = enteredUsername.trim();

      if (enteredUsername.isEmpty())
        Toast.makeText(this, "Usuari no pot estar buit", Toast.LENGTH_SHORT).show();
      else if (enteredPassword.isEmpty())
        Toast.makeText(this, "Contrassenya no pot estar buit", Toast.LENGTH_SHORT).show();
      else
        logInViewModel.logIn(enteredUsername, enteredPassword);
    });

    binding.btnSignUp.setOnClickListener(view -> {
      startActivity(new Intent(this, SignUpActivity.class));
    });
  }

  private void initViewModel() {
    logInViewModel = new ViewModelProvider(
            this,
            ((MyApplication) getApplication()).getViewModelFactory()
    ).get(LogInViewModel.class);

    logInViewModel.getLogInState().observe(this, logInState -> {
      switch (logInState.getStatus()) {
        case LOADING:
          binding.btnLogIn.setEnabled(false);
          break;
        case SUCCESS:
          SharedPreferences.Editor editor = getSharedPreferences("LOGIN", MODE_PRIVATE).edit();
          String clientJson = new Gson().toJson(logInState.getData());
          editor.putString("CLIENT_MODEL", clientJson);
          editor.apply();
          startActivity(new Intent(this, MainActivity.class));
          finish();
          break;
        case ERROR:
          Toast.makeText(this, logInState.getError().getMessage(), Toast.LENGTH_SHORT).show();
          binding.btnLogIn.setEnabled(true);
          break;
      }
    });
  }
}
