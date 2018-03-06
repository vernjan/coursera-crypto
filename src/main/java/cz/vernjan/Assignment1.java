package cz.vernjan;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The goal is to decrypt secret messages which were encrypted using the same one-time pad.
 *
 * Hint: XOR the cipher texts together, and consider what happens when a space is XORed with a character in [a-zA-Z].
 *
 * @see <a href="https://en.wikipedia.org/wiki/One-time_pad">
 * One-time pad</a>
 */
public class Assignment1 {

    // all ciphered texts trimmed to the same length
    private static final String CT_01 = "315c4eeaa8b5f8aaf9174145bf43e1784b8fa00dc71d885a804e5ee9fa40b16349c146fb778cdf2d3aff021dfff5b403b510d0d0455468aeb98622b137dae857553ccd8883a7bc37520e06e515d22c954eba50";
    private static final String CT_02 = "234c02ecbbfbafa3ed18510abd11fa724fcda2018a1a8342cf064bbde548b12b07df44ba7191d9606ef4081ffde5ad46a5069d9f7f543bedb9c861bf29c7e205132eda9382b0bc2c5c4b45f919cf3a9f1cb741";
    private static final String CT_03 = "32510ba9a7b2bba9b8005d43a304b5714cc0bb0c8a34884dd91304b8ad40b62b07df44ba6e9d8a2368e51d04e0e7b207b70b9b8261112bacb6c866a232dfe257527dc29398f5f3251a0d47e503c66e935de812";
    private static final String CT_04 = "32510ba9aab2a8a4fd06414fb517b5605cc0aa0dc91a8908c2064ba8ad5ea06a029056f47a8ad3306ef5021eafe1ac01a81197847a5c68a1b78769a37bc8f4575432c198ccb4ef63590256e305cd3a9544ee41";
    private static final String CT_05 = "3f561ba9adb4b6ebec54424ba317b564418fac0dd35f8c08d31a1fe9e24fe56808c213f17c81d9607cee021dafe1e001b21ade877a5e68bea88d61b93ac5ee0d562e8e9582f5ef375f0a4ae20ed86e935de812";
    private static final String CT_06 = "32510bfbacfbb9befd54415da243e1695ecabd58c519cd4bd2061bbde24eb76a19d84aba34d8de287be84d07e7e9a30ee714979c7e1123a8bd9822a33ecaf512472e8e8f8db3f9635c1949e640c621854eba0d";
    private static final String CT_07 = "32510bfbacfbb9befd54415da243e1695ecabd58c519cd4bd90f1fa6ea5ba47b01c909ba7696cf606ef40c04afe1ac0aa8148dd066592ded9f8774b529c7ea125d298e8883f5e9305f4b44f915cb2bd05af513";
    private static final String CT_08 = "315c4eeaa8b5f8bffd11155ea506b56041c6a00c8a08854dd21a4bbde54ce56801d943ba708b8a3574f40c00fff9e00fa1439fd0654327a3bfc860b92f89ee04132ecb9298f5fd2d5e4b45e40ecc3b9d59e941";
    private static final String CT_09 = "271946f9bbb2aeadec111841a81abc300ecaa01bd8069d5cc91005e9fe4aad6e04d513e96d99de2569bc5e50eeeca709b50a8a987f4264edb6896fb537d0a716132ddc938fb0f836480e06ed0fcd6e9759f404";
    private static final String CT_10 = "466d06ece998b7a2fb1d464fed2ced7641ddaa3cc31c9941cf110abbf409ed39598005b3399ccfafb61d0315fca0a314be138a9f32503bedac8067f03adbf3575c3b8edc9ba7f537530541ab0f9f3cd04ff50d";
    private static final String CT_11 = "32510ba9babebbbefd001547a810e67149caee11d945cd7fc81a05e9f85aac650e9052ba6a8cd8257bf14d13e6f0a803b54fde9e77472dbff89d71b57bddef121336cb85ccb8f3315f4b52e301d16e9f52f904";

    private static final List<String> CIPHERED_TEXTS = Arrays.asList(
            CT_01, CT_02, CT_03, CT_04, CT_05, CT_06, CT_07, CT_08, CT_09, CT_10, CT_11);

    private static final int CIPHER_TEXT_LENGTH = CT_01.length() / 2;


