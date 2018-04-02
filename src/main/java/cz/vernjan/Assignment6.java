package cz.vernjan;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * See Week 6 Programming assignment.
 * <p>
 * Created by VERNER Jan on 2.4.18.
 */
public class Assignment6 {

    public static void main(String[] args) {
        challenge1();
        challenge2();
        challenge3();
        challenge4();
    }

    private static void challenge1() {
        System.out.println("TASK 1");

        BigInteger N = new BigInteger("17976931348623159077293051907890247336179769789423065727343008115773267580" +
                "550562068698537944921298295958550138753716401571013985864783377860692558349754108519659161512805757" +
                "594075263500747593528871082364994994077189561705436114947486504671101510156394068052754007158456087" +
                "8577663743040086340742855278549092581");

        // A = ceil(sqrt(N))
        BigInteger A = Utils.sqrt(N).add(BigInteger.ONE); // add 1 replaces 'ceil', not the best solution though ..
        System.out.println("A = " + A);

        // x = sqrt((A^2 - N))
        BigInteger x = Utils.sqrt(A.pow(2).subtract(N));
        System.out.println("x = " + x);

        BigInteger p = A.subtract(x);
        BigInteger q = A.add(x);

        verifyFactorization(N, p, q);
    }

    private static void challenge2() {
        System.out.println("TASK 2");

        BigInteger N = new BigInteger("64845584280807166966282426534677227872634372070697626306043907037879730861" +
                "808111646271401527606141756919558732184025452065542490671989242884484183935328197298853131051173864" +
                "896596258282150250499026445210088528167330371114229642102784028930765745864523368335707783468971583" +
                "8646088239640236866252211790085787877");

        // A = ceil(sqrt(N))
        BigInteger A = Utils.sqrt(N).add(BigInteger.ONE);

        // x = sqrt((A^2 - N))
        BigInteger x = Utils.sqrt(A.pow(2).subtract(N));

        BigInteger p = A.subtract(x);
        BigInteger q = A.add(x);

        while (!verifyFactorization(N, p, q)) { // TODO check A < A' + 2^20
            A = A.add(BigInteger.ONE);
            x = Utils.sqrt(A.pow(2).subtract(N));
            p = A.subtract(x);
            q = A.add(x);
        }
    }

    private static void challenge3() {
        System.out.println("TASK 3");

        BigInteger N = new BigInteger("72006226374735042527956443552558373833808445147399984182665305798191635569" +
                "018833779042340866418766393848517526499401789708352407913568687744115513201518827933181230909199624" +
                "636189683657364311917409496134852463970788523879939683923036467667022162701835329944324119217381272" +
                "9276147530748597302192751375739387929");

        System.out.println("Not solved yet :(");
    }

    private static void challenge4() {
        System.out.println("TASK 4");

        BigInteger CT = new BigInteger("2209645186741038177630656113488341801741006978789283107173183914367613560" +
                "012053800428232965047350942434394621975151225646583996794288946076454204058156474898801373486412045" +
                "232522932017648791666640299750918872997169052608322206777160001932926087000957999372407745896777369" +
                "a7817571267229951148662959627934791540");

        BigInteger N = new BigInteger("17976931348623159077293051907890247336179769789423065727343008115773267580" +
                "550562068698537944921298295958550138753716401571013985864783377860692558349754108519659161512805757" +
                "594075263500747593528871082364994994077189561705436114947486504671101510156394068052754007158456087" +
                "8577663743040086340742855278549092581");

        BigInteger p = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030" +
                "073662768891111614362326998675040546094339320838419523375986027530441562135724301");
        BigInteger q = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030" +
                "073778560980348930557750569660049234002192590823085163940025485114449475265364281");

        BigInteger e = new BigInteger("65537");

        BigInteger phiOfN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); // φ(N)
        System.out.println("φ(N) = " + phiOfN);

        BigInteger d = e.modInverse(phiOfN);
        System.out.println("d = " + d);

        BigInteger decrypted = CT.modPow(d, N);

        byte[] pt = decrypted.toByteArray();
        for (int i = 0; i < pt.length; i++) {
            if (pt[i] == 0) {
                System.out.println(new String(Arrays.copyOfRange(pt, i + 1, pt.length)));
            }
        }
    }

    private static boolean verifyFactorization(BigInteger expected, BigInteger p, BigInteger q) {
        boolean result = p.multiply(q).equals(expected);
        if (result) {
            System.out.println("Solution found!");
            System.out.println("\tp = " + p);
            System.out.println("\tq = " + q);
        }
        return result;
    }

}
