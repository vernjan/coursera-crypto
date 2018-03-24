package cz.vernjan;

import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;

/**
 * See Week 5 Programming assignment.
 * <p>
 * Created by VERNER Jan on 15.3.18.
 */
public class Assignment5 {

    private static final BigInteger p = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030073546976801874298166903427690031858186486050853753882811946569946433649006084171");
    private static final BigInteger g = new BigInteger("11717829880366207009516117596335367088558084999998952205599979459063929499736583746670572176471460312928594829675428279466566527115212748467589894601965568");
    private static final BigInteger h = new BigInteger("3239475104050450443565264378728065788649097520952449527834792452971981976143292558073856937958553180532878928001494706097394108577585732452307673444020333");

    private static final int MIDDLE = (int) Math.pow(2, 20); // 1 048 576


    public static void main(String[] args) {
        System.out.println("Initializing hashtable ..");

        HashMap<BigInteger, Integer> hashtable = new HashMap<>();

        // initialize hashtable in Zp: h / g^x1 ==> h * (g^x1)^-1 ==> h * g^-x1
        for (int x1 = 0; x1 < MIDDLE; x1++) {
            if (x1 % 100_000 == 0) { // track progress
                System.out.println("x1 = " + x1);
            }

            BigInteger result = h.multiply(g.modPow(BigInteger.valueOf(-x1), p)).mod(p);
            hashtable.put(result, x1);
        }

        System.out.println("Hashtable initialized, looking for match ..");

        // (g^B)^x0 in Zp ==> g^(B*x0) in Zp
        for (int x0 = 0; x0 < MIDDLE; x0++) {
            if (x0 % 100_000 == 0) { // track progress
                System.out.println("x0 = " + x0);
            }

            BigInteger result = g.modPow(BigInteger.valueOf((long) MIDDLE * x0), p);
            if (hashtable.containsKey(result)) {
                int x1 = hashtable.get(result);

                System.out.println("Done");
                System.out.println("x0 = " + x0);
                System.out.println("x1 = " + x1);
                System.out.println("result = " + x0 * (long) MIDDLE + x1);

                System.exit(0);
            }
        }
    }

}
