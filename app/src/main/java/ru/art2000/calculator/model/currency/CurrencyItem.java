package ru.art2000.calculator.model.currency;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import ru.art2000.calculator.model.common.DiffComparable;

@Entity(tableName = "currency")
public class CurrencyItem implements DiffComparable<CurrencyItem> {

    @Ignore
    public CurrencyItem(@NonNull String code, Double rate) {
        this.code = code;
        this.rate = rate;
    }

    public CurrencyItem(@NonNull String code, Double rate, int position) {
        this.code = code;
        this.rate = rate;
        this.position = position;
    }

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "codeLetter")
    public String code;

    public int position = -1;

    public Double rate;

    @Ignore
    public int nameResourceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyItem)) return false;

        CurrencyItem item = (CurrencyItem) o;

        return code.equals(item.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public boolean isSameItem(@NotNull CurrencyItem anotherItem) {
        return code.equals(anotherItem.code);
    }

    @Override
    public boolean isContentSame(@NotNull CurrencyItem anotherItem) {
        return isSameItem(anotherItem);
    }
}
