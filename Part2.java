package Homework6;

import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Part2 {



	public static void main(String[] args) throws Exception {
		
		HashVer h = new HashVer();
		byte[] keyBytes;
		Path path = Paths.get("key.key");
		keyBytes = Files.readAllBytes(path);
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey pvt = kf.generatePrivate(ks);
		
		
		
		Path veri = Paths.get("sig.txt");
		List<String> lines = Files.readAllLines(veri);
		ArrayList<String> veriMessages = new ArrayList<String>();
		for(int i = 0; i < 8; i++)
			veriMessages.add(lines.get(i));
		h.initMessages(veriMessages, pvt);

		
		System.out.println("Tree root: " +h.rootHash);
		String hashValue = h.hashed.get(Integer.parseInt(lines.get(8)));
		
		String fileRoot = lines.get(lines.size()-1);
		System.out.println("Tree file: "+ fileRoot);
		String fileMessHash = lines.get(lines.size()-3);
		String currentRoot = new String(h.rootHash);
		if(fileRoot.equals(currentRoot))
			System.out.println("Root verification successful!");
		else
			System.out.println("Root verification not successful!");
		
		System.out.println("This message: " + hashValue);
		System.out.println("File message: " + fileMessHash);
		if(fileMessHash.equals(hashValue))
			System.out.println("Message verification successful!");
		else
			System.out.println("Message verification not successful!");
		
	}

}

class HashVer{
	
	ArrayList<String> hashed;
	String rootHash;

	void initMessages(ArrayList<String> toHash, PrivateKey kp) throws Exception{
		hashed = new ArrayList<String>();
		//initial messages hash
		for(int i = 0; i < toHash.size(); i++){
			hashed.add(buildHash(toHash.get(i), kp));
		}
		for(int i = 0; i < 8; i+=2){
			String next = hashed.get(i)+hashed.get((i+1));
			hashed.add(buildHash(next, kp));
		}
		for(int i = 2; i > 0; i--){
			String next = hashed.get(hashed.size()-i-1)+hashed.get(hashed.size()-i);
			hashed.add(buildHash(next, kp));
		}
		String next = hashed.get(hashed.size()-2)+hashed.get(hashed.size()-1);
		hashed.add(next);
		rootHash = hashed.get(hashed.size()-1);
		
	}
	
	String buildHash(String hash, PrivateKey kp) throws Exception{
		
		
		Signature privSig = Signature.getInstance("SHA256withRSA");
		privSig.initSign(kp);
		privSig.update(hash.getBytes());
		byte[] retString = privSig.sign();
		
		return Base64.getEncoder().encodeToString(retString);
	}
}

