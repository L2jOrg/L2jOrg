/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.votereward;

import org.l2j.gameserver.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * @author Anarchy
 */
public class Topzone extends VoteSystem {
    public Topzone(int votesDiff, boolean allowReport, int boxes, Map<Integer, Integer> rewards, int checkMins) {
        super(votesDiff, allowReport, boxes, rewards, checkMins);
    }

    @Override
    public void run() {
        reward();
    }

    @Override
    public int getVotes() {
        InputStreamReader isr;
        BufferedReader br;

        try {
            URLConnection con = new URL(Config.TOPZONE_SERVER_LINK).openConnection();
            con.addRequestProperty("User-Agent", "Mozilla/5.0");
            isr = new InputStreamReader(con.getInputStream());
            br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(">Votes : ")) {
                    int votes = Integer.valueOf(line.split("<span class=\"classement\">")[1].split("</span>")[0]);
                    return votes;
                }
            }

            br.close();
            isr.close();
        } catch (Exception e) {
            LOGGER.warn("VoteSystem: Error while getting server vote count from {}.", getSiteName(), e);
        }

        return -1;
    }

    @Override
    public String getSiteName() {
        return "Gowonda";
    }
}
