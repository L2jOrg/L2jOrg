package org.l2j.commons.xml;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author Forsaiken
 */
public final class XMLDocumentFactory
{
    public static final XMLDocumentFactory getInstance()
    {
        return SingletonHolder._instance;
    }

    private final DocumentBuilder _builder;
    private final Transformer _transformer;

    protected XMLDocumentFactory() throws Exception
    {
        try
        {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            _builder = factory.newDocumentBuilder();
            _transformer = TransformerFactory.newInstance().newTransformer();
        }
        catch (Exception e)
        {
            throw new Exception("Failed initializing", e);
        }
    }

    public final Document loadDocument(final String filePath) throws Exception
    {
        return loadDocument(new File(filePath));
    }

    public final Document loadDocument(final File file) throws IOException, SAXException {
        return _builder.parse(file);
    }

    public final void writeDocument(final String filePath, final Document doc) throws Exception
    {
        final File file = new File(filePath);

        _transformer.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(file)));
    }

    public final Document newDocument()
    {
        return _builder.newDocument();
    }



    private static class SingletonHolder
    {
        protected static final XMLDocumentFactory _instance;

        static
        {
            try
            {
                _instance = new XMLDocumentFactory();
            }
            catch (Exception e)
            {
                throw new ExceptionInInitializerError(e);
            }
        }
    }
}