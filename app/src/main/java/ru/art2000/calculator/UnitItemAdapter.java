package ru.art2000.calculator;
//Временно неиспользуемый адаптер для ListView в "мощном" конвертере
import android.content.Context;
import android.renderscript.Element;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnitItemAdapter extends RecyclerView.Adapter {

        Context mContext;
        private LayoutInflater mlayoutInflater;
        static ArrayList<String> arrayList;
    private RecyclerView mRecyclerView;

    EditText et;
        TextView tv;
        TextWatcher twatcher;
    private InputMethodManager imm;
        String[] inp;
        List l;
    public UnitItemAdapter(Context context, ArrayList<String> arr) {
        arrayList = arr;
        mContext = context;
//            l = new List() {
//            }
//            ArrayList<Element> arraylist = new ArrayList<>(Arrays.asList(arr));
//            String [] strings = new String [] {"1", "2" };
//            List<String> stringList = new ArrayList<String>(Arrays.asList(arr));
        inp = new String[arrayList.size()];
        for (int i = 0; i<arrayList.size(); i++)
            inp[i] = "0";
//                    Toast tt1 = Toast.makeText(mContext, String.valueOf(arrayList.get(1)), Toast.LENGTH_SHORT);
//            tt1.show();
        mlayoutInflater = LayoutInflater.from(context);  //Dynamic layout mapping
    }



    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View v = mlayoutInflater.inflate(R.layout.unit_list_item, viewGroup, false);
        ItemHolder pvh = new ItemHolder(v);



//        View v = recyclerView.getLayoutManager().findViewByPosition(position);

//
//            final ViewGroup p = parent;
        return pvh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder ViewHolder, int i) {
//        ViewHolder.MyCustomEditTextListener.updatePosition(ViewHolder.getAdapterPosition());

        if (tv.getText().toString().equals("type")) {
            tv.setText(String.valueOf(arrayList.get(ViewHolder.getAdapterPosition())));
//            Toast tt = Toast.makeText(mContext, "Test", Toast.LENGTH_LONG);
//                tt.show();
        }
//        if (et.getText().toString().equals("val"))
            et.setText(inp[i]);

//        EditText et1 = ViewHolder.


        final int pos = i;

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if (pos == 0) {
//                    inp[pos+1] = "56";
//                    notifyItemChanged(pos+1);
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                               Toast tt = Toast.makeText(mContext, inp[pos+1], Toast.LENGTH_LONG);
//                tt.show();
//
                for (int j=0;j<arrayList.size();j++){
                    final int pp = j;
                    if (j!=pos) {
//                        inp[j] = "Test" + String.valueOf(j);
                        inp[j] = String.valueOf(s.toString());
                      if (!mRecyclerView.isComputingLayout())
                                        notifyItemChanged(pp);

              }





            }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Toast tt = Toast.makeText(mContext, inp[pos+1], Toast.LENGTH_LONG);
//                tt.show();
////                if (et.getText().length() != 1) {
////                    inp[pos+1] = et.getText().toString();
//                for (int j=0;j<arrayList.size();j++){
//                    final int pp = j;
//                    if (j!=pos) {
////                        inp[j] = "Test" + String.valueOf(j);
//                        inp[j] = String.valueOf(s.toString());
////                                notifyItemChanged(j);
////                        mRecyclerView.post(new Runnable() {
////                            @Override public void run() {
//////                                for (int j=0;j<arrayList.size();j++){
////                                    if (j!=pos) {
////                                inp[j] = "Test" + String.valueOf(j);
//                        if (!mRecyclerView.isComputingLayout())
//                                        notifyItemChanged(pp);
//
////                                    }
////                                }
//                            }
////                        });
////                    }
//                }



//                    notifyItemChanged(pos+1);

//                }
//                if (pos == 0) {
//                    inp[pos+1] = "56";
//                    notifyItemChanged(pos+1);
//                }
            }
        });


