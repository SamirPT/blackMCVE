package tech.peller.mrblackandroidwatch.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import roboguice.util.Strings;
import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.enums.VenueRole;

/**
 * Created by Sam (samir@peller.tech) on 28.10.2016
 */

public class StringFormatter {
    public StringFormatter() {}

    /**сокращаем отображение списка ролей, если их больше numberToShow, до вида "Роль1, роль2, роль3 and N more..."
     *
     * @param rolesList список элементов класса VenueRole
     * @param numberToShow количество элементов, отображаемых полностью
     * @return String строка, отображающая список ролей, вида "Роль1, роль2, роль3 and N more...",
     * если ролей больше трех
     */
    public static String multiRoleFormer(List<VenueRole> rolesList, int numberToShow) {
        String result;

        if(rolesList.size() > numberToShow) {
            result = Strings.join(", ", rolesList.subList(0, numberToShow - 1)) +
                    " and " + String.valueOf(rolesList.size() - numberToShow) + " more";
        } else {
            result = Strings.join(", ", rolesList);
        }

        return result;
    }

    //вариант метода для добавления к роли promoter в скобках названия promo организации
    public static String multiRoleFormer(List<VenueRole> rolesList, int numberToShow, String promoName) {
        List<String> roles = new ArrayList<>();
        for(VenueRole element : rolesList) {
            roles.add(promoName != null && VenueRole.PROMOTER.equals(element) ?
                    element.toString() + " (" + promoName + ")" :
                    element.toString());
        }

        String result;

        if(roles.size() > numberToShow) {
            result = Strings.join(", ", roles.subList(0, numberToShow - 1)) +
                    " and " + String.valueOf(roles.size() - numberToShow) + " more";
        } else {
            result = Strings.join(", ", roles);
        }

        return result;
    }

    /**преобразуем строку (слово) в формат "первая заглавная буква, остальные строчные"
     *
     * @param source строка, которую необходимо преобразовать в формат "первая заглавная буква,
     *               остальные строчные"
     * @return String строка, преобразованная в формат "первая заглавная буква, остальные строчные"
     */
    public static String stringToFirstCapitalized(String source) {
        return source.substring(0, 1).toUpperCase() + source.substring(1).toLowerCase();
    }

    /**преобразуем дату в формате DateTime в строку заданного шаблона (например, "yyyy-MM-dd")
     *
     * @param date дата в формате DateTime
     * @param pattern шаблон формата даты, в который необоходимо преобразовать входные данные
     * @return String строка, содержащая преобразованную в нужный формат дату
     */
    public static String formatDate(DateTime date, String pattern) {
        DateTimeFormatter df = DateTimeFormat.forPattern(pattern);
        return df.print(date);
    }

    /** преобразуем время в разные форматы - двадцатичетырехчасовой и двенадцатичасовой
     *
     * @param time строка, содержащая время для форматирования
     * @param is12format булевое значение вида форматирования: true - из 12 в 24, false - из 24 в 12
     * @return String строка, содержащая преобразованное в новый формат время
     */
    public static String formatTime(String time, boolean is12format) {
        SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");

        String formattedTime = "";
        try {
            if(is12format) {
                formattedTime = date24Format.format(date12Format.parse(time));
            } else {
                formattedTime = date12Format.format(date24Format.parse(time));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
    }

    /** получаем из строки, содержащей время, число, обозначающее количество часов
     *
     * @param time строка, содержащая время
     * @return int число, обозначающее количество часов
     */
    public static int separateTimeHours(String time) {
        if (time.substring(0, 1).equals("0")) {
            return Integer.parseInt(time.substring(1, 2));
        } else {
            return Integer.parseInt(time.substring(0, 2));
        }
    }

    /** получаем из строки, содержащей время, число, обозначающее количество минут
     *
     * @param time строка, содержащая время
     * @return int число, обозначающее количество минут
     */
    public static int separateTimeMinutes(String time) {
        if (time.substring(3, 4).equals("0")) {
            return Integer.parseInt(time.substring(4));
        } else {
            return Integer.parseInt(time.substring(3));
        }
    }

    /** преобразуем два числа, содержащие количество часов и минут, в строку времени в формате HH:mm
     *  (24-х часовой формат)
     *
     * @param hours int число, содержащее количество часов
     * @param minutes int число, содержащее количество минут
     * @return String строка, содрежащее время в формате HH:mm (24-х часовой формат)
     */
    public static String joinTime(int hours, int minutes) {
        String result;

        if (hours < 10) {
            result = "0" + String.valueOf(hours);
        } else {
            result = String.valueOf(hours);
        }

        result = result + ":";

        if (minutes < 10) {
            result = result + "0" + String.valueOf(minutes);
        } else {
            result = result + String.valueOf(minutes);
        }

        return result;
    }

    /** изменяем цвет части текста
     *
     * @param wholeString полная строка, содержащая часть, цвет которой надо изменить
     * @param recolorStartText часть текста либо первый символ, цвет которой надо изменить
     * @param lengthToRecolor длина части текста, цвет которой надо изменить
     * @return SpannableString полная строка с измененной цветом частью текста
     */
    public static SpannableString recolorTextPart(Context context, String wholeString, String recolorStartText, int lengthToRecolor) {
        SpannableString resultSS = new SpannableString(wholeString);
        if(wholeString.contains(recolorStartText)) {
            int indexT = wholeString.indexOf(recolorStartText);
            resultSS.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.blueText)),
                    indexT, indexT + lengthToRecolor, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return resultSS;
    }
}
