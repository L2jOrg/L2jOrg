package org.l2j.scripts.ai.events;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.DefaultAI;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.entity.Reflection;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.network.l2.s2c.PlaySoundPacket;
import org.l2j.gameserver.utils.ChatUtils;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.NpcUtils;

/**
 * @author Bonux
 **/
public class SquashAI extends DefaultAI {
    private static final int small_baby_gourd = 12774;    // Молодая Тыква
    private static final int g_small_adult_gourd = 12775;    // Спелая Тыква
    private static final int b_small_adult_gourd = 12776;    // Завядшая Тыква
    private static final int big_baby_gourd = 12777;    // Большая Молодая Тыква
    private static final int g_big_adult_gourd = 12778;    // Большая Спелая Тыква
    private static final int b_big_adult_gourd = 12779;    // Большая Завядшая Тыква
    private static final int kg_small_adult_gourd = 13016;    // Королевская Тыква
    private static final int kg_big_adult_gourd = 13017;    // Большая Королевская Тыква

    private static final int s_gourd_nectar = 2005;    // Нектар
    private static final int s_gourd_nectar_good = 4513;    // Тыква - Повышение Уровня
    private static final int s_gourd_nectar_bad = 4514;    // Тыква - Отравление

    private static final NpcString[] ON_SPAWN_TEXTS_1 = new NpcString[]{
            NpcString.WHATS_THIS_WHY_AM_I_BEING_DISTURBED,
            NpcString.TADA_HERE_I_AM,
            NpcString.WHAT_ARE_YOU_LOOKING_AT,
            NpcString.IF_YOU_GIVE_ME_NECTAR_THIS_LITTLE_WINTERMELON_WILL_GROW_UP_QUICKLY,
            NpcString.ARE_YOU_MY_MOMMY,
            NpcString.FANCY_MEETING_YOU_HERE,
            NpcString.ARE_YOU_AFRAID_OF_THE_BIGBAD_WINTERMLON,
            NpcString.IMPRESSIVE_ARENT_I,
            NpcString.OBEY_ME,
            NpcString.RAISE_ME_WELL_AND_YOULL_BE_REWARDED_NEGLECT_ME_AND_SUFFER_THE_CONSEQUENCES
    };

    private static final NpcString[] ON_SPAWN_TEXTS_2 = new NpcString[]{
            NpcString.BRING_ME_NECTAR,
            NpcString.I_MUST_HAVE_NECTAR_TO_GROW,
            NpcString.GIVE_ME_SOME_NECTAR_QUICKLY_OR_YOULL_GET_NOTHING,
            NpcString.PLEASE_GIVE_ME_SOME_NECTAR_IM_HUNGRY,
            NpcString.NECTAR_PLEASE,
            NpcString.NECTAR_WILL_MAKE_ME_GROW_QUICKLY,
            NpcString.DONT_YOU_WANT_A_BIGGER_WINTERMELON_GIVE_ME_SOME_NECTAR_AND_ILL_GROW_MUCH_LARGER,
            NpcString.IF_YOU_RAISE_ME_WELL_YOULL_GET_PRIZES_OR_NOT,
            NpcString.YOU_ARE_HERE_FOR_THE_STUFF_EH_WELL_ITS_MINE_ALL_MINE,
            NpcString.TRUST_ME_GIVE_ME_SOME_NECTAR_AND_ILL_BECOME_A_GIANT_WINTERMELON
    };

    private static final NpcString[] WAIT_TEXTS = new NpcString[]{
            NpcString.SO_LONG_LOSERS,
            NpcString.IM_OUT_OF_HERE,
            NpcString.I_MUST_BE_GOING_HAVE_FUN_EVERYBODY,
            NpcString.TIME_IS_UP_PUT_YOUR_WEAPONS_DOWN,
            NpcString.GOOD_FOR_ME_BAD_FOR_YOU
    };

    private static final NpcString[] ON_ATTACK_TEXTS_1 = new NpcString[]{
            NpcString.KEEP_IT_COMING,
            NpcString.THATS_WHAT_IM_TALKING_ABOUT,
            NpcString.MAY_I_HAVE_SOME_MORE,
            NpcString.THAT_HIT_THE_SPOT,
            NpcString.I_FEEL_SPECIAL,
            NpcString.I_THINK_ITS_WORKING,
            NpcString.YOU_DO_UNDERSTAND,
            NpcString.YUCK_WHAT_IS_THIS_HA_HA_JUST_KIDDING,
            NpcString.A_TOTAL_OF_FIVE_AND_ILL_BE_TWICE_AS_ALIVE,
            NpcString.NECTAR_IS_SUBLIME
    };

    private static final NpcString[] ON_ATTACK_TEXTS_2 = new NpcString[]{
            NpcString.TRANSFORM,
            NpcString.I_FEEL_DIFFERENT,
            NpcString.IM_BIGGER_NOW_BRING_IT_ON,
            NpcString.IM_NOT_A_KID_ANYMORE,
            NpcString.BIG_TIME,
            NpcString.GOOD_LUCK,
            NpcString.IM_ALL_GROWN_UP_NOW,
            NpcString.IF_YOU_LET_ME_GO_ILL_BE_YOUR_BEST_FRIEND,
            NpcString.IM_CHUCK_FULL_OF_GOODNESS,
            NpcString.GOOD_JOB_NOW_WHAT_ARE_YOU_GOING_TO_DO
    };

    private static final NpcString[] ON_ATTACK_TEXTS_3 = new NpcString[]{
            NpcString.YOU_CALL_THAT_A_HIT,
            NpcString.WHY_ARE_YOU_HITTING_ME_OUCH_STOP_IT_GIVE_ME_NECTAR,
            NpcString.STOP_OR_ILL_WILT,
            NpcString.IM_NOT_FULLY_GROWN_YET_OH_WELL_DO_WHAT_YOU_WILL_ILL_FADE_AWAY_WITHOUT_NECTAR_ANYWAY,
            NpcString.GO_AHEAD_AND_HIT_ME_AGAIN_IT_WONT_DO_YOU_ANY_GOOD,
            NpcString.WOE_IS_ME_IM_WILTING,
            NpcString.IM_NOT_FULLY_GROWN_YET_HOW_ABOUT_SOME_NECTAR_TO_EASE_MY_PAIN,
            NpcString.THE_END_IS_NEAR,
            NpcString.PRETTY_PLEASE_WITH_SUGAR_ON_TOP_GIVE_ME_SOME_NECTAR,
            NpcString.IF_I_DIE_WITHOUT_NECTAR_YOULL_GET_NOTHING
    };

