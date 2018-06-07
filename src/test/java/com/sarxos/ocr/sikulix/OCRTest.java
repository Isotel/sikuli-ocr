package com.sarxos.ocr.sikulix;

import com.sarxos.ocr.sikulix.util.ImageFrame;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;


@SuppressWarnings("ConstantConditions")
public class OCRTest {

    private static OCR ocr = null;

    @BeforeClass
    public static void init() {
        OCR.setStoragePath(new File(OCRTest.class.getClassLoader().getResource("glyphs").getFile()).getPath());
        ocr = OCR.getSpec("test");
    }

    @Test
    public void testRead1() {
        ImageFrame f = new ImageFrame(testFile("images/test.png"));
        String text = ocr.read(f.getBounds());
        Assert.assertEquals("TEST1234", text);
        f.close();
    }

    @Test
    public void testRead2() {
        ImageFrame f = new ImageFrame(testFile("images/test2.png"));
        String text = ocr.read(f.getBounds());
        Assert.assertEquals("ABCDEFGHIJKL", text);
        f.close();
    }

    @Test
    public void testRead3() {
        ImageFrame f = new ImageFrame(testFile("images/test3.png"));
        String text = ocr.read(f.getBounds());
        Assert.assertEquals("0123456789", text);
        f.close();
    }

    @Test
    public void testRead4() {
        ImageFrame f = new ImageFrame(testFile("images/test4.png"));
        String text = ocr.read(f.getBounds());
        Assert.assertEquals("MNOPRSTUVWXYZ", text);
        f.close();
    }

    @Test
    public void testRead5() {
        ImageFrame f = new ImageFrame(testFile("images/test5.png"));
        String text = ocr.read(f.getBounds());
        Assert.assertEquals("ABCD1234", text);
    }

    private static String testFile(String fileName) {
        return new File(OCRTest.class.getClassLoader().getResource(fileName).getFile()).getAbsolutePath();
    }
}
