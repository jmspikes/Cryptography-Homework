import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Part4 {

	public static void main(String[] args) throws Exception {

		PartA partA = new PartA();
		//part A
		System.out.println("Part 4A:");
		while(partA.contains == false) {
		partA.generateRandomString();
		}
		partA.printResults();
		System.out.println("\n\nPart 4B:");
		//part B
		int average = 0;
		for(int i = 0; i < 20; i++) {
			partA.generateRandomString();
			average += partA.iterator;
		}
		System.out.println("Average number of generations needed to find collision: " + (average/20));
	}

}
class PartA{
	
	ArrayList<String> genStrings;
	ArrayList<Byte> hashes;
	Random r;
	char[] alphabet;
	boolean contains = false;
	int iterator = 0;
	
	PartA(){
		genStrings = new ArrayList<String>();
		hashes = new ArrayList<Byte>();
		r = new Random();
		alphabet = new char[52];
		int iterator = 65;
		for(int i = 0; i < 52; i++){
				alphabet[i] = (char) iterator; 
				iterator++;
				if(iterator == 91)
					iterator = 97;
	        }
	}
	
	void generateRandomString() throws NoSuchAlgorithmException{
		//generate random string
		StringBuilder build = new StringBuilder();
		for(int i = 0; i < 5; i++) {
			build.append(alphabet[r.nextInt(52)]);
		}
		//generate hash value
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(build.toString().getBytes());
		//increment iterator to account for generated strings count
		iterator++;
		//add to arraylist
		genStrings.add(build.toString());
		//check if its a duplicate
		if(hashes.contains(hash[0])) {
			contains = true;
		}
		hashes.add(hash[0]);
		}
	void printResults() {
		
		int duplicate = hashes.get(hashes.size()-1);
		System.out.print("Strings: ");
		for(int i = 0; i < hashes.size(); i++) {
			if(hashes.get(i) == duplicate) {
				System.out.print("'"+genStrings.get(i) + "' ");
			}
		}
		System.out.print("have duplicate hashes.\nHash value: "+duplicate 
						+"\nNumber of Strings generated: "+iterator);		
	}	
}