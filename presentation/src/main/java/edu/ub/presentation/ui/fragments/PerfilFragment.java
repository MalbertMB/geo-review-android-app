package edu.ub.presentation.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import edu.ub.domain.session.SessionManager;
import edu.ub.presentation.MyApplication;
import edu.ub.presentation.R;
import edu.ub.presentation.databinding.FragmentPerfilBinding;
import edu.ub.presentation.pos.ClientPO;
import edu.ub.presentation.pos.ToiletPO;
import edu.ub.presentation.ui.activities.LogInActivity;
import edu.ub.presentation.ui.activities.SignUpActivity;
import edu.ub.presentation.ui.activities.ToiletInfoActivity;
import edu.ub.presentation.ui.adapters.ValorationAdapter;
import edu.ub.presentation.ui.adapters.ValorationProfileAdapter;
import edu.ub.presentation.viewmodel.PerfilFragmentViewModel;
import edu.ub.presentation.viewmodel.ToiletInfoViewModel;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;

    private PerfilFragmentViewModel perfilFragmentViewModel;
    private ToiletInfoViewModel toiletInfoViewModel;
    private ValorationProfileAdapter valorationAdapter;
    View.OnClickListener onItemClickListener;


    public PerfilFragment() {
        // Constructor públic buit necessari
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        //TReiem ja boto d'afegir labavos no cal ni aqui ni als fragments de despres


        ImageButton imageButtonBottom = requireActivity().findViewById(R.id.fab);
        if (imageButtonBottom != null) {
            imageButtonBottom.setVisibility(View.GONE);
        }

        initViewModel();
        initToiletInfoViewModel();
        setupRecyclerView();
        mostrarUI();
        return binding.getRoot(); // Retornar la vista correcta
    }


    private void mostrarUI() {

        // Observar cambios en el cliente
        perfilFragmentViewModel.getClientActual().observe(getViewLifecycleOwner(), clientPO -> {
            if (clientPO != null) {
                updateLoggedInUI(clientPO,onItemClickListener);
            } else {
                updateLoggedOutUI();
            }
        });

        // Comprobar estado de login
        perfilFragmentViewModel.checkLoggedInClient();

        binding.noBtnLogin.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), LogInActivity.class));
        });

        binding.noBtnSignUp.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), SignUpActivity.class));
        });
    }



    private void updateLoggedInUI(ClientPO client,View.OnClickListener item) {

        binding.btnSignOut.setOnClickListener(view -> {
            perfilFragmentViewModel.logOut();
        });

        binding.btnChangePhoto.setOnClickListener(view -> {
            ConfigFragment configFragment = new ConfigFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, configFragment)
                    .addToBackStack(null)
                    .commit();

        });

        perfilFragmentViewModel.getLogoutSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                requireActivity().startActivity(new Intent(requireContext(), LogInActivity.class));
                requireActivity().finish();
            } else if (success != null) {
                Toast.makeText(requireContext(),
                        "Error al tancar la sessió",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Mostrar dades del client
        binding.profileName.setText(client.getId().toString());

        // Cargar imagen
        Glide.with(this)
                .load(client.getPhotoUrl())
                .into(binding.profileImage);

        // Hide buttons for guests
        binding.noBtnLogin.setVisibility(View.GONE);
        binding.noBtnSignUp.setVisibility(View.GONE);
        binding.noTxtWelcomeTitle.setVisibility(View.GONE);
        binding.noTxtWelcomeSubtitle.setVisibility(View.GONE);
        binding.noTxtBenefitsIntro.setVisibility(View.GONE);
        binding.noTxtBenefit1.setVisibility(View.GONE);
        binding.noTxtBenefit2.setVisibility(View.GONE);
        binding.noTxtBenefit3.setVisibility(View.GONE);
        binding.noProfileImage.setVisibility(View.GONE);
        binding.noBackImage.setVisibility(View.GONE);
        binding.noProfileName.setVisibility(View.GONE);

        binding.btnSignOut.setVisibility(View.VISIBLE);
        binding.btnChangePhoto.setVisibility(View.VISIBLE);
        binding.tvValorationsTitle.setVisibility(View.VISIBLE);
        binding.rvValorations.setVisibility(View.VISIBLE);
        binding.profileImage.setVisibility(View.VISIBLE);
        binding.profileName.setVisibility(View.VISIBLE);
        loadUserValorations(client,item);

    }

    private void updateLoggedOutUI() {
        // Mostrar/ocultar botons
        binding.btnSignOut.setVisibility(View.GONE);
        binding.btnChangePhoto.setVisibility(View.GONE);
        binding.profileImage.setVisibility(View.GONE);
        binding.profileName.setVisibility(View.GONE);


        binding.profileImage.setImageResource(R.drawable.account_circle_24px);
        binding.tvValorationsTitle.setVisibility(View.GONE);
        binding.rvValorations.setVisibility(View.GONE);
    }
    private void initViewModel() {
        Activity activity = requireActivity();

        perfilFragmentViewModel = new ViewModelProvider(
                this,
                ((MyApplication) activity.getApplication()).getViewModelFactory()
        ).get(PerfilFragmentViewModel.class);
    }

    private void initToiletInfoViewModel() {
        Activity activity = requireActivity();
        toiletInfoViewModel = new ViewModelProvider(
                this,
                ((MyApplication) activity.getApplication()).getViewModelFactory()
        ).get(ToiletInfoViewModel.class);
    }

    private void setupRecyclerView(){
        binding.rvValorations.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void loadUserValorations(ClientPO clientPO,View.OnClickListener onItemClickListener){
        perfilFragmentViewModel.loadValorationsForClient();
        perfilFragmentViewModel.getValorationsByClient().observe(getViewLifecycleOwner(), valorations -> {
            if (valorations != null && !valorations.isEmpty()) {
                valorationAdapter = new ValorationProfileAdapter(requireContext(), valorations, clientPO.getPhotoUrl(),  toiletInfoViewModel, onItemClickListener);
                binding.rvValorations.setAdapter(valorationAdapter);
            } else {
                binding.rvValorations.setAdapter(null);
            }
        });
    }


}
