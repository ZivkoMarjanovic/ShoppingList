package com.example.zivko.shoppinglist;

import android.app.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import base_adapters.ItemAdapter;
import base_adapters.ShoppingListAdapter;
import db.DataBaseHelper;
import objects.Item;
import objects.ShoppingList;

public class DeatailActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    ShoppingList shoppingList;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items);

        position = getIntent().getIntExtra("POSITION", 0);
        izmena(position);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle(shoppingList.getName() + "  "+ shoppingList.getDate());
        toolbar.setTitleTextColor(getResources().getColor(R.color.title));
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.detail_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_delete_shoplist);

                TextView name = (TextView) dialog.findViewById(R.id.nameDelete);
                TextView date = (TextView) dialog.findViewById(R.id.dateDelete);

                name.setText(shoppingList.getName());
                date.setText(shoppingList.getDate());

                Button cancelD = (Button) dialog.findViewById(R.id.cancel);
                cancelD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                Button delete = (Button) dialog.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                try {
                    if (shoppingList != null) {

                        ForeignCollection<Item> collection = shoppingList.getItems();
                        final List<Item> list = new ArrayList<Item>();

                        if (!collection.isEmpty()) {
                            CloseableIterator<Item> iterator = collection.closeableIterator();

                            try {

                                while (iterator.hasNext()) {
                                    Item f = iterator.next();
                                    list.add(f);
                                }
                            } finally {
                                try {
                                    iterator.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                            TransactionManager.callInTransaction(getDatabaseHelper().getConnectionSource(),
                                    new Callable<Void>() {
                                        public Void call() throws Exception {
                                            showMessage("Deleted shoppingList", shoppingList.getName());

                                            getDatabaseHelper().getItemDao().delete(list);

                                            getDatabaseHelper().getShoppingListDao().delete(shoppingList);

                                            dialog.dismiss();
                                            finish();

                                            return null;
                                        }
                                    });

                        } else {
                            showMessage("Deleted shoppingList", shoppingList.getName());
                            getDatabaseHelper().getShoppingListDao().delete(shoppingList);
                            dialog.dismiss();
                            finish();}

                        }

                } catch (SQLException e) {
                    e.printStackTrace();
                }


                    }
                });

                dialog.show();
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(window.getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(lp);
                break;

            case R.id.edit:
                final Dialog dialogE = new Dialog(DeatailActivity.this);
                dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogE.setContentView(R.layout.dialog_add_shoplist);

                final EditText nameE = (EditText) dialogE.findViewById(R.id.name);
                final EditText dateE = (EditText) dialogE.findViewById(R.id.date);


                nameE.setText(shoppingList.getName());
                dateE.setText(shoppingList.getDate());

                Button ok = (Button) dialogE.findViewById(R.id.save);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        shoppingList.setName(nameE.getText().toString());
                        shoppingList.setDate(dateE.getText().toString());


                        try {
                            getDatabaseHelper().getShoppingListDao().update(shoppingList);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                        showMessage("Updated shoppingList", shoppingList.getDate());
                        refreshShoppingList();
                        izmena(shoppingList.getId());
                        dialogE.dismiss();


                    }
                });

                Button cancel = (Button) dialogE.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogE.dismiss();

                    }

                });
                dialogE.show();
                Window windowE = dialogE.getWindow();
                WindowManager.LayoutParams lpE = new WindowManager.LayoutParams();
                lpE.copyFrom(windowE.getAttributes());
                lpE.width = WindowManager.LayoutParams.MATCH_PARENT;
                windowE.setAttributes(lpE);
                break;



        }

        return super.onOptionsItemSelected(item);
    }

    public void showMessage(String text, String newGlumac) {
        SharedPreferences st = PreferenceManager.getDefaultSharedPreferences(this);
        String name = st.getString("message", "Toast");
        if (name.equals("Toast")) {
            Toast.makeText(this, text + "\n"+ newGlumac, Toast.LENGTH_LONG).show();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_action_name);
            builder.setContentTitle(text);
            builder.setContentText(newGlumac);


            // Shows notification with the notification manager (notification ID is used to update the notification later on)
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, builder.build());
        }
    }

    private void refreshItem() {

        ListView listview = (ListView) findViewById(R.id.list_itemm);

        if (listview != null) {
            ItemAdapter adapter = (ItemAdapter) listview.getAdapter();

            if (adapter != null) {

              adapter.refreshItems(position);

                //adapter.notifyDataSetChanged();


            }
        }
    }

    private void refreshShoppingList() {

        ListView listview = (ListView) findViewById(R.id.shoppingList);

        if (listview != null) {
            ShoppingListAdapter adapter = (ShoppingListAdapter) listview.getAdapter();

            if (adapter != null) {
                    adapter.refreshAdapter();
               }
        }
    }


    public void izmena(int position) {
        try {
            shoppingList = getDatabaseHelper().getShoppingListDao().queryForId(position);

            Log.d("ZIL", "YYYYYYYYYYYYY");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (shoppingList != null) {
            /*TextView name = (TextView) findViewById(R.id.name);
            name.setText(shoppingList.getName());

            TextView date = (TextView) findViewById(R.id.date);
            date.setText(shoppingList.getDate());*/

            FloatingActionButton button = (FloatingActionButton) findViewById(R.id.buy);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog1 = new Dialog(DeatailActivity.this);
                    dialog1.setTitle("Add new item:");

                    dialog1.setContentView(R.layout.dialog_add_item);


                    final EditText name1 = (EditText) dialog1.findViewById(R.id.name1);
                    //final EditText zanr = (EditText) filmDialog.findViewById(R.id.zanr);
                    final EditText quantity1 = (EditText) dialog1.findViewById(R.id.quantity1);


                    Button okItem = (Button) dialog1.findViewById(R.id.save);
                    okItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Item newItem = new Item();

                            newItem.setName(name1.getText().toString());
                            newItem.setQuantity(Integer.parseInt(quantity1.getText().toString()));
                            newItem.setIs_bought(false);
                            newItem.setShoppingList(shoppingList);


                            try {
                                getDatabaseHelper().getItemDao().create(newItem);


                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            showMessage("Aded new item: ", newItem.getName());
                            refreshItem();
                            dialog1.dismiss();
                        }
                    });

                    Button cancelItem = (Button) dialog1.findViewById(R.id.cancel);
                    cancelItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                        }

                    });
                    dialog1.show();
                    if( dialog1.isShowing()) {dialog1.getWindow().setBackgroundDrawableResource(R.color.title1);}
                    Window window = dialog1.getWindow();
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(window.getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    window.setAttributes(lp);

                }
            });

            ListView listaItems = (ListView) findViewById(R.id.list_itemm);

            ForeignCollection<Item> call = shoppingList.getItems();
            List<Item> liss = new ArrayList<Item>();
            if (!call.isEmpty()) {
                for (Item item : call) {
                    liss.add(item);
                }
            }
            if (liss != null) {
                ItemAdapter adapter = new ItemAdapter(this, position);
                listaItems.setAdapter(adapter);
                listaItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final Item item = (Item) adapterView.getItemAtPosition(i);

                        final Dialog dialog = new Dialog(DeatailActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_edit_item);

                        final EditText name = (EditText) dialog.findViewById(R.id.nameItemD);
                        final EditText quantity = (EditText) dialog.findViewById(R.id.quantityItemD);
                        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.checkboxItemD);


                        name.setText(item.getName());
                        quantity.setText(Integer.toString(item.getQuantity()));
                        checkBox.setChecked(item.is_bought());

                        Button edit = (Button) dialog.findViewById(R.id.edit);
                        edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                item.setName(name.getText().toString());
                                item.setQuantity(Integer.parseInt(quantity.getText().toString()));
                                item.setIs_bought(checkBox.isChecked());

                                try {
                                    getDatabaseHelper().getItemDao().update(item);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }


                                showMessage("Changed item", item.getName());
                                //refreshItem();
                                dialog.dismiss();


                            }
                        });

                        Button cancel = (Button) dialog.findViewById(R.id.cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                            }

                        });

                        Button delete = (Button) dialog.findViewById(R.id.delete);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    showMessage("Deleted item", item.getName());
                                    getDatabaseHelper().getItemDao().delete(item);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                dialog.dismiss();

                            }

                        });
                        dialog.show();
                        Window window = dialog.getWindow();
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(window.getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        window.setAttributes(lp);

                    }
                });
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        refreshItem();
    }
}
