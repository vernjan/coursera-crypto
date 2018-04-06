package cz.vernjan.poa;

import cz.vernjan.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * For local testing.
 * <p>
 * Created by VERNER Jan on 6.4.18.
 */
class LocalDecryptingService implements DecryptingService {

    private final byte[] key;

    LocalDecryptingService(String hexKey) {
        this.key = Utils.hex2Bytes(hexKey);
    }

    @Override
    public Result decrypt(String payloadHex, int blockLength) throws Exception {
        byte[] iv = Utils.hex2Bytes(payloadHex.substring(0, 2 * blockLength));
        byte[] cipherText = Utils.hex2Bytes(payloadHex.substring(2 * blockLength));
        SecretKey aesKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

        try {
            cipher.doFinal(cipherText);
            return Result.SUCCESS;
        } catch (BadPaddingException e) {
            return Result.BAD_PADDING;
        }
    }

}
