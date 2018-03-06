package cz.vernjan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by VERNER Jan on 3.3.18.
 */
class Assignment2Test {

    private Assignment2 subject = new Assignment2();

    @Test
    void testDecryptCBC1() throws Exception {
        String cbcKey = "140b41b22a29beb4061bda66b6747e14";
        String cipherText = "4ca00ff4c898d61e1edbf1800618fb2828a226d160dad07883d04e008a7897ee2e4b7465d5290d0c0e6c6822236e1daafb94ffe0c5da05d9476be028ad7c1d81";

        String plainText = subject.decryptCbc(cipherText, cbcKey);

        assertEquals("Basic CBC mode encryption needs padding.", plainText);
    }

    @Test
    void testDecryptCBC2() throws Exception {
        String cbcKey = "140b41b22a29beb4061bda66b6747e14";
        String cipherText = "5b68629feb8606f9a6667670b75b38a5b4832d0f26e1ab7da33249de7d4afc48e713ac646ace36e872ad5fb8a512428a6e21364b0c374df45503473c5242a253";

        String plainText = subject.decryptCbc(cipherText, cbcKey);

        assertEquals("Our implementation uses rand. IV", plainText);
    }

    @Test
    void testDecryptCTR1() throws Exception {
        String cbcKey = "36f18357be4dbd77f050515c73fcf9f2";
        String cipherText = "69dda8455c7dd4254bf353b773304eec0ec7702330098ce7f7520d1cbbb20fc388d1b0adb5054dbd7370849dbf0b88d393f252e764f1f5f7ad97ef79d59ce29f5f51eeca32eabedd9afa9329";

        String plainText = subject.decryptCtr(cipherText, cbcKey);

        assertEquals("CTR mode lets you build a stream cipher from a block cipher.", plainText);
    }

    @Test
    void testDecryptCTR2() throws Exception {
        String cbcKey = "36f18357be4dbd77f050515c73fcf9f2";
        String cipherText = "770b80259ec33beb2561358a9f2dc617e46218c0a53cbeca695ae45faa8952aa0e311bde9d4e01726d3184c34451";

        String plainText = subject.decryptCtr(cipherText, cbcKey);

        assertEquals("Always avoid the two time pad!", plainText);
    }

}
