import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.io.*;
import java.util.Random;

// Julia Olmstead
//B00780639

//server gotta decrypt
//summon the random key #
// keep our overlords entertained
public class CaesarCipherServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSock = null; //create Server Socket and set to null
        boolean askedForKey = false;
        Random rand = new Random(); //create instance of random class
        int lowerBound = 1;
        int upperbound = 27;
        //generate random values from 0-26
        int key = rand.nextInt(lowerBound,upperbound);

        try{
            serverSock = new ServerSocket(50000); //instantiate socket
        } //handle exceptions
        catch (IOException ie){ //could not connect on port 50000
            System.out.println("Can't listen on 50000");
            System.exit(1);
        }
        Socket link = null; //Create client socket and set to null
        //status update
        System.out.println("Listening for connection ...");

        //look for connection
        try {
            link = serverSock.accept(); //looks for connection to be made to socket and immediately accepts it
        } //if cannot accept connection
        catch (IOException ie){
            System.out.println("Accept failed"); //print error msg
            System.exit(1); //terminate vm
        } //if connection is established
        System.out.println("Connection successful");
        System.out.println("Listening for input ...");
        //use clients socket to create PrinterWriter Object to reply to the client
        PrintWriter output = new PrintWriter(link.getOutputStream(), true);
        //use client socket to create a BufferedReader object to read the message from the client
        BufferedReader input = new BufferedReader(new InputStreamReader(link.getInputStream()));
        String inputLine;
        StringBuilder decryptMessage = new StringBuilder();

        //read one line at a time from the client
        while ((inputLine = input.readLine()) != null){
            decryptMessage = new StringBuilder().replace(0,decryptMessage.length(),"");
            System.out.println("Message from client: " + inputLine); //displayed on server side
            int length = inputLine.length();
            //System.out.println(length); //for testing purposes

            if(!askedForKey){
                if(inputLine.equals("Please send the key")){
                    output.println(key);
                    askedForKey = true;
                }
            } else { //yes, the key has been asked for, start decrypting
                decryptMessage = new StringBuilder().replace(0,decryptMessage.length(),"");

                for(int i=0; i < length; i++) {

                    char letter = inputLine.charAt(i);

                    if(Character.isLowerCase(letter)){ //yes, is lower case
                        letter = getDecryptedLetter(key, letter, 'a', 'z');
                        decryptMessage.append(letter);
                    } else if(Character.isUpperCase(letter)){ // yes is uppercase
                        letter = getDecryptedLetter(key, letter, 'A', 'Z');
                        decryptMessage.append(letter);
                    }else if (Character.isSpaceChar(letter)){ //you dont work why, cry TT^TT
                        decryptMessage.append(" ");
                    }
                }
                System.out.println("Decrypted: " + decryptMessage.toString());
            }
            output.println(inputLine); //sent to the client through output
            if(inputLine.equals("Bye")){ //end it
                break;
            }
        }
        //close IO Streams and then socket
        output.close();
        input.close();
        link.close(); //client socket
        serverSock.close(); //server socket
    }

    private static char getDecryptedLetter(int key, char letter, char a, char z) {
        // shift letter
        letter = (char) (letter - key);
        // shift letter lesser than 'a'
        if (letter < a) {
            //reshift to starting position
            letter = (char) (letter - a + z + 1);
        }
        return letter;
    }
}

