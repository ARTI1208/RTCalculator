package ru.art2000.extensions;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import ru.art2000.calculator.currency_converter.CurrencyDB;
import ru.art2000.calculator.currency_converter.EditCurrenciesActivity;

public class CurrencyValues {

    public static ArrayList<CurrencyItem> visibleList;
    public static ArrayList<CurrencyItem> hiddenList;
    public static String updateDate;

    public static void getDataFromDB(Context ctx) {
        visibleList = new ArrayList<>();
        hiddenList = new ArrayList<>();
        try {
            File db = new File(ctx.getDatabasePath("currency.db").getAbsolutePath());
            if (!db.exists() && (Objects.requireNonNull(db.getParentFile()).exists() || db.getParentFile().mkdirs())) {
                InputStream inputStream = ctx.getAssets().open("currency.db");
                OutputStream outputStream = new FileOutputStream(ctx.getDatabasePath("currency.db").getAbsolutePath());
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
        CurrencyDB DBHelper = new CurrencyDB(ctx);
        SQLiteDatabase currenciesDB = DBHelper.getWritableDatabase();
        Cursor cc = currenciesDB.query("currency", null, null, null, null, null, null);
        for (int i = 0, fullSize = DBHelper.getSize(); i < fullSize; i++) {
            if (cc.moveToNext()) {
                String curCode = cc.getString(cc.getColumnIndex("codeLetter"));
                String name = "currency_" + curCode;
                Integer id = ctx.getResources().getIdentifier(name, "string", ctx.getPackageName());
                int pos = cc.getInt(cc.getColumnIndex("position"));
                Double rate = cc.getDouble(cc.getColumnIndex("rate"));
                CurrencyItem item = new CurrencyItem();
                item.code = curCode;
                item.position = pos;
                item.rate = rate;
                item.nameResourceId = id;
                if (pos == -1) {
                    hiddenList.add(item);
                } else {
                    visibleList.add(item);
                }
            }
        }
        cc.close();
        updateDate = DBHelper.getRefreshDate();
        DBHelper.close();
        sortVisibleList();
        sortHiddenList();
    }

    public static void putRefreshDate(String date, Context ctx) {
        new Thread(() -> {
            CurrencyDB DBHelper = new CurrencyDB(ctx);
            DBHelper.putRefreshDate(date);
            DBHelper.close();
        }).start();
    }

    public static int findByCode(String code) {
        for (CurrencyItem item : visibleList) {
            if (item.code.equals(code))
                return item.position;
        }
        return 0;
    }

    public static ArrayList<CurrencyItem> findAllItems(Context context,
                                                       ArrayList<CurrencyItem> list,
                                                       String query) {
        if (query == null || query.length() == 0)
            return list;
        ArrayList<CurrencyItem> newList = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (CurrencyItem item :
                list) {
            String lowerCode = item.code.toLowerCase();
            String lowerName = context.getString(item.nameResourceId);
            if (lowerCode.contains(lowerQuery) || lowerName.contains(lowerQuery)) {
                newList.add(item);
            }
        }
        return newList;
    }

    public static void makeItemsVisible(EditCurrenciesActivity activity, ArrayList<CurrencyItem> list) {
        visibleList.addAll(list);
        hiddenList.removeAll(list);
        activity.add.removeFromCurrentList(list);
        list.clear();
        fixPositions();
    }

    public static void hideItems(EditCurrenciesActivity activity, ArrayList<CurrencyItem> list) {
        hiddenList.addAll(list);
        visibleList.removeAll(list);
        list.clear();
        fixPositions();
    }

    public static void hideItems(EditCurrenciesActivity activity, Integer... positions) {
        ArrayList<CurrencyItem> list = new ArrayList<>();
        for (int i : positions) {
            list.add(visibleList.get(i));
        }
        hideItems(activity, list);
    }

    private static void fixPositions() {
        int k = 0;
        for (CurrencyItem item : visibleList) {
            item.position = k++;
        }
        sortHiddenList();
    }

    private static ArrayList<CurrencyItem> sortVisibleList() {
        Collections.sort(visibleList, (o1, o2) ->
                Integer.compare(o1.position, o2.position));
        return visibleList;
    }

    private static ArrayList<CurrencyItem> sortVisibleList(ArrayList<CurrencyItem> visibleList) {
        Collections.sort(visibleList, (o1, o2) ->
                Integer.compare(o1.position, o2.position));
        return visibleList;
    }

    private static ArrayList<CurrencyItem> sortHiddenList() {
        Collections.sort(hiddenList, (o1, o2) ->
                o1.code.compareTo(o2.code));
        return hiddenList;
    }

    private static ArrayList<CurrencyItem> sortHiddenList(ArrayList<CurrencyItem> hiddenList) {
        Collections.sort(hiddenList, (o1, o2) ->
                o1.code.compareTo(o2.code));
        return hiddenList;
    }

    public static void swap(int pos1, int pos2) {
        visibleList.get(pos1).position = pos2;
        visibleList.get(pos2).position = pos1;
        sortVisibleList();
    }

    public static void writeValuesToDB(Context context){
        new Thread(() -> {
            CurrencyDB DBHelper = new CurrencyDB(context);
            DBHelper.writeUpdatedValuesToDB();
            DBHelper.close();
        }).start();
    }

    public static void updateRate(String code, Double value) {
        for (CurrencyItem item : visibleList) {
            if (item.code.equals(code)) {
                item.rate = value;
                return;
            }
        }
        for (CurrencyItem item : hiddenList) {
            if (item.code.equals(code)) {
                item.rate = value;
                return;
            }
        }
    }

}
