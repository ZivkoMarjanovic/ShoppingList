package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import objects.Item;
import objects.ShoppingList;

/**
 * Created by Živko on 2016-11-17.
 */

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String BASENAME = "shoppingList.db";
    public static final int VERSION = 1;

    Dao<ShoppingList, Integer> shoppingListDao = null;
    Dao<Item, Integer> itemDao = null;

    public DataBaseHelper(Context context) {
        super(context, BASENAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, ShoppingList.class);
            TableUtils.createTable(connectionSource, Item.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ShoppingList.class, true);
            TableUtils.dropTable(connectionSource, Item.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<ShoppingList, Integer> getShoppingListDao() throws SQLException {
        if (shoppingListDao == null) {
            shoppingListDao = getDao(ShoppingList.class);
        }

        return shoppingListDao;
    }

    public Dao<Item, Integer> getItemDao() throws SQLException {
        if (itemDao == null) {
            itemDao = getDao(Item.class);
        }

        return itemDao;
    }

    @Override
    public void close() {
        shoppingListDao = null;
        itemDao = null;

        super.close();
    }
}
