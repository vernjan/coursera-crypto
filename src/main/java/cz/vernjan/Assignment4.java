package cz.vernjan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * See Week 4 Programming assignment.
 * <p>
 * Created by VERNER Jan on 15.3.18.
 */
// TODO introduce Block and ByteBlock abstractions and apply to all assignments
public class Assignment4 {

    private static final int BLOCK_LENGTH = 32; // 16 bytes ~ 32 hex chars

    private static final String TARGET = "http://crypto-class.appspot.com/po?er=";

    private static final String CIPHER_TEXT =
            "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4";


    // just a speed optimization
    private static final List<Byte> GUESS_LIST = new ArrayList<>();

    static {
        GUESS_LIST.add((byte) 32); // space
        GUESS_LIST.addAll(rangeClosedOfBytes(1, 16)); // padding
        GUESS_LIST.addAll(rangeClosedOfBytes(97, 122)); // lower ascii a-z
        GUESS_LIST.addAll(rangeClosedOfBytes(65, 90)); // upper ascii A-Z
        // ..
    }

    private static List<Byte> rangeClosedOfBytes(int i2, int i3) {
        return IntStream.rangeClosed(i2, i3).mapToObj(i -> (byte) i).collect(Collectors.toList());
    }


    /**
     * Main
     */
    public static void main(String[] args) {
        String plainText = decryptCipherText(CIPHER_TEXT);
        System.out.println("Cipher text decrypted: " + plainText);
    }


    /**
     * Decrypts the cipher text.
     * @param cipherText cipher text
     * @return plain text
     */
    private static String decryptCipherText(String cipherText) {
        System.out.println("Decrypting cipher text " + cipherText);

        StringBuilder plainText = new StringBuilder();

        // decrypt one block at a time (in reverse)
        for (int i = cipherText.length(); i >= 2 * BLOCK_LENGTH; i -= BLOCK_LENGTH) {
            int blockIndex = i - BLOCK_LENGTH;
            int ivIndex = blockIndex - BLOCK_LENGTH;
            String iv = cipherText.substring(ivIndex, blockIndex);
            String decryptedBlock = decryptBlock(ivIndex, Utils.hex2Bytes(iv));
            plainText.insert(0, decryptedBlock); // note: expecting ASCII only
        }

        return plainText.toString(); // note: padding not trimmed
    }

    /**
     * Decrypts a single block.
     * @param ivIndex start index of the IV block
     * @param iv IV block
     * @return plain text
     */
    private static String decryptBlock(int ivIndex, byte[] iv) {
        System.out.format("Decrypting block [%d, %d] tampering with IV %s%n",
                ivIndex + BLOCK_LENGTH, ivIndex + 2 * BLOCK_LENGTH, Utils.bytesToHex(iv));

        byte[] plainText = new byte[iv.length];

        byte padding = 1;

        // decrypt one byte at a time (in reverse)
        for (int i = iv.length; i > 0; i--) {
            String ivTail = modifyIvTail(iv, plainText, padding);
            PayloadBuilder payloadBuilder = new PayloadBuilder(CIPHER_TEXT, ivIndex, 2 * i, ivTail);

            byte ivByte = iv[i - 1];
            byte decryptedLetter = decryptByte(ivByte, padding++, payloadBuilder);
            plainText[i - 1] = decryptedLetter;
        }

        System.out.println("Block decrypted: " + new String(plainText));

        return new String(plainText);
    }

    /**
     * Modify the IV tail with the given pad.
     * @param iv IV block
     * @param plainText plain text decrypted up to now
     * @param pad pad (from 1 to 16)
     * @return padded IV
     */
    private static String modifyIvTail(byte[] iv, byte[] plainText, byte pad) {
        StringBuilder tail = new StringBuilder();

        for (int i = 0; i < pad - 1; i++) {
            byte ivByte = iv[15 - i];
            byte plainTextByte = plainText[15 - i];
            byte payloadByte = (byte) (ivByte ^ plainTextByte ^ pad);
            tail.insert(0, Utils.byteToHex(payloadByte));
        }

        return tail.toString();
    }

    /**
     * Decrypts a single byte.
     */
    private static byte decryptByte(byte ivByte, byte pad, PayloadBuilder payloadBuilder) {
        System.out.format("Decrypting letter at position %d tampering with IV byte %s and using padding %d%n",
                17 - pad, Utils.byteToHex(ivByte), pad);

        for (byte b : GUESS_LIST) {
            System.out.print(".");

            byte guess = (byte) (ivByte ^ b ^ pad);
            String payload = payloadBuilder.build(guess);
            int responseCode = callUrl(payload);

            if (responseCode == 404) {
                printByteDecrypted(b);
                return b;
            }
        }

        printByteDecrypted(pad);
        return pad; // 404 not found, this pad is actually correct -> service would return 200
        // (e.g. pad 1 for position 16, or pad 2 for position 15 and so on ..)
    }

    private static void printByteDecrypted(byte padding) {
        System.out.println();
        System.out.println("Byte decrypted: " + padding + "\t -->\t'" + (char) padding + "'");
    }


    /**
     * Calls the service with the crafted payload.
     * 403 - invalid padding
     * 404 - valid padding, cipher text decrypted to some most likely non-existing URL
     * @param queryPayload crafted query payload
     * @return HTTP status code
     */
    // TODO mock for testing
    private static int callUrl(String queryPayload) {
        try {
            URL url = new URL(TARGET + queryPayload);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            return connection.getResponseCode();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    static class PayloadBuilder {

        private final String payload;
        private final int ivIndex;
        private final int ivByteIndex;
        private final String ivTail;

        public PayloadBuilder(String payload, int ivIndex, int ivByteIndex, String ivTail) {
            this.payload = payload;
            this.ivIndex = ivIndex;
            this.ivByteIndex = ivByteIndex;
            this.ivTail = ivTail;
        }

        public String build(byte guess) {
            return new StringBuilder(payload)
                    .delete(ivIndex + 2 * BLOCK_LENGTH, payload.length()) // decrypted block must be the last one
                    .replace(ivIndex + ivByteIndex - 2, ivIndex + ivByteIndex, Utils.byteToHex(guess)) // guessed byte
                    .replace(ivIndex + ivByteIndex, ivIndex + BLOCK_LENGTH, ivTail) // ensure proper padding after guesses byte
                    .toString();
        }
    }

}
