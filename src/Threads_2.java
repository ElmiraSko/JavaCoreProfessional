public class Threads_2 {
        Runnable printA, printB, printC;
        void go(){
            Object lock = new Object();
            printA = () -> {
                synchronized (lock){
                    for(int i = 0; i < 5; i++){
                        int count_A = 0; // вспомогательный счетчик
                        while (count_A < 2){
                            System.out.print("A");
                            count_A++;
                        }
                        try {
                            Thread.sleep(1000);
                            lock.notifyAll(); //продолжает работу поток, у которого ранее был вызван метод wait()
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
                        int count_C = 0;
                        try{
                            lock.wait();
                        }catch (InterruptedException ex){
                            ex.printStackTrace();
                        }
                        while (count_C < 2){
                            System.out.print("C");
                            count_C++;
                        }
                        try {
                            Thread.sleep(1000);
                            lock.notifyAll();
                            if (i < 4){
                                lock.wait();
                            }
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
        new Threads_2().go();
    }
}
