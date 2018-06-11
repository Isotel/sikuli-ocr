package com.sarxos.ocr.sikulix;

import org.sikuli.script.Finder;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.ScreenImage;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Simple OCR tool. It recognize only one line strings and require glyphs
 * library to be prepared first.
 *
 * @author Bartosz Firyn (SarXos)
 */
@SuppressWarnings("WeakerAccess")
public class OCR {

    /**
     * Default path for glyphs def storage.
     */
    private static String storage = "data/glyphs";

    /**
     * Specs cache.
     */
    private static Map<String, OCR> specs = new HashMap<String, OCR>();

    /**
     * Glyphs list.
     */
    private List<Glyph> glyphs;

    /**
     * Get specific OCR engine (loaded with given glyphs).
     *
     * @param name
     *         - glyphs library name
     * @return OCR engine
     */
    public static OCR getSpec(String name) {
        OCR ocr = specs.get(name);
        if (ocr == null) {
            List<Glyph> glyphs = Glyphs.load(storage + File.separatorChar + name);
            ocr = new OCR(glyphs);
            specs.put(name, ocr);
        }
        return ocr;
    }

    /**
     * Get storage location.
     *
     * @return Path
     */
    public static String getStoragePath() {
        return storage;
    }

    /**
     * Set storage path.
     *
     * @param storage
     *         storage path
     */
    public static void setStoragePath(String storage) {
        if (!new File(storage).exists()) {
            throw new IllegalArgumentException("Glyph storage location should point to existing directory");
        }
        OCR.storage = storage;
    }

    /**
     * Read text from screen rectangle basing on the glyphs data.
     *
     * @param rectangle
     *         rectangle object with the coordinates
     * @return Recognized text as String
     */
    public String read(Rectangle rectangle) {
        return this.read(Region.create(rectangle));
    }

    /**
     * Read text from screen region basing on the glyphs data.
     *
     * @param region
     *         region to read text from
     * @return Recognized text as String
     */
    public String read(Region region) {

        if (region.getThrowException()) {
            region.setThrowException(false);
        }

        List<CharacterMatch> matches = new ArrayList<CharacterMatch>();

        ScreenImage screenImage = region.getScreen().capture(region.x, region.y, region.w, region.h);

        for (Glyph g : glyphs) {
            Finder finder = new Finder(screenImage);
            finder.findAll(g.getPattern());
            while (finder.hasNext()) {
                matches.add(new CharacterMatch(finder.next(), g.getCharacter()));
            }
            finder.destroy();
        }

        Collections.sort(matches, new MatchesComparator());

        StringBuilder sb = new StringBuilder();
        for (CharacterMatch m : matches) {
            sb.append(m.character);
        }

        return sb.toString();
    }

    /**
     * Comparator used to sort matches position on x-axis.
     *
     * @author Bartosz Firyn (SarXos)
     */
    public static class MatchesComparator implements Comparator<Match> {

        @Override
        public int compare(Match a, Match b) {
            if (a.x < b.x) {
                return -1;
            } else if (a.x > b.x) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static class CharacterMatch extends Match {
        public final String character;

        CharacterMatch(Match match, String character) {
            super(match);
            this.character = character;
        }
    }

    /**
     * Create OCR engine.
     *
     * @param glyphs
     *         - list of glyphs that can be recognized
     */
    private OCR(List<Glyph> glyphs) {
        if (glyphs == null) {
            throw new IllegalArgumentException("Glyphs cannot be null");
        }
        this.glyphs = glyphs;
    }
}
