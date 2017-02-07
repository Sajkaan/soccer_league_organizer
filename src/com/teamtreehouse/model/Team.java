package com.teamtreehouse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team implements Comparable{
  private String teamName;
  private String coachName;
  private List<Player> players;

  public Team(String teamName, String coachName) {
    this.teamName = teamName;
    this.coachName = coachName;
    players = new ArrayList<>();
  }

  public List<Player> getPlayers() {
    return players;
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  public String getCoachName() {
    return coachName;
  }

  public void setCoachName(String coachName) {
    this.coachName = coachName;
  }

  public void addPlayer(Player player) {
    players.add(player);
    Collections.sort(players);
  }

  public void removePlayer(Player player) {
    players.remove(player);
    Collections.sort(players);
  }

  @Override
  public int compareTo(Object o) {
    Team other = (Team) o;
    if (equals(other)) {
      return 0;
    }
    return teamName.compareTo(other.teamName);
  }

  public int getSize() {
    return players.size();
  }
}