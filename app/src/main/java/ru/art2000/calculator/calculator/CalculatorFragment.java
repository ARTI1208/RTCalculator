package ru.art2000.calculator.calculator;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import ru.art2000.calculator.R;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.GeneralHelper;
import ru.art2000.helpers.PrefsHelper;

import static ru.art2000.calculator.calculator.CalculationClass.isAfterUnarySign;
import static ru.art2000.calculator.calculator.CalculationClass.isDot;
import static ru.art2000.calculator.calculator.CalculationClass.isNumber;
import static ru.art2000.calculator.calculator.CalculationClass.isPreUnarySign;
import static ru.art2000.calculator.calculator.CalculationClass.isSign;
import static ru.art2000.calculator.calculator.CalculationClass.memory;
import static ru.art2000.calculator.calculator.CalculationClass.radians;


public class CalculatorFragment extends Fragment {

    private static final int COPY_ALL = 101;
    private static final int COPY_EXPR = 102;
    private static final int COPY_RES = 103;
    private static final int DELETE = 104;
    private static final int PASTE = 105;
    private static final int PASTE_AFTER = 106;
    public SlidingUpPanelLayout panel;
    private Context mContext;
    private TextView InputTV;
    private TextView ResultTV;
    private TextView memoryTextView;
    private TextView degRadTextView;
    private HorizontalScrollView hsv;
    private ViewPager horizontal;
    private View v;
    private RecyclerView history_list;
    private HistoryDB hdb;
    private SQLiteDatabase db;
    private HistoryListAdapter adapter;
    private TextView empty;
    private ViewGroup recycler_container;

