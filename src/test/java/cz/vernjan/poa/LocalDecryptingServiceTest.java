package cz.vernjan.poa;

import cz.vernjan.Utils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static cz.vernjan.poa.DecryptingService.Result;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by VERNER Jan on 6.4.18.
 */
class LocalDecryptingServiceTest {

    private static final String HEX_IV = "00000000000000000000000000000000";
    private static final String HEX_KEY = "ffffffffffffffffffffffffffffffff";

    private LocalDecryptingService decryptingService = new LocalDecryptingService(HEX_KEY);


    @Test
    void shouldReturnSuccess() throws Exception {
        String hexCipherText = HEX_IV + "d4eb6b635ca094843bd3e0bf4f3417cf8dd284586534cf85959c6fc6c54d4b42ca7daeb12baddd817e55d0b152e89f32";

        Result result = decryptingService.decrypt(hexCipherText, 16);

        assertEquals(Result.SUCCESS, result);
    }

    @Test
    void shouldReturnBadPadding() throws Exception {
        String hexCipherText = HEX_IV + "d4eb6b635ca094843bd3e0bf4f3417cf8dd284586534cf85959c6fc6c54d4b42ca7daeb12baddd807e55d0b152e89f32";

        Result result = decryptingService.decrypt(hexCipherText, 16);

        assertEquals(Result.BAD_PADDING, result);
    }


    @Test
    @Disabled // just to get testing data
    void preparePayload() throws Exception {
        String plainText = "Secret plaintext to be encrypted using AES CBC.";
        byte[] iv = Utils.hex2Bytes(HEX_IV);
        byte[] key = Utils.hex2Bytes(HEX_KEY);
        SecretKey aesKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));
        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        System.out.println(Utils.bytesToHex(cipherText));
    }

}
