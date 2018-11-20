package org.l2j.commons.xml;

import org.l2j.commons.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

public abstract class XMLReader<T> implements ValidationEventHandler {

    protected  static final Logger logger = LoggerFactory.getLogger(XMLReader.class);
    private Schema schema;
    private String processingFile;
    private Unmarshaller unmarshaller;

    public XMLReader() throws JAXBException {
        loadSchema();
        createUnmarshaller();
    }

    private void loadSchema()  {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schema = factory.newSchema(new File(Config.DATAPACK_ROOT, getSchemaFilePath()));
        } catch (SAXException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }

    private void createUnmarshaller() throws JAXBException {
        JAXBContext context = getJAXBContext();
        unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);
        unmarshaller.setEventHandler(this);
    }

    public void readAll() {
        for (String directory : getXmlFileDirectories()) {
            read(getDirectoryFiles(directory));
        }
    }

    private File[] getDirectoryFiles(String directory) {
        File fileDir = new File(Config.DATAPACK_ROOT, directory);
        return fileDir.listFiles((dir, name) -> name.endsWith(".xml"));
    }

    public void read(File... files) {
        if(files == null || files.length < 1) {
            return;
        }

        for (File file : files) {
            try {
                readFile(file);
            } catch (JAXBException e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private void readFile(File file) throws JAXBException{
        T entity = processFile(file);
        processEntity(entity);
    }

    @SuppressWarnings("unchecked")
    private T processFile(File file) throws JAXBException {
        processingFile = file.getAbsolutePath();
        return (T) unmarshaller.unmarshal(file);
    }

    @Override
    public boolean handleEvent(ValidationEvent event) {
        ValidationEventLocator locator = event.getLocator();
        logger.error("reading {} on line {} column {} : {} ", processingFile, locator.getLineNumber(), locator.getColumnNumber(), event.getMessage());
        return true;
    }

    protected abstract void processEntity(T entity);
    protected abstract JAXBContext getJAXBContext() throws JAXBException;
    protected abstract String getSchemaFilePath();
    protected abstract String[] getXmlFileDirectories();

}
