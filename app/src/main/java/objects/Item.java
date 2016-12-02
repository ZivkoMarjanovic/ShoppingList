package objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import static objects.Item.ITEM_TABLE_NAME;

/**
 * Created by Živko on 2016-10-25.
 */
@DatabaseTable(tableName = ITEM_TABLE_NAME)
public class Item {

    public static final String ITEM_TABLE_NAME = "item";
    public static final String ID_ITEM = "id";
    public static final String NAME = "name";
    public static final String QUANTITY = "quantity";
    public static final String IS_BOUGHT = "bought";
    public static final String GLUMAC_ID = "shoppingList";

    @DatabaseField(generatedId = true, columnName = ID_ITEM)
    int id;

    @DatabaseField(columnName = NAME)
    String name;

    @DatabaseField(columnName = QUANTITY)
    int quantity;

    @DatabaseField(columnName = IS_BOUGHT)
    boolean is_bought;

    @DatabaseField(columnName = GLUMAC_ID, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private ShoppingList shoppingList;

    public Item() {
    }

    public Item(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public boolean is_bought() {
        return is_bought;
    }

    public void setIs_bought(boolean is_bought) {
        this.is_bought = is_bought;
    }

    @Override
    public String toString() {
        return name + "  " + quantity;
    }
}
