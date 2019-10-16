import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean flag = false;
    private long timeStart;
    private String name;
    public String getName() {
        return name;
    }

    public ClientHandler(Server myServer, Socket socket) {
        timeStart = System.currentTimeMillis();
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    authentication();
                    readMessages();  // чтение сообщений от клиента
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    public void authentication() throws IOException {
        while ((System.currentTimeMillis()-timeStart)/1000 <= 260) {
            try {
                String str = in.readUTF(); //ожидаем текст от клиента
                System.out.println(str);
                if (str.startsWith("/auth")) { // если текст начинается с "/auth", т.е. авторизуемся, то
                    String[] parts = str.split("\\s");
                    String nick = myServer.getConnectBase().getNickByLoginPass(parts[1], parts[2]);
                    if (nick != null) { // получили ник из БД и он не пустой
                        if (!myServer.isNickBusy(nick)) { // если сейчас он не используется
                            sendMsg("/authok " + nick);//отправили клиенту сообщение, что он авторизован
                            name = nick;
                            flag=true;
                            myServer.broadcastMsg(name + " зашел в чат");
                            myServer.subscribe(this);
                            return; // вышли из цикла авторизации
                        } else {
                            sendMsg("Учетная запись уже используется");
                            flag = false;
                        }
                    } else {
                        flag=false;
                        sendMsg("Неверные логин/пароль. Введите снова или зарегистрируйтесь.");
                    }
                }
                if (str.startsWith("/reg ")) {  // если текст начинается с "/reg ", т.е. регистрируемся, то
                    String[] parts = str.split("\\s");
                    String nick = myServer.getConnectBase().registration(parts[1], parts[2], parts[3]);
                    if (nick!=null){
                    sendMsg("/authok " + nick);//отправили клиенту, что он авторизовался
                    name = nick;
                    flag=true;
                    myServer.broadcastMsg(name + " зашел в чат");
                    myServer.subscribe(this);
                    return;
                    }else{
                        flag=false;
                        sendMsg("Указанный пользователь уже существует");
                    }
                }
            } catch (IOException ex) {
                flag = false; //если клиент закрыл приложение не пройдя авторизацию
                break;
            }
        }
        if(flag){sendMsg("Авторизация прошла успешно!");}
        else{sendMsg("/time");}
    }
    //метод чтения из потока от клиента с которым связан
    public void readMessages() throws IOException {
        while (flag) {
            String strFromClient = in.readUTF();
            System.out.println("от " + name + ": " + strFromClient);
            if (strFromClient.equals("/end")){
                sendMsg("/end");
                socket.close();
                return;
            }
            if (strFromClient.startsWith("/update ")){
                String[] parts = strFromClient.split("\\s");
                name = myServer.getConnectBase().getNewNick(parts[1], parts[2], parts[3]);
            }
            if (strFromClient.startsWith("/w")){
                String[] words = strFromClient.split("\\s");
                String forName = words[1];
                String textForClient = strFromClient.substring(4 + forName.length());
                myServer.sendOnly(textForClient, forName, name);
                sendMsg("Для " + forName + ": " + textForClient);//дублируем самому отправителю
            } else
                myServer.broadcastMsg(name + ": " + strFromClient);//отправка сообщения всем клиентам через сервер
        }
    }

    public void sendMsg(String msg) { //отправляет сообщение клиенту с которым связан
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() { // закрытие соединения, удаляет ClientHandler- объект из списка
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + " вышел из чата"); //всем клиентам отправка сообщения
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}