package quests;

import org.l2j.gameserver.model.quest.Quest;

import java.util.ServiceLoader;

/**
 * @author NosBit
 */
public class Init
{
	public static void main(String[] args) {
		ServiceLoader.load(Quest.class, Init.class.getClassLoader()).forEach( (q) -> { });
	}
}
