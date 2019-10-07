public class Threads_1 {

    Runnable printA, printB, printC;
    private void go(){
        Object lock = new Object();
        printA = () -> {
            synchronized (lock){
                for(int i = 0; i < 5; i++){
                    System.out.print("A");
                    try {
                        Thread.sleep(1000);
                        lock.notifyAll(); //продолжает работу потока, у которого ранее был вызван метод wait()
                        lock.wait(); // освободили монитор
                    }catch (InterruptedException ex){ex.printStackTrace();}
                }
            }

        };
        printB = () -> {
            synchronized (lock){
                for(int i = 0; i < 5; i++){
                    System.out.print("B");
                    try {
                        Thread.sleep(1000);
                        lock.notifyAll();
                        lock.wait();

                    }catch (InterruptedException ex){ex.printStackTrace();}
                }
            }

        };
        printC = () -> {
            synchronized (lock){
                for(int i = 0; i < 5; i++){
                    try{
                        lock.wait();
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                    System.out.print("C");
                    try {
                        Thread.sleep(1000);
                        lock.notifyAll();
                        lock.wait();
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
            }
        };
        new Thread(printA).start();
        new Thread(printB).start();
        new Thread(printC).start();

    }
    public static void main(String[] args) {
        new Threads_1().go();
    }
}