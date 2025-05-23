package edu.ub.presentation.ui.adapters;

import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import android.os.Handler;

import edu.ub.presentation.R;
import edu.ub.presentation.databinding.LavaboInfoWindowBinding;
import edu.ub.presentation.pos.ToiletPO;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.HashSet;
import java.util.Set;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final Context context;
    private final Set<Marker> updatedMarkers = new HashSet<>();
    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoContents(@NonNull Marker marker) {
        LavaboInfoWindowBinding binding = LavaboInfoWindowBinding.inflate(LayoutInflater.from(context));
        ToiletPO toilet = (ToiletPO) marker.getTag();  // Accés directe al lavabo

        if (toilet != null) {
            binding.nomLavabo.setText(toilet.getName());
            binding.LavaboDesc.setText(toilet.getDescription());
            binding.LavaboRating.setRating(toilet.getRatingAverage());

            String imageUrl = toilet.getImg_url().trim();

            Glide.with(context)
                    .load(imageUrl)
                    .error(R.drawable.wc_pd)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            binding.LavaboImg.setImageDrawable(resource);
                            binding.LavaboRating.setRating(toilet.getRatingAverage());

                            // Actualitzem només un cop per marcador
                            if (!updatedMarkers.contains(marker)) {
                                updatedMarkers.add(marker);  // Marcar que ya esta actualitzat

                                // Refresquem el InfoWindow només una vegada
                                marker.hideInfoWindow();
                                new Handler(Looper.getMainLooper()).postDelayed(() -> marker.showInfoWindow(), 100);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }

                    });
        }
            return binding.getRoot();
    }

    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null; // Deixa que getInfoContents es mostri dins la "caixa" predeterminada
    }
}
