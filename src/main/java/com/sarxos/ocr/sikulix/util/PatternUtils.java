package com.sarxos.ocr.sikulix.util;

import com.sarxos.ocr.sikulix.Glyph;
import com.sarxos.ocr.sikulix.Glyphs;
import org.sikuli.script.Pattern;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public class PatternUtils {
    private static final String DEFAULT_GLYPHS = "0123456789.+-";
    private static final String DEFAULT_FONT_NAME = "Tahoma";
    private static final int DEFAULT_FONT_SIZE = 11;
    private static final int DEFAULT_SIMILARITY = 85;

    public static void createStringPattern(String str, String fontName, int fontSize, String fileName) {
        createStringPattern(str, fontName, fontSize, fileName, Color.BLACK, Color.WHITE);
    }

    public static void createStringPattern(String str, String fontName, int fontSize, String fileName,
                                           Color foregroundColor, Color backgroundColor) {
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
        g2d.setPaint(backgroundColor);
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
        g2d.setColor(foregroundColor);
        g2d.setBackground(backgroundColor);
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

    public static void generateGlyphs(String glyphChars, String storagePrefix, String font, int fontSize,
                                      int similarity) {
        System.out.printf("generating glyphs for \"%s\", font: [%s], size: [%d], " +
                "similarity: [%d], output directory: [%s]", glyphChars, font, fontSize, similarity, storagePrefix);

        Glyphs glyphs = new Glyphs();
        for (char c : glyphChars.toCharArray()) {
            String glyphName = c + ".png";
            String fileName = (storagePrefix != null ? storagePrefix + File.separatorChar : "") + glyphName;
            glyphs.getGlyphList().add(new Glyph(String.valueOf(c), glyphName, similarity));
            PatternUtils.createCharacterPattern(c, font, fontSize, fileName);
        }
        glyphs.save((storagePrefix != null ? storagePrefix + File.separatorChar : "") + "glyphs.xml");
    }

    /**
     * Pattern from file.
     *
     * @param path
     *         - image file path
     * @param similarity
     *         - similarity fraction
     * @return Pattern created from image
     */
    public static Pattern pattern(String path, float similarity) {
        return pattern(path).similar(similarity);
    }

    /**
     * Pattern from file.
     *
     * @param path
     *         - image file path
     * @return Pattern created from image
     */
    public static Pattern pattern(String path) {
        return new Pattern(new File(path).getAbsolutePath());
    }

    /**
     * Command line utility to generate glyphs
     */
    public static void main(String[] args) {
        String glyphChars = DEFAULT_GLYPHS;
        String storagePrefix = null;
        String font = DEFAULT_FONT_NAME;
        int fontSize = DEFAULT_FONT_SIZE;
        int similarity = DEFAULT_SIMILARITY;

        for (int i = 0; i < args.length; ++i) {
            // @formatter:off
            if (args[i].startsWith("-")) {
                if (args[i].equals("-o") && i < args.length + 1) { storagePrefix = args[++i]; }
                else if (args[i].equals("-f") && i < args.length + 1) { font = args[++i]; }
                else if (args[i].equals("-l") && i < args.length + 1) { fontSize = Integer.valueOf(args[++i]); }
                else if (args[i].equals("-s") && i < args.length + 1) { similarity = Integer.valueOf(args[++i]); }
                else {
                    usage();
                    System.exit(0);
                }
            } else {
                glyphChars = args[i];
            }
            // @formatter:on
        }

        generateGlyphs(glyphChars, storagePrefix, font, fontSize, similarity);
    }

    private static void usage() {
        System.out.println("Usage:\n" +
                "-o\toutput directory\n" +
                "-f\tfont (default " + DEFAULT_FONT_NAME + ")\n" +
                "-l\tfont size (default )" + DEFAULT_FONT_SIZE + "\n" +
                "-s\tsimilarity (default )" + DEFAULT_SIMILARITY + "\n" +
                "glyph_characters");
    }
}
