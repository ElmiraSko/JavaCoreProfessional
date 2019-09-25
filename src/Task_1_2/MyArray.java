package Task_1_2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyArray<T> { //обобщенный класс для создания массива
    private T[] array;
    public MyArray(T... array){
        this.array = array;
    }

    //метод меняет местами элементы на указанных позициях
    void changePlace(int i, int j){
        T obj;
        if ((i >=0 && i < array.length) && (j >= 0 && j < array.length)){
            obj = array[i];
            array[i] = array[j];
            array[j] = obj;
        }
    }
    //метод преобразует массив в ArrayList;
    ArrayList toArrayList(){
        return new ArrayList<T>(Arrays.asList(array));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (T o : array){
            sb.append(o.toString());
            sb.append(", ");
        }
        return  sb.toString();
    }
}