    // key: [a-zA-Z] xor SPACE --> value: [a-zA-Z]
    private static final Map<Byte, Byte> ALPHABET_XOR_SPACE_MAP = new HashMap<>();
    static {
        initAlphabetXorSpaceMapWithRangeOf((byte) 'a', (byte) 'z');
        initAlphabetXorSpaceMapWithRangeOf((byte) 'A', (byte) 'Z');

        System.out.println("ALPHABET_XOR_SPACE_MAP:");
        ALPHABET_XOR_SPACE_MAP.entrySet().forEach(System.out::println);
    }

    private static void initAlphabetXorSpaceMapWithRangeOf(byte from, byte to) {
        for (byte b = from; b <= to; b++) {
            byte byteXorSpace = (byte) (b ^ ' ');
            ALPHABET_XOR_SPACE_MAP.put(byteXorSpace, b);
        }
    }


    /**
     * The main method!
     */
    public static void main(String[] args) {
        byte[] key = recoverKey();

        for (String cipheredText : CIPHERED_TEXTS) {
            decipherText(cipheredText, key);
        }
    }

    private static byte[] recoverKey() {
        Map<Integer, List<Byte>> keysPartsMap = recoverAllPossibleKeyParts();
        return chooseMostFrequentKeyParts(keysPartsMap);
    }

    private static Map<Integer, List<Byte>> recoverAllPossibleKeyParts() {
        // key: cipher key index --> value: list of all possible key parts
        final Map<Integer, List<Byte>> keyPartsMap = new TreeMap<>();

        // permute all cipher texts
        for (String hex1 : CIPHERED_TEXTS) {
            for (String hex2 : CIPHERED_TEXTS) {

                BigInteger ct1 = new BigInteger(hex1, 16);
                BigInteger ct2 = new BigInteger(hex2, 16);

                // rule: ct1 xor ct2 = pt1 xor pt2 (ct - cipher text, pt - plain text)
                byte[] ct1XorCt2 = ct1.xor(ct2).toByteArray();

                for (int i = 0; i < ct1XorCt2.length; i++) {
                    byte xorByte = ct1XorCt2[i];

                    if (ALPHABET_XOR_SPACE_MAP.containsKey(xorByte)) {
                        byte originalChar = ALPHABET_XOR_SPACE_MAP.get(xorByte);

                        // 2 possible key parts (we don't know which of the 2 cipher texts contains space)
                        byte keyPart1 = (byte) (originalChar ^ ct1.toByteArray()[i]);
                        byte keyPart2 = (byte) (originalChar ^ ct2.toByteArray()[i]);

                        System.out.println(String.format(
                                "Recovered part of key at position\t%d\tOriginal character:\t%c Key parts: %d, %d",
                                i, originalChar, keyPart1, keyPart2));

                        keyPartsMap.putIfAbsent(i, new ArrayList<>());
                        keyPartsMap.get(i).add(keyPart1);
                        keyPartsMap.get(i).add(keyPart2);
                    }
                }
            }
        }

        System.out.println("ALL POSSIBLE KEY PARTS:");
        keyPartsMap.entrySet().forEach(System.out::println);

        return keyPartsMap;
    }

    private static byte[] chooseMostFrequentKeyParts(Map<Integer, List<Byte>> keysPartsMap) {
        final byte[] key = new byte[CIPHER_TEXT_LENGTH];

        for (int i = 0; i < key.length; i++) {
            List<Byte> keyParts = keysPartsMap.get(i);
            if (keyParts == null) {
                System.out.println("Unrecoverable key part with index: " + i);
                continue;
            }

            key[i] = chooseMostFrequentKeyPart(keyParts);
        }

        System.out.println("RECOVERED KEY: " + Arrays.toString(key));

        return key;
    }

    private static byte chooseMostFrequentKeyPart(List<Byte> keyParts) {
        Map<Byte, Long> keyPartsWithFrequency = keyParts.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return keyPartsWithFrequency.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElseThrow(AssertionError::new).getKey();
    }

    private static void decipherText(String cipheredText, byte[] key) {
        BigInteger deciphered = new BigInteger(cipheredText, 16).xor(new BigInteger(key));
        System.out.println(new String(deciphered.toByteArray()));
    }

}
