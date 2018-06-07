package com.sarxos.ocr.sikulix;

import org.sikuli.script.Match;
import org.sikuli.script.Region;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


/**
 * Simple OCR tool. It recognize only one line strings and require glyphs
 * library to be prepared first.
 *
 * @author Bartosz Firyn (SarXos)
 */
public class OCR {

    /**
     * Default path for glyphs def storage.
     */
    private static String storage = "data/glyphs";

    /**
     * Execution service.
     */
    private static ExecutorService executor = Executors.newCachedThreadPool(new DaemonThreadFactory());

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
    private String read(Region region) {

        if (region.getThrowException()) {
            region.setThrowException(false);
        }

        Map<Match, Glyph> mapping = new HashMap<Match, Glyph>();
        List<Match> matches = new ArrayList<Match>();

        List<FutureTask<List<Match>>> futures = new ArrayList<FutureTask<List<Match>>>();

        for (Glyph g : glyphs) {
            FutureTask<List<Match>> future = new FutureTask<List<Match>>(
                    new ParallelMatcher(g, region, mapping));
            futures.add(future);
            executor.execute(future);
        }

        for (FutureTask<List<Match>> future : futures) {
            Collection<Match> mc;
            try {
                if ((mc = future.get()) != null) {
                    matches.addAll(mc);
                }
            } catch (ExecutionException e) {
                /* intentionally empty */
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Collections.sort(matches, new MatchesComparator());

        StringBuilder sb = new StringBuilder();
        for (Match m : matches) {
            sb.append(mapping.get(m).getCharacter());
        }

        return sb.toString();
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
