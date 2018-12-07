package org.l2j.commons.data.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: VISTALL
 * Date:  18:34/30.11.2010
 */
public abstract class AbstractHolder {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public void log() {
		logger.info("Loaded {}(s) count.", size());
	}

	protected void process() { }

	public abstract int size();

	public abstract void clear();
}