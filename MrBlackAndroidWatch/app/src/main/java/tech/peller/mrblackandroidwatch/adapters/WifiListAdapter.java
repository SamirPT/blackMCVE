package tech.peller.mrblackandroidwatch.adapters;

import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.enums.WifiSignalStrengthEnum;

/**
 * Created by Sam (salyasov@gmail.com) on 13.04.2018
 */

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.LiveSpendViewHolder> {
    private List<ScanResult> wifiList;
    private ElementClickListener listener;
    private ViewGroup parent;

    public WifiListAdapter(List<ScanResult> wifiList, ElementClickListener listener) {
        this.wifiList = wifiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WifiListAdapter.LiveSpendViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                  int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_wifi,
                parent, false);
        this.parent = parent;
        return new LiveSpendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveSpendViewHolder holder, int position) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Resources resources = parent.getContext().getResources();

        int bottomM =
                (position == wifiList.size() - 1)
                        ? (int) (resources.getDimension(R.dimen.reservation_element_margin) * Resources.getSystem().getDisplayMetrics().density)
                        : 10;

        params.setMargins(0, 0, 0, bottomM);
        holder.wifiLL.setLayoutParams(params);

        ScanResult element = wifiList.get(position);

        holder.wifiLL.setOnClickListener(v -> listener.onClick(element.SSID));

        holder.wifiTitle.setText(element.SSID);

        String wifiStrength = "";
        if (element.level > -100 && element.level <= -66) {
            wifiStrength = WifiSignalStrengthEnum.LOW.toString();
        }
        if (element.level > -66 && element.level <= -33) {
            wifiStrength = WifiSignalStrengthEnum.MEDIUM.toString();
        }
        if (element.level > -33 && element.level <= 0) {
            wifiStrength = WifiSignalStrengthEnum.HIGH.toString();
        }
        holder.wifiSignalStr.setText(wifiStrength);
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    static class LiveSpendViewHolder extends RecyclerView.ViewHolder {
        LinearLayout wifiLL;
        TextView wifiTitle, wifiSignalStr;

        LiveSpendViewHolder(View itemView) {
            super(itemView);

            wifiLL = itemView.findViewById(R.id.wifiLL);
            wifiTitle = itemView.findViewById(R.id.wifiTitle);
            wifiSignalStr = itemView.findViewById(R.id.wifiSignalStr);
        }
    }

    /**
     * функциональный интерфейс для передачи метода обработки нажатия на элемент
     */
    @FunctionalInterface
    public interface ElementClickListener {
        void onClick(String wifiSSID);
    }
}
