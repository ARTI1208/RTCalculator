package ru.art2000.helpers;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

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

}
