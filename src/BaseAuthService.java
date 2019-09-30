import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    //==========================================================================================
    private class Entry { //Объект - логин, пароль, ник
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }
    //====================================================================================
    private List<Entry> entries; //список объектов являющихся "логин-пароль-ник"
    public BaseAuthService() {  // конструктор, создает список объектов "паролей" и дабавляет три пароля
        entries = new ArrayList<>();
        entries.add(new Entry("login1", "pass1", "nick1"));
        entries.add(new Entry("login2", "pass2", "nick2"));
        entries.add(new Entry("login3", "pass3", "nick3"));
    }

    // три метода интерфейса:
    @Override
    public void start() { // вывод на консоль сообщения
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() { // вывод на консоль сообщения
        System.out.println("Сервис аутентификации остановлен");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) { // получение ника по логину и паролю
        for (Entry o : entries) {
            if (o.login.equals(login) && o.pass.equals(pass)) return o.nick;
        }
        return null;
    }
}