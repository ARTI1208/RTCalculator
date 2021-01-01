package ru.art2000.calculator.view_model.calculator;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.art2000.calculator.model.calculator.HistoryItem;

public interface HistoryViewModel {

    int COPY_ALL = 100;
    int COPY_EXPR = 101;
    int COPY_RES = 102;

    int DELETE = 200;

    LiveData<List<HistoryItem>> getHistoryItems();

    String copyHistoryItemToClipboard(@NonNull HistoryItem item, int type);

    void removeHistoryItem(int id);

    void clearHistoryDatabase();

}
