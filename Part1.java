package homework2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class Part1 {

	public static void main(String[] args) throws UnsupportedEncodingException, DataLengthException, InvalidCipherTextException {

		
		SecureRandom random = new SecureRandom();
        byte[] key = new byte[24];
        random.nextBytes(key);
 
        CBCAESBouncyCastle cabc = new CBCAESBouncyCastle();
        cabc.setKey(key);

        String input = "This is a secret message!";
        System.out.println("Input[" + input.length() + "]: " + input);

        byte[] plain = input.getBytes("UTF-8");
        System.out.println("Plaintext[" + plain.length + "]: " + new String(Hex.encode(plain)));

        byte[] encr = cabc.encrypt(plain);
        System.out.println("Encrypted[" + encr.length + "]: " + new String(Hex.encode(encr)));

        byte[] decr = cabc.decrypt(encr);
        System.out.println("Decrypted[" + decr.length + "]: " + new String(Hex.encode(decr)));

        String output = new String(decr, "UTF-8");
        System.out.println("Output[" + output.length() + "]: " + output);

		
		
	}
	

}


class CBCAESBouncyCastle {

    private final CBCBlockCipher cbcBlockCipher = new CBCBlockCipher(new AESEngine());
    private final SecureRandom random = new SecureRandom();

    private KeyParameter key;
    private BlockCipherPadding bcp = new PKCS7Padding();

    public void setPadding(BlockCipherPadding bcp) {
        this.bcp = bcp;
    }

    public void setKey(byte[] key) {
        this.key = new KeyParameter(key);
    }

    public byte[] encrypt(byte[] input)
            throws DataLengthException, InvalidCipherTextException {
        return processing(input, true);
    }

    public byte[] decrypt(byte[] input)
            throws DataLengthException, InvalidCipherTextException {
        return processing(input, false);
    }

    private byte[] processing(byte[] input, boolean encrypt)
            throws DataLengthException, InvalidCipherTextException {

        PaddedBufferedBlockCipher pbbc =
            new PaddedBufferedBlockCipher(cbcBlockCipher, bcp);

        int blockSize = cbcBlockCipher.getBlockSize();
        int inputOffset = 0;
        int inputLength = input.length;
        int outputOffset = 0;

        byte[] iv = new byte[blockSize];
        if(encrypt) {
            random.nextBytes(iv);
            outputOffset += blockSize;
        } else {
            System.arraycopy(input, 0 , iv, 0, blockSize);
            inputOffset += blockSize;
            inputLength -= blockSize;
        }

        pbbc.init(encrypt, new ParametersWithIV(key, iv));
        byte[] output = new byte[pbbc.getOutputSize(inputLength) + outputOffset];

        if(encrypt) {
            System.arraycopy(iv, 0 , output, 0, blockSize);
        }

        int outputLength = outputOffset + pbbc.processBytes(
            input, inputOffset, inputLength, output, outputOffset);

        outputLength += pbbc.doFinal(output, outputLength);

        return Arrays.copyOf(output, outputLength);

    }
 }
