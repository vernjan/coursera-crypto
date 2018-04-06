package cz.vernjan.poa;

import cz.vernjan.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cz.vernjan.poa.DecryptingService.Result;

/**
 * Extensible implementation of https://en.wikipedia.org/wiki/Padding_oracle_attack
 * <p>
 * Created by VERNER Jan on 15.3.18.
 */
// TODO introduce Block and ByteBlock abstractions and apply to all assignments
public class PaddingOracleAttack {

    // just a speed optimization
    private final List<Byte> GUESS_LIST = new ArrayList<>();

    private final String cipherTextHex;
    private final DecryptingService decryptingService;
    private final int blockHexLength;


    /**
     * @param cipherTextHex cipher text in hex
     * @param decryptingService implementation of {@link DecryptingService}
     * @param blockLength block length (in bytes)
     */
    public PaddingOracleAttack(String cipherTextHex, DecryptingService decryptingService, int blockLength) {
        this.cipherTextHex = cipherTextHex;
        this.decryptingService = decryptingService;
        this.blockHexLength= 2 * blockLength;

        initOrderedGuessList(blockLength);
    }

    private void initOrderedGuessList(int blockLength) {
        GUESS_LIST.add((byte) 32); // space
        GUESS_LIST.addAll(rangeClosedOfBytes(1, blockLength)); // padding
        GUESS_LIST.addAll(rangeClosedOfBytes(97, 122)); // lower ascii a-z
        GUESS_LIST.addAll(rangeClosedOfBytes(65, 90)); // upper ascii A-Z
        GUESS_LIST.addAll(rangeClosedOfBytes(33, 64)); // special characters
        GUESS_LIST.addAll(rangeClosedOfBytes(91, 96));
        GUESS_LIST.addAll(rangeClosedOfBytes(blockLength + 1, 31));
        GUESS_LIST.addAll(rangeClosedOfBytes(123, 127));
        GUESS_LIST.addAll(rangeClosedOfBytes(Byte.MIN_VALUE, 0)); // non ascii
    }

    private List<Byte> rangeClosedOfBytes(int from, int to) {
        return IntStream.rangeClosed(from, to).mapToObj(i -> (byte) i).collect(Collectors.toList());
    }


    /**
     * Starts the attack.
     */
    public String start() {
        String plainText = decryptCipherText();
        System.out.println("Cipher text decrypted: " + plainText);
        return unpad(plainText);
    }

    private String unpad(String plainText) {
        int paddingSize = plainText.toCharArray()[plainText.length() - 1];
        return plainText.substring(0, plainText.length() - paddingSize);
    }

    /**
     * Decrypts the cipher text.
     *
     * @return plain text
     */
    private String decryptCipherText() {
        System.out.println("Decrypting cipher text " + cipherTextHex);

        StringBuilder plainText = new StringBuilder();

        // decrypt one block at a time (in reverse)
        for (int i = cipherTextHex.length(); i >= 2 * blockHexLength; i -= blockHexLength) {
            int blockIndex = i - blockHexLength;
            int ivIndex = blockIndex - blockHexLength;
            String iv = cipherTextHex.substring(ivIndex, blockIndex);
            String decryptedBlock = decryptBlock(ivIndex, Utils.hex2Bytes(iv));
            plainText.insert(0, decryptedBlock); // note: expecting ASCII only
        }

        return plainText.toString(); // note: padding not trimmed
    }

    /**
     * Decrypts a single block.
     *
     * @param ivIndex start index of the IV block
     * @param iv      IV block
     * @return plain text
     */
    private String decryptBlock(int ivIndex, byte[] iv) {
        System.out.format("Decrypting block [%d, %d] tampering with IV %s%n",
                ivIndex + blockHexLength, ivIndex + 2 * blockHexLength, Utils.bytesToHex(iv));

        byte[] plainText = new byte[iv.length];

        byte padding = 1;

        // decrypt one byte at a time (in reverse)
        for (int i = iv.length; i > 0; i--) {
            String ivTail = modifyIvTail(iv, plainText, padding);
            PayloadBuilder payloadBuilder = new PayloadBuilder(cipherTextHex, ivIndex, 2 * i, ivTail);

            byte ivByte = iv[i - 1];
            byte decryptedLetter = decryptByte(ivByte, padding++, payloadBuilder);
            plainText[i - 1] = decryptedLetter;
        }

        System.out.println("Block decrypted: " + new String(plainText));

        return new String(plainText);
    }

    /**
     * Modify the IV tail with the given pad.
     *
     * @param iv        IV block
     * @param plainText plain text decrypted up to now
     * @param pad       pad (from 1 to 16)
     * @return padded IV
     */
    private String modifyIvTail(byte[] iv, byte[] plainText, byte pad) {
        StringBuilder tail = new StringBuilder();

        for (int i = 0; i < pad - 1; i++) {
            byte ivByte = iv[blockHexLength / 2 - 1 - i];
            byte plainTextByte = plainText[blockHexLength / 2 - 1 - i];
            byte payloadByte = (byte) (ivByte ^ plainTextByte ^ pad);
            tail.insert(0, Utils.byteToHex(payloadByte));
        }

        return tail.toString();
    }

    /**
     * Decrypts a single byte.
     */
    private byte decryptByte(byte ivByte, byte pad, PayloadBuilder payloadBuilder) {
        System.out.format("Decrypting letter at position %d tampering with IV byte %s and using padding %d%n",
                blockHexLength / 2 + 1 - pad, Utils.byteToHex(ivByte), pad);

        for (byte b : GUESS_LIST) {
            System.out.print(".");

            // We need to skip this: 1 ^ 1 cancels and would always return success.
            if (b == 1 && pad == 1) {
                continue;
            }

            byte guess = (byte) (ivByte ^ b ^ pad);
            String payload = payloadBuilder.build(guess);

            try {
                Result result = decryptingService.decrypt(payload, blockHexLength / 2);
                if (result == Result.SUCCESS) {
                    printByteDecrypted(b);
                    return b;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // No other padding found so it must be 1 we've skipped earlier.
        if (pad == 1) {
            printByteDecrypted((byte) 1);
            return 1;
        } else {
            throw new AssertionError("No guess was correct");
        }
    }

    private void printByteDecrypted(byte guess) {
        System.out.println();
        System.out.println("Byte decrypted: " + guess + "\t -->\t'" + (char) guess + "'");
    }


    class PayloadBuilder {

        private final String payload;
        private final int ivIndex;
        private final int ivByteIndex;
        private final String ivTail;

        PayloadBuilder(String payload, int ivIndex, int ivByteIndex, String ivTail) {
            this.payload = payload;
            this.ivIndex = ivIndex;
            this.ivByteIndex = ivByteIndex;
            this.ivTail = ivTail;
        }

        String build(byte guess) {
            return new StringBuilder(payload)
                    .delete(ivIndex + 2 * blockHexLength, payload.length()) // decrypted block must be the last one
                    .replace(ivIndex + ivByteIndex - 2, ivIndex + ivByteIndex, Utils.byteToHex(guess)) // guessed byte
                    .replace(ivIndex + ivByteIndex, ivIndex + blockHexLength, ivTail) // ensure proper padding after guesses byte
                    .toString();
        }
    }

}
