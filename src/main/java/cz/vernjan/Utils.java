package cz.vernjan;

import com.google.common.io.BaseEncoding;

import java.math.BigInteger;

/**
 * Created by VERNER Jan on 3.3.18.
 */
// TODO test
public class Utils {

    public static byte[] hex2Bytes(String hex) {
        return BaseEncoding.base16().lowerCase().decode(hex);
    }

    public static byte hex2Byte(String hexByte) {
        return BaseEncoding.base16().lowerCase().decode(hexByte)[0];
    }

    public static String bytesToHex(byte[] bytes) {
        return BaseEncoding.base16().lowerCase().encode(bytes);
    }

    public static String byteToHex(byte b) {
        return BaseEncoding.base16().lowerCase().encode(new byte[]{b});
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

    /**
     * https://en.wikipedia.org/wiki/Integer_square_root
     * https://stackoverflow.com/questions/4407839/how-can-i-find-the-square-root-of-a-java-biginteger
     * @param x an integer
     * @return integer square root
     */
    public static BigInteger sqrt(BigInteger x) {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength() / 2);
        BigInteger div2 = div;
        // Loop until we hit the same value twice in a row, or wind up alternating.
        for (; ; ) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2)) {
                return y;
            }
            div2 = div;
            div = y;
        }
    }

}
