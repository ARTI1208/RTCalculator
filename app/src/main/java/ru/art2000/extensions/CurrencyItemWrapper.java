package ru.art2000.extensions;

public class CurrencyItemWrapper extends CurrencyItem {
    public boolean isSelected;
    public boolean isShown = true;
    public boolean isCounted;

    public boolean isVisible() {
        return position != -1;
    }
}
