package com.example.zivko.shoppinglist;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

import base_adapters.ShoppingListAdapter;
import db.DataBaseHelper;
import objects.Item;
import objects.ShoppingList;

public class ListActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    ListView shoppingList;
    List<ShoppingList> shList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        toolbar.setTitle("Shopping Lists");
        toolbar.setTitleTextColor(getResources().getColor(R.color.title));
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);

        shoppingList = (ListView) findViewById(R.id.shoppingList);

        try {
            shList = getDatabaseHelper().getShoppingListDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (shList != null) {

            ShoppingListAdapter adapter = new ShoppingListAdapter(this);
            shoppingList.setAdapter(adapter);
            shoppingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ShoppingList shoppingList = (ShoppingList) ListActivity.this.shoppingList.getItemAtPosition(i);
                    Intent intent = new Intent(ListActivity.this, DeatailActivity.class);
                    intent.putExtra("POSITION", shoppingList.getId());
                    startActivity(intent);
                }
            });

            FloatingActionButton button = (FloatingActionButton) findViewById(R.id.buy);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(ListActivity.this);
                    dialog.setTitle("Enter new ShoppingList");
                    dialog.setContentView(R.layout.dialog_add_shoplist);

                    final EditText name = (EditText) dialog.findViewById(R.id.name);
                    final EditText date = (EditText) dialog.findViewById(R.id.date);

                    Button ok = (Button) dialog.findViewById(R.id.save);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ShoppingList newShoppingList = new ShoppingList();

                            newShoppingList.setName(name.getText().toString());
                            newShoppingList.setDate(date.getText().toString());
                            try {
                                getDatabaseHelper().getShoppingListDao().create(newShoppingList);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            refreshShoppingList();

                            showMessage("New Shopping list is made: ", newShoppingList.getName());

                            dialog.dismiss();
                        }
                    });

                    Button cancel = (Button) dialog.findViewById(R.id.cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showMessage("Shopping list is NOT made: ", " ");
                            dialog.dismiss();
                        }

                    });

                    dialog.show();

                    if( dialog.isShowing()) {dialog.getWindow().setBackgroundDrawableResource(R.color.title1);}
                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(window.getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    window.setAttributes(lp);
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_item_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(ListActivity.this, Settings.class);
                startActivity(intent);
                break;

            case R.id.About:
                About about = new About();
                about.show(getSupportFragmentManager(), "ABOUT");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshShoppingList();
    }

    private void refreshShoppingList() {

        ListView listview = (ListView) findViewById(R.id.shoppingList);

        if (listview != null) {
            ShoppingListAdapter adapter = (ShoppingListAdapter) listview.getAdapter();

            if (adapter != null) {
                //try {
                    /*adapter.clear();
                    List<ShoppingList> list = getDatabaseHelper().getShoppingListDao().queryForAll();

                    adapter.addAll(list);*/

                    adapter.refreshAdapter();
                /*} catch (SQLException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }

    public void showMessage(String text, String newGlumac) {
        SharedPreferences st = PreferenceManager.getDefaultSharedPreferences(ListActivity.this);
        String name = st.getString("message", "Toast");
        if (name.equals("Toast")) {
            Toast.makeText(ListActivity.this, text + "\n"+ newGlumac, Toast.LENGTH_LONG).show();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ListActivity.this);
            builder.setSmallIcon(R.drawable.ic_action_name);
            builder.setContentTitle(text);
            builder.setContentText(newGlumac);


            // Shows notification with the notification manager (notification ID is used to update the notification later on)
            NotificationManager manager = (NotificationManager) ListActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, builder.build());
        }
    }

    public DataBaseHelper getDatabaseHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        }
        return dataBaseHelper;
    }

    @Override
    public void onDestroy() {
        Log.d("ZIL", "Main onDESTROY");
        super.onDestroy();

        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
    }
}