//        EditText et = personViewHolder.findViewById(R.id.value);
//        TextView tv = itemView.findViewById(R.id.type);
//        tv.setText(String.valueOf(arrayList.get(getAdapterPosition())));
    }

    public void haha(int position) {

        for (int i = 0; i<arrayList.size(); i++){
            if (i != position)
                notifyItemChanged(i);
        }
//        super(v);
//        taskTV = (TextView)v.findViewById(R.id.taskDesc);
    }

    private Object getItem(int position){
        return arrayList.get(position);
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {

        TextView personName;
        TextView personAge;
        ItemHolder(View itemView) {
            super(itemView);

            et = itemView.findViewById(R.id.value);
            tv = itemView.findViewById(R.id.type);

        }
    }

    public class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//            arrayList[position] = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
//}



//        @Override
//        public int getCount() {
////            return arrayList.size();
//            return arrayList.size();
//        }
//
//    @Override
//    public Object getItem(int position) {
//        return arrayList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
////        @Override
////        public Object getItem(int position) {
////            return super(getItem());
////        }
////
////        @Override
////        public long getItemId(int position) {
////            return getItemId();
////        }
//
//
//
////        @Override
////        public void notifyDataSetChanged
//
//    @Override
//    public void notifyDataSetChanged() {
//           updateArr();
//
//        super.notifyDataSetChanged();
////        Object tr = getItem(curp);
////        View convertView = mlayoutInflater.inflate(R.layout.unit_list_item, null);  //  According to the layout of the document to instantiate view
////        final EditText et = convertView.findViewById(R.id.value);
//
////        final ViewGroup p = parent;
////        if (getItemId(curp) == curp) {
////            et.requestFocus();
////            int pos = et.length();
////            Editable etext = et.getText();
////            Selection.setSelection(etext, 1);
////            et.setSelection(et.getText().length());
////            Toast tt1 = Toast.makeText(mContext, String.valueOf(et.getText().length()), Toast.LENGTH_SHORT);
////            tt1.show();
////        }
//    }
//
////    @Override
////    public boolean dispatchKeyEvent(KeyEvent event) {
////
////        if (event.getAction() == KeyEvent.ACTION_DOWN) {
////            Log.e(TAG, "Key down, code " + event.getKeyCode());
////
////        } else if (event.getAction() == KeyEvent.ACTION_UP) {
////            Log.e(TAG, "Key up, code " + event.getKeyCode());
////        }
////
////        return true;
////    }
//
//    public void updateArr(){
//            switch (curp){
//                case 0:
//                    inpmm();
//                    break;
//                 default:
//                     for (int i = 0; i<arrayList.size();i++)
//                         inp[i] = inp[curp];
//            }
//    }
//
//
//    public void inpmm(){
//            double mm = Double.parseDouble(inp [0]);
//            inp[1] = String.valueOf(mm/10);
//            inp[2] = String.valueOf(mm/100);
//        inp[3] = String.valueOf(mm/1000);
//    }
//
//        int curp = -1;
//        int k = 0;
//        Boolean isChanged = false;
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
////            if (i<arrayList.length-1){
//
////            if (position == 1){
////                Toast tt = Toast.makeText(mContext, inp[1], Toast.LENGTH_SHORT);
////                tt.show();
////            }
//
//            convertView = mlayoutInflater.inflate(R.layout.unit_list_item, parent, false);  //  According to the layout of the document to instantiate view
//            final EditText et = convertView.findViewById(R.id.value);
//
//            final ViewGroup p = parent;
//            if (position == curp) {
//                et.requestFocus();
//                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View view, boolean hasFocus) {
////                    if (position == curp) {
////                        et.requestFocus();
////                int pos = et.length();
////                Editable etext = et.getText();
////                Selection.setSelection(etext, pos);
////                et.setSelection(et.getText().length());
////                    }
//                        et.dispatchWindowFocusChanged(hasFocus); // Fix for text selection handle not disappearing
//                    if (position == curp) {
//                        int pos = et.length();
//                        Editable etext = et.getText();
//                        Selection.setSelection(etext, pos);
//                    }
//                        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
////
//                                              imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
//                    }
//                });
////                int pos = et.length();
////                Editable etext = et.getText();
////                Selection.setSelection(etext, 1);
////                et.setSelection(et.getText().length());
//                Toast tt1 = Toast.makeText(mContext, String.valueOf(et.getText().length()), Toast.LENGTH_SHORT);
//                tt1.show();
//            }
//
////            et.setText("0");
////            et.setHint("0");
////            if (inp[position] != null)
//            et.setText(String.valueOf(inp[position]));
//
//               if (isChanged){
//                   et.setText("hah");
//                   isChanged = false;
//               }
//
//            TextView tv2 = convertView.findViewById(R.id.type);
//            tv2.setText(String.valueOf(getItem(position)));
////            final EditText editText = convertView.findViewById(R.id.EditText1);
//            et.setOnKeyListener(new View.OnKeyListener()
//                                      {
////                                          @Override
////                                          public boolean dispatchKeyEvent(KeyEvent event) {
////
////                                              if (event.getAction() == KeyEvent.ACTION_DOWN) {
////                                                  Log.e(TAG, "Key down, code " + event.getKeyCode());
////
////                                              } else if (event.getAction() == KeyEvent.ACTION_UP) {
////                                                  Log.e(TAG, "Key up, code " + event.getKeyCode());
////                                              }
////
////                                              return true;
////                                          }
//
//                                          public boolean onKey(View v, int keyCode, KeyEvent event)
//                                          {
//                                              curp = position;
//
//                                              if(event.getAction() == KeyEvent.ACTION_DOWN )
//                                              {
//                                                  // сохраняем текст, введенный до нажатия Enter в переменную
////                                                 inp[position] = et.getText().toString();
////                                                  return true;
//                                                  Toast tt = Toast.makeText(mContext, String.valueOf(curp), Toast.LENGTH_SHORT);
//                                                  tt.show();
//                                              }
//
////                                              return false;
////                                              isChanged = true;
//                                              k++;
////                                              for (int i = 0; i < arrayList.size(); i++){
////                                                  inp[i] = "gh" + String.valueOf(k);
//
////                                              }
//                                              if (et.getText().length() != 0)
//                                                 inp[position] = et.getText().toString();
//                                              else {
//                                                  char c = event.getDisplayLabel();
//
////                                                  }
//                                                  inp[position] = String.valueOf(c);
//                                              }
////                                              for (int i = 0; i < arrayList.size(); i++){
////                                                  if (i!=curp)
////                                                      notifyItemCh
////
////                                              }
//
//
//
////                                              et.requestFocus();
////                                              et.setSelection(et.getText().length()-1);
//
////Create object
//
//
////                                              imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
////
//////                                              imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_UNCHANGED_HIDDEN);
////
////                                              et.setInputType(InputType.TYPE_CLASS_NUMBER);
//
//                                              return false;
//                                          }
//                                      }
//            );
////            if (i<arrayList.length-1)
////                i++;
//////            else i = 0;
//
//                return convertView;
////            }
////            return null;
//        }
//
//

    }