package cz.vernjan;

import com.google.common.io.Resources;
import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * See Week 3 Programming assignment.
 *
 * Created by VERNER Jan on 11.3.18.
 */
public class Assignment3 {

    /**
     * Returns tag for the given resource. The tag is calculated from each 1 KB block.
     *
     * @param resourceURL resource URL
     * @return tag
     * @throws IOException if the resource can't be loaded
     */
    public String sign(URL resourceURL) throws IOException {
        System.out.println("Signing " + resourceURL);

        byte[] bytes = Resources.toByteArray(resourceURL);
        byte[] lastBlock = getLastBlock(bytes);

        byte[] nextBlockHash = computeSha256(lastBlock);

        for (int i = bytes.length - lastBlock.length; i >= 1024; i -= 1024) {
            byte[] block = Arrays.copyOfRange(bytes, i - 1024, i);
            byte[] blockAndNextBlockHash = Bytes.concat(block, nextBlockHash);
            nextBlockHash = computeSha256(blockAndNextBlockHash);
        }

        return Utils.bytesToHex(nextBlockHash);
    }

    private byte[] getLastBlock(byte[] bytes) {
        int lastBlockLength = bytes.length % 1024;
        System.out.println("Last block length is " + lastBlockLength);
        int from = bytes.length - lastBlockLength;
        return Arrays.copyOfRange(bytes, from, bytes.length);
    }

    private static byte[] computeSha256(byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

}
