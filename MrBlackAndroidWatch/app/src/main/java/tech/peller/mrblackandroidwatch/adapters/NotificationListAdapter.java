package tech.peller.mrblackandroidwatch.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.models.venue.NotificationTO;
import tech.peller.mrblackandroidwatch.utils.StringFormatter;

/**
 * Created by Sam (salyasov@gmail.com) on 13.04.2018
 */

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.LiveSpendViewHolder> {
    private List<NotificationTO> notificationsList;
    private ElementClickListener listener;

    public NotificationListAdapter(List<NotificationTO> notificationsList, ElementClickListener listener) {
        this.notificationsList = notificationsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationListAdapter.LiveSpendViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_notification,
                parent, false);
        return new LiveSpendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveSpendViewHolder holder, int position) {
        NotificationTO element = notificationsList.get(position);

        holder.notificationText.setText(element.getReplacedMessage());
        holder.notificationTime.setText(StringFormatter.formatTime(element.getTime(), false));

        if(position == notificationsList.size() - 1) {

        }
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    static class LiveSpendViewHolder extends RecyclerView.ViewHolder {
        TextView notificationText, notificationTime;

        LiveSpendViewHolder(View itemView) {
            super(itemView);

            notificationText = itemView.findViewById(R.id.notificationText);
            notificationTime = itemView.findViewById(R.id.notificationTime);
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
