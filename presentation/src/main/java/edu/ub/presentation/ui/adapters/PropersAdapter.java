package edu.ub.presentation.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.Locale;

import edu.ub.presentation.R;
import edu.ub.presentation.pos.ToiletPO;

public class PropersAdapter extends RecyclerView.Adapter<PropersAdapter.PropersViewHolder> {
    private List<ToiletPO> lavabos;
    private Context context;
    private View.OnClickListener onClickListener;
    double lavaboLat;
    double lavaboLng;
    private double clientLat;
    private double clientLng;

    public PropersAdapter(Context context, List<ToiletPO> labavos, View.OnClickListener onClickListener, double clientLat, double clientLng){
        this.context = context;
        this.lavabos = labavos;
        this.onClickListener = onClickListener;
        this.clientLat = clientLat;
        this.clientLng = clientLng;
    }

    @NonNull
    @Override
    public PropersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.labavo_rv_proper, parent, false);
        return new PropersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropersViewHolder holder, int position) {
        ToiletPO lavabo = lavabos.get(position);
        float rating = lavabo.getRatingAverage();

        holder.txtTitle.setText(lavabo.getName());
        holder.txtRatingValue.setText(String.format(Locale.getDefault(), "%.1f", rating));
        holder.txtRatingCount.setText("(" + lavabo.getValorationUidList().size() + ")");
        holder.ratingBar.setRating(rating);
        holder.desc.setText(lavabo.getDescription());
        holder.caracteristiques.setText("Característiques:");
        lavaboLat = lavabo.getLatitude();
        lavaboLng = lavabo.getLongitude();
        double distancia = calculateDistance(clientLat, clientLng, lavaboLat, lavaboLng);

        holder.distancia.setText(String.format("Distància: %.0f m", distancia));


        // Carrega imatge amb Glide, Picasso, etc.
        Glide.with(context)
                .load(lavabo.getImg_url())
                .placeholder(R.drawable.img_not_ava)
                .into(holder.imatge);

        // Mostra o amaga icones segons les característiques del lavabo
        holder.icon_men.setVisibility(lavabo.getMen() ? View.VISIBLE : View.GONE);
        holder.icon_women.setVisibility(lavabo.getWomen() ? View.VISIBLE : View.GONE);
        holder.icon_unisex.setVisibility(lavabo.getUnisex() ? View.VISIBLE : View.GONE);
        holder.icon_accessible.setVisibility(lavabo.getHandicap() ? View.VISIBLE : View.GONE);
        holder.icon_free.setVisibility(lavabo.getFree() ? View.VISIBLE : View.GONE);
        holder.icon_not_free.setVisibility(lavabo.getFree() ? View.GONE : View.VISIBLE);
        holder.icon_baby.setVisibility(lavabo.getBaby() ? View.VISIBLE : View.GONE);

        holder.itemView.setTag(lavabo);
        holder.itemView.setOnClickListener(onClickListener);

    }

    @Override
    public int getItemCount() {
        return lavabos != null ? lavabos.size() : 0;
    }

    public void updateData(List<ToiletPO> newLavabos) {
        this.lavabos = newLavabos;
        notifyDataSetChanged();
    }

    static class PropersViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtRatingValue, txtRatingCount, desc, caracteristiques, distancia;
        RatingBar ratingBar;
        ShapeableImageView imatge;
        ImageView icon_men, icon_women, icon_unisex, icon_accessible, icon_free, icon_not_free, icon_baby;

        public PropersViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtRatingValue = itemView.findViewById(R.id.txtRatingValue);
            txtRatingCount = itemView.findViewById(R.id.txtRatingCount);
            ratingBar = itemView.findViewById(R.id.LavaboRating);
            imatge = itemView.findViewById(R.id.LavaboImg);
            desc = itemView.findViewById(R.id.LavaboDesc);
            caracteristiques = itemView.findViewById(R.id.LavaboCaracteristicas);
            distancia = itemView.findViewById(R.id.distancia);

            icon_men = itemView.findViewById(R.id.icon_men);
            icon_women = itemView.findViewById(R.id.icon_women);
            icon_unisex = itemView.findViewById(R.id.icon_unisex);
            icon_accessible = itemView.findViewById(R.id.icon_accessible);
            icon_free = itemView.findViewById(R.id.icon_free);
            icon_not_free = itemView.findViewById(R.id.icon_not_free);
            icon_baby = itemView.findViewById(R.id.icon_baby);
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double earthRadius = 6371.0; // en km
        return earthRadius * c * 1000; // en metres
    }

}