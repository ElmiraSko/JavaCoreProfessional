import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int PORT = 8189;
    private ConnectBase connectBase;  // ссылка на объект ConnectBase
    private List<ClientHandler> clients; //clients- ссылка на список(ArrayList) будущих клиентов
// объект ConnectBase позволит получить соединение с бд, содержит "методы-запросы"
    public ConnectBase getConnectBase() {
        return connectBase;
    } // возвращает ссылку на объект, у которого есть соединение с бд

    public Server() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            connectBase = new ConnectBase(); // получили соединение с базой
            clients = new ArrayList<>(); //создается список clients, для хранения объектов типа ClientHandler
// заходим в цикл ожидания клиентов
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept(); // подключенного клиента отдаем ClientHandler-у
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket); //создали держателя ClientHandler
            }
        } catch (IOException e) {
            System.out.println("Ошибка в работе сервера");
        } finally {
            if (connectBase != null) { //здесь надо закрыть базу
                connectBase.close();
            }
        }
    }
    //проверяем, есть ли среди подключившихся клиентов этот ник
    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }
    //отправка сообщения всем клиентам подряд
    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }
    //отправка сообщения (msg) конкретному клиенту с именем forClient от from
    public synchronized void sendOnly(String msg, String forClient, String from){
        for (ClientHandler o : clients) {
            if (o.getName().equals(forClient)){
                o.sendMsg("От " + from + ": " + msg);
            }
        }

    }

    public synchronized void unsubscribe(ClientHandler o) { //удаление клиента
        clients.remove(o);
    }

    public synchronized void subscribe(ClientHandler o) { //добавление клиента
        clients.add(o);
    }
}