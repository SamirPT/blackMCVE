package tech.peller.mrblackandroidwatch.api.response;

/**
 * Created by crashkin on 28.03.16.
 */
public class ExceptionUtil {

    private static final String CAUSE_SEPARATOR = " cause by: ";

    /**
     * Разворачивает Throwable -> cause by: Throwable в строчку с перечеслением проблем.
     * Полностью null safe
     *
     * @param t
     * @return
     */
    public static String causeList(Throwable t) {
        StringBuilder sb = new StringBuilder(String.valueOf(t.getMessage()));
        Throwable cause = t.getCause();
        while (cause != null) {
            sb.append(CAUSE_SEPARATOR).append(String.valueOf(cause.getMessage()));
            cause = cause.getCause();
        }
        return sb.toString();
    }

}
