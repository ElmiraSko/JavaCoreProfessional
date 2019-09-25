package Task3;

public class TestClass {
    public static void main(String[] args) {
        Box<Apple> boxApple_1 = new Box<>(); // 1-я коробка для яблок
        Box<Apple> boxApple_2 = new Box<>(); // 2-я коробка для яблок
        Box<Orange> boxOrange = new Box<>(); // 1-я коробка для апельсинов

        Apple[] apples = new Apple[9];  // создали массив из 9 яблок
        for (int i = 0; i < apples.length; i++){
            apples[i] = new Apple();
            boxApple_1.setToBox(apples[i]);//тут же помещаем яблоки в 1-ю коробку
        }
        Orange[] oranges = new Orange[6];   //создали массив из 6 апельсинов
        for (int i = 0; i < oranges.length; i++){
            oranges[i] = new Orange();
            boxOrange.setToBox(oranges[i]); //тут же помещаем арельсины в коробку
        }

        float f = boxApple_1.getWeight(); //находим вес 1-й коробки с яблоками
        float f2 = boxOrange.getWeight();
        System.out.println(f);
        System.out.println(f2);

        System.out.println(boxApple_1.boxCompare(boxOrange)); // сравниваем коробки

        boxApple_1.putOnNewBox(boxApple_2); // пересыпали яблоки из boxApple_1 в boxApple_2
        System.out.println(boxApple_2.getWeight()); // проверяем вес boxApple_2
    }
}
