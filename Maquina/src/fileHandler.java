package Maquina;

import java.net.*;
import java.io.*;



class fileHandler implements Runnable{
    private String[] parts = null;
    private String filename = null;
    private Socket socket = null;
    private DataOutputStream out     = null;
    private static String wait = "Please use command (ls, get, put, delete, quit):";
    private File currentDir = new File("./Maquina");

    public fileHandler(Socket clientSocket){
        this.socket = clientSocket;
    }

    public void run(){
        try {
            InputStream input  = socket.getInputStream();
            DataInputStream in = new DataInputStream(new BufferedInputStream(input));
            OutputStream output = socket.getOutputStream();
            File outfile = null;
            String line = in.readUTF();
            System.out.println(line);
            String block;
            int bytesRead;
            int current = 0;

            InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
            String address = isa.getAddress().getHostAddress();

            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("connection established");
            line = in.readUTF();
            System.out.println(line);

            line = in.readUTF();
            parts = line.split(" ");
            while ( !parts[0].equals("quit")){
                switch (parts[0]){
                    case "put":
                        outfile = new File("./Maquina/" + parts[1]);
                        FileWriter fileWriter = new FileWriter(outfile);
                        fileWriter.write(in.readUTF());
                        fileWriter.flush();
                        fileWriter.close();
                        out.writeUTF("archivo subido 1");
                        break;
                    case "get":
                        FileReader myFile = new FileReader("./Maquina/"+parts[1]);
                        char[] s = new char[65532];
                        int u =myFile.read(s);
                        String ss =new String(s);
                        out.writeUTF(ss);
                        myFile.close();
                        break;
                    case "delete":
                        File delfile = new File("./Maquina/"+parts[1]);
                        if(delfile.delete()){
                            out.writeUTF("Archivo "+ parts[1]+" eliminado");
                        }else out.writeUTF("Archivo "+ parts[1]+" no existe o no se pudo eliminar");
                        break;
                    default:
                        out.writeUTF(wait);

                }
                line = in.readUTF();
                parts = line.split(" ");
            }
            output.close();
            input.close();
            System.out.println("Request processed");
            Maquina.threadlisto(); //Quito uno del contador de threads del servidor
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}

