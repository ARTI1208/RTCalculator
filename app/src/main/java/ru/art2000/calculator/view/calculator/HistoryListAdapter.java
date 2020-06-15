package ru.art2000.calculator.view.calculator;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.calculator.model.calculator.HistoryItem;
import ru.art2000.calculator.model.common.GlobalDependencies;

import static ru.art2000.calculator.view.calculator.CalculatorFragment.COPY_ALL;
import static ru.art2000.calculator.view.calculator.CalculatorFragment.COPY_EXPR;
import static ru.art2000.calculator.view.calculator.CalculatorFragment.COPY_RES;
import static ru.art2000.calculator.view.calculator.CalculatorFragment.DELETE;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private HistoryItem selectedItem;
    private LayoutInflater mInflater;
    private Context mContext;

    private List<HistoryItem> historyList = new ArrayList<>();

    public List<HistoryItem> getHistoryList() {
        return historyList;
    }

    HistoryListAdapter(Context context, LifecycleOwner lifecycleOwner, LiveData<List<HistoryItem>> items) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        items.observe(lifecycleOwner, this::setNewData);
    }

    public void setNewData(@NonNull List<HistoryItem> newData) {
        if (historyList == null || historyList.isEmpty()) {
            historyList = newData;
            notifyItemRangeInserted(0, newData.size());
        } else {
            DiffUtil.DiffResult result = GlobalDependencies.calculateDiff(historyList, newData);

            historyList = newData;
//            toggleEmptyView();
            result.dispatchUpdatesTo(this);
        }
    }

    public HistoryItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(HistoryItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        SQLiteDatabase db = hdb.getReadableDatabase();
//        Cursor cc = db.query(
//                "history",
//                null,
//                null,
//                null,
//                null,
//                null,
//                null);
        HistoryItem item = historyList.get(position);
        holder.expr.setText(item.getExpression());
        holder.res.setText(item.getResult());
//        cc.close();
        holder.itemView.setOnLongClickListener(v -> {
            selectedItem = historyList.get(holder.getBindingAdapterPosition());
            return false;
        });

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
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
            menu.add(Menu.NONE, COPY_ALL, Menu.NONE, mContext.getString(R.string.context_menu_copy_all));
            menu.add(Menu.NONE, COPY_EXPR, Menu.NONE, mContext.getString(R.string.context_menu_copy_expr));
            menu.add(Menu.NONE, COPY_RES, Menu.NONE, mContext.getString(R.string.context_menu_copy_res));
            menu.add(Menu.NONE, DELETE, Menu.NONE, mContext.getString(R.string.delete_record));
            menu.removeItem(DELETE);
        }

    }
}

