import java.util.ArrayList;
import java.util.Arrays;

public class ArraysMethods {
    public static void main(String[] args) {

        Integer[] intArray = new Integer[7]; // массив объектов Integer
        for (int i = 0; i < intArray.length; i++){
            intArray[i] = i;
        }
    for (Integer o : intArray){
        System.out.print(o + " "); // вывод до перестановки
    }
    changePlace(intArray, 0, 3); // применили метод перестановки элементов
        System.out.println(" ");

        for (Integer o : intArray){  // вывод после перестановки
            System.out.print(o + " ");
        }
 //===================================================================
        ArrayList<Integer> intList= toArrayList(intArray); // применили метод для преобразования массива в ArrayList
        System.out.println(intList);
    }


    //метод меняет местами элементы на указанных позициях
    private static <T> void changePlace(T[] array, int index_1, int index_2){
        if ((index_1 >=0 && index_1 < array.length) && (index_2 >= 0 && index_2 < array.length)){
           T obj = array[index_1];
            array[index_1] = array[index_2];
            array[index_2] = obj;
        }
    }
    //метод преобразует массив в ArrayList;
   static <T> ArrayList<T> toArrayList(T[] array){
        return new ArrayList<T>(Arrays.asList(array));
    }

}
