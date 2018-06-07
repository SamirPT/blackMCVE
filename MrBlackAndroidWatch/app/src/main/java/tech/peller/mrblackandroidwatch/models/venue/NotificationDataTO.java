package tech.peller.mrblackandroidwatch.models.venue;

import java.util.HashMap;

import io.realm.RealmObject;

/**
 * Created by Sam (samir@peller.tech) on 21.03.2017
 */

public class NotificationDataTO extends RealmObject {
    private String date;
    private String name;
    private String venue;
    private String bs_type;
    private String minimums;
    private String day_of_week;
    private String employee_name;
    private String client_name;
    private String table;
    private String guests_number;

    public NotificationDataTO() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getBs_type() {
        return bs_type;
    }

    public void setBs_type(String bs_type) {
        this.bs_type = bs_type;
    }

    public String getMinimums() {
        return minimums;
    }

    public void setMinimums(String minimums) {
        this.minimums = minimums;
    }

    public String getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(String day_of_week) {
        this.day_of_week = day_of_week;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    /**получаем HashMap переменных класса для работы метода NotificationTO.getMessage(NotificationDataTO dataTO)
     *
     * @return HashMap ключ - имя переменной, значение - значение переменной
     */
    HashMap<String, String> toHashMap() {
        return new HashMap<String, String>() {{
            put("date", (date == null) ? "" : date);
            put("name", (name == null) ? "" : name);
            put("venue", (venue == null) ? "" : venue);
            put("bs_type", (bs_type == null) ? "" : bs_type);
            put("minimums", (minimums == null) ? "" : minimums);
            put("day_of_week", (day_of_week == null) ? "" : day_of_week);
            put("employee_name", (employee_name == null) ? "" : employee_name);
            put("client_name", (client_name == null) ? "" : client_name);
            put("table", (table == null) ? "" : table);
            put("guests_number", (guests_number == null) ? "" : guests_number);
        }};
    }
}
