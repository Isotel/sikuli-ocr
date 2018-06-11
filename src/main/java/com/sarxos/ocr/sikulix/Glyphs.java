package com.sarxos.ocr.sikulix;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Read glyphList.
 *
 * @author Bartosz Firyn (SarXos)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "glyphs")
public class Glyphs implements Serializable {
    /**
     * Classes to be deserialized by JAXB
     */
    private static final Class<?>[] CLASSES = new Class[] {
            Glyphs.class,
            Glyph.class,
    };

    /**
     * Single glyph element to be marshaled as XML element.
     */
    @XmlElement(name = "glyph")
    private List<Glyph> glyphList = new ArrayList<Glyph>();

    public static List<Glyph> load(String name) {
        File path = new File(name);
        if (path.exists()) {
            if (path.isDirectory()) {
                return loadFromDir(path);
            } else {
                return loadFromZIP(path);
            }
        } else {
            throw new IllegalArgumentException("Path " + name + " does not exist");
        }
    }

    public void save(String fileName) {
        try {
            JAXBContext context = JAXBContext.newInstance(CLASSES);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(this, new File(fileName));
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Glyph> getGlyphList() {
        return glyphList;
    }

    private static List<Glyph> loadFromDir(File path) {
        String root = path.getAbsolutePath();

        File p = new File(root + "/glyphs.xml");
        if (!p.exists()) {
            throw new IllegalStateException("Missing glyphList file in " + path);
        }

        try {
            JAXBContext context = JAXBContext.newInstance(CLASSES);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            List<Glyph> loadedGlyphs
                    = ((Glyphs) (unmarshaller.unmarshal(new FileInputStream(p)))).getGlyphList();
            for (Glyph g : loadedGlyphs) {
                g.relativize(root);
            }
            return loadedGlyphs;
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static List<Glyph> loadFromZIP(File path) {
        throw new UnsupportedOperationException();
    }
}
