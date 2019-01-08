import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Client {
private static final int portNumber=10000;
    public static void main(String[] args) throws Exception {
        while (true) {

            Scanner scanner = new Scanner(System.in);
            String request = scanner.nextLine();
            String splitRequest[] = request.split(" ");
            String requestType = splitRequest[0];
            String directory = "D:/TORrent_1/";

            switch (requestType) {
                case RequestTypes.GET_FILE: {
                    String fileName =splitRequest[1];
                    FileOutputStream fileOutputStream=new FileOutputStream(directory+fileName);
                    getFile(portNumber, directory, fileName, fileOutputStream);
                    fileOutputStream.close();
                    break;
                }
                case RequestTypes.PUSH_FILE: {
                    String fileName =splitRequest[1];
                    sendFile(portNumber, fileName,directory);
                    break;
                }
                case RequestTypes.SEND_FILE_INFORMATION: {
                    System.out.println("Tutaj będzie wysyłanie informacji o plikach");
                    break;
                }
                case RequestTypes.GET_LIST_OF_FILES: {
                    System.out.println("Tutaj będzie wypisana lista plików");
                    break;
                }
                default:
                    break;
            }
            System.out.println("Operacja została wykonana");
        }
    }


    static private void getFile(int port, String directory, String fileName, FileOutputStream fileOutputStream) throws Exception{
        Socket clientSocket = new Socket("localhost", port);
        DataOutputStream outToServer=new DataOutputStream(clientSocket.getOutputStream());

        outToServer.writeBytes(RequestTypes.GET_FILE+" "+fileName+'\n');
        BufferedInputStream inFromServer = new BufferedInputStream(clientSocket.getInputStream());
        int i=0;
        byte buffer[] = new byte[4096];
        try {
            while (inFromServer.read(buffer) != -1) {
                fileOutputStream.write(buffer);
                System.out.println(i);
                i++;
            }
        } catch (Exception error) {
            error.printStackTrace();
        }

            inFromServer.close();
            clientSocket.close();
            fileOutputStream.close();
            outToServer.close();
    }
    private static void sendFile(int port, String fileName,String directory) throws Exception{
        Socket clientSocket = new Socket("localhost", port);
        DataOutputStream outToServer=new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeBytes(RequestTypes.PUSH_FILE+" "+fileName+'\n');
        BufferedReader inFromServer= null;
        inFromServer=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        DataOutputStream outToClient=null;
        try {
            outToClient= new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File file = new File(directory+fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer= new byte[4096];
            while (fileInputStream.read(buffer) != -1) {
                if (outToClient != null) {
                    outToClient.write(buffer);
                }
            }
            fileInputStream.close();
        } catch (NullPointerException error) {
            error.printStackTrace();
        }
        inFromServer.close();
    }
}