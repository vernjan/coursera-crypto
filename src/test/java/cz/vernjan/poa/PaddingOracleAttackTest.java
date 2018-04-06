package cz.vernjan.poa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by VERNER Jan on 6.4.18.
 */
class PaddingOracleAttackTest {

    private static final String HEX_IV = "00000000000000000000000000000000";
    private static final String HEX_KEY = "ffffffffffffffffffffffffffffffff";


    @Test
    void startAttackOnPayloadWithPadding1() {
        String plainText = new PaddingOracleAttack(
                HEX_IV + "d4eb6b635ca094843bd3e0bf4f3417cf8dd284586534cf85959c6fc6c54d4b42ca7daeb12baddd817e55d0b152e89f32",
                new LocalDecryptingService(HEX_KEY), 16).start();

        assertEquals("Secret plaintext to be encrypted using AES CBC.", plainText);
    }

    @Test
    void startAttackOnPayloadWithPadding8() {
        String plainText = new PaddingOracleAttack(
                HEX_IV + "a94a1b93a353bc4054801c750bdf4e769fc06e2f232b6af5cc9b4fb7800559dccf49dabeecd202ab429de7643c3d296a",
                new LocalDecryptingService(HEX_KEY), 16).start();

        assertEquals("Plaintext to be encrypted using AES CBC.", plainText);
    }

    @Test
    void startAttackOnPayloadWithPadding16() {
        String plainText = new PaddingOracleAttack(
                HEX_IV + "d4eb6b635ca094843bd3e0bf4f3417cf8dd284586534cf85959c6fc6c54d4b42b7784011d4daaa42c7cfe43a625e1b6459e5e56d60b3864d759665802b31d4a2",
                new LocalDecryptingService(HEX_KEY), 16).start();

        assertEquals("Secret plaintext to be encrypted using AES CBC..", plainText);
    }

}
