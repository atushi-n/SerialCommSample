import com.sun.jdi.Bootstrap;
import com.fazecast.jSerialComm.*;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {

        //シリアルポートの一覧を表示
        int i = 0;
        for (SerialPort serialPort : SerialPort.getCommPorts()) {
            System.out.println(i++ + ": " + serialPort);//SerialPortクラスのtoString()メソッドを確認
            //SerialPort.getCommPort(serialPort.getPortDescription());//Linuxでは適切な権限をもたせる必要がある
        }
        System.out.println("\n利用するポートを選択してください");

        Scanner scanner = new Scanner(System.in);
        SerialPort serialPort = SerialPort.getCommPorts()[Integer.parseInt(scanner.next())];

        serialPort.setBaudRate(9600);


        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                try {
                    int evt = event.getEventType();

                    System.out.println("Event " + evt + " received");

                    if (evt == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {

                        int bytesToRead = serialPort.bytesAvailable();

                        System.out.println(bytesToRead + " byte(s) available.");
                        byte[] newData = new byte[bytesToRead];
                        serialPort.readBytes(newData, bytesToRead);
                        String s = new String(newData, "UTF8");
                        System.out.println("Received [" + s + "]");

                    }

                } catch (Exception ex) {
                    System.err.println(ex.getMessage());

                }
            }
        });

        System.out.println("送るデータを入力してください（qで抜ける）");
        String  sendWord = scanner.next() + "\r\n";
        while (!sendWord.equals("q")){
            System.out.println(sendWord.getBytes()[0]);
            sendByte(serialPort, sendWord.getBytes());
            System.out.println("送るデータを入力してください（qで抜ける）");
            sendWord = scanner.next();

        }


    }

    static void sendByte(SerialPort serialPort, byte b[]) {

        serialPort.writeBytes(b, b.length);
    }


}



