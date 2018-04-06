package cz.vernjan;

import cz.vernjan.poa.DecryptingService;
import cz.vernjan.poa.PaddingOracleAttack;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * See Week 4 Programming assignment.
 * <p>
 * Created by VERNER Jan on 15.3.18.
 */
public class Assignment4 {

    private static final String CIPHER_TEXT =
            "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4";

    private static class Assignment4DecryptingService implements DecryptingService {

        private static final String TARGET = "http://crypto-class.appspot.com/po?er=";

        /**
         * Calls the service with the crafted payload.
         * HTTP 403 - invalid padding
         * HTTP 404 - valid padding, cipher text decrypted to some most likely non-existing URL
         */
        @Override
        public Result decrypt(String payloadHex, int blockLength) throws Exception {
            URL url = new URL(TARGET + payloadHex);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            return (responseCode == 200 || responseCode == 404) ? Result.SUCCESS : Result.BAD_PADDING;
        }
    }

    /**
     * Main
     */
    public static void main(String[] args) {
        new PaddingOracleAttack(CIPHER_TEXT, new Assignment4DecryptingService(), 16).start();
    }

}
