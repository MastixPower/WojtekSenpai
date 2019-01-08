import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(10000);
        while(true) {
            System.out.println("odbyt");
            Socket connectionSocket=null;
            try {
                connectionSocket= welcomeSocket.accept();
                System.out.println("Połączenie zaakceptowane");
            }catch (Exception ex){
                ex.printStackTrace();
                System.out.println("Problem z połączeniem z klientem");
            }
            BufferedReader inFromClient=new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            String request=inFromClient.readLine();
            String splitRequest[]= request.split(" ");
            String requestType= splitRequest[0];
            switch (requestType) {
                case RequestTypes.GET_FILE:
                {
                    String fileName= splitRequest[1];
                    sendFile(connectionSocket, fileName);
                    inFromClient.close();
                    break;
                }
                case RequestTypes.PUSH_FILE:
                {
                    String fileName= splitRequest[1];
                    String directory="";
                    FileOutputStream fileOutputStream=new FileOutputStream(directory+fileName);
                    getFile(connectionSocket,fileOutputStream);
                    fileOutputStream.close();
                    break;
                }
                case RequestTypes.SEND_FILE_INFORMATION:
                {
                    break;
                }
                case RequestTypes.GET_LIST_OF_FILES:
                {
                    break;
                }
                default: break;
            }
            System.out.println("Operacja została wykonana");
            connectionSocket.close();
        }
    }
    private static void sendFile(Socket connectionSocket, String directory){
        DataOutputStream outToClient=null;
        try {
            outToClient= new DataOutputStream(connectionSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File file = new File(directory);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer= new byte[4096];
            while (fileInputStream.read(buffer) != -1) {
                if (outToClient != null) {
                    outToClient.write(buffer);
                }
            }
            fileInputStream.close();
        } catch (NullPointerException | IOException error) {
            error.printStackTrace();
        }
    }

    static private void getFile(Socket connectionSocket, FileOutputStream fileOutputStream) throws Exception{
        DataOutputStream outToServer=new DataOutputStream(connectionSocket.getOutputStream());
        BufferedInputStream inFromServer = new BufferedInputStream(connectionSocket.getInputStream());
        byte buffer[] = new byte[4096];
        try {
            while (inFromServer.read(buffer) != -1) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }

        inFromServer.close();
        fileOutputStream.close();
        outToServer.close();
    }
}