    private boolean isNumberResult;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v == null) {
            mContext = getActivity();
            if (AndroidHelper.isLongScreen(mContext))
                v = inflater.inflate(R.layout.calc_layout_long, null);
            else
                v = inflater.inflate(R.layout.calc_layout, null);
            horizontal = v.findViewById(R.id.button_pager);
            horizontal.setAdapter(new CalculatorPagesAdapter(getActivity()));
            InputTV = v.findViewById(R.id.tv_input);
            memoryTextView = v.findViewById(R.id.memory);
            degRadTextView = v.findViewById(R.id.degRadTv);
            hsv = (HorizontalScrollView) InputTV.getParent();
            registerForContextMenu(InputTV);
            hsv.setOnLongClickListener(view -> {
                hsv.showContextMenu();
                return true;
            });
            InputTV.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    hsv.postDelayed(() ->
                            hsv.smoothScrollTo(InputTV.getWidth(), 0), 100L);
                }
            });
            ResultTV = v.findViewById(R.id.tv_result);
            setupHistoryPart();
        }

        return v;
    }

    private void buttonClick(View v) {
        Button button_pressed = (Button) v;
        String buttonText = button_pressed.getText().toString();
        if (isCButton(button_pressed))
            OnCBtnClick(button_pressed);
        else if (buttonText.equals("("))
            onOBCClick();
        else if (buttonText.equals(")"))
            onCBCClick();
        else if (isSign(buttonText) || buttonText.equalsIgnoreCase("div") ||
                buttonText.equalsIgnoreCase("mod"))
            onSignBtnClick(button_pressed);
        else if (buttonText.equals("="))
            onResult();
        else if (isPreUnarySign(buttonText))
            onPreUnarySignClick(buttonText);
        else if (isAfterUnarySign(buttonText))
            onAfterUnarySignClick(buttonText);
        else if (buttonText.startsWith("M"))
            onMemoryBtnClick(buttonText);
        else if (isConstant(buttonText))
            onConstantBtnClick(buttonText);
        else if (button_pressed.getId() == R.id.buttonDEGRAD)
            onDegBtnClick(button_pressed);
        else
            onBtnClick(button_pressed);
    }

    private void onDegBtnClick(Button button) {
        CalculationClass.radians ^= true;
        button.setText(!radians ? "RAD" : "DEG");
        degRadTextView.setText(radians ? "Rad" : "Deg");
    }

    private void onConstantBtnClick(String text) {
        String ex = InputTV.getText().toString();
        String last = ex.substring(ex.length() - 1);
        String append;
        if (isNumber(last)) {
            append = "×" + text;
        } else if (isDot(last)) {
            append = "0×" + text;
        } else
            append = text;
        InputTV.append(append);
    }

    private boolean isConstant(String string) {
        return "eπφ".contains(string);
    }

    private void onMemoryBtnClick(String text) {
        switch (text.substring(text.length() - 1)) {
            default:
            case "+":
                onResult();
                try {
                    CalculationClass.memory += Integer.parseInt(ResultTV.getText().toString());
                } catch (Exception ignored) {
                }
                break;
            case "-":
                onResult();
                try {
                    CalculationClass.memory -= Integer.parseInt(ResultTV.getText().toString());
                } catch (Exception ignored) {
                }
                break;
            case "R":
                if (CalculationClass.memory != 0) {
                    if (ResultTV.getVisibility() == View.VISIBLE) {
                        ResultTV.setVisibility(View.INVISIBLE);
                    }
                    InputTV.setText(GeneralHelper.resultNumberFormat.format(CalculationClass.memory));
                }
                break;
            case "C":
                CalculationClass.memory = 0;
                break;
        }

        updateMemoryView();
    }

    private void updateMemoryView() {
        if (memory == 0) memoryTextView.setVisibility(View.GONE);
        else {
            memoryTextView.setText("M" + GeneralHelper.resultNumberFormat.format(memory));
            memoryTextView.setVisibility(View.VISIBLE);
        }
    }

    private void onAfterUnarySignClick(String buttonText) {
        String ex = InputTV.getText().toString();
        String last = ex.substring(ex.length() - 1);
        String append = "";
        if (CalculationClass.isDot(last))
            append += "0";
        else if (CalculationClass.isSign(last))
            append += "1";
        append += buttonText;
        InputTV.append(append);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu,
                                    @NonNull View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, PASTE, Menu.NONE, mContext.getString(R.string.paste_replace));
        menu.add(Menu.NONE, PASTE_AFTER, Menu.NONE, mContext.getString(R.string.paste_after));
        ClipboardManager cmg = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmg == null || !cmg.hasPrimaryClip()) {
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(false);
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem menuItem) {
        HistoryDB hdb = new HistoryDB(mContext);
        SQLiteDatabase db = hdb.getReadableDatabase();
        Cursor cc = db.query("history", null, null, null, null, null, null);
        ClipboardManager cmg = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        boolean shouldShowToast = true;
        String toastText = getString(R.string.error);
        int position = adapter.getPosition();
        cc.move(position + 1);
        String expr = cc.getString(cc.getColumnIndex("expression"));
        String res = cc.getString(cc.getColumnIndex("result"));
        switch (menuItem.getItemId()) {
            case PASTE:
                ClipData.Item clipItem = cmg.getPrimaryClip().getItemAt(0);
                InputTV.setText(clipItem.getText().toString());
                shouldShowToast = false;
                break;
            case PASTE_AFTER:
                clipItem = cmg.getPrimaryClip().getItemAt(0);
                InputTV.append(clipItem.getText().toString());
                shouldShowToast = false;
                break;
            case COPY_EXPR:
                toastText = getResources().getString(R.string.copied) + " " + expr;
                clip = ClipData.newPlainText("Expression", expr);
                cmg.setPrimaryClip(clip);
                break;
            case COPY_RES:
                toastText = getResources().getString(R.string.copied) + " " + res;
                clip = ClipData.newPlainText("Result", res);
                cmg.setPrimaryClip(clip);
                break;
            case COPY_ALL:
                String toCopy = expr + "=" + res;
                toastText = getResources().getString(R.string.copied) + " " + toCopy;
                clip = ClipData.newPlainText("AllInOne", toCopy);
                cmg.setPrimaryClip(clip);
                break;
            case DELETE:
                toastText = getResources().getString(R.string.deleted) + " " + expr + "=" + res;
                db.delete("history", "id=" + position + 1, null);
                hdb.fixIDs(db, position);
                adapter.setNewData();
                break;
        }
        if (shouldShowToast)
            Toast.makeText(mContext, toastText, Toast.LENGTH_SHORT).show();
        cc.close();
        return true;
    }

    private void onPreUnarySignClick(String buttonText) {
        String ex = InputTV.getText().toString();
        String last = ex.substring(ex.length() - 1);
        String append = "";
        if (CalculationClass.isDot(last))
            append += "0×";
        else if (CalculationClass.isNumber(last) && !ex.equals("0"))
            append += "×";
        else if (ex.equals("0")) {
            InputTV.setText(buttonText);
            return;
        }
        append += buttonText;
        InputTV.append(append);
    }

    private void onOBCClick() {
        String ex = InputTV.getText().toString();
        String last = ex.substring(ex.length() - 1);
        if (ResultTV.getVisibility() == View.VISIBLE) {
            ResultTV.setVisibility(View.INVISIBLE);
            InputTV.setText("(");
            return;
        }
        if (ex.equals("0"))
            InputTV.setText("(");
        else if (CalculationClass.isNumber(last) || last.equals(")"))
            InputTV.append("×(");
        else if (CalculationClass.isDot(last))
            InputTV.append("0×(");
        else
            InputTV.append("(");
    }

    private void onCBCClick() {
        String ex = InputTV.getText().toString();
        char[] ar = ex.toCharArray();
        int o = 0;
        int c = 0;
        int lastOpenBr = -1;
        for (int i = 0, exLength = ar.length; i < exLength; i++) {
            char anEx = ar[i];
            if (anEx == '(') {
                o++;
                lastOpenBr = i;
            }
            if (anEx == ')')
                c++;
        }

        if (o - c > 0) {
            String exInBrs = ex.substring(lastOpenBr + 1);
            char[] newAr = exInBrs.toCharArray();
            if (CalculationClass.signsInExpr(exInBrs) > 0 && (String.valueOf(newAr[0]).equals("-")
                    || CalculationClass.isNumber(newAr[0])) && !CalculationClass.isSign(newAr[newAr.length - 1]))
                InputTV.append(")");
        }
    }

    private void onSignBtnClick(Button v) {
        String ToAdd;
        int InpLen = InputTV.length();
        String InputText = InputTV.getText().toString();
        String last = InputText.substring(InpLen - 1, InpLen);
        String prelast = "";
        if (InpLen > 1)
            prelast = InputText.substring(InpLen - 2, InpLen - 1);
        switch (v.getId()) {
            case R.id.buttonRDiv:
                ToAdd = "/";
                break;
            case R.id.buttonMod:
                ToAdd = ":";
                break;
            default:
                ToAdd = v.getText().toString();
                break;
        }

        if (ResultTV.getVisibility() == View.VISIBLE) {
            ResultTV.setVisibility(View.INVISIBLE);
            if (isNumberResult) {
                ToAdd = ResultTV.getText() + ToAdd;
                InputTV.setText(ToAdd);
                isNumberResult = false;
                return;
            }
        }

        if (!(InputText.equals("0") || InputText.equals("-"))) {
            if (isSign(last) && !ToAdd.equals("-")) {
                String Copied = InputText.substring(0, InpLen - 1) + ToAdd;
                InputTV.setText(Copied);
            } else if (last.equals(".") || last.equals(",")) {
                ToAdd = "0" + ToAdd;
                InputTV.append(ToAdd);
            } else if (ToAdd.equals("-") && last.equals("-")) {
                if (isSign(prelast))
                    InputTV.setText(InputText.substring(0, InpLen - 1));
            } else if (ToAdd.equals("-") && isSign(last)) {
                InputTV.append("(-");
            } else {
                InputTV.append(ToAdd);
            }
        } else if (ToAdd.equals("-"))
            InputTV.setText(ToAdd);
        hsv.postDelayed(() ->
                hsv.smoothScrollTo(InputTV.getWidth(), 0), 100L);
    }

    private void onBtnClick(View v) {
        String ToAdd;
        int lastSign = 0;
        String buttonText = ((Button) v).getText().toString();
        int InpLen = InputTV.length();
        String InputText = InputTV.getText().toString();
        String last = InputText.substring(InpLen - 1, InpLen);
        String ZeroStr = "0";
        if (buttonText.equals("."))
            ToAdd = ",";
        else
            ToAdd = buttonText;
        if (ResultTV.getVisibility() == View.VISIBLE) {
            ResultTV.setVisibility(View.INVISIBLE);
            if (buttonText.equals(".") || buttonText.equals(","))
                ToAdd = "0,";
            InputTV.setText(ToAdd);
            return;
        }
        switch (buttonText) {
            case ",":

            case ".":
                int i;
                for (i = InpLen - 1; i > 0; i--) {
                    if (isSign(String.valueOf(InputText.toCharArray()[i]))) {
                        lastSign = i;
                        break;
                    }
                }
                String lNum = InputText.substring(lastSign, InpLen);
                if (lNum.contains(".") || lNum.contains(","))
                    return;
                else if (isSign(last) && !InputText.equals(ZeroStr))
                    ToAdd = "0,";
                break;
            case "0":
                if (isSign(last))
                    ToAdd = "0,";
                break;
        }
        if (InputTV.getText().toString().equals(ZeroStr) && v.getId() != R.id.buttonDot)
            InputTV.setText("");
        InputTV.append(ToAdd);
    }

    private void onResult() {
        String CountStr = InputTV.getText().toString();
        boolean err = false;
        int CountLen = CountStr.length();
        String last = CountStr.substring(CountLen - 1, CountLen);
        if (last.equals(".") || last.equals(",") || isSign(last) ||
                ResultTV.getVisibility() == View.VISIBLE)
            return;
        String expression = CalculationClass.addRemoveBrackets(CountStr);
        if (expression.length() == 0) {
            expression = mContext.getString(R.string.error);
        }
        InputTV.setText(expression);

        HistoryDB hdb = new HistoryDB(mContext);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = hdb.getWritableDatabase();
        hdb.nextId(db);
        CountStr = CalculationClass.calculateStr(expression);
        isNumberResult = false;
        switch (CountStr) {
            case "zero":
                CountStr = getString(PrefsHelper.getZeroDivResult());
                Log.d("zerol", CountStr);
                break;
            case "error":
                CountStr = getString(R.string.error);
                Log.d("eero", CountStr);
                err = true;
                break;
            default:
                isNumberResult = true;
                break;
        }
        if (!err) {
            cv.put("expression", expression);
            cv.put("result", CountStr);
            db.insert("history", null, cv);
        }

        hdb.close();
        ResultTV.setText(CountStr);
        ResultTV.setVisibility(View.VISIBLE);
        writeResult();
    }

    private boolean isCButton(View v) {
        return v.getId() == R.id.buttonClear || v.getId() == R.id.buttonDel;
    }

    private void clearInput() {
        ResultTV.setVisibility(View.INVISIBLE);
        InputTV.setText("0");
    }

    private void OnCBtnClick(View v) {
        switch (v.getId()) {
            case R.id.buttonClear:
                clearInput();
                break;
            case R.id.buttonDel:
                int InpLen = InputTV.length();
                String InputText = InputTV.getText().toString();
                String last = InputText.substring(InpLen - 1, InpLen);
                String prelast = "-1";
                if (InpLen > 1)
                    prelast = InputText.substring(InpLen - 2, InpLen - 1);
                if ((last.equals(".") || last.equals(",")) && prelast.equals("0")) {
                    String NewText = InputText.substring(0, InpLen - 2);
                    InputTV.setText(NewText);
                } else {
                    String NewText = InputText.substring(0, InpLen - 1);
                    InputTV.setText(NewText);
                }
                if (InpLen == 1)
                    InputTV.setText("0");
                if (ResultTV.getVisibility() == View.VISIBLE) {
                    InputTV.setText("0");
                    ResultTV.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    private void setButtonsClickListener(View v) {
        for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
            View child = ((ViewGroup) v).getChildAt(i);
            if (child instanceof ViewGroup) {
                setButtonsClickListener(child);
            }
            if (child instanceof Button) {
                child.setOnClickListener(this::buttonClick);
            }
        }
    }

    private void clearHistory() {
        SQLiteDatabase db = hdb.getWritableDatabase();
        HistoryDB.recreateDB(db);
        adapter.setNewData();
        setEmptyView();
        Toast.makeText(mContext, getString(R.string.history_cleared), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setupHistoryPart() {
        panel = v.findViewById(R.id.sliding_panel);
        RelativeLayout handle = v.findViewById(R.id.history_handle);
        handle.setOnClickListener(view -> {
            panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        });
        v.findViewById(R.id.header).setOnClickListener(view -> {
            panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        });
        panel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset > 0)
                    handle.setVisibility(View.GONE);
                else
                    handle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPanelStateChanged(View panelView, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.DRAGGING &&
                        previousState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                    history_list.scrollToPosition(adapter.getItemCount() - 1);
                if (newState == SlidingUpPanelLayout.PanelState.ANCHORED)
                    panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                Log.d("stattt", newState.toString());
                Log.d("empty", String.valueOf(empty.getVisibility() == View.VISIBLE));

            }
        });
        LinearLayout history = v.findViewById(R.id.history_part);
        ViewTreeObserver observer = horizontal.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int h = horizontal.getHeight();
                if (h > 0) {
                    ViewGroup.LayoutParams params = history.getLayoutParams();
                    params.height = h;
                    history.setLayoutParams(params);
                    horizontal.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        recycler_container = v.findViewById(R.id.recycler_layout);
        empty = v.findViewById(R.id.empty_tv);
        history_list = v.findViewById(R.id.history_list);

        v.findViewById(R.id.clear_history).setOnClickListener(clearBtn -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Стереть историю").setMessage("Вы уверены, что хотите удалить историю?")
                    .setCancelable(true).setNegativeButton("Отмена", (dialog, which) ->
                    dialog.cancel())
                    .setPositiveButton("Удалить", (dialog, which) ->
                            clearHistory());
            builder.create().show();
        });
        v.findViewById(R.id.scroll_up).setOnClickListener(scrollUp ->
                history_list.smoothScrollToPosition(0));
        v.findViewById(R.id.scroll_bottom).setOnClickListener(scrollDown ->
                history_list.smoothScrollToPosition(adapter.getItemCount()));
        history_list.setLayoutManager(new LinearLayoutManager(mContext));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(mContext, R.drawable.ic_clear_history);
                ColorFilter whiteColorFilter =
                        new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMark.setColorFilter(whiteColorFilter);
                xMarkMargin = (int) mContext.getResources().getDimension(R.dimen.activity_horizontal_margin);
                initiated = true;
            }


            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                int windowBackgroundColor = AndroidHelper.getColorAttribute(mContext, android.R.attr.windowBackground);
                itemView.setBackgroundColor(windowBackgroundColor);

                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();
                int xMarkLeft;
                int xMarkRight;
                int xMarkTop;
                int xMarkBottom;
                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + (int) dX, itemView.getBottom());
                    xMarkLeft = itemView.getLeft() + xMarkMargin;
                    xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
                    xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                    xMarkBottom = xMarkTop + intrinsicHeight;
                } else {
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                    xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                    xMarkRight = itemView.getRight() - xMarkMargin;
                    xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                    xMarkBottom = xMarkTop + intrinsicHeight;
                }
                background.draw(c);

                // draw x mark
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int item = viewHolder.getLayoutPosition() + 1;
                db.delete("history", "id=" + item, null);
                if (hdb.getSize() == 0)
                    setEmptyView();
                hdb.fixIDs(db, item);
                adapter.setNewData();
            }
        });

        itemTouchHelper.attachToRecyclerView(history_list);
        hdb = new HistoryDB(mContext);
        db = hdb.getReadableDatabase();
        adapter = new HistoryListAdapter(mContext, hdb);
        Log.d("HisAdapter", "es");
        if (hdb.getSize() != 0) {
            recycler_container.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            history_list.addItemDecoration(
                    new DividerItemDecoration(
                            history_list.getContext(),
                            DividerItemDecoration.VERTICAL));
            history_list.setAdapter(adapter);
            history_list.scrollToPosition(adapter.getItemCount() - 1);
            setUpAnimationDecoratorHelper();
        } else
            setEmptyView();
    }

    private void writeResult() {
        if (empty.getVisibility() == View.VISIBLE) {
            setupHistoryPart();
        }
        adapter.setNewData();
    }

    private void setEmptyView() {
        recycler_container.setVisibility(View.INVISIBLE);
        empty.setVisibility(View.VISIBLE);
    }

    private void setUpAnimationDecoratorHelper() {
        history_list.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(@NonNull Canvas c,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

    class CalculatorPagesAdapter extends PagerAdapter {

        Context ctx;
        int[] pages = {R.layout.calculator_page1, R.layout.calculator_page2};

        CalculatorPagesAdapter(Context context) {
            ctx = context;
        }

        @Override
        public int getCount() {
            return pages.length;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(ctx).inflate(pages[position], container, false);
            setButtonsClickListener(view);
            Button del = view.findViewById(R.id.buttonDel);
            if (del != null) {
                del.setOnLongClickListener(v -> {
                    clearInput();
                    return false;
                });
            }
            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

}
