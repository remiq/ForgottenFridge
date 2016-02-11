package pl.kyon.forgottenfridge;

/**
 * Created by remiq on 10.02.16.
 */
public class Item {
    public int id;
    public String name;
    public String amount;
    public String expire;
    public Item(int id, String name, String amount, String expire) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.expire = expire;
    }

    @Override
    public String toString() {
        return "{id: " + id + ", name: " + name + ", amount: " + amount + ", expire: " + expire + "}";
    }
}
