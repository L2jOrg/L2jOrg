package l2s.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.item.support.SynthesisData;

/**
 * @author Bonux
**/
public final class SynthesisDataHolder extends AbstractHolder
{
	private static final SynthesisDataHolder _instance = new SynthesisDataHolder();

	private List<SynthesisData> _data = new ArrayList<SynthesisData>();

	public static SynthesisDataHolder getInstance()
	{
		return _instance;
	}

	public void addData(SynthesisData data)
	{
		_data.add(data);
	}

	public SynthesisData[] getDatas()
	{
		return _data.toArray(new SynthesisData[_data.size()]);
	}

	@Override
	public int size()
	{
		return _data.size();
	}

	@Override
	public void clear()
	{
		_data.clear();
	}
}