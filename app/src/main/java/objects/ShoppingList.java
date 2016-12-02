package objects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Živko on 2016-10-25.
 */
@DatabaseTable(tableName = ShoppingList.TABLE_NAME)
public class ShoppingList {

    public static final String TABLE_NAME = "shoppingList";
    public static final String ID_GLUMAC = "id";
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String IS_DONE = "isDone";

    public static final String LISTAFILMOVA = "shoppingList";

    @DatabaseField(generatedId = true, columnName = ID_GLUMAC)
    int id;

    @DatabaseField(columnName = NAME)
    String name;

    @DatabaseField(columnName = DATE)
    String date;

    @DatabaseField(columnName = IS_DONE)
    boolean isDone;

   @ForeignCollectionField(columnName = LISTAFILMOVA, eager = true)
    ForeignCollection<Item> items;

    public ShoppingList() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }

    public ForeignCollection<Item> getItems() {
        return items;
    }

    public void setItems(ForeignCollection<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return name;
    }
}
