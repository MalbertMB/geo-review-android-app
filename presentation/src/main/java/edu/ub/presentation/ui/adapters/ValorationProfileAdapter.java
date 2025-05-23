package edu.ub.presentation.ui.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.time.LocalDateTime;
import java.util.List;

import edu.ub.presentation.R;
import edu.ub.presentation.pos.ValorationPO;
import edu.ub.presentation.viewmodel.ToiletInfoViewModel;

public class ValorationProfileAdapter extends RecyclerView.Adapter<ValorationProfileAdapter.ValorationViewHolder> {

    private final Context context;
    private final List<ValorationPO> valorations;

    private final String userFotoUrl;
    private View.OnClickListener onClickListener;
    private ToiletInfoViewModel toiletInfoViewModel;

    public ValorationProfileAdapter(Context context, List<ValorationPO> valorations, String userFotoUrl, ToiletInfoViewModel toiletInfoViewModel, View.OnClickListener onClickListener) {
        this.context = context;
        this.valorations = valorations;
        this.onClickListener = onClickListener;
        this.toiletInfoViewModel = toiletInfoViewModel;
        this.userFotoUrl = userFotoUrl;
    }

    @NonNull
    @Override
    public ValorationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_valoration_profile, parent, false);
        return new ValorationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ValorationViewHolder holder, int position) {
        ValorationPO valoration = valorations.get(position);

        // Set user foto
        if (userFotoUrl != null && !userFotoUrl.isEmpty() && !userFotoUrl.equals("default")) {
            Glide.with(context)
                    .load(userFotoUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.imgUserPhoto);
        } else {
            holder.imgUserPhoto.setImageResource(R.drawable.ic_launcher_foreground);
        }


        if (valoration.getImg_Url() != null && !valoration.getImg_Url().isEmpty() && !valoration.getImg_Url().equals("default")) {
            Glide.with(context)
                    .load(valoration.getImg_Url())
                    .placeholder(R.drawable.wc_pd)
                    .into(holder.LavaboImg);


            holder.LavaboImg.setOnClickListener(v -> showFullscreenImageDialog(valoration.getImg_Url()));
            holder.LavaboImg.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(onClickListener);
        } else {
            holder.LavaboImg.setVisibility(View.GONE);
        }


        // Set rating
        holder.ratingBar.setRating(valoration.getRating());

        // Set comment
        holder.txtUserComment.setText(valoration.getComment().getText());

        // Set date
        LocalDateTime date = valoration.getDate();
        LocalDateTime now = LocalDateTime.now();
        long weeksOld = java.time.temporal.ChronoUnit.WEEKS.between(date, now);
        String dateText = " fa " + weeksOld + " setmanes ";
        if (weeksOld == 0) {
            dateText = " fa menys d'una setmana ";
        } else if (weeksOld == 1) {
            dateText = " fa una setmana ";
        }
        holder.txtWeeksOld.setText(dateText);

        // Set background color based on weeks old
        if (weeksOld < 2) {
            holder.txtWeeksOld.setBackgroundResource(R.drawable.rounded_text_background_green);
        } else {
            holder.txtWeeksOld.setBackgroundResource(R.drawable.rounded_text_background_gray);
        }
    }

    @Override
    public int getItemCount() {
        return valorations.size();
    }

    public static class ValorationViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserComment, txtWeeksOld;
        RatingBar ratingBar;
        ImageView imgUserPhoto;
        ImageView LavaboImg; // Declare LavaboImg

        public ValorationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserComment = itemView.findViewById(R.id.txtUserComment);
            txtWeeksOld = itemView.findViewById(R.id.txtWeeksOld);
            ratingBar = itemView.findViewById(R.id.LavaboRating);
            LavaboImg = itemView.findViewById(R.id.LavaboImg); // Initialize LavaboImg
            imgUserPhoto = itemView.findViewById(R.id.imgUserPhoto);
        }
    }

    private void showFullscreenImageDialog(String imageUrl) {
        final android.app.Dialog dialog = new android.app.Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_fullscreen);

        ImageView fullscreenImage = dialog.findViewById(R.id.full_image);

        if ("default".equals(imageUrl)) {
            fullscreenImage.setImageResource(R.drawable.not_ava); // o qualsevol imatge per defecte
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.wc_pd)
                    .into(fullscreenImage);
        }

        // Tancar el diàleg en fer clic a la imatge
        fullscreenImage.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}