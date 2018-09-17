import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Part3 {

	public static void main(String[] args) {
		
		
		
		int keySize = 1024;
		for(int j = 1; j < 4; j++){

			double startTime = System.currentTimeMillis();
				for(int i = 0; i < 100; i++){
					RSA runRSA = new RSA((keySize));
					runRSA.encrypt(args[0].getBytes());
					runRSA.decrypt(runRSA.encrypted);
				}
				double duration = System.currentTimeMillis() - startTime;
				System.out.println("---RSA with "+ (keySize)+" bit key---");
				System.out.println("Total Milliseconds:  "+duration + " \nTotal Seconds: " + (duration/1000.00));
				System.out.println("Average milliseconds over 100 iterations: " + (duration/100) + "\nAverage seconds over 100 iterations: " + ((duration/1000.00)/100)+"\n");
				keySize *= 2;
		}
		keySize = 16;
		for(int j = 1; j < 4; j++){

			double startTime = System.currentTimeMillis();
			for(int i = 0; i < 100; i++){
				AES runAES = new AES(keySize);
				runAES.encrypt(args[0].getBytes());
				runAES.decrypt(runAES.encrypted);
			}
			double duration = System.currentTimeMillis() - startTime;
			System.out.println("---AES with "+ (keySize*8)+" bit key---");
			System.out.println("Total Milliseconds:  "+duration + " \nTotal Seconds: " + (duration/1000.00));
			System.out.println("Average milliseconds over 100 iterations: " + (duration/100) + "\nAverage seconds over 100 iterations: " + ((duration/1000.00)/100)+"\n");
			keySize += 8;

		}

	}
}
class AES{
	
	Key key = null;
	byte[] encrypted = null;
	byte[] decrypted = null;
	//generate keys
	AES(int amount){
		byte[] keyBytes = new byte[amount];
        try {
			SecureRandom.getInstanceStrong().nextBytes(keyBytes);
	        key = new SecretKeySpec(keyBytes, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
      		
	}
	
	void encrypt(byte[] message){
		
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, key);
			encrypted = c.doFinal(message);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void decrypt(byte[] message){
		
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, key);
			decrypted = c.doFinal(message);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		} 	
	}
	
	
}


class RSA{

		KeyPairGenerator kGenerator;
		KeyPair pair;
		Key pubKey = null;
		Key privKey = null;
		byte[] encrypted = null;
		byte[] decrypted = null;
		
		//generate keys
		RSA(int amount){
			
			try {
				kGenerator = KeyPairGenerator.getInstance("RSA");
				kGenerator.initialize(amount);
				pair = kGenerator.generateKeyPair();
				pubKey = pair.getPublic();
				privKey = pair.getPrivate();
				
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}	
		
		}
	
		void encrypt(byte[] input){
			
			try {
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.ENCRYPT_MODE, pubKey);
				encrypted = cipher.doFinal(input);
				
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			}
			
		}
		
		void decrypt(byte[] input){
			
			try {
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.DECRYPT_MODE, privKey);
				decrypted = cipher.doFinal(input);
				
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			}
		}
	
}
