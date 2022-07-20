package ru.art2000.calculator.view_model.calculator;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.art2000.calculator.model.calculator.history.HistoryDatabaseItem;
import ru.art2000.calculator.model.calculator.history.HistoryListItem;

public interface HistoryViewModel {

    int COPY_ALL = 100;
    int COPY_EXPR = 101;
    int COPY_RES = 102;

    int DELETE = 200;

    LiveData<List<HistoryListItem>> getHistoryListItems();

    String copyHistoryItemToClipboard(@NonNull HistoryDatabaseItem item, int type);

    void removeHistoryItem(int id);

    void clearHistoryDatabase();

}
