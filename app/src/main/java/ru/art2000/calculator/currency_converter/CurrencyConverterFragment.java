package ru.art2000.calculator.currency_converter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.art2000.calculator.R;
import ru.art2000.extensions.CurrencyItem;
import ru.art2000.extensions.CurrencyValues;

public class CurrencyConverterFragment extends Fragment {

    private TextView date;
    public Context mContext;
    private RecyclerView recycler;
    private Activity parent;
    public CurrencyListAdapter adapter = null;
    private View v = null;
    private String updDate;
    private SwipeRefreshLayout refresher;
    private boolean onUpdate;

    public CurrencyConverterFragment() {
        super();
    }

    public void scrollToTop(){
        if (recycler != null)
            recycler.smoothScrollToPosition(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 666) {
            if (resultCode == 1)
                adapter.getDataFromDB();
        }
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (v == null) {
            mContext = getActivity();
            parent = getActivity();
            v = inflater.inflate(R.layout.currency_layout, null);
            recycler = v.findViewById(R.id.currency_list);
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            recycler.setLayoutManager(llm);
            adapter = new CurrencyListAdapter(mContext);
            recycler.setAdapter(adapter);
            updateList();
            recycler.setOnFocusChangeListener((v, hasFocus) ->
                    adapter.removeEditText());
            refresher = v.findViewById(R.id.refresher);
            TypedValue accentValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.colorAccent, accentValue, true);
            refresher.setColorSchemeColors(ContextCompat.getColor(mContext, accentValue.resourceId));
            refresher.setRefreshing(true);
            updateData();
            refresher.setOnRefreshListener(this::updateData);
            date = v.findViewById(R.id.tv_update_date);
            updateDate(null);
            EditText focus = v.findViewById(R.id.focuser);
            v.findViewById(R.id.edit_currencies).setOnClickListener(v-> {
                adapter.removeEditText();
                focus.requestFocus();
                Intent intent = new Intent(getActivity(), EditShownCurrencies.class);
                startActivityForResult(intent, 666);
            });
        }
        return v;
    }

    private void updateDate(String newDate){
        parent.runOnUiThread(()->{
            if (newDate == null) {
                updDate = CurrencyValues.updateDate;
                date.setText(updDate);
            } else {
                date.setText(newDate);
                CurrencyValues.putRefreshDate(newDate, mContext);
            }
        });
    }

    private String getShownCodesSearch(){
        StringBuilder builder = new StringBuilder();
        String def = "tr:contains(";
        int i = 0;
        for (CurrencyItem item : CurrencyValues.visibleList){
            if (i != 0)
                builder.append(", ");
            builder.append(def).append(item.code).append(")");
            i=1;
        }
        return builder.toString();
    }

    @Override
    public void onPause() {
        adapter.removeEditText();
        Log.d("CurrencyConverter", "pause time!");
        super.onPause();
    }


    private void parseHTML() {
        new Thread(() -> {
            try {
                Document webpage = Jsoup.connect("http://www.cbr.ru/currency_base/daily/").get();
                onUpdate = true;
                String dateFormat = "[0-9]{2}.[0-9]{2}.[0-9]{4}";
                Pattern datePattern = Pattern.compile(dateFormat);
                Matcher matcher = datePattern.matcher(webpage.select("h2").first().text());

                String date = "";

                if (matcher.find())
                    date = webpage.select("h2").first().text().substring(matcher.start(), matcher.end());

                if (updDate.equals(date)) {
                    refresher.setRefreshing(false);
                    return;
                }
                parent.runOnUiThread(()->
                        Toast.makeText(mContext, "Обновление...", Toast.LENGTH_SHORT).show());
                CurrencyDB currencyDB = new CurrencyDB(mContext);
                ContentValues contentValues = new ContentValues();
                SQLiteDatabase database = currencyDB.getWritableDatabase();
                Elements tables = webpage.select("table").first().children().first().children();
                Double ru_val = Double.parseDouble(tables.select("tr:contains(USD)").first().child(4).text().replace(',', '.'));
                CurrencyValues.updateRate("RUB", ru_val);
                NumberFormat dot2dig = new DecimalFormat("#.##");
                tables.remove(0);
                for (Element child : tables.select(getShownCodesSearch())){
                    Double values = Double.parseDouble(child.child(4).text().replace(',', '.'));
                    Double units = Double.parseDouble(child.child(2).text().replace(',', '.'));
                    Double valuesPerUnit = Double.parseDouble(dot2dig.format(ru_val / (values / units)).replace(',', '.'));
                    String letterCode = child.child(1).text();
                    CurrencyValues.updateRate(letterCode, valuesPerUnit);
                }
                updateList();
                updateDate(date);

                for (Element child : tables) {
//                                Log.d("Цифр. код", child.child(0).text());
//                                Log.d("Букв. код", child.child(1).text());
//                                Log.d("Единиц", child.child(2).text());
//                                Log.d("Валюта", child.child(3).text());
//                                Log.d("Курс", child.child(4).text());
                    Double values = Double.parseDouble(child.child(4).text().replace(',', '.'));
                    Double units = Double.parseDouble(child.child(2).text().replace(',', '.'));
                    Double valuesPerUnit = Double.parseDouble(dot2dig.format(ru_val / (values / units)).replace(',', '.'));
                    String letterCode = child.child(1).text();
                    CurrencyValues.updateRate(letterCode, valuesPerUnit);
                    contentValues.put("rate", valuesPerUnit);
                    database.update("currency", contentValues,
                                            "codeLetter = ?", new String[]{letterCode});
                    contentValues.clear();
                }
                contentValues.put("rate", ru_val);
                database.update("currency", contentValues,
                        "codeLetter = ?", new String[]{"RUB"});
                currencyDB.close();
                refresher.setRefreshing(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateList(){
        if (adapter != null) {
            parent.runOnUiThread(()->
                adapter.getDataFromDB());
        }
    }

    private void updateData() {
        if (isOnline()) {
            parseHTML();
        } else {
            refresher.setRefreshing(false);
            Toast.makeText(mContext, "Failed: no Internet", Toast.LENGTH_SHORT).show();
            updateList();
        }
    }

    private static boolean isOnline() {
        try {
            return new checkOnline().execute().get();
        } catch (Exception e) {
            return false;
        }
    }

    static class checkOnline extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress address = new InetSocketAddress("8.8.8.8", 53);
                sock.connect(address, timeoutMs);
                sock.close();
                return true;
            } catch (IOException e){
                return false;
            }
        }

    }

}