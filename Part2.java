package homework;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;

public class Part2 {

	public static void main(String[] args) {
		
		//specify which user is connected A or B
		boolean a = true;
		boolean b = true;
		byte[] cipher = null;
		byte[] decrypted = null;
		SaveKeys save = null;
		boolean waiting = true;
		String user = args[0];
		if(user.equals("a") || user.equals("A"))
			b = false;
		else
			a = false;
		if(a) {
			while(waiting) {
				File tmp = new File("keys.pub");
				boolean exists = tmp.exists();
				if(exists) {
					waiting = false;
					break;
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			String toCipher = args[1];
			save = new SaveKeys();
			PrivateKey privKey = save.getPrivateKey();
			PublicKey pubKey = save.getPublicKey();
			Encrypt e = new Encrypt(privKey, pubKey);
			try {
				cipher = e.generateEncrypt(toCipher.getBytes());
				FileOutputStream outputStream = new FileOutputStream("ctext.txt");
				outputStream.write(cipher);
				outputStream.close();
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | IOException e1) {
				e1.printStackTrace();
			}

		}
		
		if(b) {
			File key = new File("keys.pub");
			boolean exists = key.exists();
			if(!exists) {
			GenerateKeys init = new GenerateKeys();
			save = new SaveKeys(init.pubKey, init.privKey);
			save.writeKey();
			}
			else
				save = new SaveKeys();
			PrivateKey privKey = save.getPrivateKey();
			PublicKey pubKey = save.getPublicKey();
			while(waiting) {
				File tmp = new File("ctext.txt");
				exists = tmp.exists();
				if(exists) {
					waiting = false;
					try {
						FileInputStream in = new FileInputStream("ctext.txt");
						cipher = in.readAllBytes();
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			Decrypt d = new Decrypt(privKey, pubKey);
			try {
				decrypted = d.generateDecrypt(cipher);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e1) {
				e1.printStackTrace();
			}
			String s = new String(decrypted);
			System.out.println(s);
		}
	}

}

class Decrypt{
	
	PrivateKey pvk;
	PublicKey pk;
		
	Decrypt(PrivateKey pvk, PublicKey pk){
		this.pvk = pvk;
		this.pk = pk;
	}
	
	byte[] generateDecrypt(byte[] input) throws NoSuchAlgorithmException, 
												NoSuchPaddingException, 
												InvalidKeyException, 
												IllegalBlockSizeException, 
												BadPaddingException {
		
		byte[] retData = null;
		Cipher cipher =  Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, this.pvk);
		retData = cipher.doFinal(input);
	
		return retData;
	}
	
}

class Encrypt{
	
	PrivateKey pvk;
	PublicKey pk;
		
	Encrypt(PrivateKey pvk, PublicKey pk){
		this.pvk = pvk;
		this.pk = pk;
	}
	
	byte[] generateEncrypt(byte[] input) throws NoSuchAlgorithmException, 
												NoSuchPaddingException, 
												InvalidKeyException, 
												IllegalBlockSizeException, 
												BadPaddingException {
		
		byte[] retData = null;
		Cipher cipher =  Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, this.pk);
		retData = cipher.doFinal(input);
	
		return retData;
	}
}

class SaveKeys{
	Key pub;
	Key priv;
	FileOutputStream out;
	
	SaveKeys(){
		
	}
	SaveKeys(Key pub, Key priv){
		
		this.pub = pub;
		this.priv = priv;
	}
	void writeKey() {
		String outfile = "keys";
		try {
			out = new FileOutputStream(outfile+".key");
			out.write(priv.getEncoded());
			out.close();
			
			out = new FileOutputStream(outfile+".pub");
			out.write(pub.getEncoded());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	PrivateKey getPrivateKey() {
		
		PrivateKey pvt = null;
		Path path = Paths.get("keys.key");
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(path);
			PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			 pvt = kf.generatePrivate(ks);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	

		return pvt;
	}
	
	PublicKey getPublicKey() {
		
		PublicKey pub = null;
		Path path = Paths.get("keys.pub");
		try {
			byte[] bytes = Files.readAllBytes(path);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
			KeyFactory keyF = KeyFactory.getInstance("RSA");
			pub = keyF.generatePublic(keySpec);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) 
			{e.printStackTrace();}
		
		return pub;
	}
}

class GenerateKeys{
	
	KeyPairGenerator kGenerator;
	KeyPair pair;
	Key pubKey;
	Key privKey;
	GenerateKeys(){
		
		try {
			kGenerator = KeyPairGenerator.getInstance("RSA");
			kGenerator.initialize(2048);
			pair = kGenerator.generateKeyPair();
			pubKey = pair.getPublic();
			privKey = pair.getPrivate();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}	
	
	}
}
/*
		String toCipher = args[1];
		GenerateKeys init = new GenerateKeys();
		SaveKeys save = new SaveKeys(init.pubKey, init.privKey);
		save.writeKey();
		PrivateKey privKey = save.getPrivateKey();
		PublicKey pubKey = save.getPublicKey();
		
		Encrypt e = new Encrypt(privKey, pubKey);
		try {
			cipher = e.generateEncrypt(toCipher.getBytes());
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e1) {
			e1.printStackTrace();
		}
		
		Decrypt d = new Decrypt(privKey, pubKey);
		try {
			decrypted = d.generateDecrypt(cipher);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e1) {
			e1.printStackTrace();
		}
		String s = new String(decrypted);
		System.out.println(s);
	}

}

class Decrypt{
	
	PrivateKey pvk;
	PublicKey pk;
		
	Decrypt(PrivateKey pvk, PublicKey pk){
		this.pvk = pvk;
		this.pk = pk;
	}
	
	byte[] generateDecrypt(byte[] input) throws NoSuchAlgorithmException, 
												NoSuchPaddingException, 
												InvalidKeyException, 
												IllegalBlockSizeException, 
												BadPaddingException {
		
		byte[] retData = null;
		
		Cipher cipher =  Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, this.pvk);
		retData = cipher.doFinal(input);
	
		return retData;
	}
	
}

class Encrypt{
	
	PrivateKey pvk;
	PublicKey pk;
		
	Encrypt(PrivateKey pvk, PublicKey pk){
		this.pvk = pvk;
		this.pk = pk;
	}
	
	byte[] generateEncrypt(byte[] input) throws NoSuchAlgorithmException, 
												NoSuchPaddingException, 
												InvalidKeyException, 
												IllegalBlockSizeException, 
												BadPaddingException {
		
		byte[] retData = null;
		
		Cipher cipher =  Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, this.pk);
		retData = cipher.doFinal(input);
	
		return retData;
	}
}

class SaveKeys{
	Key pub;
	Key priv;
	FileOutputStream out;
	
	SaveKeys(Key pub, Key priv){
		
		this.pub = pub;
		this.priv = priv;
	}
	void writeKey() {
		String outfile = "keys";
		try {
			out = new FileOutputStream(outfile+".key");
			out.write(priv.getEncoded());
			out.close();
			
			out = new FileOutputStream(outfile+".pub");
			out.write(pub.getEncoded());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	PrivateKey getPrivateKey() {
		
		PrivateKey pvt = null;
		Path path = Paths.get("keys.key");
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(path);
			PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			 pvt = kf.generatePrivate(ks);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	

		return pvt;
	}
	
	PublicKey getPublicKey() {
		
		PublicKey pub = null;
		Path path = Paths.get("keys.pub");
		try {
			byte[] bytes = Files.readAllBytes(path);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
			KeyFactory keyF = KeyFactory.getInstance("RSA");
			pub = keyF.generatePublic(keySpec);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) 
			{e.printStackTrace();}
		
		return pub;
	}
}

class GenerateKeys{
	
	KeyPairGenerator kGenerator;
	KeyPair pair;
	Key pubKey;
	Key privKey;
	GenerateKeys(){
		
		try {
			kGenerator = KeyPairGenerator.getInstance("RSA");
			kGenerator.initialize(2048);
			pair = kGenerator.generateKeyPair();
			pubKey = pair.getPublic();
			privKey = pair.getPrivate();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}	
	
	}
}
*/
