package cz.vernjan;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by VERNER Jan on 11.3.18.
 */
class Assignment3Test {

    private Assignment3 subject = new Assignment3();

    @Test
    void testSign() throws IOException {
        // Please download the videos yourself and place them to @{code src/test/resources/cz/vernjan}.
        URL resourceURL = getResourceURL();

        String hash = subject.sign(resourceURL);

        assertEquals("03c08f4ee0b576fe319338139c045c89c3e8e9409633bea29442e21425006ea8", hash);
    }

    private URL getResourceURL() {
        try {
            return Resources.getResource(Assignment3Test.class,"6.2.birthday.mp4_download");
        } catch (Exception e) {
            throw new IllegalArgumentException("Download the videos first!", e);
        }
    }

}
