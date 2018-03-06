package cz.vernjan;

/**
 * Created by VERNER Jan on 3.3.18.
 */
// TODO test
public class Utils {

    // TODO use for Assignment1
    public static byte[] hex2ByteArray(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte)
                    ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }

    public static byte[] xor(byte[] a, byte[] b) {
        int len = Math.min(a.length, b.length);
        byte[] aXorB = new byte[len];
        for (int i = 0; i < len; i++) {
            aXorB[i] = (byte) (a[i] ^ b[i]);
        }
        return aXorB;
    }

    public static String incrementHexNumber(String hexNumber) {
        final String baseHex = hexNumber.substring(0, 24);
        // FIXME possible overflow
        // use the last 4 bytes (i.e. Integer) of IV as counter
        int counter = Integer.parseUnsignedInt(hexNumber.substring(24, 32), 16) + 1;
        return baseHex + Integer.toHexString(counter);
    }

}
