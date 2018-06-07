package tech.peller.mrblackandroidwatch.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.util.List;

import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.helpers.VenueHelper;
import tech.peller.mrblackandroidwatch.models.venue.Venue;

/**
 * Created by Sam (salyasov@gmail.com) on 12.04.2018
 */

public class VenueListAdapter extends RecyclerView.Adapter<VenueListAdapter.LiveSpendViewHolder> {
    private List<Venue> venuesList;
    private ElementClickListener listener;

    public VenueListAdapter(List<Venue> venuesList, ElementClickListener listener) {
        this.venuesList = venuesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VenueListAdapter.LiveSpendViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_venue,
                parent, false);
        return new LiveSpendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveSpendViewHolder holder, int position) {
        Drawable drawable;
        if(venuesList.get(position).getId().equals(0L)) {
            drawable = ContextCompat.getDrawable(holder.venueImage.getContext(),
                    R.drawable.main_menu_icon_log_out);
        } else {
            drawable = ContextCompat.getDrawable(holder.venueImage.getContext(),
                    R.drawable.ava2x);
        }
        Picasso.with(holder.venueImage.getContext()).load(venuesList.get(position).getCoverUrl())
                .fit().centerCrop().error(drawable)
                .placeholder(drawable).into(holder.venueImage);

        holder.venueImage.setOnClickListener(v -> {
            VenueHelper vH = new VenueHelper();
            vH.saveCurrentVenue(venuesList.get(position));
            listener.onClick();
        });

        holder.venueTitle.setText(venuesList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return venuesList.size();
    }

    static class LiveSpendViewHolder extends RecyclerView.ViewHolder {
        ImageView venueImage;
        TextView venueTitle;

        LiveSpendViewHolder(View itemView) {
            super(itemView);

            venueImage = itemView.findViewById(R.id.venueImage);
            venueTitle = itemView.findViewById(R.id.venueTitle);
        }
    }

    /**
     * функциональный интерфейс для передачи метода обработки нажатия на элемент
     */
    @FunctionalInterface
    public interface ElementClickListener {
        void onClick();
    }
}
