package org.l2j.gameserver.model.votereward;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Anarchy
 */
public abstract class VoteSystem implements Runnable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(VoteSystem.class);

    private static List<VoteSystem> voteSystems = new ArrayList<>();

    private final int votesDiff;
    private final boolean allowReport;
    private final int boxes;
    private final Map<Integer, Integer> rewards;
    private final Map<String, Integer> playerIps = new HashMap<>();
    private int lastVotes = 0;

    public VoteSystem(int votesDiff, boolean allowReport, int boxes, Map<Integer, Integer> rewards, int checkMins) {
        this.votesDiff = votesDiff;
        this.allowReport = allowReport;
        this.boxes = boxes;
        this.rewards = rewards;

        ThreadPool.scheduleAtFixedRate(this, checkMins * 1000 * 60, checkMins * 1000 * 60);
    }

    public static void initialize() {
        if (Config.ALLOW_NETWORK_VOTE_REWARD || Config.ALLOW_TOPZONE_VOTE_REWARD || Config.ALLOW_HOPZONE_VOTE_REWARD) {
            LOGGER.info("VoteSystem: Initialized.");
            if (Config.ALLOW_NETWORK_VOTE_REWARD) {
                voteSystems.add(new Network(Config.NETWORK_VOTES_DIFFERENCE, Config.ALLOW_NETWORK_GAME_SERVER_REPORT, Config.NETWORK_DUALBOXES_ALLOWED, Config.NETWORK_REWARD, Config.NETWORK_REWARD_CHECK_TIME));
                LOGGER.info("VoteSystem: Network votes enabled.");
            } else {
                LOGGER.info("VoteSystem: Network votes disabled.");
            }
            if (Config.ALLOW_TOPZONE_VOTE_REWARD) {
                voteSystems.add(new Topzone(Config.TOPZONE_VOTES_DIFFERENCE, Config.ALLOW_TOPZONE_GAME_SERVER_REPORT, Config.TOPZONE_DUALBOXES_ALLOWED, Config.TOPZONE_REWARD, Config.TOPZONE_REWARD_CHECK_TIME));
                LOGGER.info("VoteSystem: Topzone votes enabled.");
            } else {
                LOGGER.info("VoteSystem: Topzone votes disabled.");
            }
            if (Config.ALLOW_HOPZONE_VOTE_REWARD) {
                voteSystems.add(new Hopzone(Config.HOPZONE_VOTES_DIFFERENCE, Config.ALLOW_HOPZONE_GAME_SERVER_REPORT, Config.HOPZONE_DUALBOXES_ALLOWED, Config.HOPZONE_REWARD, Config.HOPZONE_REWARD_CHECK_TIME));
                LOGGER.info("VoteSystem: Hopzone votes enabled.");
            } else {
                LOGGER.info("VoteSystem: Hopzone votes disabled.");
            }
        } else {
            LOGGER.info("VoteSystem: Disabled.");
        }
    }

    private static void announce(String msg) {
        CreatureSay cs = new CreatureSay(0, ChatType.CRITICAL_ANNOUNCE, "", msg);
        Broadcast.toAllOnlinePlayers(cs);
    }

    protected void reward() {
        int currentVotes = getVotes();

        if (currentVotes == -1) {
            LOGGER.info("VoteSystem: There was a problem on getting server votes.");
            return;
        }

        if (lastVotes == 0) {
            lastVotes = currentVotes;
            announce(getSiteName() + ": Current vote count is " + currentVotes + ".");
            announce(getSiteName() + ": We need " + ((lastVotes + votesDiff) - currentVotes) + " vote(s) for reward.");
            if (allowReport) {
                LOGGER.info("VoteSystem: Server votes on " + getSiteName() + ": " + currentVotes);
                LOGGER.info("VoteSystem: Votes needed for reward: " + ((lastVotes + votesDiff) - currentVotes));
            }
            return;
        }

        if (currentVotes >= (lastVotes + votesDiff)) {
            Collection<Player> pls = World.getInstance().getPlayers();
            if (allowReport) {
                LOGGER.info("VoteSystem: Server votes on " + getSiteName() + ": " + currentVotes);
                LOGGER.info("VoteSystem: Votes needed for next reward: " + ((currentVotes + votesDiff) - currentVotes));
            }
            announce(getSiteName() + ": Everyone has been rewarded.");
            announce(getSiteName() + ": Current vote count is " + currentVotes + ".");
            announce(getSiteName() + ": We need " + votesDiff + " vote(s) for next reward.");
            for (Player p : pls) {
                if ((p.getClient() == null) || p.getClient().isDetached()) {
                    continue;
                }

                boolean canReward = false;
                String pIp = p.getClient().getHostAddress();
                if (playerIps.containsKey(pIp)) {
                    int count = playerIps.get(pIp);
                    if (count < boxes) {
                        playerIps.remove(pIp);
                        playerIps.put(pIp, count + 1);
                        canReward = true;
                    }
                } else {
                    canReward = true;
                    playerIps.put(pIp, 1);
                }
                if (canReward) {
                    for (int i : rewards.keySet()) {
                        p.addItem("Vote reward.", i, rewards.get(i), p, true);
                    }
                } else {
                    p.sendMessage("Already " + boxes + " character(s) of your ip have been rewarded, so this character won't be rewarded.");
                }
            }
            playerIps.clear();

            lastVotes = currentVotes;
        } else {
            if (allowReport) {
                LOGGER.info("VoteSystem: Server votes on " + getSiteName() + ": " + currentVotes);
                LOGGER.info("VoteSystem: Votes needed for next reward: " + ((lastVotes + votesDiff) - currentVotes));
            }
            announce(getSiteName() + ": Current vote count is " + currentVotes + ".");
            announce(getSiteName() + ": We need " + ((lastVotes + votesDiff) - currentVotes) + " vote(s) for reward.");
        }
    }

    public abstract int getVotes();

    public abstract String getSiteName();
}