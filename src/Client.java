import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {
    private String SERVER_ADDR = "localhost";
    private int SERVER_PORT = 8189;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private boolean flag_exit = false;
    private boolean conect = false;
    private String myNick;
    private StringBuilder fileName = new StringBuilder("C:\\Users\\Admin\\Documents\\Elmira Studying\\Github_2019\\JavaCoreProfessional\\files\\history_");
    private File file;
    private String[] stringsArr = new String[10];

    private JPanel panelButton;
    private JButton btnEnter, btnAuth, btnRegistration, btnReg ;
    JLabel label_login, label_pass, label_nick;
    private JTextField msgInputField, login, password, nick;
    private JTextArea chatArea;
    AuthWindow wind = null;

    public Client() {
        try {
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        prepareGUI();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }
    //=================================================================
    public void openConnection() throws IOException {
        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {       // цикл авторизации
                            String strFromServer;
                            if (!(strFromServer = in.readUTF()).trim().isEmpty()) {
                                System.out.println(strFromServer + " - проверка");
                                if (!strFromServer.startsWith("/time")) {
                                    if (strFromServer.startsWith("/authok")) { // если авторизовались
                                        myNick = strFromServer.split("\\s")[1];//клиент получил свой ник
                                        if (myNick != null) {
                                            System.out.println("Ник получен");
                                            msgInputField.setEditable(true);
                                            msgInputField.setBackground(Color.YELLOW);
                                            conect = true;
                                            flag_exit = true;
                                            btnAuth.setEnabled(false);
                                            btnRegistration.setText("Сменить ник");
                                            fileName.append(myNick);
                                            fileName.append(".txt");
                                            file = new File(fileName.toString());
                                            if(file.createNewFile()){
                                                System.out.println("Файл создан");
                                            }else System.out.println("Файл уже существует");
// Создала массив stringsArr пока для 10 элементов. В блоке авторизации в этот массив считываем построчно из файла.
// Затем в этот массив будут добавляться новые записи во время общения в чате. В конце сессии содержимое массива будет записываться опять в файл.
// Таким образом в файле хранится последние 10 записей. Есть проблема: одна запись почему-то дублируется.
                                            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                                                String strFor;
                                                while ((strFor = reader.readLine()) != null) { //считали строку из файла, если есть
                                                chatArea.append(strFor); // выводим эту строку в чат
                                                chatArea.append("\n");
                                                }
                                            }catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        break;
                                    }
                                    JOptionPane.showMessageDialog(wind, strFromServer);
                                }else {
                                    btnReg.setEnabled(true);
                                    btnAuth.setEnabled(false);
                                    flag_exit = true;
                                    btnAuth.setEnabled(false);
                                    if (wind != null){
                                        wind.dispose();
                                    }
                                    JOptionPane.showMessageDialog(wind, "Время авторизации истекло.");
                                    closeConnection();
                                    break;
                                }
                            }
                        }
                        while (conect) { // если авторизовались, начинаем общение в чате
                            String strFromServer;
                            if (!(strFromServer = in.readUTF()).trim().isEmpty()) {
                                if (strFromServer.equalsIgnoreCase("/end")) {
                                    System.out.println(strFromServer);
                                    flag_exit = true; //использую при закрытии окна
                                    break;
                                }
                                chatArea.append(strFromServer);
                                chatArea.append("\n");
// Используем массив stringsArr  пока для 10 элементов.
                                if (stringsArr[stringsArr.length-1]!= null){ // если последний элемент в массиве не null
                                    for (int i = 0; i < stringsArr.length-1; i++) { // передвигаем элементы массива на одну позицию
                                        stringsArr[i] = stringsArr[i+1];
                                    }
                                    stringsArr[stringsArr.length-1] = strFromServer;
                                }else{
                                    for (int i = 0; i < stringsArr.length; i++) {
                                        if (stringsArr[i] == null) {
                                            stringsArr[i] = strFromServer;
                                            break;
                                        }
                                    }
                                }
                            }
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                                for (String s : stringsArr){
                                    if (s != null){
                                        writer.write( s + "\n");
                                        System.out.println(s);    // проверила, что записалось в файл
                                    }
                                }
                            }catch (IOException ex){
                                ex.printStackTrace();
                            }
                        }

                    } catch (EOFException ex){System.out.println("Ошибка при чтении");}
                    catch (Exception e) {
                        System.out.println("Ошибка при закрытии окна клиента");
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }catch (Exception ee){System.out.println("Где ошибка?");}
    }
    //=====================================
    public void closeConnection() {
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

    public void sendMessage() {
        if (!msgInputField.getText().trim().isEmpty()) {
            try {
                out.writeUTF(msgInputField.getText());
                msgInputField.setText("");
                msgInputField.grabFocus();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
            }
        }
    }

    private void sendLoginAndPassword(String metaString) { // используем в слушателе кнопки "Войти"
        if (!login.getText().trim().isEmpty()) {
            if (!password.getText().trim().isEmpty()) {
                if (!password.getText().trim().isEmpty()) {
                    try{
                        String message = metaString + login.getText() + " " + password.getText() + " " + nick.getText();
                        System.out.println(message);
                        out.writeUTF(message);
                        out.flush();
                        login.setText("");
                        password.setText("");
                        nick.setText("");
                    }catch (IOException e){JOptionPane.showMessageDialog(this, "Ошибка передачи данных.");}
                    finally {
                        wind.dispose();
                    }
                }
                else {
                    try{
                        String message = metaString + login.getText() + " " + password.getText();
                        System.out.println(message);
                        out.writeUTF(message);
                        out.flush();
                        login.setText("");
                        password.setText("");
                    }catch (IOException e){JOptionPane.showMessageDialog(this, "Ошибка передачи данных.");}
                    finally {
                        wind.dispose();
                    }
                }
            } else JOptionPane.showMessageDialog(this, "Введите пароль!");
        } else JOptionPane.showMessageDialog(this, "Введите логин!");
    }
//вспомогательное окно для авторизации, регистрации нового пользователя и для подключения к серверу
    class AuthWindow extends JFrame{
        AuthWindow(){
            super("Авторизация");
            panelButton = new JPanel();
            panelButton.setLayout(new GridLayout(7,1));
            panelButton.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
            add(panelButton);
            label_login = new JLabel("Введите логин:");
            label_pass = new JLabel("Введите пароль:");
            label_nick = new JLabel("");
            label_nick.setEnabled(false);
            login = new JTextField(11);
            password = new JTextField(11);
            nick = new JTextField(11);
            nick.setEditable(false);
            btnEnter = new JButton("Войти");
            panelButton.add(label_login);
            panelButton.add(login);
            panelButton.add(label_pass);
            panelButton.add(password);
            panelButton.add(label_nick);
            panelButton.add(nick);
            panelButton.add(btnEnter);
            btnEnter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals("Войти")){sendLoginAndPassword("/auth ");}
                    if (e.getActionCommand().equals("Зарегистрироваться")){sendLoginAndPassword("/reg ");}
                    if (e.getActionCommand().equals("Сменить ник")){sendLoginAndPassword("/update ");}
                    if (e.getActionCommand().equals("Подключиться к серверу")){
                        if(!login.getText().equals("") && !password.getText().equals("")) {
                            SERVER_ADDR = login.getText();
                            SERVER_PORT = Integer.parseInt(password.getText());
                        }
                        try {
                            // подключаемся к серверу
                            socket = new Socket(SERVER_ADDR, SERVER_PORT);
                            in = new DataInputStream(socket.getInputStream());
                            out = new DataOutputStream(socket.getOutputStream());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }finally {
                            btnAuth.setEnabled(true);
                            wind.dispose();
                        }
                    }
                }
            });
            setBounds(650, 250 , 400, 200);
            setResizable(false);
            setVisible(true);
        }
    }
    //==== главное окно чата
    public void prepareGUI() {
        setBounds(600, 150, 500, 500);
        setTitle("Клиент");
        // Текстовое поле для вывода сообщений
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
//Верхняя панель для ввода логина, пароля и ника
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        btnRegistration = new JButton("Регистрация");
        btnRegistration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Регистрация")){
                    wind = new AuthWindow();
                    wind.setTitle("Регистрация");
                    label_nick.setEnabled(true);
                    label_nick.setText("Введите ник:");
                    nick.setEditable(true);
                    btnEnter.setText("Зарегистрироваться");}
                if (e.getActionCommand().equals("Сменить ник")){
                    wind = new AuthWindow();
                    wind.setTitle("Смена ника пользователя");
                    label_nick.setEnabled(true);
                    label_nick.setText("Введите новый ник:");
                    nick.setEditable(true);
                    btnEnter.setText("Сменить ник");
                }
            }
        });
        btnAuth = new JButton("Войти в чат");
        btnAuth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wind = new AuthWindow();
            }
        });
        btnReg = new JButton("Подключиться");
        btnReg.setEnabled(false);
        btnReg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (socket.isConnected()){
                    wind = new AuthWindow();
                    btnEnter.setText("Подключиться к серверу");
                    label_login.setText("SERVER_HOST (localhost)");
                    label_pass.setText("SERVER_PORT (8189)");
                }
            }
        });


        //=====================

        topPanel.add(btnAuth);
        topPanel.add(btnRegistration);
        topPanel.add(btnReg);
        // Нижняя панель с полем для ввода сообщений и кнопкой отправки сообщений
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        JButton btnSendMsg = new JButton("Отправить");
        bottomPanel.add(btnSendMsg, BorderLayout.EAST);
        msgInputField = new JTextField();
        msgInputField.setEditable(false);
        add(bottomPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        bottomPanel.add(msgInputField, BorderLayout.CENTER);
        btnSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        msgInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Настраиваем действие на закрытие окна
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (flag_exit) {
                    try {
                        out.writeUTF("/end");
                        out.flush();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                    System.exit(0);
                } else {new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            out.writeUTF("/end");
                            out.flush();
                        } catch (IOException exc) {
                            exc.printStackTrace();
                        }
                    }
                }).start();
                    System.exit(0);
                }
            }
        });
        setResizable(false);
        setVisible(true);
    }
}