package base_adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.zivko.shoppinglist.R;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

import db.DataBaseHelper;
import objects.ShoppingList;

/**
 * Created by Živko on 2016-11-28.
 */

public class ShoppingListAdapter extends BaseAdapter {

    Context context;
    List<ShoppingList> list;
    DataBaseHelper dataBaseHelper;
    public ShoppingListAdapter(Context context) {
        this.context = context;
        try {
            list = getDatabaseHelper().getShoppingListDao().queryForAll();
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
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.shop_single_row,viewGroup, false);

        TextView name2 = (TextView) row.findViewById(R.id.name2);
        TextView date2 = (TextView) row.findViewById(R.id.date2);

        name2.setText(list.get(i).getName());
        date2.setText(list.get(i).getDate());

        return row;
    }

    public void refreshAdapter (){
        try {
            list = getDatabaseHelper().getShoppingListDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
        this.notifyDataSetChanged();
    }

    public DataBaseHelper getDatabaseHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        }
        return dataBaseHelper;
    }


}
