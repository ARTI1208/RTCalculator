package ru.art2000.helpers;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ru.art2000.calculator.model.currency.CurrencyItem;
import ru.art2000.calculator.view_model.currency.CurrencyDependencies;

public class CurrencyValuesHelper {

    public static void checkCurrencyDBExists(Context ctx) {

        try {
            File db = new File(ctx.getDatabasePath("currency.db").getAbsolutePath());
            if (!db.exists() && (Objects.requireNonNull(db.getParentFile()).exists()
                    || db.getParentFile().mkdirs())) {
                InputStream inputStream = ctx.getAssets().open("currency.db");
                OutputStream outputStream = new FileOutputStream(
                        ctx.getDatabasePath("currency.db").getAbsolutePath());
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0)
                    outputStream.write(buffer, 0, length);
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
        } catch (Exception ignored) {
        }

    }

    public static List<CurrencyItem> findAllItems(Context context,
                                                  List<CurrencyItem> list,
                                                  String query) {
        if (query == null || query.length() == 0)
            return list;
        ArrayList<CurrencyItem> newList = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        Locale mainLocale = Locale.getDefault();

        for (CurrencyItem item : list) {

            String lowerCode = item.code.toLowerCase();
            int itemNameResourceId = CurrencyDependencies.getNameIdentifierForCode(context, item.code);
            String lowerName = context.getString(itemNameResourceId).toLowerCase();

            if (lowerCode.contains(lowerQuery) || lowerName.contains(lowerQuery)
                    || (!mainLocale.equals(Locale.ENGLISH)
                    && AndroidHelper.getLocalizedString(context, Locale.ENGLISH, itemNameResourceId)
                    .toLowerCase().contains(lowerQuery))) {
                newList.add(item);
            }
        }
        return newList;
    }

}
