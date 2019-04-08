package ru.art2000.calculator.calculator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.art2000.calculator.R;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private int position;
    private LayoutInflater mInflater;
    private HistoryDB hdb;
    private Context mContext;
    private static final int COPY_ALL = 101;
    private static final int COPY_EXPR = 102;
    private static final int COPY_RES = 103;
    private static final int DELETE = 104;
    private int size;

    HistoryListAdapter(Context context, HistoryDB hdb) {
        this.mInflater = LayoutInflater.from(context);
        this.hdb = hdb;
        mContext = context;
        size = hdb.getSize();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.history_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SQLiteDatabase db = hdb.getReadableDatabase();
        Cursor cc = db.query("history", null, null, null, null, null, null);
        if (position < getItemCount()){
            Log.d("binding pos", String.valueOf(position));
            cc.move(position+1);
            holder.expr.setText(cc.getString(cc.getColumnIndex("expression")));
            holder.res.setText(cc.getString(cc.getColumnIndex("result")));
        }
        cc.close();
        holder.itemView.setOnLongClickListener(v -> {
            setPosition(holder.getAdapterPosition());
            return false;
        });

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    void setNewData(){
        size = hdb.getSize();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView expr;
        TextView res;

        ViewHolder(View itemView) {
            super(itemView);
            expr = itemView.findViewById(R.id.expression);
            res = itemView.findViewById(R.id.result);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(mContext.getString(R.string.you_can));
            menu.add(Menu.NONE, COPY_ALL, Menu.NONE, mContext.getString(R.string.copy_all));
            menu.add(Menu.NONE, COPY_EXPR, Menu.NONE, mContext.getString(R.string.copy_expr));
            menu.add(Menu.NONE, COPY_RES, Menu.NONE, mContext.getString(R.string.copy_res));
            menu.add(Menu.NONE, DELETE, Menu.NONE, mContext.getString(R.string.delete_record));
            menu.removeItem(DELETE);
        }

    }
}

