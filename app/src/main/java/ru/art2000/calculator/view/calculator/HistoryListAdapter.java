package ru.art2000.calculator.view.calculator;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.art2000.calculator.R;
import ru.art2000.calculator.model.calculator.HistoryItem;
import ru.art2000.calculator.view_model.calculator.HistoryViewModel;
import ru.art2000.extensions.collections.CollectionsKt;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final HistoryViewModel mModel;

    private List<HistoryItem> historyList = new ArrayList<>();

    HistoryListAdapter(Context context, LifecycleOwner lifecycleOwner, HistoryViewModel model, LiveData<List<HistoryItem>> items) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mModel = model;
        items.observe(lifecycleOwner, this::setNewData);
    }

    public List<HistoryItem> getHistoryList() {
        return historyList;
    }

    private void setNewData(@NonNull List<HistoryItem> newData) {
        if (historyList == null || historyList.isEmpty()) {
            historyList = newData;
            notifyItemRangeInserted(0, newData.size());
        } else {
            DiffUtil.DiffResult result = CollectionsKt.calculateDiff(historyList, newData);

            historyList = newData;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);
        holder.expr.setText(item.getExpression());
        holder.res.setText(item.getResult());
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

            menu.add(Menu.NONE, HistoryViewModel.COPY_ALL, Menu.NONE, mContext.getString(R.string.context_menu_copy_all)).setOnMenuItemClickListener(this::onContextItemSelected);
            menu.add(Menu.NONE, HistoryViewModel.COPY_EXPR, Menu.NONE, mContext.getString(R.string.context_menu_copy_expr)).setOnMenuItemClickListener(this::onContextItemSelected);
            menu.add(Menu.NONE, HistoryViewModel.COPY_RES, Menu.NONE, mContext.getString(R.string.context_menu_copy_res)).setOnMenuItemClickListener(this::onContextItemSelected);
            menu.add(Menu.NONE, HistoryViewModel.DELETE, Menu.NONE, mContext.getString(R.string.delete_record)).setOnMenuItemClickListener(this::onContextItemSelected);
            menu.removeItem(HistoryViewModel.DELETE);
        }

        private boolean onContextItemSelected(@NonNull MenuItem menuItem) {

            int id = menuItem.getItemId();

            String toastText;
            if (id >= HistoryViewModel.COPY_ALL && id <= HistoryViewModel.COPY_RES) {

                HistoryItem selectedItem = historyList.get(getBindingAdapterPosition());
                toastText = mModel.copyHistoryItemToClipboard(selectedItem, id);

            } else if (id == HistoryViewModel.DELETE) {

                HistoryItem selectedItem = historyList.get(getBindingAdapterPosition());
                mModel.removeHistoryItem(selectedItem.getId());
                toastText = mContext.getString(R.string.deleted) + " " + selectedItem.getFullExpression();

            } else return true;

            Toast.makeText(mContext, toastText, Toast.LENGTH_SHORT).show();

            return true;
        }

    }
}

