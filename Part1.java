package homework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Part1 {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		
		Key key = null;
		Cipher cipher = null;
		String s = null;
		Keys keyC = null;
		try {
			File k = new File("keys.key");
			if(!k.exists()) {
				keyC = new Keys();
				key = keyC.getKey();
				FileOutputStream f = new FileOutputStream(k, false);
				f.write(key.getEncoded());
				f.close();
			}
			if(key == null)
				key = loadKeyFromFile();
			if(args[1] == "a" || args[1] == "A") {
				
			}
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] envVal = cipher.doFinal(args[0].getBytes());
			s = new String(Base64.getEncoder().encodeToString(envVal));
		
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decoded = Base64.getDecoder().decode(s.getBytes());
			byte[] dec = cipher.doFinal(decoded);
			s = new String(dec);
		}catch(Exception e) {}
		
		
	}

	static Key loadKeyFromFile() {
		Key ret = null;
		
		return ret;
	}

}
class Keys{
	
	Key k;
	
	Keys() {
		byte[] b = new byte[24];
		Random r = new Random();
		r.nextBytes(b);
		k = new SecretKeySpec(b, "AES");
	}
	
	Key getKey() {
		return k;
	}
}
