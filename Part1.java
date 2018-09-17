import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javax.crypto.spec.SecretKeySpec;

public class Part1 {

    public static void main(String[] args) throws Exception {
    	   
    	boolean Alice = true;
    	boolean Bob = true;
    	if(args[0].equalsIgnoreCase("a")){
    		Bob = false;
    	}
    	else
    		Alice = false;
    	
    	if(Alice){
    		GenerateKey gen = new GenerateKey();
    		gen.genKey();
    		gen.saveKey();
    		Key k = gen.getKey();
    		Encrypt e = new Encrypt();
    		e.encrypt(args[1], k);
    		e.saveToFile();
    		String s = new String(e.encrypted);
    		System.out.println("Alice's message to file: " + s);
    	}
    	if(Bob){

    		Decrypt d = new Decrypt();
    		d.getMessage();
    		String s = new String(d.message);
    		System.out.println("Alice's ciphertext: "+ s);
    		GenerateKey g = new GenerateKey();
    		d.decrypt(g.getKey());
    		s = new String(d.getDecrypted());
    		System.out.println("Decrypted: " +s);
    	}

    }
}

class Decrypt{
	byte[] message;
	byte[] decrypted;
	void getMessage(){
		File path = new File("ctext.txt");
		try {
			message = Files.readAllBytes(path.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void decrypt(Key key){
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, key);
			decrypted = c.doFinal(message);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		} 
	}
	
	byte[] getDecrypted(){
		return decrypted;
	}
}

class Encrypt{
	
	byte[] encrypted;
	
	void encrypt(String message, Key key) throws Exception {
    	
		   Cipher c = Cipher.getInstance("AES");
           c.init(Cipher.ENCRYPT_MODE, key);
           encrypted = c.doFinal(message.getBytes());
         }
	
	void saveToFile(){
		
		try{
			File path = new File("ctext.txt");
			OutputStream out = new FileOutputStream(path);
			out.write(encrypted);
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

class GenerateKey{
	
	Key key = null;
	void genKey(){
        byte[] keyBytes = new byte[24];
        try {
			SecureRandom.getInstanceStrong().nextBytes(keyBytes);
	        key = new SecretKeySpec(keyBytes, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	void saveKey(){
		
		try{
			File path = new File("key.txt");
			OutputStream out = new FileOutputStream(path);
			out.write(key.getEncoded());
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	Key getKey() throws NoSuchAlgorithmException, InvalidKeySpecException{
		byte[] holder = null;
		File path = new File("key.txt");
		if(path.exists()){
			try {
				holder = Files.readAllBytes(path.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Key s = new SecretKeySpec(holder, "AES");
		
		return s;
		}
}