    private static final NpcString[] ON_ATTACK_TEXTS_4 = new NpcString[]{
            NpcString.BETTER_LUCK_NEXT_TIME,
            NpcString.NICE_SHOT,
            NpcString.IM_NOT_AFRAID_OF_YOU,
            NpcString.IF_I_KNEW_THIS_WAS_GOING_TO_HAPPEN_I_WOULD_HAVE_STAYED_HOME,
            NpcString.TRY_HARDER_OR_IM_OUT_OF_HERE,
            NpcString.IM_TOUGHER_THAN_I_LOOK,
            NpcString.GOOD_STRIKE,
            NpcString.OH_MY_GOURD,
            NpcString.THATS_ALL_YOUVE_GOT,
            NpcString.WHY_ME
    };

    private static final NpcString[] ON_ATTACK_TEXTS_5 = new NpcString[]{
            NpcString.SOUNDTASTIC,
            NpcString.I_CAN_SING_ALONG_IF_YOU_LIKE,
            NpcString.I_THINK_YOU_NEED_SOME_BACKUP,
            NpcString.KEEP_UP_THAT_RHYTHM_AND_YOULL_BE_A_STAR,
            NpcString.MY_HEART_YEARNS_FOR_MORE_MUSIC,
            NpcString.YOURE_OUT_OF_TUNE_AGAIN,
            NpcString.THIS_IS_AWFUL,
            NpcString.I_THINK_I_BROKE_SOMETHING,
            NpcString.WHAT_A_LOVELY_MELODY_PLAY_IT_AGAIN,
            NpcString.MUSIC_TO_MY_UH_EARS
    };

    private static final NpcString[] ON_ATTACK_TEXTS_6 = new NpcString[]{
            NpcString.YOU_NEED_MUSIC_LESSONS,
            NpcString.I_CANT_HEAR_YOU,
            NpcString.YOU_CANT_HURT_ME_LIKE_THAT,
            NpcString.IM_STRONGER_THAN_YOU_ARE,
            NpcString.NO_MUSIC_IM_OUT_OF_HERE,
            NpcString.THAT_RACKET_IS_GETTING_ON_MY_NERVES_TONE_IT_DOWN_A_BIT,
            NpcString.YOU_CAN_ONLY_HURT_ME_THROUGH_MUSIC,
            NpcString.ONLY_MUSICAL_INSTRUMENTS_CAN_HURT_ME_NOTHING_ELSE,
            NpcString.YOUR_SKILLS_ARE_IMPRESSIVE_BUT_SADLY_USELESS,
            NpcString.CATCH_A_CHRONO_FOR_ME_PLEASE
    };

    private static final NpcString[] ON_DEAD_TEXTS_1 = new NpcString[]{
            NpcString.IM_FEELING_BETTER_ANOTHER_THIRTY_SECONDS_AND_ILL_BE_OUT_OF_HERE,
            NpcString.TWENTY_SECONDS_AND_ITS_CIAO_BABY,
            NpcString.WOOHOO_JUST_TEN_SECONDS_LEFT_NINE_EIGHT_SEVEN,
            NpcString.GIVE_ME_NECTAR_OR_ILL_BE_GONE_IN_TWO_MINUTES,
            NpcString.GIVE_ME_NECTAR_OR_ILL_BE_GONE_IN_ONE_MINUTE
    };

    private static final NpcString[] ON_DEAD_TEXTS_2 = new NpcString[]{
            NpcString.YOU_GOT_ME,
            NpcString.NOW_LOOK_AT_WHAT_YOUVE_DONE,
            NpcString.YOU_WIN,
            NpcString.FINGERS_CROSSED,
            NpcString.DONT_TELL_ANYONE,
            NpcString.GROSS_MY_GUTS_ARE_COMING_OUT,
            NpcString.TAKE_IT_AND_GO,
            NpcString.I_SHOULDVE_LEFT_WHEN_I_COULD,
            NpcString.NOW_LOOK_WHAT_YOU_HAVE_DONE,
            NpcString.I_FEEL_DIRTY
    };

    private boolean _attacked = false;

    private int _tryCount = 0;
    private int _successCount = 0;
    private int _failCount = 0;

    public SquashAI(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        if (actor == null)
            return;

        int npcId = actor.getNpcId();
        if (npcId == small_baby_gourd) {
            ChatUtils.say(actor, Rnd.get(ON_SPAWN_TEXTS_1));
            addTimer(99702, 1000 * 2);
            addTimer(99723, 1000 * 60);
        } else if (npcId == g_small_adult_gourd)
            addTimer(99711, 1000 * 60);
        else if (npcId == b_small_adult_gourd)
            addTimer(99714, 1000 * 60);
        else if (npcId == big_baby_gourd) {
            ChatUtils.say(actor, Rnd.get(ON_SPAWN_TEXTS_1));
            addTimer(99704, 1000 * 3);
            addTimer(99725, 1000 * 60);
        } else if (npcId == g_big_adult_gourd)
            addTimer(99717, 1000 * 60);
        else if (npcId == b_big_adult_gourd)
            addTimer(99720, 1000 * 60);
        else if (npcId == kg_small_adult_gourd)
            addTimer(99711, 1000 * 60);
        else if (npcId == kg_big_adult_gourd)
            addTimer(99717, 1000 * 60);
    }

