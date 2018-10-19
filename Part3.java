import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

public class Part3 {

	public static void main(String[] args) throws Exception {

		String message = args[0];
		HMAC h = new HMAC(message);
		RSA r = new RSA(message);
		h.iterate();
		r.iterate();
		
		System.out.println("---HMAC---\nHMAC Generation Average over 100 iterations: " + h.average + " milliseconds.");
		System.out.println("---RSA---\nHMAC Generation with RSA Average over 100 iterations.\nHMAC Generation Average - " +r.hGenAvg+" milliseconds.\nSignature Generation - "
				+ r.sigAvg + " milliseconds\nSignature Verification - " + r.sigVer + " nanoseconds");	
	}

}

class RSA{
	
	String message;
	long hGenAvg = 0;
	long sigAvg = 0;
	long sigVer = 0;
	KeyPair pair;
	KeyPairGenerator kGenerator;
	RSA(String message) throws NoSuchAlgorithmException{
		this.message = message;
		kGenerator = KeyPairGenerator.getInstance("RSA");
		kGenerator.initialize(2048);
		pair = kGenerator.generateKeyPair();
	}
	
	void iterate() throws Exception{
		

		for(int i = 0; i < 100; i++){
		//Hash Generation start
		Instant tBeforeTime = Instant.now();
		//signature time start
		Instant sigTimeBefore = Instant.now();


		PublicKey pubKey = pair.getPublic();
		PrivateKey privKey = pair.getPrivate();
		Signature sig = Signature.getInstance("SHA256WithRSA");
		sig.initSign(privKey);
		sig.update(message.getBytes());
		byte[] sign = sig.sign();
		Instant sigTimeAfter = Instant.now();
		sigAvg += Duration.between(sigTimeBefore, sigTimeAfter).toMillis();
		//signature time end
		String hash = Base64.getEncoder().encodeToString(sign);
		Instant tAfterTime = Instant.now();
		hGenAvg += Duration.between(tBeforeTime, tAfterTime).toMillis();
		//Hash Generation end
		//verification start
		long  verTimeBefore = System.nanoTime();
		sig.initVerify(pubKey);
		sig.update(message.getBytes());
		long verTimeAfter = System.nanoTime();
		sigVer += verTimeAfter-verTimeBefore;
		}

		hGenAvg /= 100;
		sigAvg /= 100;
		sigVer /= 100;
	}
	
}

class HMAC{
	
	String message;
	long average;
	HMAC(String message){
		this.message = message;
	}
	
	void iterate() throws Exception{

		Instant before = Instant.now();
		for(int i = 0; i < 100; i++){
		byte[] keyInit = new byte[16];
		keyInit = new byte[16];
		new Random().nextBytes(keyInit);
		SecretKeySpec skey = new SecretKeySpec(keyInit, "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(skey);
		String hash = Base64.getEncoder().encodeToString(mac.doFinal(message.getBytes()));
		}
		Instant after = Instant.now();
		average = Duration.between(before, after).toMillis();
		average /=100;
	}
	
}

