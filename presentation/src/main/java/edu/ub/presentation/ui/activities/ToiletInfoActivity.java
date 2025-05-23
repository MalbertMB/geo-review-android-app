package edu.ub.presentation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import edu.ub.domain.valueobjects.ValorationUid;
import edu.ub.presentation.MyApplication;
import edu.ub.presentation.R;
import edu.ub.presentation.databinding.ActivityToiletInfoBinding;
import edu.ub.presentation.pos.ToiletPO;
import edu.ub.presentation.ui.adapters.ValorationAdapter;
import edu.ub.presentation.viewmodel.ToiletInfoViewModel;

public class ToiletInfoActivity extends AppCompatActivity {
    private ActivityToiletInfoBinding binding;
    private ToiletInfoViewModel toiletInfoViewModel;
    private ToiletPO currentToilet;
    private TextView nomLavabo, descripcioLavabo, numValoracions;
    private RatingBar valoracioMid;
    private String toiletId;
    private String clientActual;
    private int totalValorations;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToiletInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        if (getIntent() != null) {
            toiletId = getIntent().getStringExtra("toiletId");
        }

        nomLavabo = findViewById(R.id.txtTitle);
        descripcioLavabo = findViewById(R.id.LavaboDesc);
        //Aquesta es la rating bar no mutable que mostra unicament la mitjana
        valoracioMid = findViewById(R.id.LavaboRating);
        numValoracions = findViewById(R.id.txtRatingCount);
        recyclerView = findViewById(R.id.recyclerValorations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initViewModel();

        toiletInfoViewModel.loadToiletInfo(toiletId);

        toiletInfoViewModel.getToiletLiveData().observe(this, toilet -> {
            if (toilet != null) {
                currentToilet = toilet;
                nomLavabo.setText(currentToilet.getName());
                descripcioLavabo.setText(currentToilet.getDescription());

                // Manejo seguro del rating
                float rating = toilet.getRatingAverage();
                binding.txtRatingValue.setText(String.format(Locale.getDefault(), "%.1f", rating));

                binding.LavaboRating.setRating(rating);

                //Número de valoracions
                binding.txtRatingCount.setText(String.valueOf(toilet.getValorationUidList().size()));

                String imageUrl = toilet.getImg_url(); // URL de la foto

                // Set visibility for men icon
                ImageView iconMen = findViewById(R.id.icon_men);
                iconMen.setVisibility(currentToilet.getMen() ? View.VISIBLE : View.GONE);

                // Set visibility for women icon
                ImageView iconWomen = findViewById(R.id.icon_women);
                iconWomen.setVisibility(currentToilet.getWomen() ? View.VISIBLE : View.GONE);

                // Set visibility for unisex icon
                ImageView iconUnisex = findViewById(R.id.icon_unisex);
                iconUnisex.setVisibility(currentToilet.getUnisex() ? View.VISIBLE : View.GONE);

                // Set visibility for accessible icon
                ImageView iconAccessible = findViewById(R.id.icon_accessible);
                iconAccessible.setVisibility(currentToilet.getHandicap() ? View.VISIBLE : View.GONE);

                // Set visibility for free icon
                ImageView iconFree = findViewById(R.id.icon_free);
                iconFree.setVisibility(currentToilet.getFree() ? View.VISIBLE : View.GONE);

                ImageView iconNotFree = findViewById(R.id.icon_not_free);
                iconNotFree.setVisibility(currentToilet.getFree() ? View.GONE : View.VISIBLE);

                // Set visibility for baby icon
                ImageView iconBaby = findViewById(R.id.icon_baby);
                iconBaby.setVisibility(currentToilet.getBaby() ? View.VISIBLE : View.GONE);

                if (imageUrl.equals("default")) {
                    // Set a default image (drawable resource, not_ava.png)
                    Glide.with(this)
                            .load(R.drawable.img_not_ava)
                            .into(binding.LavaboImg);

                }else{
                    // Load the image from the URL
                    Glide.with(this)
                            .load(imageUrl)
                            .into(binding.LavaboImg);
                }
                binding.LavaboImg.setOnClickListener(v -> {
                    showFullscreenImageDialog(currentToilet.getImg_url());
                });


                //Get valorations
                List<String> valorationUids = new ArrayList<>();
                for (ValorationUid uidObj : currentToilet.getValorationUidList()) {
                    valorationUids.add(uidObj.getUID());
                }

                toiletInfoViewModel.fetchAllValorationsByUids(valorationUids, valorationsList -> {
                    Log.d("ToiletInfoActivity", "All valorations fetched: " + valorationsList.size());

                    ValorationAdapter adapter = new ValorationAdapter(this, valorationsList, toiletInfoViewModel);
                    recyclerView.setAdapter(adapter);
                });

            }

            toiletInfoViewModel.getClientActual().observe(this, clientId -> {
                clientActual = String.valueOf(clientId);

                if (clientActual != null && !clientActual.equals("null")) {
                    String toiletId = currentToilet.getToiletUid().getUid();

                    FirebaseFirestore.getInstance()
                            .collection("valoracions")
                            .whereEqualTo("clientId", clientActual)
                            .whereEqualTo("toiletUid", toiletId)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // L'usuari ja ha valorat
                                    binding.btnAddValoration.setEnabled(false);
                                    binding.btnAddValoration.setVisibility(View.VISIBLE);
                                    binding.btnAddValoration.setText("Ja has valorat aquest lavabo");

                                } else {
                                    // L'usuari no ha valorat encara, pot valorar
                                    binding.btnAddValoration.setEnabled(true);
                                    binding.btnAddValoration.setVisibility(View.VISIBLE);
                                    binding.btnAddValoration.setText("Valora aquest lavabo");

                                    // Open AddValorationActivity
                                    binding.btnAddValoration.setOnClickListener(v -> {
                                        Intent intent = new Intent(ToiletInfoActivity.this, AddValorationActivity.class);
                                        intent.putExtra("toiletId", toiletId);
                                        intent.putExtra("clientId", clientActual);
                                        startActivity(intent);
                                        finish();
                                    });

                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error buscant valoracions", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    binding.btnAddValoration.setEnabled(false);
                    binding.btnAddValoration.setVisibility(View.VISIBLE);
                    binding.btnAddValoration.setText("Registra't per valorar");
                }


            });

            //Comprovem el LogIn de l'usuari
            toiletInfoViewModel.checkLoggedInClient();

        });

    }

    private void initViewModel() {
        /* Init viewmodel */
        toiletInfoViewModel = new ViewModelProvider(
                this,
                ((MyApplication) getApplication()).getViewModelFactory()
        ).get(ToiletInfoViewModel.class);
    }

    private void showFullscreenImageDialog(String imageUrl) {
        final android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_fullscreen);

        ImageView fullscreenImage = dialog.findViewById(R.id.full_image);

        if (imageUrl.equals("default")) {
            fullscreenImage.setImageResource(R.drawable.img_not_ava);
        } else {
            Glide.with(this)
                    .load(imageUrl)
                    .into(fullscreenImage);
        }

        // Tanquem el diàleg al fer clic
        fullscreenImage.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}