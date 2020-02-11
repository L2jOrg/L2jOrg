package quests;

import org.l2j.gameserver.model.quest.Quest;

import java.util.ServiceLoader;

public class Loader {

	public static void main(String[] args) {
		ServiceLoader.load(Quest.class, Loader.class.getClassLoader()).forEach( (q) -> { });
	}
}
