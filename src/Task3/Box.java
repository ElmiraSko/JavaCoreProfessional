package Task3;

import java.util.ArrayList;

class Box<T extends Fruit> {
    private ArrayList<T> list;
    Box(){
        list = new ArrayList<>(100);
    }
    void setToBox(T fruit){  // добавляет по одному фрукту в коробку
        list.add(fruit);
    }
    // метод для получения веса коробки
    float getWeight(){  // возвращает вес коробки с фруктами
        return (list.size() > 0) ? list.size()* list.get(0).getWeight() : 0f;
    }
    // метод для сравнения двух коробок
    boolean boxCompare(Box box){  //метод для сравнения двух коробок
        return this.getWeight() == box.getWeight();
    }
    // метод пересыпает фрукты из одной коробки в другую
    void putOnNewBox(Box<T> box){
        box.list.addAll(this.list);
        this.list.clear();
    }
}
