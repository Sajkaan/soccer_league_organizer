package com.teamtreehouse.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Menu {
  private List<Player> allPlayers;
  private List<Team> teams;
  private BufferedReader reader;
  private Map<String, String> menu;

  public Menu (Player[] players) {
    // Add all created players into a HashSet to
    // check for duplicates and then insert into
    // an ArrayList for easier handling the User
    // input and finding the specific element
    Set<Player> uniquePlayers = new HashSet<>();
    for (int i = 0; i < players.length; i++) {
      uniquePlayers.add(players[i]);
    }
    allPlayers = new ArrayList<>(uniquePlayers);

    Collections.sort(allPlayers);
    teams = new ArrayList<>();

    reader = new BufferedReader(new InputStreamReader(System.in));
    menu = new TreeMap<>();

    menu.put(" create ", "Create a new team");
    menu.put(" add    ", "Add a player to a team");
    menu.put(" roster ", "Prints out a team roster");
    menu.put(" height ", "Prints the height ");
    menu.put(" remove ", "Removes a player from a team");
    menu.put(" exp    ", "View teams by experience  ");
    menu.put(" quit   ", "Exit the program");
  }

  private String promptAction() throws IOException {
    System.out.println("Welcome to the MENU");
    System.out.println("Your options are: ");
    for (Map.Entry<String, String> options : menu.entrySet()) {
      System.out.printf("%n%s   | %s %n ",
          options.getKey(),
          options.getValue());
      System.out.print("-----------------------------------------");
    }
    System.out.print("\n\n\nWhat do you want to do : ");
    String choice = reader.readLine();
    return choice.trim().toLowerCase();
  }

  // Method that enables the user to make input choices from the offered menu items
  public void run() {
    String choice = "";
    do {
      try {
        choice = promptAction();
        switch (choice) {
          case "add":
            addPlayer();
            break;
          case "create":
            // Check and create a new team
            if (isNewTeamAllowed()){
              promptNewTeam();
            } else {
              System.out.println("Maximum number of teams is created.");
            }
            break;
          case "roster":
            printRoster();
            break;
          case "exp":
            expReport();
            break;
          case "height":
            heightReport();
            break;
          case "remove":
            removePlayer();
            break;
          case "quit":
            System.out.println("YOU HAVE EXITED THE PROGRAM.");
            break;
          default:
            System.out.printf("Unknown choice: '%s' . Please try again. %n%n%n", choice);
        }

      }catch (IOException ioe) {
        System.out.println("Problem with input.");
        ioe.printStackTrace();
      }

    }while(!choice.equals("quit"));
  }

  // Experience report
  private void expReport() {
    Map<Team, Integer> experiencedPlayerCount = new HashMap<>();

    for (Team team : teams) {
      int exp = 0;
      for (Player player : team.getPlayers()) {
        if (player.isPreviousExperience()) {
          exp++;
        }
      }
      experiencedPlayerCount.put(team, exp);
    }

    for (Team team : teams) {
      int experiencedCount = experiencedPlayerCount.get(team);
      int inexperiencedCount = team.getSize() - experiencedCount;
      float average = ((float) experiencedCount / team.getSize()) * 100;
      line();
      System.out.printf("Team: %s%n"
                      + "Experienced players: %d%nInexperienced players: %d%n"
                      + "Average experience: %.2f%% \n",
                        team.getTeamName(),
                        experiencedCount,
                        inexperiencedCount,
                        average);
      line();
    }
  }

  private void line() {
    System.out.println("=================================");
  }

  // Sorts team list by height starting from the shortest
  private void heightReport() throws IOException {
    List<Player> playersByHeight = new ArrayList<>();
    int teamIndex = selectTeam();
    Collections.sort(teams.get(teamIndex).getPlayers(), new Comparator<Player>() {

      @Override
      public int compare(Player o1, Player o2) {
        return Integer.compare(o1.getHeightInInches(), o2.getHeightInInches());
      }
    });
    System.out.printf("Height report for %s%n", teams.get(teamIndex).getTeamName());

    Map<String, Player> heightReport = new HashMap<>();
    for (Player player : teams.get(teamIndex).getPlayers()) {
      if (player.getHeightInInches() <= 40) {
        heightReport.put("35-40", player);
      } else if (player.getHeightInInches() > 40 && player.getHeightInInches() <= 46) {
        heightReport.put("41-46", player);
      } else {
        heightReport.put("47-50", player);
      }

      for (Map.Entry<String, Player> entry : heightReport.entrySet()) {
        System.out.printf("Height %s : %s%n",
                              entry.getKey(), entry.getValue().getPlayerInfo());
        line();

      }
    }
  }

  // Prints out team roster and checks if there is a
  // team available
  private void printRoster() throws IOException {
    if (isThereAnyTeam()) {
      int teamIndex = selectTeam();
      line();
      System.out.printf("Team roster for team %s%n", teams.get(teamIndex).getTeamName());
      printTeam(teamIndex);
      line();
    } else {
      System.out.println("No team roster available.");
      run();
    }
  }


  // Removes a player by asking first which
  // team to select then remove a player from the list
  private void removePlayer() throws IOException {
    if (isThereAnyTeam()) {
      int teamIndex = selectTeam();
      printTeam(teamIndex);
      System.out.print("\nSelect a player to remove: ");
      Player player = teams.get(teamIndex)
          .getPlayers()
          .get(inputForSelectingPlayers());
      allPlayers.add(player);
      teams.get(teamIndex).removePlayer(player);
      Collections.sort(allPlayers);
      System.out.printf("Removed %s %n", player.getPlayerInfo());
    } else {
      System.out.println("No team available.");
      run();
    }
  }

  private void printTeam(int teamIndex) {
    int playerCount = 1;
    for (Player player : teams.get(teamIndex).getPlayers()) {
      printPlayers(playerCount, player);
      playerCount++;
    }
  }

  // Input for selecting players that converts String to int
  private int inputForSelectingPlayers() throws IOException {
    System.out.print("Select a player: ");
    String choice = reader.readLine();
    int selectedPlayer = Integer.parseInt(choice);
    return --selectedPlayer;
  }

  // Adds the player to the team
  private void addPlayer() throws IOException {
    if (isThereAnyTeam()) {
      int teamIndex = selectTeam();
      getAvailablePlayers();
      Player player = allPlayers.get(inputForSelectingPlayers());

      // Adds the player to the team
      teams.get(teamIndex).addPlayer(player);

      // Removes the player from the main list
      allPlayers.remove(player);
      Collections.sort(allPlayers);
      System.out.printf("Added %s %n%n" ,player.getPlayerInfo());
    } else {
      run();
    }
  }

  // Prints available players from the MAIN-LIST
  private void getAvailablePlayers() {
    int playerCount = 1;
    System.out.println("\nAvailable players: ");
    for (Player player : allPlayers) {
      printPlayers(playerCount, player);
      playerCount++;
    }
    line();
  }

  // Prints players from loops
  private void printPlayers(int playerCount, Player player) {
    System.out.printf("%d .)  %s %n",
        playerCount,
        player.getPlayerInfo());
  }

  // Prompts for choosing teams
  private int selectTeam() throws IOException {
    System.out.println("Choose a team for adding players: ");
    int nr = 1;
    for (Team team : teams) {
      System.out.printf("%d ). %s | managed by coach %s %n",
          nr,
          team.getTeamName(),
          team.getCoachName());
      nr++;
    }
    String input = reader.readLine();
    int index = Integer.parseInt(input);
    return index - 1;
  }


  // Ask the user for input of the team name and coach
  private void promptNewTeam() throws IOException {
    System.out.print("Enter the new team name: ");
    String teamname = reader.readLine();
    System.out.print("Enter coach name: ");
    String coach = reader.readLine();

    Team team = new Team(teamname, coach);

    teams.add(team);
    Collections.sort(teams);
    System.out.printf("Team %s successfully added.%n%n", team.getTeamName());
  }

  /* Checks if the maximum number of teams is reached
     every team should have 11 players*/
  private boolean isNewTeamAllowed() {
    int max = allPlayers.size() / 11;
    if (max > teams.size()){
      return true;
    } else {
      return false;
    }
  }

  /* Checks the number of teams, if there isn't any
     team add,remove,roster and balance shouldn't
     be able to select*/
  private boolean isThereAnyTeam() {
    if (teams.size() == 0) {
      System.out.println("No teams available. Please create a new team first.\n");
      return false;
    } else {
      return true;
    }
  }
}