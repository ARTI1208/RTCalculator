package ru.art2000.helpers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class GeneralHelper {

    public static NumberFormat resultNumberFormat = new DecimalFormat("#.#######");

    @SuppressWarnings("SameParameterValue")
    public static String joinToString(List list, String separator, String prefix, String postfix) {
        StringBuilder builder = new StringBuilder(prefix);
        int count = 0;
        for (Object obj : list) {
            if (++count > 1) {
                builder.append(separator);
            }
            builder.append(obj);
        }
        builder.append(postfix);
        return builder.toString();
    }

}
