package com.sarxos.ocr.sikulix;

import com.sarxos.ocr.sikulix.util.PatternUtils;
import org.sikuli.script.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.Serializable;


/**
 * Single glyph to be recognized by OCR.
 *
 * @author Bartosz Firyn (SarXos)
 */
@XmlRootElement(name = "glyph")
@XmlAccessorType(XmlAccessType.FIELD)
public class Glyph implements Serializable {
    private transient Pattern pattern = null;
    private transient String path = null;

    /**
     * Percentage similarity value.
     */
    @XmlAttribute(name = "similarity")
    private int similarity = 95;
    /**
     * Character representing the glyph.
     */
    @XmlAttribute(name = "char")
    private String character = null;
    /**
     * Image filename.
     */
    @XmlAttribute(name = "image")
    private String file = null;
    /**
     * Relative image path.
     */

    public Glyph() {}

    public Glyph(String character, String imageFileName, int similarity) {
        this.character = character;
        this.file = imageFileName;
        this.similarity = similarity;
    }

    public Pattern getPattern() {
        if (pattern == null) {
            pattern = PatternUtils.pattern(path, (float) similarity / 100);
        }
        return pattern;
    }

    public String getCharacter() {
        return character;
    }

    public String getFile() {
        return file;
    }

    public void relativize(String path) {
        this.path = path + File.separatorChar + file;
    }

    @Override
    public String toString() {
        return "glyph[" + character + "](" + file + ")";
    }
}
