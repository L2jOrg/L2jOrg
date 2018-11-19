package l2s.commons.data.xml;

import l2s.commons.data.xml.helpers.ErrorHandlerImpl;
import l2s.commons.data.xml.helpers.SimpleDTDEntityResolver;
import l2s.commons.logging.LoggerObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

/**
 * Author: VISTALL
 * Date:  18:35/30.11.2010
 */
public abstract class AbstractParser<H extends AbstractHolder> extends LoggerObject
{
	protected final H _holder;

	protected String _currentFile;
	protected SAXReader _reader;

	protected AbstractParser(H holder)
	{
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

	protected abstract void readData(Element rootElement) throws Exception;

	protected void parse()
	{
		File path = getXMLPath();
		if(!path.exists())
		{
			warn("directory or file " + path.getAbsolutePath() + " not exists");
			return;
		}

		if(path.isDirectory())
		{
			File dtd = new File(path, getDTDFileName());
			if(!dtd.exists())
			{
				error("DTD file: " + dtd.getName() + " not exists.");
				return;
			}

			initDTD(dtd);

			parseDir(path);
			parseDir(getCustomXMLPath());
		}
		else
		{
			File dtd = new File(path.getParent(), getDTDFileName());
			if(!dtd.exists())
			{
				info("DTD file: " + dtd.getName() + " not exists.");
				return;
			}

			initDTD(dtd);

			try
			{
				parseDocument(new FileInputStream(path), path.getName());
			}
			catch(Exception e)
			{
				warn("Exception: " + e, e);
			}

			File customPath = getCustomXMLPath();
			if(customPath != null && customPath.exists())
			{
				try
				{
					parseDocument(new FileInputStream(customPath), customPath.getName());
				}
				catch(Exception e)
				{
					warn("Exception: " + e, e);
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

	public void load()
	{
		parse();
		_holder.process();
		_holder.log();
	}

	public void reload()
	{
		info("reload start...");
		_holder.clear();
		load();
	}

	private void parseDir(File dir)
	{
		if(dir == null)
			return;

		if(!dir.exists())
		{
			warn("Dir " + dir.getAbsolutePath() + " not exists");
			return;
		}

		try
		{
			Collection<File> files = FileUtils.listFiles(dir, FileFilterUtils.suffixFileFilter(".xml"), FileFilterUtils.directoryFileFilter());

			for(File f : files)
			{
				if(!f.isHidden() && !isIgnored(f))
				{
					try
					{
						parseDocument(new FileInputStream(f), f.getName());
					}
					catch(Exception e)
					{
						info("Exception: " + e + " in file: " + f.getName(), e);
					}
				}
			}
		}
		catch(Exception e)
		{
			warn("Exception: " + e, e);
		}
	}
}