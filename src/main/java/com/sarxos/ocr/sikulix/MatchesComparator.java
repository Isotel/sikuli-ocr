package com.sarxos.ocr.sikulix;

import org.sikuli.script.Match;

import java.util.Comparator;


/**
 * Comparator used to sort matches position on x-axis.
 *
 * @author Bartosz Firyn (SarXos)
 */
public class MatchesComparator implements Comparator<Match> {

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
