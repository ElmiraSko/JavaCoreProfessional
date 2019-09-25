package Task_1_2;

class Cat { //временный класс, участвует в проверке
    private String name;
    public Cat(String name){
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
