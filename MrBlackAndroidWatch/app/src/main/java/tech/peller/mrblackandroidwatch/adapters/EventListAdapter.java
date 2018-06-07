package tech.peller.mrblackandroidwatch.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.helpers.EventHelper;
import tech.peller.mrblackandroidwatch.models.event.EventInfo;
import tech.peller.mrblackandroidwatch.utils.StringFormatter;

/**
 * Created by Sam (salyasov@gmail.com) on 13.04.2018
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.LiveSpendViewHolder> {
    private List<EventInfo> eventsList;
    private ElementClickListener listener;

    public EventListAdapter(List<EventInfo> venuesList, ElementClickListener listener) {
        this.eventsList = venuesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventListAdapter.LiveSpendViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_event,
                parent, false);
        return new LiveSpendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveSpendViewHolder holder, int position) {
        EventInfo element = eventsList.get(position);

        Drawable drawable = ContextCompat.getDrawable(holder.eventPic.getContext(),
                R.drawable.ava2x);
        Picasso.with(holder.eventPic.getContext()).load(element.getPictureUrl())
                .fit().centerCrop().error(drawable)
                .placeholder(drawable).into(holder.eventPic);

        holder.eventLL.setOnClickListener(v -> {
            EventHelper eh = new EventHelper();
            if(element.getCurrentDate() == null || element.getCurrentDate().isEmpty()) {
                element.setCurrentDate(element.getDate());
            }
            eh.saveCurrentEvent(element);

            listener.onClick();
        });

        holder.eventTitle.setText(element.getName());

        String eventTimeText = StringFormatter.formatTime(element.getStartsAt(), false) + " - " +
                StringFormatter.formatTime(element.getEndsAt(), false);
        holder.eventTime.setText(eventTimeText);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    static class LiveSpendViewHolder extends RecyclerView.ViewHolder {
        LinearLayout eventLL;
        RoundedImageView eventPic;
        TextView eventTitle, eventTime;

        LiveSpendViewHolder(View itemView) {
            super(itemView);

            eventLL = itemView.findViewById(R.id.eventLL);
            eventPic = itemView.findViewById(R.id.eventPic);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventTime = itemView.findViewById(R.id.eventTime);
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
