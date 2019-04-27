//Speicherkomplexitaet: n.
public class Set {
    private int size;
    private boolean[] items;
    public Set(int initialSize){
        items = new boolean[initialSize];
        size = 0;
    }
    public void add(int item){
        if(item > items.length || item < 0) throw new IllegalArgumentException("Cannot add item to set: invalid index");
        items[item] = true;
        size++;
    }
    public boolean contains(int item){
        return items[item];
    }
    public int size(){
        return size;
    }
}
