package hw4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Part1 {

	public static void main(String[] args) throws Exception {

		String id = args[0];
		String message;
		Random r;
		SecretKeySpec skey = null;
		SecretKeySpec readKey = null;
		String hash = null;
		

		if(id.equalsIgnoreCase("A")){
			//check if key is generated, if not create it
			if(!new File("key.key").isFile()){
				byte[] secret = new byte[16];
				new Random().nextBytes(secret);
				skey = new SecretKeySpec(secret, "HmacSHA256");
				Path path = Paths.get("key.key");
				Files.write(path, skey.getEncoded());
			}
			message = args[1];
			byte[] keyBytes;
			Path path = Paths.get("key.key");
			keyBytes = Files.readAllBytes(path);
			readKey = new SecretKeySpec(keyBytes, "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(readKey);
			hash = Base64.encodeBase64String(mac.doFinal(message.getBytes()));
			System.out.println("Alice HMAC: " + hash);
			FileWriter writer = new FileWriter("mactext.txt", false);
			writer.write(message);
			writer.write("\r\n");
			writer.write(hash);
			writer.close();
		}
		
		if(id.equalsIgnoreCase("B")){
			String line = null;
			String fMessage = null;
			String fKey = null;
			int i = 0; 
			FileReader fr = new FileReader("mactext.txt");
			BufferedReader br = new BufferedReader(fr);
			while((line = br.readLine()) != null){
				if(i == 0){
					fMessage = line;
					i++;
				}
				else
					fKey = line;			
			}
			byte[] keyBytes;
			Path path = Paths.get("key.key");
			keyBytes = Files.readAllBytes(path);
			readKey = new SecretKeySpec(keyBytes, "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(readKey);
			hash = Base64.encodeBase64String(mac.doFinal(fMessage.getBytes()));
			System.out.println("Bob HMAC: " + hash);
			if(hash.equals(fKey))
				System.out.println("Verification successful");
			else
				System.out.println("Verification unsuccessful");

		}
		
	}

}
