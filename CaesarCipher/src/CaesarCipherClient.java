import javax.management.ConstructorParameters;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

// Julia Olmstead
//B00780639
//set the key
//use the key to encrypt
//send the encrypted message
public class CaesarCipherClient {
    public static void main(String[] args) throws IOException {
        Socket link = null; //create socket object and set to null
        PrintWriter output = null; //create PrintWriter object and set to null
        BufferedReader input = null; //create BufferedReader obj and set to null

        int key = 0; //not set yet as it will be set by the server.

        try {
            //connect to server and instantiate the socket
            link = new Socket("127.0.0.1", 50000);
            //open up IO streams and instantiate the output and input streams
            output = new PrintWriter(link.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(link.getInputStream()));
        } catch (UnknownHostException e) {// ip address of host is undetermined
            System.out.println("Unknown Host");
            System.exit(1); //terminate java vm
        } catch (IOException e) { //could not connect to end point (server)
            System.out.println("Cannot connect to host");
            System.exit(1);// terminate java vm
        }

        //get sockets input stream and create/open a buffer reader
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String usrInput;
        String encryptedText;
        char letter; //to use for the letters of the message

        while ((usrInput = stdIn.readLine()) != null) { //while there is more to be said
            encryptedText = "";
            if (26 > key && key > 0) { //yes, a key is set, start encrypting the messages to be sent
                for (int i = 0; i < usrInput.length(); i++) {
                    //set the letter to position i
                    letter = usrInput.charAt(i);
                    if (Character.isLowerCase(letter)) { //yes, the letter is lowercase.
                        // key for letter encryption, letter, lowercase z and lower case a params
                        letter = getEncryptedLetter(key, letter, 'z', 'a');
                        encryptedText = encryptedText + letter; //append the letter to the encrypted string

                    } else if (Character.isUpperCase(letter)) { //yes, the letter is uppercase
                        // key for letter encryption, letter, Uppercase Z and uppercase A
                        letter = getEncryptedLetter(key, letter, 'Z', 'A');
                        encryptedText = encryptedText + letter;//append the letter to the encrypted string
                    }
                }
                usrInput = encryptedText;
            }
            output.println(usrInput); //send over socket to server


            if(key==0){
                String inputLine = input.readLine();
                System.out.println("echo: " + inputLine); //print out the server response only if the key isn't set
                if (isANumber(inputLine)){
                    key = Integer.parseInt(inputLine);
                }
            }
        }
        //close io streams then socket
        output.close();
        input.close();
        stdIn.close();
        link.close(); //socket
    }
    /*
       @params: int key for encryption
       Character - letter to be encrypted
       Charcater z = tells if it should be Uppercase or Lower Case end of Alphabet
       Charcter a = tells if it should be a upper case or lowercase start of alphabet
       idea from: https://javahungry.blogspot.com/2019/02/caesar-cipher-program-in-java.html
        */
    private static char getEncryptedLetter(int key, char letter, char z, char a) {
        letter = (char) (letter + key); //cast to character
        // wraparound from z
        if (letter > z) {
            // move to the start
            letter = (char) (letter + a - z - 1);
        }
        return letter;
    }

    private static boolean isANumber(String usrInput) {
        try {
            int intValue = Integer.parseInt(usrInput);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
