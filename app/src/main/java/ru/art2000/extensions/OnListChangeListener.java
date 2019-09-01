package ru.art2000.extensions;

import java.util.ArrayList;

public interface OnListChangeListener<T> {
    void onItemsAdded(ArrayList<T> addedItems);

    void onItemsRemoved(ArrayList<T> removedItems);
}
