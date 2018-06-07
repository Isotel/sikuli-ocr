package com.sarxos.ocr.sikulix;

import org.sikuli.script.Match;
import org.sikuli.script.Region;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


/**
 * Parallel matcher is used to execute matching operation in separate thread.
 *
 * @author Bartoz Firyn (SarXos)
 */
public class ParallelMatcher implements Callable<List<Match>> {

    /**
     * Glyph to be recognized
     */
    private Glyph glyph;

    /**
     * Region in which we should search for given glyph
     */
    private Region region;

    /**
     * Match - glyph mapping
     */
    private Map<Match, Glyph> mapping;

    /**
     * Construct me.
     *
     * @param glyph
     *         - glyph to be recognized
     * @param region
     *         - region in which we should search for glyps
     * @param mapping
     *         - mapping
     */
    public ParallelMatcher(Glyph glyph, Region region, Map<Match, Glyph> mapping) {
        this.glyph = glyph;
        this.region = region;
        this.mapping = mapping;
    }

    @Override
    public List<Match> call() throws Exception {
        List<Match> matches = new ArrayList<Match>();
        Iterator<Match> all = region.findAll(glyph.getPattern());

        if (all == null) {
            return null;
        }

        while (all.hasNext()) {
            Match m = all.next();
            matches.add(m);
            mapping.put(m, glyph);
        }

        return matches;
    }
}
