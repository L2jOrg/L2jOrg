package l2s.commons.data.xml.helpers;


import l2s.commons.data.xml.AbstractParser;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Author: VISTALL
 * Date:  20:43/30.11.2010
 */
public class ErrorHandlerImpl implements ErrorHandler
{
	private AbstractParser<?> _parser;

	public ErrorHandlerImpl(AbstractParser<?> parser)
	{
		_parser = parser;
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException
	{
	 	_parser.warn("File: " + _parser.getCurrentFileName() + ":" + exception.getLineNumber() + " warning: " + exception.getMessage());
	}

	@Override
	public void error(SAXParseException exception) throws SAXException
	{
		_parser.error("File: " + _parser.getCurrentFileName() + ":" + exception.getLineNumber() + " error: " + exception.getMessage());
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException
	{
		_parser.error("File: " + _parser.getCurrentFileName() + ":" + exception.getLineNumber() + " fatal: " + exception.getMessage());
	}
}