    @Override
    protected void onEvtTimer(int timerId, Object arg1, Object arg2) {
        super.onEvtTimer(timerId, arg1, arg2);

        NpcInstance actor = getActor();
        if (actor == null)
            return;

        int npcId = actor.getNpcId();
        if (npcId == small_baby_gourd) {
            if (timerId == 99702)
                ChatUtils.say(actor, Rnd.get(ON_SPAWN_TEXTS_2));
            else if (timerId == 99723) {
                ChatUtils.say(actor, NpcString.GIVE_ME_NECTAR_OR_ILL_BE_GONE_IN_TWO_MINUTES);
                addTimer(99724, 1000 * 60);
            } else if (timerId == 99724) {
                ChatUtils.say(actor, NpcString.GIVE_ME_NECTAR_OR_ILL_BE_GONE_IN_ONE_MINUTE);
                addTimer(99709, 1000 * 60);
            } else if (timerId == 99709)
                actor.deleteMe();
        } else if (npcId == g_small_adult_gourd) {
            if (timerId == 99701) {
                ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
                actor.deleteMe();
            } else if (timerId == 99711) {
                ChatUtils.say(actor, NpcString.IM_FEELING_BETTER_ANOTHER_THIRTY_SECONDS_AND_ILL_BE_OUT_OF_HERE);
                addTimer(99712, 1000 * 10);
            } else if (timerId == 99712) {
                ChatUtils.say(actor, NpcString.TWENTY_SECONDS_AND_ITS_CIAO_BABY);
                addTimer(99713, 1000 * 10);
            } else if (timerId == 99713) {
                ChatUtils.say(actor, NpcString.WOOHOO_JUST_TEN_SECONDS_LEFT_NINE_EIGHT_SEVEN);
                addTimer(99701, 1000 * 10);
            }
        } else if (npcId == b_small_adult_gourd) {
            if (timerId == 99703) {
                ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
                actor.deleteMe();
            } else if (timerId == 99714) {
                ChatUtils.say(actor, NpcString.IM_FEELING_BETTER_ANOTHER_THIRTY_SECONDS_AND_ILL_BE_OUT_OF_HERE);
                addTimer(99715, 1000 * 10);
            } else if (timerId == 99715) {
                ChatUtils.say(actor, NpcString.TWENTY_SECONDS_AND_ITS_CIAO_BABY);
                addTimer(99716, 1000 * 10);
            } else if (timerId == 99716) {
                ChatUtils.say(actor, NpcString.WOOHOO_JUST_TEN_SECONDS_LEFT_NINE_EIGHT_SEVEN);
                addTimer(99703, 1000 * 10);
            }
        } else if (npcId == big_baby_gourd) {
            if (timerId == 99704)
                ChatUtils.say(actor, Rnd.get(ON_SPAWN_TEXTS_2));
            else if (timerId == 99725) {
                ChatUtils.say(actor, NpcString.GIVE_ME_NECTAR_OR_ILL_BE_GONE_IN_TWO_MINUTES);
                addTimer(99726, 1000 * 60);
            } else if (timerId == 99726) {
                ChatUtils.say(actor, NpcString.GIVE_ME_NECTAR_OR_ILL_BE_GONE_IN_ONE_MINUTE);
                addTimer(99710, 1000 * 60);
            } else if (timerId == 99710)
                actor.deleteMe();
        } else if (npcId == g_big_adult_gourd) {
            if (timerId == 99705) {
                ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
                actor.deleteMe();
            } else if (timerId == 99717) {
                ChatUtils.say(actor, NpcString.IM_FEELING_BETTER_ANOTHER_THIRTY_SECONDS_AND_ILL_BE_OUT_OF_HERE);
                addTimer(99718, 1000 * 10);
            } else if (timerId == 99718) {
                ChatUtils.say(actor, NpcString.TWENTY_SECONDS_AND_ITS_CIAO_BABY);
                addTimer(99719, 1000 * 10);
            } else if (timerId == 99719) {
                ChatUtils.say(actor, NpcString.WOOHOO_JUST_TEN_SECONDS_LEFT_NINE_EIGHT_SEVEN);
                addTimer(99705, 1000 * 10);
            }
        } else if (npcId == b_big_adult_gourd) {
            if (timerId == 99706) {
                ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
                actor.deleteMe();
            } else if (timerId == 99720) {
                ChatUtils.say(actor, NpcString.IM_FEELING_BETTER_ANOTHER_THIRTY_SECONDS_AND_ILL_BE_OUT_OF_HERE);
                addTimer(99721, 1000 * 10);
            } else if (timerId == 99721) {
                ChatUtils.say(actor, NpcString.TWENTY_SECONDS_AND_ITS_CIAO_BABY);
                addTimer(99722, 1000 * 10);
            } else if (timerId == 99722) {
                ChatUtils.say(actor, NpcString.WOOHOO_JUST_TEN_SECONDS_LEFT_NINE_EIGHT_SEVEN);
                addTimer(99706, 1000 * 10);
            }
        } else if (npcId == kg_small_adult_gourd) {
            if (timerId == 99701) {
                ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
                actor.deleteMe();
            } else if (timerId == 99711) {
                ChatUtils.say(actor, NpcString.IM_FEELING_BETTER_ANOTHER_THIRTY_SECONDS_AND_ILL_BE_OUT_OF_HERE);
                addTimer(99712, 1000 * 10);
            } else if (timerId == 99712) {
                ChatUtils.say(actor, NpcString.TWENTY_SECONDS_AND_ITS_CIAO_BABY);
                addTimer(99713, 1000 * 10);
            } else if (timerId == 99713) {
                ChatUtils.say(actor, NpcString.WOOHOO_JUST_TEN_SECONDS_LEFT_NINE_EIGHT_SEVEN);
                addTimer(99701, 1000 * 10);
            }
        } else if (npcId == kg_big_adult_gourd) {
            if (timerId == 99705) {
                ChatUtils.say(actor, Rnd.get(WAIT_TEXTS));
                actor.deleteMe();
            } else if (timerId == 99717) {
                ChatUtils.say(actor, NpcString.IM_FEELING_BETTER_ANOTHER_THIRTY_SECONDS_AND_ILL_BE_OUT_OF_HERE);
                addTimer(99718, 1000 * 10);
            } else if (timerId == 99718) {
                ChatUtils.say(actor, NpcString.TWENTY_SECONDS_AND_ITS_CIAO_BABY);
                addTimer(99719, 1000 * 10);
            } else if (timerId == 99719) {
                ChatUtils.say(actor, NpcString.WOOHOO_JUST_TEN_SECONDS_LEFT_NINE_EIGHT_SEVEN);
                addTimer(99705, 1000 * 10);
            }
        }
    }

    @Override
    protected void onEvtSeeSpell(Skill skill, Creature caster, Creature target) {
        NpcInstance actor = getActor();
        if (actor == null || actor != target)
            return;

        onEvtAttacked(caster, skill, 0);
    }

