package tech.peller.mrblackandroidwatch.adapters;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.enums.ReservationStatus;
import tech.peller.mrblackandroidwatch.models.reservation.ReservationInfo;
import tech.peller.mrblackandroidwatch.models.reservation.SignatureTO;
import tech.peller.mrblackandroidwatch.models.reservation.TableInfo;
import tech.peller.mrblackandroidwatch.utils.FlowLayout;
import tech.peller.mrblackandroidwatch.utils.StringFormatter;

/**
 * Created by Sam (salyasov@gmail.com) on 13.04.2018
 */

public class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.LiveSpendViewHolder> {
    private List<ReservationInfo> resList;
    private ElementClickListener listener;
    private ViewGroup parent;

    public ReservationListAdapter(List<ReservationInfo> resList, ElementClickListener listener) {
        this.resList = resList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservationListAdapter.LiveSpendViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_reservaton,
                parent, false);
        this.parent = parent;
        return new LiveSpendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveSpendViewHolder holder, int position) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Resources resources = parent.getContext().getResources();
        int topM =
                position == 0
                        ? (int) (resources.getDimension(R.dimen.reservation_element_margin) * Resources.getSystem().getDisplayMetrics().density)
                        : 0;
        int bottomM =
                (position == resList.size() - 1)
                        ? (int) (resources.getDimension(R.dimen.reservation_element_margin) * Resources.getSystem().getDisplayMetrics().density)
                        : 10;

        params.setMargins(0, topM, 0, bottomM);
        holder.resLL.setLayoutParams(params);

        ReservationInfo res = resList.get(position);

        holder.resLL.setOnClickListener(v -> {
            listener.onClick(res.getId(), res.getLiveSpend(), res.getSignatures(),
                    ReservationStatus.PRE_RELEASED.equals(res.getStatus()),
                    res.getPreviousStatus());
        });

        if (res.getTableInfo() != null && res.getTableInfo().getBottleService() != null) {
            String seatingText = res.getTableInfo().getBottleService().name().substring(0, 1) +
                    "-" + res.getTableInfo().getPlaceNumber();

            //если MinSpend > 1 - отображаем
            if (res.getMinSpend() != null && res.getMinSpend() > 1) {
                seatingText += " (Min $" + String.valueOf(res.getMinSpend()) + ")";
            }

            holder.resTableTitleMin.setText(seatingText);
            holder.resTableTitleMin.setVisibility(View.VISIBLE);
        } else {
            holder.resTableTitleMin.setVisibility(View.INVISIBLE);
        }

        switch (res.getStatus()) {
            case PENDING:
                break;
            case APPROVED:
                if(res.getTableInfo() == null || res.getTableInfo().getId() == null) {
                    holder.resStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.unassignedStatus));
                    holder.resStatus.setText("Unassigned");
                } else {
                    holder.resStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.assignedStatus));
                    holder.resStatus.setText("Assigned");
                }
                break;
            case ARRIVED:
                holder.resStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.arrivedStatus));
                holder.resStatus.setText("Arrived");
                break;
            case PRE_RELEASED:
                holder.resStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.releasedStatus));
                holder.resStatus.setText("Pending released");
                break;
            case RELEASED:
                holder.resStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.releasedStatus));
                holder.resStatus.setText("Released");
                break;
            case COMPLETED:
                holder.resStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.completedStatus));
                holder.resStatus.setText("Completed");
                break;
            case NO_SHOW:
                holder.resStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.noShowStatus));
                holder.resStatus.setText("No show");
                break;
            case CONFIRMED_COMPLETE:
                holder.resStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.confirmedCompletedStatus));
                holder.resStatus.setText("Finalized");
                break;
            case REJECTED:
                holder.resStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.rejectedStatus));
                holder.resStatus.setText("Rejected");
                break;
            case CANCELLED:
                holder.resStatus.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.cancelledStatus));
                holder.resStatus.setText("Cancelled");
                break;
        }

        holder.resGuest.setText(res.getGuestInfo().getFullName());

        holder.resBooked.setText("by " + res.getBookedBy().getFullName());

        if(res.getBookingNote() != null && !res.getBookingNote().isEmpty()) {
            holder.resNote.setVisibility(View.VISIBLE);
            holder.resNote.setText(res.getBookingNote());
        } else {
            holder.resNote.setVisibility(View.GONE);
        }

        holder.resTagsFL.removeAllViews();
        if (res.getTags().isEmpty()) {
            holder.resTagsFL.setVisibility(View.GONE);
        } else {
            holder.resTagsFL.setVisibility(View.VISIBLE);
            for (int i = 0; i < res.getTags().size(); i++) {
                TextView tagItem = new TextView(parent.getContext());
                tagItem.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.tag_background));
                tagItem.setPadding(10, 5, 10, 5);
                tagItem.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.whiteText));
                tagItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
                tagItem.setText(res.getTags().get(i));

                holder.resTagsFL.addView(tagItem);
            }
        }

        if(res.getLiveSpend() != null && res.getLiveSpend() > 0) {
            holder.resLiveSpend.setText("Live $" + res.getLiveSpend());
        } else {
            holder.resLiveSpend.setText("Live $0");
        }

        holder.resGuestCount.setText(res.getArrivedGuests() + " / " + res.getTotalGuests());

        if(res.getEstimatedArrivalTime() != null && !res.getEstimatedArrivalTime().isEmpty()) {
            holder.resETA.setText(StringFormatter.formatTime(res.getEstimatedArrivalTime(), false));
        } else {
            holder.resETA.setText("--:--");
        }
    }

    @Override
    public int getItemCount() {
        return resList.size();
    }

    static class LiveSpendViewHolder extends RecyclerView.ViewHolder {
        TextView resTableTitleMin, resStatus, resGuest, resBooked, resNote, resLiveSpend,
                resGuestCount, resETA;
        FlowLayout resTagsFL;
        LinearLayout resLL;

        LiveSpendViewHolder(View itemView) {
            super(itemView);

            resLL =  itemView.findViewById(R.id.resLL);

            resTableTitleMin = itemView.findViewById(R.id.resTableTitleMin);
            resStatus = itemView.findViewById(R.id.resStatus);
            resGuest = itemView.findViewById(R.id.resGuest);
            resBooked = itemView.findViewById(R.id.resBooked);
            resNote = itemView.findViewById(R.id.resNote);
            resLiveSpend = itemView.findViewById(R.id.resLiveSpend);
            resGuestCount = itemView.findViewById(R.id.resGuestCount);
            resETA = itemView.findViewById(R.id.resETA);

            resTagsFL = itemView.findViewById(R.id.resTagsFL);
        }
    }

    /**
     * функциональный интерфейс для передачи метода обработки нажатия на элемент
     */
    @FunctionalInterface
    public interface ElementClickListener {
        void onClick(Long resId, Integer liveSpend, List<SignatureTO> signatures,
                     boolean isReleased, ReservationStatus previousStatus);
    }
}
