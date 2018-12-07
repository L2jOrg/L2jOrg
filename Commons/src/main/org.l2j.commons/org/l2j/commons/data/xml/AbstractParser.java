package org.l2j.commons.data.xml;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.l2j.commons.data.xml.helpers.ErrorHandlerImpl;
import org.l2j.commons.data.xml.helpers.SimpleDTDEntityResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.isNull;

/**
 * Author: VISTALL
 * Date:  18:35/30.11.2010
 */
public abstract class AbstractParser<H extends AbstractHolder> {

    private final H _holder;
    private SAXReader _reader;

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected String _currentFile;

    protected AbstractParser(H holder) {
        _holder = holder;
        _reader = new SAXReader();
        _reader.setValidation(true);
        _reader.setErrorHandler(new ErrorHandlerImpl(this));
    }

    public abstract File getXMLPath();

    public File getCustomXMLPath()
    {
        return null;
    }

    public abstract String getDTDFileName();

    public boolean isIgnored(File f)
    {
        return false;
    }

    protected void initDTD(File f)
    {
        _reader.setEntityResolver(new SimpleDTDEntityResolver(f));
    }

    protected void parseDocument(InputStream f, String name) throws Exception
    {
        _currentFile = name;

        Document document = _reader.read(f);

        readData(document.getRootElement());
    }

    protected void parse() {
        File path = getXMLPath();
        if(!path.exists()) {
            logger.warn("directory or file {} not exists", path.getAbsolutePath());
            return;
        }

        if(path.isDirectory()) {
            File dtd = new File(path, getDTDFileName());
            if(!dtd.exists()) {
                logger.warn("DTD file: {} not exists.", dtd.getName());
                return;
            }

            initDTD(dtd);

            parseDir(path);
            parseDir(getCustomXMLPath());
        } else {
            File dtd = new File(path.getParent(), getDTDFileName());
            if(!dtd.exists()) {
                logger.info("DTD file: {} not exists.", dtd.getName());
                return;
            }

            initDTD(dtd);

            try {
                parseDocument(new FileInputStream(path), path.getName());
            } catch(Exception e) {
                logger.warn(e.getLocalizedMessage(), e);
            }

            File customPath = getCustomXMLPath();
            if(customPath != null && customPath.exists()) {
                try {
                    parseDocument(new FileInputStream(customPath), customPath.getName());
                } catch(Exception e) {
                    logger.warn(e.getLocalizedMessage(), e);
                }
            }
        }
        afterParseActions();
    }

    protected void afterParseActions()
    {}

    protected H getHolder()
    {
        return _holder;
    }

    public String getCurrentFileName()
    {
        return _currentFile;
    }

    public void load() {
        parse();
        _holder.process();
        _holder.log();
    }

    public void reload() {
        logger.info("reload start...");
        _holder.clear();
        load();
    }

    private void parseDir(File dir) {
        if(isNull(dir)) {
            return;
        }

        if(!dir.exists()) {
            logger.warn("Dir {} not exists", dir.getAbsolutePath());
            return;
        }

        try(var paths = Files.walk(dir.toPath())) {
            var files =  paths.filter(this::acceptFile).map(Path::toFile).toArray(File[]::new);
            for (File file : files) {
                try {
                    parseDocument(new FileInputStream(file), file.getName());
                }
                catch(Exception e) {
                    logger.error("Parsing Document file {}: {}", file.getName(), e.getStackTrace());
                }

            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private boolean acceptFile(Path path) {
        var file = path.toFile();
        return file.getName().endsWith(".xml") && !file.isHidden() && !isIgnored(file);
    }

    protected abstract void readData(Element rootElement) throws Exception;
}