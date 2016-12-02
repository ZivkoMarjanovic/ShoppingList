package base_adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.zivko.shoppinglist.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.ForeignCollection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DataBaseHelper;
import objects.Item;

/**
 * Created by Živko on 2016-11-28.
 */

public class ItemAdapter extends BaseAdapter {

    Context context;
    List<Item> list = new ArrayList<>();
    DataBaseHelper dataBaseHelper;
    int position;

    public ItemAdapter(Context context, int position) {
        this.context = context;
        this.position = position;
        try {
            ForeignCollection<Item> items = getDatabaseHelper().getShoppingListDao().queryForId(position).getItems();

            if (!items.isEmpty()) {
                for (Item i : items) {
                    list.add(i);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //final int item = i;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.list_single_row, viewGroup, false);

        TextView nameItem = (TextView) row.findViewById(R.id.nameItem);
        Typeface tp = Typeface.createFromAsset(context.getAssets(), "fonts/XLines.ttf");
        nameItem.setTypeface(tp);
        TextView quantityItem = (TextView) row.findViewById(R.id.quantityItem);
        CheckBox checkBoxItem = (CheckBox) row.findViewById(R.id.checkboxItem);

        /*checkBoxItem.setFocusable(false);
        checkBoxItem.setFocusableInTouchMode(false);*/

        checkBoxItem.setTag(i);
        checkBoxItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox ch = (CheckBox) view;
                int i = (int) ch.getTag();
                if (ch.isChecked()){
                list.get(i).setIs_bought(true);}
                else { list.get(i).setIs_bought(false);}
                try {
                    getDatabaseHelper().getItemDao().update(list.get(i));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (dataBaseHelper != null) {
                    OpenHelperManager.releaseHelper();
                    dataBaseHelper = null;
                }
                //refreshItems(position);
                //notifyDataSetChanged();
            }

        });

        nameItem.setText(list.get(i).getName());
        quantityItem.setText(Integer.toString(list.get(i).getQuantity()));
        checkBoxItem.setChecked(list.get(i).is_bought());
        return row;
    }

    public void refreshItems(int position) {

        try {
            ForeignCollection<Item> items = getDatabaseHelper().getShoppingListDao().queryForId(position).getItems();
            list.clear();
            if (!items.isEmpty()) {
                for (Item i : items) {
                    list.add(i);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }

    }

    public DataBaseHelper getDatabaseHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        }
        return dataBaseHelper;
    }


}
