package tech.peller.mrblackandroidwatch.models.venue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.regex.Matcher;

import tech.peller.mrblackandroidwatch.enums.NotificationTypeEnum;

/**
 * Created by Sam (samir@peller.tech) on 16.02.2017
 */

public class NotificationTO {
    private Long id;
    private String type;
    private String date;
    private String time;
    private Integer venueId;
    private Integer eventId;
    private String eventDate;
    private Long reservationId;
    private String message;
    private boolean seen;
    @SerializedName("data")
    @Expose
    private Map<String, String> result;

    public NotificationTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationTypeEnum getType() {
        return NotificationTypeEnum.valueOf(type);
    }

    public void setType(NotificationTypeEnum type) {
        this.type = type.name();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }

    /**заменяем все элементы шаблона сообщения this.message на соответствующие значения переменных
     * this.data
     *
     * @return String строка шаблона с замененными значениями
     */
    public String getReplacedMessage(){
        String message = this.message;
        for (String key : result.keySet()) {
            //используем Matcher.quoteReplacement экранирования возможных спецсимволов
            message = message.replaceAll("\\{" + key + "\\}", Matcher.quoteReplacement(result.get(key)));
        }
        return message;
    }
}
