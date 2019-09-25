package Task_1_2;

import java.util.ArrayList;

public class Test {//для тестирования
    public static void main(String[] args) {

        Cat[] arrr = {new Cat("Мурзик"), new Cat("Черныш"), new Cat("Пушистик"), new Cat("Барсик")};

        MyArray<String> array1 = new MyArray<String>("а", "б", "в", "г", "д");
        MyArray<Integer> array2 = new MyArray<Integer>(4, 6, 7, 88, 12, 3);
        MyArray<Cat> array3 = new MyArray<>(arrr);

        System.out.println(array1);
        System.out.println(array2);
        System.out.println(array3);

        array1.changePlace(0, 4);
        array2.changePlace(2, 0);
        array3.changePlace(1, 3);

        System.out.println(array1);
        System.out.println(array2);
        System.out.println(array3);

        ArrayList arrayList = array1.toArrayList();
        System.out.println(arrayList.getClass());


    }
}
