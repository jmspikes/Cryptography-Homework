
class Main {
	public static void main(String[] args) {

		StringBuilder plainText = new StringBuilder();
		plainText.append(args[0]);

		int key = Integer.parseInt(args[1]);

		Encrypt eDriver = new Encrypt();
		Decrypt dDriver = new Decrypt();


		StringBuilder encode = eDriver.encrypt(plainText, key);
		StringBuilder decode = dDriver.decrypt(encode, key);

		System.out.println("Encoded: "+encode.toString());
		System.out.println("Decoded: "+decode.toString());


	}
}

class Encrypt{

	public StringBuilder encrypt(StringBuilder chars, int key){

		StringBuilder en = new StringBuilder();
		for(int i = 0; i < chars.length(); i++){
			en.append((char)(chars.charAt(i)+key));
		}
		return en;
	}
}

class Decrypt{

	public StringBuilder decrypt(StringBuilder chars, int key){

		StringBuilder de = new StringBuilder();
		for(int i = 0; i < chars.length(); i++){
			de.append((char)(chars.charAt(i)-key));
		}
		return de;
	}

}