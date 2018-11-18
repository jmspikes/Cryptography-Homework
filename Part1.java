package Homework6;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;


public class Part1 {

	public static void main(String[] args) throws Exception {

		//gets inputed messages
		ArrayList<String> messages = new ArrayList<String>(8);
		for(int i = 0; i < 8; i++)
			messages.add(args[i]);

		Hash hash = new Hash(messages);
		System.out.println("Tree root: " +hash.rootHash);
		System.out.println("Choose a message 0-7");
		for(int i = 0; i < 8; i++){
			System.out.println(i+": "+messages.get(i));	
		}
		Scanner sc = new Scanner(System.in);
		int which = sc.nextInt();
		sc.nextLine();
		String hashValue = hash.hashed.get(which);
		BufferedWriter writer = new BufferedWriter(new FileWriter("sig.txt", false));
		for(String m : messages)
			writer.write(m+"\n");
		writer.write(which+"\n");
		writer.write("Message hash: \n"+hashValue+"\n");
		writer.write("Root hash: \n"+hash.rootHash);
		writer.close();
		System.out.println("Proceed?");
		String s = sc.nextLine();
		Path veri = Paths.get("sig.txt");
		List<String> lines = Files.readAllLines(veri);
		ArrayList<String> veriMessages = new ArrayList<String>();
		for(int i = 0; i < 8; i++)
			veriMessages.add(lines.get(i));
		hash.initMessages(veriMessages);
		String currentRoot = new String(hash.rootHash);

		String fileRoot = lines.get(lines.size()-1);
		if(fileRoot.equals(currentRoot))
			System.out.println("Verification successful!");
		else
			System.out.println("Verification not successful!");

	}

}


class Hash{
	
	ArrayList<String> hashed;
	String rootHash;
	KeyPairGenerator kp;
	KeyPair pair;
	
	Hash(ArrayList<String> toHash) throws Exception{
		kp = KeyPairGenerator.getInstance("RSA");
		kp.initialize(2048);
		pair = kp.generateKeyPair();
		initMessages(toHash);
		
		if(!new File("key.key").isFile()){
			Path path = Paths.get("key.key");
			Files.write(path, pair.getPrivate().getEncoded());
		}
		
	}
	
	
	void initMessages(ArrayList<String> toHash) throws Exception{
		hashed = new ArrayList<String>();
		//initial messages hash
		for(int i = 0; i < toHash.size(); i++){
			hashed.add(buildHash(toHash.get(i)));
		}
		for(int i = 0; i < 8; i+=2){
			String next = hashed.get(i)+hashed.get((i+1));
			hashed.add(buildHash(next));
		}
		for(int i = 2; i > 0; i--){
			String next = hashed.get(hashed.size()-i-1)+hashed.get(hashed.size()-i);
			hashed.add(buildHash(next));
		}
		String next = hashed.get(hashed.size()-2)+hashed.get(hashed.size()-1);
		hashed.add(next);
		rootHash = hashed.get(hashed.size()-1);
		
	}
	
	String buildHash(String hash) throws Exception{
		
		//generate keys if needed
		if(kp == null){
			kp = KeyPairGenerator.getInstance("RSA");
			kp.initialize(2048);
			pair = kp.generateKeyPair();
		}
		
		Signature privSig = Signature.getInstance("SHA256withRSA");
		privSig.initSign(pair.getPrivate());
		privSig.update(hash.getBytes());
		byte[] retString = privSig.sign();
		
		return Base64.getEncoder().encodeToString(retString);
	}
}