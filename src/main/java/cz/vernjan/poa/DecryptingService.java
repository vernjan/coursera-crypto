package cz.vernjan.poa;

/**
 * Represents any AES/CBC/PKCS5Padding decrypting service.
 * It may be accessible locally, via HTTP, sockets, ..
 * The service must give away bad padding error.
 *
 * Created by VERNER Jan on 6.4.18.
 */
public interface DecryptingService {

    /**
     * Tries decrypting the cipher text.
     * @param payloadHex IV + cipher text in hex
     * @param blockLength block length (in bytes)
     * @return SUCCESS/BAD_PADDING
     * @throws Exception any kind of exception (IO, invalid input, ..)
     */
    Result decrypt(String payloadHex, int blockLength) throws Exception;

    enum Result {

        /**
         * Successfully decrypted and padding is OK.
         */
        SUCCESS,

        /**
         * Error, hopefully due to broken padding.
         */
        BAD_PADDING
    }

}
