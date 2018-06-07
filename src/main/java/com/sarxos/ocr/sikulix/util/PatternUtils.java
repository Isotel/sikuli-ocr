package com.sarxos.ocr.sikulix.util;

import com.sarxos.ocr.sikulix.Glyph;
import com.sarxos.ocr.sikulix.Glyphs;
import org.sikuli.script.Pattern;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PatternUtils {
    private static final String STORAGE_PREFIX = "C:\\Work\\github\\sikuli-ocr\\src\\main\\resources\\glyphs\\mui";
    private static final String FONT_NAME = "Tahoma";
    private static final int FONT_SIZE = 11;

    public static void createStringPattern(String str, String fontName, int fontSize, String fileName) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font(fontName, Font.PLAIN, fontSize);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(str);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = img.createGraphics();
        g2d.setPaint(Color.WHITE);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.setBackground(Color.WHITE);
        g2d.drawString(str, 0, fm.getAscent());
        g2d.dispose();
        try {
            ImageIO.write(img, "png", new File(fileName));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void createCharacterPattern(char c, String fontName, int fontSize, String fileName) {
        createStringPattern(String.valueOf(c), fontName, fontSize, fileName);
    }

    public static void main(String[] args) {
        Glyphs glyphs = new Glyphs();
        for (char c : "0123456789.+-".toCharArray()) {
            String glyphName = c + ".png";
            String fileName = STORAGE_PREFIX + File.separatorChar + glyphName;
            glyphs.getGlyphList().add(new Glyph(String.valueOf(c), glyphName, 85));
            PatternUtils.createCharacterPattern(c, FONT_NAME, FONT_SIZE, fileName);
        }
        glyphs.save(STORAGE_PREFIX + File.separatorChar + "glyphs.xml");
    }

    /**
     * Pattern from file.
     *
     * @param path - image file path
     * @param similarity - similarity fraction
     * @return Pattern created from image
     */
    public static Pattern pattern(String path, float similarity) {
        return pattern(path).similar(similarity);
    }

    /**
     * Pattern from file.
     *
     * @param path - image file path
     * @return Pattern created from image
     */
    public static Pattern pattern(String path) {
        return new Pattern(new File(path).getAbsolutePath());
    }
}
