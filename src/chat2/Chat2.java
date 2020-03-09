package chat2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Chat2 extends Thread {
    
    private Socket clientSocket;
    public static ArrayList<Cliente> clientes = new ArrayList<>();
    
    public Chat2(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
    public static class Cliente extends Thread {
        InputStream is;
        OutputStream os;
        String nick;

        public Cliente(InputStream is, OutputStream os, String nick) {
            this.is = is;
            this.os = os;
            this.nick = nick;
        }
        
        @Override
        public void run() {
            try {
                String mensaje = nick + " se ha conectado";
                for (Cliente c : Chat2.clientes) {
                    c.os.write(mensaje.getBytes());
                }
            
                do {
                    String envio = "";
                    byte[] msg2 = new byte[100];
                    is.read(msg2);
                    mensaje = new String(msg2);
                    if(mensaje.contains("/bye")){
                        System.out.println(mensaje);
                        String salir = nick+" abandonó este chat";
                        for (Cliente c : Chat2.clientes) {
                            c.os.write(salir.getBytes());
                        }
                    }
                    else{
                        envio = nick + ": " + mensaje;
                        System.out.println(envio);
                        for (Cliente c : Chat2.clientes) {
                            c.os.write(envio.getBytes());
                        }
                    }
                }while (mensaje.contains("/bye")==false);

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Conexión terminada");
        }
    }

    public static void main(String[] args) {
        int contador = 0;
        System.out.println("Creando socket servidor");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            System.out.println("Realizando el bind");
            InetSocketAddress addr = new InetSocketAddress("192.168.0.1", 5555);
            serverSocket.bind(addr);
        } catch (IOException e){
            e.printStackTrace();
        }
        
        System.out.println("Aceptando conexiones");
        
        while (contador < 3) {
            try {
                Socket newSocket = serverSocket.accept();
                System.out.println("Conexión recibida");
                
                InputStream is = newSocket.getInputStream();
                OutputStream os = newSocket.getOutputStream();
            
                byte[] msg1 = new byte[50];
                is.read(msg1);
                String nick = new String(msg1);
                System.out.println(nick);
                Cliente hilo = new Cliente(is, os, nick);
                clientes.add(hilo);
                hilo.start();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    
}
