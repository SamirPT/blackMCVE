package tech.peller.mrblackandroidwatch.utils;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by r2d2 on 13.02.2017.
 */

public class PhoneEditText extends TextInputEditText {
    TextWatcher mPhoneTextWatcher = new PhoneTextWatcher();

    public PhoneEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.addTextChangedListener(mPhoneTextWatcher);
    }

    /**
     * @param phone
     * @return
     * @deprecated use PhoneEditText and method {@link #getPhone()}
     */
    @Deprecated
    public static String simplifyNumber(String phone) {
        return PhoneTextWatcher.simplifyNumber(phone);
    }

    public String getPhone() {
        return PhoneTextWatcher.simplifyNumber(getText().toString());
    }

    public TextWatcher getPhoneTextWatcher() {
        return mPhoneTextWatcher;
    }

    public void setPhoneTextWatcher(TextWatcher mPhoneTextWatcher) {
        removeTextChangedListener(mPhoneTextWatcher);
        this.mPhoneTextWatcher = mPhoneTextWatcher;
        addTextChangedListener(mPhoneTextWatcher);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        if (watcher instanceof PhoneTextWatcher)
            setPhoneTextWatcher(watcher);
        else
            super.addTextChangedListener(watcher);
    }

    /**
     * @param tView
     * @return
     * @deprecated use PhoneEditText and method {@link #getPhone()}
     */
    @Deprecated
    public String simplifyNumber(TextView tView) {
        return PhoneTextWatcher.simplifyNumber(tView);
    }

    /**
     * Created by r2d2 on 25.11.2016.
     */
    static class PhoneTextWatcher extends PhoneNumberFormattingTextWatcher {

        /**
         * Метод удаляет все лишние символы из телефонного номера и подставляет знак + в начале
         *
         * @param s номер телефона
         * @return строку вида +XYYYZZZKKNN
         */
        static String simplifyNumber(String s) {
            if (s == null || s.isEmpty())
                return "";
            else if (s.length() > 9)
                return "+" + s.replaceAll("\\D", "");
            else
                return s;
        }

        static String simplifyNumber(TextView view) {
            if (view == null) return "";
            return simplifyNumber(view.getText().toString());
        }


        @Override
        public synchronized void afterTextChanged(Editable s) {
            int indexOfDigit = getIndexOf11Digit(s);
            if (s.length() == 1 && Character.isDigit(s.charAt(0)))
                //не будет выполняться для inputType, которые не поддерживают символ "+"
                s.insert(0, "+");
            else if (s.length() == 1 && s.toString().equals("+"))
                s.clear();
            else if (indexOfDigit > -1)
                s.delete(indexOfDigit, s.length());
        }

        private int getIndexOf11Digit(Editable s) {
            int counter = 0;
            int index = 0;
            while (index < s.length() && counter < 11) {
                if (Character.isDigit(s.charAt(index))) counter++;
                index++;
            }
            return (counter == 11 && index < s.length()) ? index : -1;
        }
    }
}
