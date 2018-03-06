package cz.vernjan;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Implementation using self-programmed CBC and CTR modes.
 * For educational purposes only!
 * <p>
 * Created by VERNER Jan on 3.3.18.
 */
public class Assignment2 {

    /**
     * Decrypts the cipher text (in hex) using the given key (also in hex) and CBC mode.
     * @param cipherTextHex cipher text (in hex)
     * @param keyHex key (in hex)
     * @return plain text
     * @throws Exception NOTE: single exception to keep it simple
     * @see <a href="https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation#Cipher_Block_Chaining_(CBC)">
     * Cipher Block Chaining (CBC)</a>
     */
    String decryptCbc(String cipherTextHex, String keyHex) throws Exception {
        assert (cipherTextHex.length() % 32 == 0) : "Cipher text is not divisible by 16";
        assert (keyHex.length() % 32 == 0) : "Key is not divisible by 16";

        final byte[] key = Utils.hex2ByteArray(keyHex);

        StringBuilder plainText = new StringBuilder();

        for (int i = 0; i <= cipherTextHex.length() - 64; i += 32) {

            // use the current block as IV
            byte[] iv = Utils.hex2ByteArray(cipherTextHex.substring(i, i + 32));
            // take the next block as cipher text
            byte[] block = Utils.hex2ByteArray(cipherTextHex.substring(i + 32, i + 64));

            // CBC: decrypt block using key and then xor with IV
            byte[] decrypted = Utils.xor(decryptBlock(block, key), iv);

            plainText.append(new String(decrypted));

            // Decrypting the last block?
            if (i == cipherTextHex.length() - 64) {
                byte paddingBytesCount = decrypted[15];
                removePKCS5Padding(plainText, paddingBytesCount);
            }
        }

        return plainText.toString();
    }

    private byte[] decryptBlock(byte[] block, byte[] key) throws Exception {
        return cipherBlock(block, key, Cipher.DECRYPT_MODE);
    }

    private void removePKCS5Padding(StringBuilder plainText, byte paddingBytesCount) {
        plainText.delete(plainText.length() - paddingBytesCount, plainText.length());
    }


    /**
     * Decrypts the cipher text (in hex) using the given key (also in hex) and CTR mode.
     * @param cipherTextHex cipher text (in hex)
     * @param keyHex key (in hex)
     * @return plain text
     * @throws Exception NOTE: single exception to keep it simple
     * @see <a href="https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation#Counter_(CTR)">
     * Counter (CTR)</a>
     */
    String decryptCtr(String cipherTextHex, String keyHex) throws Exception {
        // NOTE: with CTR, cipherText can be of any length
        assert (keyHex.length() % 32 == 0) : "Key is not divisible by 32";

        return decryptCtr(
                cipherTextHex.substring(32),
                Utils.hex2ByteArray(keyHex),
                cipherTextHex.substring(0, 32));
    }

    private String decryptCtr(String cipherTextHex, byte[] key, String ivHex) throws Exception {
        StringBuilder plaintext = new StringBuilder();

        // increments by one for each block
        String ivForThisBlockHex = ivHex;

        for (int i = 0; i <= cipherTextHex.length(); i += 32) {
            byte[] ivForThisBlock = Utils.hex2ByteArray(ivForThisBlockHex);

            String blockHex = cipherTextHex.substring(i, Math.min(cipherTextHex.length(), i + 32));
            byte[] block = Utils.hex2ByteArray(blockHex);

            // CTR: encrypt(!) IV using key and then xor with block
            byte[] decrypted = Utils.xor(encryptBlock(key, ivForThisBlock), block);

            plaintext.append(new String(decrypted));

            ivForThisBlockHex = Utils.incrementHexNumber(ivForThisBlockHex);
        }

        return plaintext.substring(0, cipherTextHex.length() / 2);
    }

    private byte[] encryptBlock(byte[] key, byte[] ivForThisBlock) throws Exception {
        return cipherBlock(ivForThisBlock, key, Cipher.ENCRYPT_MODE);
    }


    /**
     * Use ECB and NoPadding -  we want to this manually.
     */
    private byte[] cipherBlock(byte[] block, byte[] key, int encryptMode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(encryptMode, new SecretKeySpec(key, "AES"));
        return cipher.doFinal(block);
    }

}
