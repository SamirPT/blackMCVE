package tech.peller.mrblackandroidwatch.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.enums.DateFormatEnum;
import tech.peller.mrblackandroidwatch.loader.reservations.LiveSpendTO;
import tech.peller.mrblackandroidwatch.utils.StringFormatter;

/**
 * Created by Sam (salyasov@gmail.com) on 13.04.2018
 */

public class LiveSpendListAdapter extends RecyclerView.Adapter<LiveSpendListAdapter.LiveSpendViewHolder> {
    private List<LiveSpendTO> liveSpendList;
    private ElementClickListener listener;

    public LiveSpendListAdapter(List<LiveSpendTO> notificationsList, ElementClickListener listener) {
        this.liveSpendList = notificationsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LiveSpendListAdapter.LiveSpendViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_live_spend,
                parent, false);
        return new LiveSpendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveSpendViewHolder holder, int position) {
        LiveSpendTO element = liveSpendList.get(position);

        holder.liveSpendUserName.setText(element.getUser().getName());
        String liveSpendValueText = "Spend: $" + String.valueOf(element.getSpend());
        holder.liveSpendValue.setText(liveSpendValueText);

        DateTime dateTime = DateTime.parse(element.getDateTime());
        String date = StringFormatter.formatDate(dateTime, DateFormatEnum.SHORT_HEADER.toString());
        String time = dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour();

        String dateAndTime = date + " " + StringFormatter.formatTime(time, false);
        holder.liveSpendDateTime.setText(dateAndTime);
    }

    @Override
    public int getItemCount() {
        return liveSpendList.size();
    }

    static class LiveSpendViewHolder extends RecyclerView.ViewHolder {
        TextView liveSpendUserName, liveSpendValue, liveSpendDateTime;

        LiveSpendViewHolder(View itemView) {
            super(itemView);

            liveSpendUserName = itemView.findViewById(R.id.liveSpendUserName);
            liveSpendValue = itemView.findViewById(R.id.liveSpendValue);
            liveSpendDateTime = itemView.findViewById(R.id.liveSpendDateTime);
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
