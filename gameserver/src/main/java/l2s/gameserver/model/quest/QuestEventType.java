package l2s.gameserver.model.quest;

public enum QuestEventType
{
	MOB_TARGETED_BY_SKILL, // onSkillUse action triggered when a character uses a skill on a mob
	ATTACKED_WITH_QUEST, // onAttack action triggered when a mob attacked by someone
	MOB_KILLED_WITH_QUEST, // onKill action triggered when a mob killed.
	QUEST_START, // onTalk action from start npcs
	QUEST_TALK, // onTalk action from npcs participating in a quest
	NPC_FIRST_TALK
}