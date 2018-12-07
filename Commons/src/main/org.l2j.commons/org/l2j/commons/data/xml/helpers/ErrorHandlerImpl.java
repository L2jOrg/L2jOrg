package org.l2j.commons.data.xml.helpers;

import org.l2j.commons.data.xml.AbstractParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Author: VISTALL
 * Date:  20:43/30.11.2010
 */
public class ErrorHandlerImpl implements ErrorHandler {
	private static final Logger logger = LoggerFactory.getLogger(ErrorHandlerImpl.class);
	private AbstractParser<?> _parser;

	public ErrorHandlerImpl(AbstractParser<?> parser) {
		_parser = parser;
	}

	@Override
	public void warning(SAXParseException exception) {
	 	logger.warn("File: {}:{} warning: {}", _parser.getCurrentFileName(), exception.getLineNumber(), exception.getLocalizedMessage());
	}

	@Override
	public void error(SAXParseException exception) {
		logger.error("File: {}:{} error: {}", _parser.getCurrentFileName(), exception.getLineNumber(), exception.getMessage());
	}

	@Override
	public void fatalError(SAXParseException exception) {
		logger.error("File: {}:{} fatal: {}", _parser.getCurrentFileName(), exception.getLineNumber(), exception.getMessage());
	}
}

