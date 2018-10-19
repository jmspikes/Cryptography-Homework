import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import java.util.Base64;

public class Part2 {

	public static void main(String[] args) throws Exception {
	
		
		SaveKeys save = null;
		String user = args[0];
		String message = args[1];
		String hash = null;
		if(user.equalsIgnoreCase("a")){
			File key = new File("keys.pub");
			boolean exists = key.exists();
			if(!exists) {
			GenerateKeys init = new GenerateKeys();
			save = new SaveKeys(init.pubKey, init.privKey);
			save.writeKey();
			}
			else
				save = new SaveKeys();
			
			Signature sig = Signature.getInstance("SHA256WithRSA");
			sig.initSign(save.getPrivateKey());
			sig.update(message.getBytes());
			byte[] signature = sig.sign();
			hash = Base64.getEncoder().encodeToString(signature);
			System.out.println("Alice RSA Signature: " + hash);
			FileWriter writer = new FileWriter("sigtext.txt", false);
			writer.write(message);
			writer.write("\r\n");
			writer.write(hash);
			writer.close();
		}
		
		if(user.equalsIgnoreCase("b")){
			save = new SaveKeys();
			String line = null;
			String fMessage = null;
			String fKey = null;
			int i = 0; 
			FileReader fr = new FileReader("sigtext.txt");
			BufferedReader br = new BufferedReader(fr);
			while((line = br.readLine()) != null){
				if(i == 0){
					fMessage = line;
					i++;
				}
				else
					fKey = line;			
			}
			Signature sigVer = Signature.getInstance("SHA256WithRSA");
			sigVer.initSign(save.getPrivateKey());
			sigVer.update(fMessage.getBytes());
			byte[] signature = sigVer.sign();
			hash = Base64.getEncoder().encodeToString(signature);
			System.out.println("Bob's RSA Signature: "+ hash);
			if(hash.equals(fKey))
				System.out.println("Verification successful");
			else
				System.out.println("Verification unsuccessful");			
		}
		
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