    @Override
    protected void onEvtAttacked(Creature attacker, Skill skill, int damage) {
        NpcInstance actor = getActor();
        if (actor == null || actor.isDead())
            return;

        int npcId = actor.getNpcId();
        int skillId = skill == null ? 0 : skill.getId();
        if (npcId == small_baby_gourd) {
            if (!_attacked) {
                _tryCount = 0;
                _successCount = 0;
                _failCount = 0;

                if (skillId == s_gourd_nectar) {
                    _tryCount++;

                    if (Rnd.get(1000) < 631) {
                        _successCount++;

                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
                        actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_good, 1), actor, false);
                    } else {
                        _failCount++;

                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
                        actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_bad, 1), actor, false);
                    }
                }
                _attacked = true;
            } else {
                if (skillId == s_gourd_nectar && _tryCount >= 4) {
                    _tryCount++;

                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_2));

                    Player owner = actor.getPlayer();
                    Location loc = actor.getLoc();
                    Reflection reflection = actor.getReflection();

                    actor.deleteMe();

                    if (Rnd.get(1000) < 631)
                        _successCount++;
                    else
                        _failCount++;

                    if (_successCount == 5) {
                        spawnNextNpc(kg_small_adult_gourd, loc, reflection, owner);

                        _successCount = 0;
                        _failCount = 0;
                    } else if (_successCount == 4) {
                        spawnNextNpc(g_small_adult_gourd, loc, reflection, owner);

                        _successCount = 0;
                        _failCount = 0;
                    } else {
                        spawnNextNpc(b_small_adult_gourd, loc, reflection, owner);

                        _successCount = 0;
                        _failCount = 0;
                    }
                    attacker.sendPacket(new PlaySoundPacket("ItemSound3.sys_sow_success"));
                } else if (skillId == s_gourd_nectar) {
                    _tryCount++;

                    if (Rnd.get(1000) < 631) {
                        _successCount++;

                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
                        actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_good, 1), actor, false);
                    } else {
                        _failCount++;

                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
                        actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_bad, 1), actor, false);
                    }
                } else if (Rnd.get(2) == 0)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_3));
            }
        } else if (npcId == g_small_adult_gourd) {
            if (!_attacked) {
                ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
                _attacked = true;
            } else {
                if (Rnd.get(2) == 0)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
            }
        } else if (npcId == b_small_adult_gourd) {
            if (!_attacked) {
                ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
                _attacked = true;
            } else {
                if (Rnd.get(2) == 0)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
            }
        } else if (npcId == big_baby_gourd) {
            if (!_attacked) {
                _tryCount = 0;
                _successCount = 0;
                _failCount = 0;

                if (skillId == s_gourd_nectar) {
                    _tryCount++;

                    if (Rnd.get(1000) < 631) {
                        _successCount++;

                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
                        actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_good, 1), actor, false);
                    } else {
                        _failCount++;

                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
                        actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_bad, 1), actor, false);
                    }
                }
                _attacked = true;
            } else {
                if (skillId == s_gourd_nectar && _tryCount >= 4) {
                    _tryCount++;

                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_2));

                    Player owner = actor.getPlayer();
                    Location loc = actor.getLoc();
                    Reflection reflection = actor.getReflection();

                    actor.deleteMe();

                    if (Rnd.get(1000) < 631)
                        _successCount++;
                    else
                        _failCount++;

                    if (_successCount == 5) {
                        spawnNextNpc(kg_big_adult_gourd, loc, reflection, owner);

                        _successCount = 0;
                        _failCount = 0;
                    } else if (_successCount == 4) {
                        spawnNextNpc(g_big_adult_gourd, loc, reflection, owner);

                        _successCount = 0;
                        _failCount = 0;
                    } else {
                        spawnNextNpc(b_big_adult_gourd, loc, reflection, owner);

                        _successCount = 0;
                        _failCount = 0;
                    }
                    attacker.sendPacket(new PlaySoundPacket("ItemSound3.sys_sow_success"));
                } else if (skillId == s_gourd_nectar) {
                    _tryCount++;

                    if (Rnd.get(1000) < 631) {
                        _successCount++;

                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
                        actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_good, 1), actor, false);
                    } else {
                        _failCount++;

                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_1));
                        actor.doCast(SkillHolder.getInstance().getSkillEntry(s_gourd_nectar_bad, 1), actor, false);
                    }
                } else if (Rnd.get(5) == 0)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_3));
            }
        } else if (npcId == g_big_adult_gourd) {
            if (!_attacked) {
                int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
                if (weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));

                _attacked = true;
            } else {
                int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
                if (weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350) {
                    if (Rnd.get(5) == 0)
                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));
                } else if (Rnd.get(3) == 0)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_6));
            }
        } else if (npcId == b_big_adult_gourd) {
            if (!_attacked) {
                int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
                if (weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));

                _attacked = true;
            } else {
                int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
                if (weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350) {
                    if (Rnd.get(5) == 0)
                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));
                } else if (Rnd.get(3) == 0)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_6));
            }
        } else if (npcId == kg_small_adult_gourd) {
            if (!_attacked) {
                ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
                _attacked = true;
            } else {
                if (Rnd.get(2) == 0)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_4));
            }
        } else if (npcId == kg_big_adult_gourd) {
            if (!_attacked) {
                int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
                if (weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));

                _attacked = true;
            } else {
                int weaponId = attacker.getActiveWeaponInstance() == null ? 0 : attacker.getActiveWeaponInstance().getItemId();
                if (weaponId == 5817 || weaponId == 4202 || weaponId == 5133 || weaponId == 7058 || weaponId == 8350) {
                    if (Rnd.get(5) == 0)
                        ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_5));
                } else if (Rnd.get(3) == 0)
                    ChatUtils.say(actor, Rnd.get(ON_ATTACK_TEXTS_6));
            }
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        super.onEvtDead(killer);

        NpcInstance actor = getActor();
        if (actor == null)
            return;

        if (killer != null && actor.getDistance(killer) <= 1500) {
            int npcId = actor.getNpcId();
            if (npcId == small_baby_gourd)
                ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_1));
            else if (npcId == g_small_adult_gourd) {
                ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));

                int dropedCount = 0;
                if (Rnd.chance(30)) {
                    dropedCount++;
                    int i = Rnd.get(11);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 29688, 1);    // Свиток Регенерации
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 3926, 1);        // Свиток Наведения
                            break;
                        case 2:
                            actor.dropItem(actor.getPlayer(), 3927, 1);        // Свиток Шепота Смерти
                            break;
                        case 3:
                            actor.dropItem(actor.getPlayer(), 29689, 1);    // Свиток Магического Барьера
                            break;
                        case 4:
                            actor.dropItem(actor.getPlayer(), 3930, 1);        // Свиток Ускорения
                            break;
                        case 5:
                            actor.dropItem(actor.getPlayer(), 29690, 1);    // Свиток Благословения Тела
                            break;
                        case 6:
                            actor.dropItem(actor.getPlayer(), 29691, 1);    // Свиток Благословения Духа
                            break;
                        case 7:
                            actor.dropItem(actor.getPlayer(), 70107, 1);    // Свиток Духа Берсерка
                            break;
                        case 8:
                            actor.dropItem(actor.getPlayer(), 4218, 1);        // Свиток Регенерации Маны
                            break;
                        case 9:
                            actor.dropItem(actor.getPlayer(), 29013, 1);    // Свиток Песни Охотника
                            break;
                        case 10:
                            actor.dropItem(actor.getPlayer(), 29014, 1);    // Свиток Танца Огня
                            break;
                    }
                }
                if (Rnd.chance(15)) {
                    dropedCount++;
                    int i = Rnd.get(5);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 29011, 1);    // Свиток Эффектов 1-й Профессии
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 29012, 1);    // Свиток Эффектов 2-й Профессии
                            break;
                        case 2:
                            actor.dropItem(actor.getPlayer(), 49080, 1);    // Рисовый Пирожок Боевого Духа
                            break;
                        case 3:
                            actor.dropItem(actor.getPlayer(), 29008, 1);    // Свиток Опыта/SP
                            break;
                        case 4:
                            actor.dropItem(actor.getPlayer(), 49518, 1);    // Особый Плод Пиратов
                            break;
                    }
                }
                if (Rnd.chance(10)) {
                    dropedCount++;
                    int i = Rnd.get(5);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 5592, 2);        // Большое Зелье CP
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 29695, 1);    // Быстродействующее Зелье Исцеления
                            break;
                        case 2:
                            actor.dropItem(actor.getPlayer(), 70159, 2);    // Зелье Восстановления Маны
                            break;
                        case 3:
                            actor.dropItem(actor.getPlayer(), 29029, 1);    // Свиток Благ. Телепорта
                            break;
                        case 4:
                            actor.dropItem(actor.getPlayer(), 29030, 1);    // Свиток Благ. Воскрешения
                            break;
                    }
                }
                if (Rnd.chance(10)) {
                    if (Rnd.chance(60)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 29584, 1);    // Сундук с Благословением Кота-Ангела
                    } else if (Rnd.chance(30)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49541, 1);    // Сундук с Книгой Заклинаний
                    } else if (Rnd.chance(2)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49491, 1);    // Сундук с Оружием Ранга D
                    }
                }
                if (Rnd.chance(5)) {
                    if (Rnd.chance(30)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 952, 1);        // Свиток: Модифицировать Доспех (С)
                    } else if (Rnd.chance(15)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 951, 1);        // Свиток: Модифицировать Оружие (С)
                    } else if (Rnd.chance(10)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6576, 1);        // Благословенный Свиток: Модифицировать Доспех (D)
                    } else if (Rnd.chance(5)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6575, 1);        // Благословенный Свиток: Модифицировать Оружие (D)
                    } else if (Rnd.chance(2)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6574, 1);        // Благословенный Свиток: Модифицировать Доспех (C)
                    } else if (Rnd.chance(1)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6573, 1);        // Благословенный Свиток: Модифицировать Оружие (C)
                    }
                }
                if (dropedCount < 1)
                    actor.dropItem(actor.getPlayer(), 6391, 5);    // Нектар

            } else if (npcId == b_small_adult_gourd) {
                ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));

                int dropedCount = 0;
                if (Rnd.chance(30)) {
                    dropedCount++;
                    int i = Rnd.get(18);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 29684, 1);    // Свиток Щита
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 29685, 1);    // Свиток Могущества
                            break;
                        case 2:
                            actor.dropItem(actor.getPlayer(), 29686, 1);    // Свиток Концентрации
                            break;
                        case 3:
                            actor.dropItem(actor.getPlayer(), 3929, 1);        // Свиток Проницательности
                            break;
                        case 4:
                            actor.dropItem(actor.getPlayer(), 3928, 1);        // Свиток Фокусировки
                            break;
                        case 5:
                            actor.dropItem(actor.getPlayer(), 29687, 1);    // Свиток Проворства
                            break;
                        case 6:
                            actor.dropItem(actor.getPlayer(), 3932, 1);        // Свиток Воодушевления
                            break;
                        case 7:
                            actor.dropItem(actor.getPlayer(), 29688, 1);    // Свиток Регенерации
                            break;
                        case 8:
                            actor.dropItem(actor.getPlayer(), 3926, 1);        // Свиток Наведения
                            break;
                        case 9:
                            actor.dropItem(actor.getPlayer(), 3927, 1);        // Свиток Шепота Смерти
                            break;
                        case 10:
                            actor.dropItem(actor.getPlayer(), 29689, 1);    // Свиток Магического Барьера
                            break;
                        case 11:
                            actor.dropItem(actor.getPlayer(), 3930, 1);        // Свиток Ускорения
                            break;
                        case 12:
                            actor.dropItem(actor.getPlayer(), 29690, 1);    // Свиток Благословения Тела
                            break;
                        case 13:
                            actor.dropItem(actor.getPlayer(), 29691, 1);    // Свиток Благословения Духа
                            break;
                        case 14:
                            actor.dropItem(actor.getPlayer(), 70107, 1);    // Свиток Духа Берсерка
                            break;
                        case 15:
                            actor.dropItem(actor.getPlayer(), 4218, 1);        // Свиток Регенерации Маны
                            break;
                        case 16:
                            actor.dropItem(actor.getPlayer(), 29013, 1);    // Свиток Песни Охотника
                            break;
                        case 17:
                            actor.dropItem(actor.getPlayer(), 29014, 1);    // Свиток Танца Огня
                            break;
                    }
                }
                if (Rnd.chance(15)) {
                    dropedCount++;
                    int i = Rnd.get(2);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 29011, 1);    // Свиток Эффектов 1-й Профессии
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 49080, 1);    // Рисовый Пирожок Боевого Духа
                            break;
                    }
                }
                if (Rnd.chance(10)) {
                    dropedCount++;
                    int i = Rnd.get(2);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 5592, 1);        // Большое Зелье CP
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 1539, 1);        // Сильнодействующее Зелье Исцеления
                            break;
                    }
                }
                if (Rnd.chance(10)) {
                    dropedCount++;
                    actor.dropItem(actor.getPlayer(), 29584, 1);    // Сундук с Благословением Кота-Ангела
                }
                if (Rnd.chance(5)) {
                    if (Rnd.chance(30)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 956, 1);        // Свиток: Модифицировать Доспех (D)
                    } else if (Rnd.chance(15)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 955, 1);        // Свиток: Модифицировать Оружие (D)
                    } else if (Rnd.chance(10)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 952, 1);        // Свиток: Модифицировать Доспех (С)
                    } else if (Rnd.chance(5)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 951, 1);        // Свиток: Модифицировать Оружие (С)
                    } else if (Rnd.chance(2)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6576, 1);        // Благословенный Свиток: Модифицировать Доспех (D)
                    } else if (Rnd.chance(1)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6575, 1);        // Благословенный Свиток: Модифицировать Оружие (D)
                    }
                }
                if (dropedCount < 1)
                    actor.dropItem(actor.getPlayer(), 6391, 3);    // Нектар
            } else if (npcId == big_baby_gourd)
                ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_1));
            else if (npcId == g_big_adult_gourd) {
                ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));

                int dropedCount = 0;
                if (Rnd.chance(30)) {
                    dropedCount++;
                    actor.dropItem(actor.getPlayer(), 49081, 1);    // Рисовый Пирожок Пылающего Боевого Духа
                }
                if (Rnd.chance(20)) {
                    dropedCount++;
                    int i = Rnd.get(2);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 70159, 5);    // Зелье Восстановления Маны
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 29696, 1);    // Большое Быстродействующее Зелье Исцеления
                            break;
                    }
                }
                if (Rnd.chance(10)) {
                    if (Rnd.chance(60)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 29584, 1);    // Сундук с Благословением Кота-Ангела
                    } else if (Rnd.chance(30)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49541, 1);    // Сундук с Книгой Заклинаний
                    } else if (Rnd.chance(5)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49527, 1);    // Коробка Телепорта к Флагу
                    } else if (Rnd.chance(2)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49492, 1);    // Сундук с Оружием Ранга C
                    } else if (Rnd.chance(1)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49525, 1);    // Сундук Редких Аксессуаров
                    }
                }
                if (Rnd.chance(5)) {
                    if (Rnd.chance(30)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 33813, 1);    // Улучшенный Свиток: Модифицировать Доспех (С)
                    } else if (Rnd.chance(15)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 33807, 1);    // Улучшенный Свиток: Модифицировать Оружие (С)
                    } else if (Rnd.chance(10)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 33814, 1);    // Улучшенный Свиток: Модифицировать Доспех (B)
                    } else if (Rnd.chance(5)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 33808, 1);    // Улучшенный Свиток: Модифицировать Оружие (B)
                    } else if (Rnd.chance(2)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6572, 1);        // Благословенный Свиток: Модифицировать Доспех (B)
                    } else if (Rnd.chance(1)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6571, 1);        // Благословенный Свиток: Модифицировать Оружие (B)
                    }
                }
                if (Rnd.chance(1)) {
                    if (Rnd.chance(60)) {
                        dropedCount++;
                        int i = Rnd.get(2);
                        switch (i) {
                            case 0:
                                actor.dropItem(actor.getPlayer(), 13501, 1);    // Элегантная Модная Шляпа
                                break;
                            case 1:
                                actor.dropItem(actor.getPlayer(), 5808, 1);        // Карнавальная Маска
                                break;
                        }
                    } else {
                        dropedCount++;
                        int i = Rnd.get(18);
                        switch (i) {
                            case 0:
                                actor.dropItem(actor.getPlayer(), 70146, 1);    // Сундук Производства Черного Ожерелья
                                break;
                            case 1:
                                actor.dropItem(actor.getPlayer(), 70147, 1);    // Сундук Производства Черного Кольца
                                break;
                            case 2:
                                actor.dropItem(actor.getPlayer(), 70148, 1);    // Сундук Производства Черной Серьги
                                break;
                            case 3:
                                actor.dropItem(actor.getPlayer(), 70140, 1);    // Сундук Производства Мантии Авадона
                                break;
                            case 4:
                                actor.dropItem(actor.getPlayer(), 70141, 1);    // Сундук Производства Диадемы Авадона
                                break;
                            case 5:
                                actor.dropItem(actor.getPlayer(), 70142, 1);    // Сундук Производства Перчаток Авадона
                                break;
                            case 6:
                                actor.dropItem(actor.getPlayer(), 70143, 1);    // Сундук Производства Сапог Авадона
                                break;
                            case 7:
                                actor.dropItem(actor.getPlayer(), 70129, 1);    // Сундук Производства Щита Рока
                                break;
                            case 8:
                                actor.dropItem(actor.getPlayer(), 70130, 1);    // Сундук Производства Латного Доспеха Рока
                                break;
                            case 9:
                                actor.dropItem(actor.getPlayer(), 70131, 1);    // Сундук Производства Шлема Рока
                                break;
                            case 10:
                                actor.dropItem(actor.getPlayer(), 70132, 1);    // Сундук Производства Перчаток Рока
                                break;
                            case 11:
                                actor.dropItem(actor.getPlayer(), 70133, 1);    // Сундук Производства Сапог Рока
                                break;
                            case 12:
                                actor.dropItem(actor.getPlayer(), 70134, 1);    // Сундук Производства Кирасы Синего Волка
                                break;
                            case 13:
                                actor.dropItem(actor.getPlayer(), 70135, 1);    // Сундук Производства Набедренников Синего Волка
                                break;
                            case 14:
                                actor.dropItem(actor.getPlayer(), 70136, 1);    // Сундук Производства Шлема Синего Волка
                                break;
                            case 15:
                                actor.dropItem(actor.getPlayer(), 70137, 1);    // Сундук Производства Перчаток Синего Волка
                                break;
                            case 16:
                                actor.dropItem(actor.getPlayer(), 70138, 1);    // Сундук Производства Сапог Синего Волка
                                break;
                            case 17:
                                actor.dropItem(actor.getPlayer(), 70139, 1);    // Сундук Производства Кожаного Доспеха Рока
                                break;
                        }
                    }
                }
                if (dropedCount < 1) {
                    int i = Rnd.get(3);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 29012, 1);    // Свиток Эффектов 2-й Профессии
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 49518, 1);    // Особый Плод Пиратов
                            break;
                        case 2:
                            actor.dropItem(actor.getPlayer(), 29009, 1);    // Свиток Опыта/SP - Обычный
                            break;
                    }
                }
            } else if (npcId == b_big_adult_gourd) {
                ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));

                int dropedCount = 0;
                if (Rnd.chance(30)) {
                    dropedCount++;
                    actor.dropItem(actor.getPlayer(), 49081, 1);    // Рисовый Пирожок Пылающего Боевого Духа
                }
                if (Rnd.chance(20)) {
                    dropedCount++;
                    int i = Rnd.get(6);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 5592, 3);        // Большое Зелье CP
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 70159, 3);    // Зелье Восстановления Маны
                            break;
                        case 2:
                            actor.dropItem(actor.getPlayer(), 29695, 1);    // Быстродействующее Зелье Исцеления
                            break;
                        case 3:
                            actor.dropItem(actor.getPlayer(), 29696, 1);    // Большое Быстродействующее Зелье Исцеления
                            break;
                        case 4:
                            actor.dropItem(actor.getPlayer(), 29029, 1);    // Свиток Благ. Телепорта
                            break;
                        case 5:
                            actor.dropItem(actor.getPlayer(), 29030, 1);    // Свиток Благ. Воскрешения
                            break;
                    }
                }
                if (Rnd.chance(10)) {
                    if (Rnd.chance(60)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 29584, 1);    // Сундук с Благословением Кота-Ангела
                    } else if (Rnd.chance(30)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49541, 1);    // Сундук с Книгой Заклинаний
                    } else if (Rnd.chance(5)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49527, 1);    // Коробка Телепорта к Флагу
                    } else if (Rnd.chance(2)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49491, 1);    // Сундук с Оружием Ранга D
                    } else if (Rnd.chance(1)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49492, 1);    // Сундук с Оружием Ранга C
                    }
                }
                if (Rnd.chance(5)) {
                    if (Rnd.chance(30)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 948, 1);        // Свиток: Модифицировать Доспех (B)
                    } else if (Rnd.chance(15)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 947, 1);        // Свиток: Модифицировать Оружие (B)
                    } else if (Rnd.chance(10)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6574, 1);        // Благословенный Свиток: Модифицировать Доспех (C)
                    } else if (Rnd.chance(5)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6573, 1);        // Благословенный Свиток: Модифицировать Оружие (C)
                    } else if (Rnd.chance(2)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6572, 1);        // Благословенный Свиток: Модифицировать Доспех (B)
                    } else if (Rnd.chance(1)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 6571, 1);        // Благословенный Свиток: Модифицировать Оружие (B)
                    }
                }
                if (Rnd.chance(1)) {
                    if (Rnd.chance(20)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 13501, 1);    // Элегантная Модная Шляпа
                    } else if (Rnd.chance(20)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 5808, 1);        // Карнавальная Маска
                    }
                }
                if (dropedCount < 1) {
                    int i = Rnd.get(4);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 29012, 1);    // Свиток Эффектов 2-й Профессии
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 49518, 1);    // Особый Плод Пиратов
                            break;
                        case 2:
                            actor.dropItem(actor.getPlayer(), 49080, 1);    // Рисовый Пирожок Боевого Духа
                            break;
                        case 3:
                            actor.dropItem(actor.getPlayer(), 29009, 1);    // Свиток Опыта/SP - Обычный
                            break;
                    }
                }
            } else if (npcId == kg_small_adult_gourd) {
                ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));

                int dropedCount = 0;
                if (Rnd.chance(50)) {
                    dropedCount++;
                    int i = Rnd.get(3);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 29013, 1);    // Свиток Песни Охотника
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 29014, 1);    // Свиток Танца Огня
                            break;
                        case 2:
                            actor.dropItem(actor.getPlayer(), 4218, 1);        // Свиток Регенерации Маны
                            break;
                    }
                }
                if (Rnd.chance(40)) {
                    dropedCount++;
                    int i = Rnd.get(4);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 29012, 1);    // Свиток Эффектов 2-й Профессии
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 49080, 1);    // Рисовый Пирожок Боевого Духа
                            break;
                        case 2:
                            actor.dropItem(actor.getPlayer(), 29009, 1);    // Свиток Опыта/SP - Обычный
                            break;
                        case 3:
                            actor.dropItem(actor.getPlayer(), 49518, 1);    // Особый Плод Пиратов
                            break;
                    }
                }
                if (Rnd.chance(20)) {
                    dropedCount++;
                    int i = Rnd.get(6);
                    switch (i) {
                        case 0:
                            actor.dropItem(actor.getPlayer(), 5592, 2);        // Большое Зелье CP
                            break;
                        case 1:
                            actor.dropItem(actor.getPlayer(), 70159, 3);    // Зелье Восстановления Маны
                            break;
                        case 2:
                            actor.dropItem(actor.getPlayer(), 29695, 1);    // Быстродействующее Зелье Исцеления
                            break;
                        case 3:
                            actor.dropItem(actor.getPlayer(), 29696, 1);    // Большое Быстродействующее Зелье Исцеления
                            break;
                        case 4:
                            actor.dropItem(actor.getPlayer(), 29029, 1);    // Свиток Благ. Телепорта
                            break;
                        case 5:
                            actor.dropItem(actor.getPlayer(), 29030, 1);    // Свиток Благ. Воскрешения
                            break;
                    }
                }
                if (Rnd.chance(10)) {
                    if (Rnd.chance(60)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 29584, 1);    // Сундук с Благословением Кота-Ангела
                    } else if (Rnd.chance(30)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49541, 1);    // Сундук с Книгой Заклинаний
                    } else if (Rnd.chance(5)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49527, 1);    // Коробка Телепорта к Флагу
                    } else if (Rnd.chance(2)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49491, 1);    // Сундук с Оружием Ранга D
                    } else if (Rnd.chance(1)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49492, 1);    // Сундук с Оружием Ранга C
                    }
                }
                if (Rnd.chance(5)) {
                    if (Rnd.chance(70)) {
                        if (Rnd.chance(30)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 33812, 1);    // Улучшенный Свиток: Модифицировать Доспех (D)
                        } else if (Rnd.chance(15)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 33806, 1);    // Улучшенный Свиток: Модифицировать Оружие (D)
                        } else if (Rnd.chance(10)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 33813, 1);    // Улучшенный Свиток: Модифицировать Доспех (С)
                        } else if (Rnd.chance(5)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 33807, 1);    // Улучшенный Свиток: Модифицировать Оружие (С)
                        }
                    } else {
                        if (Rnd.chance(30)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 6576, 1);        // Благословенный Свиток: Модифицировать Доспех (D)
                        } else if (Rnd.chance(15)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 6575, 1);        // Благословенный Свиток: Модифицировать Оружие (D)
                        } else if (Rnd.chance(10)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 6574, 1);        // Благословенный Свиток: Модифицировать Доспех (C)
                        } else if (Rnd.chance(5)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 6573, 1);        // Благословенный Свиток: Модифицировать Оружие (C)
                        }
                    }
                }
                if (Rnd.chance(1)) {
                    if (Rnd.chance(20)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 13501, 1);    // Элегантная Модная Шляпа
                    } else if (Rnd.chance(20)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 5808, 1);        // Карнавальная Маска
                    }
                }
                if (dropedCount < 1)
                    actor.dropItem(actor.getPlayer(), 6391, 10);    // Нектар
            } else if (npcId == kg_big_adult_gourd) {
                ChatUtils.say(actor, Rnd.get(ON_DEAD_TEXTS_2));

                int dropedCount = 0;
                if (Rnd.chance(40)) {
                    if (Rnd.chance(60)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 29584, 1);    // Сундук с Благословением Кота-Ангела
                    } else if (Rnd.chance(30)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49541, 1);    // Сундук с Книгой Заклинаний
                    } else if (Rnd.chance(5)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49527, 1);    // Коробка Телепорта к Флагу
                    } else if (Rnd.chance(2)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49492, 1);    // Сундук с Оружием Ранга C
                    } else if (Rnd.chance(1)) {
                        dropedCount++;
                        actor.dropItem(actor.getPlayer(), 49525, 1);    // Сундук Редких Аксессуаров
                    }
                }
                if (Rnd.chance(15)) {
                    if (Rnd.chance(60)) {
                        if (Rnd.chance(30)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 6574, 1);        // Благословенный Свиток: Модифицировать Доспех (C)
                        } else if (Rnd.chance(15)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 6573, 1);        // Благословенный Свиток: Модифицировать Оружие (C)
                        } else if (Rnd.chance(10)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 6572, 1);        // Благословенный Свиток: Модифицировать Доспех (B)
                        } else if (Rnd.chance(5)) {
                            dropedCount++;
                            actor.dropItem(actor.getPlayer(), 6571, 1);        // Благословенный Свиток: Модифицировать Оружие (B)
                        }
                    } else {
                        if (Rnd.chance(60)) {
                            if (Rnd.chance(30)) {
                                dropedCount++;
                                actor.dropItem(actor.getPlayer(), 33813, 1);    // Улучшенный Свиток: Модифицировать Доспех (С)
                            } else if (Rnd.chance(15)) {
                                dropedCount++;
                                actor.dropItem(actor.getPlayer(), 33807, 1);    // Улучшенный Свиток: Модифицировать Оружие (С)
                            } else if (Rnd.chance(10)) {
                                dropedCount++;
                                actor.dropItem(actor.getPlayer(), 33814, 1);    // Улучшенный Свиток: Модифицировать Доспех (B)
                            } else if (Rnd.chance(5)) {
                                dropedCount++;
                                actor.dropItem(actor.getPlayer(), 33808, 1);    // Улучшенный Свиток: Модифицировать Оружие (B)
                            }
                        } else {
                            if (Rnd.chance(30)) {
                                dropedCount++;
                                actor.dropItem(actor.getPlayer(), 49483, 1);    // Стабильный Свиток: Модифицировать Доспех (C)
                            } else if (Rnd.chance(15)) {
                                dropedCount++;
                                actor.dropItem(actor.getPlayer(), 49484, 1);    // Стабильный Свиток: Модифицировать Оружие (C)
                            } else if (Rnd.chance(10)) {
                                dropedCount++;
                                actor.dropItem(actor.getPlayer(), 49485, 1);    // Стабильный Свиток: Модифицировать Доспех (B)
                            } else if (Rnd.chance(5)) {
                                dropedCount++;
                                actor.dropItem(actor.getPlayer(), 49486, 1);    // Стабильный Свиток: Модифицировать Оружие (B)
                            }
                        }
                    }
                }
                if (Rnd.chance(1)) {
                    if (Rnd.chance(60)) {
                        dropedCount++;
                        int i = Rnd.get(2);
                        switch (i) {
                            case 0:
                                actor.dropItem(actor.getPlayer(), 13501, 1);    // Элегантная Модная Шляпа
                                break;
                            case 1:
                                actor.dropItem(actor.getPlayer(), 5808, 1);        // Карнавальная Маска
                                break;
                        }
                    } else {
                        if (Rnd.chance(90)) {
                            dropedCount++;
                            int i = Rnd.get(18);
                            switch (i) {
                                case 0:
                                    actor.dropItem(actor.getPlayer(), 70146, 1);    // Сундук Производства Черного Ожерелья
                                    break;
                                case 1:
                                    actor.dropItem(actor.getPlayer(), 70147, 1);    // Сундук Производства Черного Кольца
                                    break;
                                case 2:
                                    actor.dropItem(actor.getPlayer(), 70148, 1);    // Сундук Производства Черной Серьги
                                    break;
                                case 3:
                                    actor.dropItem(actor.getPlayer(), 70140, 1);    // Сундук Производства Мантии Авадона
                                    break;
                                case 4:
                                    actor.dropItem(actor.getPlayer(), 70141, 1);    // Сундук Производства Диадемы Авадона
                                    break;
                                case 5:
                                    actor.dropItem(actor.getPlayer(), 70142, 1);    // Сундук Производства Перчаток Авадона
                                    break;
                                case 6:
                                    actor.dropItem(actor.getPlayer(), 70143, 1);    // Сундук Производства Сапог Авадона
                                    break;
                                case 7:
                                    actor.dropItem(actor.getPlayer(), 70129, 1);    // Сундук Производства Щита Рока
                                    break;
                                case 8:
                                    actor.dropItem(actor.getPlayer(), 70130, 1);    // Сундук Производства Латного Доспеха Рока
                                    break;
                                case 9:
                                    actor.dropItem(actor.getPlayer(), 70131, 1);    // Сундук Производства Шлема Рока
                                    break;
                                case 10:
                                    actor.dropItem(actor.getPlayer(), 70132, 1);    // Сундук Производства Перчаток Рока
                                    break;
                                case 11:
                                    actor.dropItem(actor.getPlayer(), 70133, 1);    // Сундук Производства Сапог Рока
                                    break;
                                case 12:
                                    actor.dropItem(actor.getPlayer(), 70134, 1);    // Сундук Производства Кирасы Синего Волка
                                    break;
                                case 13:
                                    actor.dropItem(actor.getPlayer(), 70135, 1);    // Сундук Производства Набедренников Синего Волка
                                    break;
                                case 14:
                                    actor.dropItem(actor.getPlayer(), 70136, 1);    // Сундук Производства Шлема Синего Волка
                                    break;
                                case 15:
                                    actor.dropItem(actor.getPlayer(), 70137, 1);    // Сундук Производства Перчаток Синего Волка
                                    break;
                                case 16:
                                    actor.dropItem(actor.getPlayer(), 70138, 1);    // Сундук Производства Сапог Синего Волка
                                    break;
                                case 17:
                                    actor.dropItem(actor.getPlayer(), 70139, 1);    // Сундук Производства Кожаного Доспеха Рока
                                    break;
                            }
                        } else
                            actor.dropItem(actor.getPlayer(), 6660, 1);        // Кольцо Королевы Муравьев
                    }
                }
                if (dropedCount < 1) {
                    if (Rnd.chance(70)) {
                        actor.dropItem(actor.getPlayer(), 29012, 1);    // Свиток Эффектов 2-й Профессии
                        actor.dropItem(actor.getPlayer(), 49518, 1);    // Особый Плод Пиратов
                        actor.dropItem(actor.getPlayer(), 29009, 1);    // Свиток Опыта/SP - Обычный
                    } else {
                        actor.dropItem(actor.getPlayer(), 29010, 1);    // Свиток Опыта/SP - Высокое Качество
                        actor.dropItem(actor.getPlayer(), 49081, 1);    // Рисовый Пирожок Пылающего Боевого Духа
                    }
                }
            }
            killer.sendPacket(new PlaySoundPacket("ItemSound.quest_middle"));
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    private static void spawnNextNpc(int npcId, Location loc, Reflection reflection, Player owner) {
        if (owner == null)
            return;

        NpcInstance npc = NpcUtils.spawnSingle(npcId, loc, reflection);
        npc.setOwner(owner);
        npc.onAction(owner, false);
    }
}