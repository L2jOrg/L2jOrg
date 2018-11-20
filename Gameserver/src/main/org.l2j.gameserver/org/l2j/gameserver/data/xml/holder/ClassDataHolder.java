package org.l2j.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.player.ClassData;

/**
 * @author Bonux
**/
public final class ClassDataHolder extends AbstractHolder
{
	private static final ClassDataHolder _instance = new ClassDataHolder();

	private final TIntObjectHashMap<ClassData> _classDataList = new TIntObjectHashMap<ClassData>();

	public static ClassDataHolder getInstance()
	{
		return _instance;
	}

	public void addClassData(ClassData classData)
	{
		_classDataList.put(classData.getClassId(), classData);
	}

	public ClassData getClassData(int classId)
	{
		return _classDataList.get(classId);
	}

	@Override
	public int size()
	{
		return _classDataList.size();
	}

	@Override
	public void clear()
	{
		_classDataList.clear();
	}
}