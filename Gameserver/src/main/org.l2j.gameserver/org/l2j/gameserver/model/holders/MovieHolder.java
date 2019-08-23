package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.enums.Movie;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author St3eT
 */
public final class MovieHolder {
    private final Movie _movie;
    private final List<Player> _players;
    private final Collection<Player> _votedPlayers = ConcurrentHashMap.newKeySet();

    public MovieHolder(List<Player> players, Movie movie) {
        _players = players;
        _movie = movie;

        _players.forEach(p -> p.playMovie(this));
    }

    public Movie getMovie() {
        return _movie;
    }

    public void playerEscapeVote(Player player) {
        if (_votedPlayers.contains(player) || !_players.contains(player) || !_movie.isEscapable()) {
            return;
        }

        _votedPlayers.add(player);

        if (((_votedPlayers.size() * 100) / _players.size()) >= 50) {
            _players.forEach(Player::stopMovie);
        }
    }

    public List<Player> getPlayers() {
        return _players;
    }
}