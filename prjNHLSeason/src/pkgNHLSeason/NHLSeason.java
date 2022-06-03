package pkgNHLSeason;

import java.io.*;
import java.sql.*;
import java.util.*;

// Project Name:	NHLSeason
// Author:			Christian Gibbons
// Date Created:	5/24/2020

// Description: 	This program is to process the events that occur in a typical NHL Season (Console only)
//					The program is capable of:
//						1. Entering 1-16 games in one day of NHL action, and updating team records accurately based on game results. (Wins, losses, etc.)
//						2. Displaying accurate standings of all NHL teams using several tiebreakers.
//						3. Displaying one teams record, as well as all of their games played saved in the game.txt file.
//						4. a. Begin and/or continue a playoffs (if previous playoffs are saved in playoffs.txt) where the user can enter game stats for a
//							  	seven game series in the Stanley Cup Playoffs.
//						   b. Correctly "seed" each team in each round of the playoffs based on their record in the regular season.
//						5. Perform a draft lottery simulation at the end of the playoffs based on the standings of those teams that missed the Stanley Cup 
//						     	playoffs.
//						6. Saving and reseting save data for teams records (does not erase team information such as city, team name, division), playoff data,
//								game data, and standings.
//
//					Note: This program DOES NOT process player information including player names and player stats (goals, assists, etc.).
//

public class NHLSeason
{
	final int numNHLTeams = 31;
	final int numPacificTeams = 8;
	final int numCentralTeams = 7;
	final int numMetroTeams = 8;
	final int numAtlanticTeams = 8;
	final int numEastTeams = numAtlanticTeams + numMetroTeams;
	final int numWestTeams = numCentralTeams + numPacificTeams;
	final int numPlayoffSeries = 15;
	
	Team[] teamArray = new Team[numNHLTeams];
	ArrayList<Game> gameArray = new ArrayList<Game>();
	PlayoffSeries[] playoffArray = new PlayoffSeries[numPlayoffSeries];
	
	// Standings array ( standings[0] is best team in league at start of playoffs )
	int[] standings = new int[numNHLTeams];

	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	NHLSeason() throws IOException
	{
		loadTeams();
		loadGames();
		loadPlayoffs();
		loadStandings();
	}
	
	void buffer() throws IOException
	{
		System.out.print("Press ENTER to continue");
		br.readLine();
		System.out.println();
	}
	
	void bufferMenu() throws IOException
	{
		System.out.print("Press ENTER to return to Main Menu");
		br.readLine();
		System.out.println();
	}
	
	void bufferStandingsMenu() throws IOException
	{
		System.out.print("Press ENTER to return to Standings Menu");
		br.readLine();
		System.out.println();
	}
	
	void bufferPlayoffMenu() throws IOException
	{
		System.out.print("Press ENTER to return to Playoff Menu");
		br.readLine();
		System.out.println();
	}
	
	void bufferSeriesMenu() throws IOException
	{
		System.out.print("Press ENTER to return to Series Menu");
		br.readLine();
		System.out.println();
	}
	
	void bufferLotteryMenu() throws IOException
	{
		System.out.print("Press ENTER to return to Lottery Menu");
		br.readLine();
		System.out.println();
	}
	
	void mainMenu() throws IOException
	{
		int mainMenuChoice = -1;
		String resetConfirm = "";
		String saveConfirm = "";
		boolean validInput = false;
		
		do
		{
			validInput = false;
			resetConfirm = "";
			saveConfirm = "";
			
			System.out.print("Main Menu: \n" +
				"\t1)  Enter games for one day\n" +
				"\t2)  Display Current Standings\n" +
				"\t3)  Display One Team's Stats\n" +
				"\t4)  View Legend for Standings\n" +
				"\t5)  View Tiebreaking Procedure\n" +
				"\t6)  Playoff Menu\n" +
				"\t7)  View Playoff Procedure\n" +
				"\t8)  Save Season Progress\n" +
				"\t9)  Reset Season\n" +
				"\t10) Exit Program\n\n" +
				"Enter your choice (1-10): ");
			
			mainMenuChoice = Integer.parseInt(br.readLine());
			
			switch(mainMenuChoice) {
				
				case(1):
					enterGames();
					break;
					
				case(2):
					viewStandingsMenu();
					break;	
					
				case(3):
					viewOneTeamStats();
					break;
				
				case(4):
					viewLegend();
					break;
					
				case(5):
					viewTiebreakingProcedure();
					break;
					
				case(6):
					playoffMenu();
					break;
					
				case(7):
					viewPlayoffProcedure();
					break;
					
				case(8):
					saveTeams();
					saveGames();
					savePlayoffs();
					saveStandings();
					//updateDB();
					
					System.out.println("\nSeason Progress has been Saved.\n");
					bufferMenu();
					break;
				
				case(9):					
					while(!validInput)
					{
						System.out.print("\nAre you sure you would like to reset the current season progress? (Y/N) ");
						resetConfirm = br.readLine();
						
						if (resetConfirm.equalsIgnoreCase("Y"))
						{
							validInput = true;
							
							resetSeason();
							saveTeams();
							saveGames();
							savePlayoffs();
							saveStandings();
							//updateDB();
							
							System.out.println("\nSeason Progress has been Reset.\n");
						}
						else if (resetConfirm.equalsIgnoreCase("N"))
						{
							validInput = true;
							System.out.println("\nReset has been cancelled.\n");
						}
						else
						{
							System.out.print("\nError. Please enter either \"Y\" for Yes or \"N\" for No");
						}
					}
					bufferMenu();
					break;
				
				case(10): 
					while(!validInput)
					{
						System.out.print("\nWould you like to save current season progress? (Y/N) ");
						saveConfirm = br.readLine();
						
						if (saveConfirm.equalsIgnoreCase("Y"))
						{
							validInput = true;
							
							saveTeams();
							saveGames();
							savePlayoffs();
							saveStandings();
							updateDB();
							
							System.out.println("\nSeason Progress has been Saved.\n");
						}
						else if (saveConfirm.equalsIgnoreCase("N"))
						{
							validInput = true;
						}
						else
						{
							System.out.print("\nError. Please enter either \"Y\" for Yes or \"N\" for No");
						}
					}
					// Program Exiting...
					break;
				
				default:
					System.out.println("\nError. Please enter a choice between 1 and 10.\n");
					
			}
			
		} while (mainMenuChoice != 10);		
	} // end of mainMenu()
	
	void enterGames() throws IOException
	{
		String date = "";
		int gamesOnDay = -1;
		
		String homeTeam = "";
		String awayTeam = "";
		int homeScore = -1;
		int awayScore = -1;
		boolean ot = false;
		boolean so = false;
		String userOT = "";
		String userSO = "";
		boolean validInput = false;
		boolean validTeams = false;
		boolean validScore = false;
		boolean homeWins = false;
		int homeIndex = -1;
		int awayIndex = -1;
		int difference = -20;
		
		System.out.println("\nEnter Games for one day:\n");
		
		System.out.print("Enter the date (MM/DD/YYYY): ");
		date = br.readLine();
		
		System.out.print("How many games occured on this day? ");
		gamesOnDay = Integer.parseInt(br.readLine());
		
		System.out.println();
		
		// Based on the fact that there are a max of 32 teams in the league
		// there cannot be more than 16 games in a day (as teams do not play twice in one day)
		if(gamesOnDay >= 1 && gamesOnDay <= 16)
		{
			for(int i = 0; i < gamesOnDay; i++)
			{
				homeTeam = "";
				awayTeam = "";
				homeScore = -1;
				awayScore = -1;
				ot = false;
				so = false;
				userOT = "";
				userSO = "";
				validTeams = false;
				validScore = false;
				homeWins = false;
				homeIndex = -1;
				awayIndex = -1;
				difference = -20;
								
				System.out.println("Game " + (i+1) + "/" + gamesOnDay);
				
				while(!validTeams)
				{
					// Enter home team
					validInput = false;
					
					while(!validInput)
					{
						System.out.print("Enter the home team (abbreviation): ");
						homeTeam = br.readLine();
						
						homeTeam = homeTeam.toUpperCase();
						
						for(int j = 0; j < teamArray.length; j++)
						{
							if(homeTeam.equals(teamArray[j].getTeamAbr()))
							{
								validInput = true;
								homeIndex = j;
								break;
							}
						}
						
						if (!validInput)
						{
							System.out.println("\nError. Team Abbreviation does not match any team in file.\n");
						}
					}
					
					// Enter away team
					validInput = false;
					
					while(!validInput)
					{
						System.out.print("Enter the away team (abbreviation): ");
						awayTeam = br.readLine();
						
						awayTeam = awayTeam.toUpperCase();
						
						for(int j = 0; j < teamArray.length; j++)
						{
							if(awayTeam.equals(teamArray[j].getTeamAbr()))
							{
								validInput = true;
								awayIndex = j;
								break;
							}
						}
						
						if (!validInput)
						{
							System.out.println("\nError. Team Abbreviation does not match any team in file.\n");
						}
					}
					
					if (homeTeam.equals(awayTeam))
					{
						System.out.println("\nError. Home team and Away team cannot be the same.\n");
					}
					else
					{
						validTeams = true;
					}

				}
					
				// Did the game read overtime
				validInput = false;
				
				while(!validInput)
				{
					System.out.print("\nDid the game reach Overtime? (Y/N) ");
					userOT = br.readLine();
					
					if(userOT.equalsIgnoreCase("Y"))
					{
						validInput = true;
						ot = true;
					}
					else if (userOT.equalsIgnoreCase("N"))
					{
						validInput = true;
						ot = false;
					}
					else
					{
						System.out.println("\nError. Please enter either \"Y\" for Yes or \"N\" for No");
					}
				}
				
				if (ot)
				{
					// Did the game reach a shootout
					validInput = false;
					
					while(!validInput)
					{
						System.out.print("Did the game reach a Shootout? (Y/N) ");
						userSO = br.readLine();
						
						if(userSO.equalsIgnoreCase("Y"))
						{
							validInput = true;
							so = true;
						}
						else if(userSO.equalsIgnoreCase("N"))
						{
							validInput = true;
							so = false;
						}
						else
						{
							System.out.println("\nError. Please enter either \"Y\" for Yes or \"N\" for No");
						}
					}		
				}
				
				// Enter both teams score
				while (!validScore)
				{
					// Final home team score
					validInput = false;
					
					while(!validInput)
					{
						System.out.print("\nEnter final " + homeTeam + " score: ");
						homeScore = Integer.parseInt(br.readLine());
						
						if(homeScore >= 0)
						{
							validInput = true;
						}
						else
						{
							System.out.println("\nError. Invalid Score (must be greater than or equal to 0\n");
						}
					}
					
					// Final away team score
					validInput = false;
					
					while(!validInput)
					{
						System.out.print("Enter final " + awayTeam + " score: ");
						awayScore = Integer.parseInt(br.readLine());
						
						if(awayScore >= 0)
						{
							validInput = true;
						}
						else
						{
							System.out.println("\nError. Invalid Score - must be greater than or equal to zero\n");
						}
					}
					
					difference = homeScore - awayScore;
					
					if (homeScore == 0 && awayScore == 0)
					{
						System.out.println("\nError. Invalid Score - both teams cannot have zero goals\n");
					}
					else if (homeScore == awayScore)
					{
						System.out.println("\nError. Invalid Score - both teams cannot have the same amount of goals\n");
					}
					else if ( ot && difference != 1 && difference != -1)
					{
						System.out.println("\nError. Invalid Score - Since the game entered OT / SO the final score between both teams must be one goal\n");
					}
					else
					{
						validScore = true;
						System.out.println();
						
						// Change records in Game ArrayList
						gameArray.add(new Game(date, homeTeam, awayTeam, homeScore, awayScore, ot, so));
						
						
						// Change records in Team array
						// There are six potential outcomes to a single game: Home Regulaion Win, Away Regulation Win, 
						// 		Home Overtime Win, Away Overtime Win, Home Shootout Win, Away Shootout Win
						
						if(homeScore > awayScore)
							homeWins = true;
						else
							homeWins = false;
						
						if (homeWins) // if home team wins
						{
							if (so) // home teams wins in a shootout
							{
								teamArray[homeIndex].setGp( teamArray[homeIndex].getGp() + 1 );
								teamArray[homeIndex].setW( teamArray[homeIndex].getW() + 1 );
								teamArray[homeIndex].setPts( teamArray[homeIndex].getPts() + 2 );
								teamArray[homeIndex].setGf( teamArray[homeIndex].getGf() + homeScore );
								teamArray[homeIndex].setGa( teamArray[homeIndex].getGa() + awayScore );
								
								////////////////////////////////////////////////////////////////////////////////////
								
								teamArray[awayIndex].setGp( teamArray[awayIndex].getGp() + 1 );
								teamArray[awayIndex].setOtl( teamArray[awayIndex].getOtl() + 1 );
								teamArray[awayIndex].setPts( teamArray[awayIndex].getPts() + 1 );
								teamArray[awayIndex].setGf( teamArray[awayIndex].getGf() + awayScore );
								teamArray[awayIndex].setGa( teamArray[awayIndex].getGa() + homeScore );
							}
							else if (ot) // home team wins in overtime
							{
								teamArray[homeIndex].setGp( teamArray[homeIndex].getGp() + 1 );
								teamArray[homeIndex].setW( teamArray[homeIndex].getW() + 1 );
								teamArray[homeIndex].setPts( teamArray[homeIndex].getPts() + 2 );
								teamArray[homeIndex].setRow( teamArray[homeIndex].getRow() + 1 );
								teamArray[homeIndex].setGf( teamArray[homeIndex].getGf() + homeScore );
								teamArray[homeIndex].setGa( teamArray[homeIndex].getGa() + awayScore );
								
								////////////////////////////////////////////////////////////////////////////////////
								
								teamArray[awayIndex].setGp( teamArray[awayIndex].getGp() + 1 );
								teamArray[awayIndex].setOtl( teamArray[awayIndex].getOtl() + 1 );
								teamArray[awayIndex].setPts( teamArray[awayIndex].getPts() + 1 );
								teamArray[awayIndex].setGf( teamArray[awayIndex].getGf() + awayScore );
								teamArray[awayIndex].setGa( teamArray[awayIndex].getGa() + homeScore );
							}
							else // home team wins in regulation
							{
								teamArray[homeIndex].setGp( teamArray[homeIndex].getGp() + 1 );
								teamArray[homeIndex].setW( teamArray[homeIndex].getW() + 1 );
								teamArray[homeIndex].setPts( teamArray[homeIndex].getPts() + 2 );
								teamArray[homeIndex].setRw( teamArray[homeIndex].getRw() + 1 );
								teamArray[homeIndex].setRow( teamArray[homeIndex].getRow() + 1 );
								teamArray[homeIndex].setGf( teamArray[homeIndex].getGf() + homeScore );
								teamArray[homeIndex].setGa( teamArray[homeIndex].getGa() + awayScore );
								
								////////////////////////////////////////////////////////////////////////////////////
								
								teamArray[awayIndex].setGp( teamArray[awayIndex].getGp() + 1 );
								teamArray[awayIndex].setL( teamArray[awayIndex].getL() + 1 );
								teamArray[awayIndex].setGf( teamArray[awayIndex].getGf() + awayScore );
								teamArray[awayIndex].setGa( teamArray[awayIndex].getGa() + homeScore );
							}
						}
						else // if away team wins
						{
							if (so) // away teams wins in a shootout
							{
								teamArray[awayIndex].setGp( teamArray[awayIndex].getGp() + 1 );
								teamArray[awayIndex].setW( teamArray[awayIndex].getW() + 1 );	
								teamArray[awayIndex].setPts( teamArray[awayIndex].getPts() + 2 );
								teamArray[awayIndex].setGf( teamArray[awayIndex].getGf() + awayScore );
								teamArray[awayIndex].setGa( teamArray[awayIndex].getGa() + homeScore );
								
								////////////////////////////////////////////////////////////////////////////////////
								
								teamArray[homeIndex].setGp( teamArray[homeIndex].getGp() + 1 );
								teamArray[homeIndex].setOtl( teamArray[homeIndex].getOtl() + 1 );
								teamArray[homeIndex].setPts( teamArray[homeIndex].getPts() + 1 );
								teamArray[homeIndex].setGf( teamArray[homeIndex].getGf() + homeScore );
								teamArray[homeIndex].setGa( teamArray[homeIndex].getGa() + awayScore );
							}
							else if (ot) // away team wins in overtime
							{
								teamArray[awayIndex].setGp( teamArray[awayIndex].getGp() + 1 );
								teamArray[awayIndex].setW( teamArray[awayIndex].getW() + 1 );
								teamArray[awayIndex].setPts( teamArray[awayIndex].getPts() + 2 );
								teamArray[awayIndex].setRow( teamArray[awayIndex].getRow() + 1 );
								teamArray[awayIndex].setGf( teamArray[awayIndex].getGf() + awayScore );
								teamArray[awayIndex].setGa( teamArray[awayIndex].getGa() + homeScore );
								
								////////////////////////////////////////////////////////////////////////////////////
								
								teamArray[homeIndex].setGp( teamArray[homeIndex].getGp() + 1 );
								teamArray[homeIndex].setOtl( teamArray[homeIndex].getOtl() + 1 );
								teamArray[homeIndex].setPts( teamArray[homeIndex].getPts() + 1 );
								teamArray[homeIndex].setGf( teamArray[homeIndex].getGf() + homeScore );
								teamArray[homeIndex].setGa( teamArray[homeIndex].getGa() + awayScore );
							}
							else // away team wins in regulation
							{
								teamArray[awayIndex].setGp( teamArray[awayIndex].getGp() + 1 );
								teamArray[awayIndex].setW( teamArray[awayIndex].getW() + 1 );
								teamArray[awayIndex].setPts( teamArray[awayIndex].getPts() + 2 );
								teamArray[awayIndex].setRw( teamArray[awayIndex].getRw() + 1 );
								teamArray[awayIndex].setRow( teamArray[awayIndex].getRow() + 1 );
								teamArray[awayIndex].setGf( teamArray[awayIndex].getGf() + awayScore );
								teamArray[awayIndex].setGa( teamArray[awayIndex].getGa() + homeScore );
								
								////////////////////////////////////////////////////////////////////////////////////
								
								teamArray[homeIndex].setGp( teamArray[homeIndex].getGp() + 1 );
								teamArray[homeIndex].setL( teamArray[homeIndex].getL() + 1 );
								teamArray[homeIndex].setGf( teamArray[homeIndex].getGf() + homeScore );
								teamArray[homeIndex].setGa( teamArray[homeIndex].getGa() + awayScore );
							}
						}
					}
				}
			}
			
		}
		else if(gamesOnDay == 0)
		{
			System.out.println("There are zero games on " + date + ". Guess there is nothing else to do here.\n");
		}
		else
		{
			System.out.println("Error. Invalid number of games on " + date + ". Games on any day must be between 1 and 16.\n");
		}
		
		System.out.println();
		bufferMenu();
	}
	
	void viewAllTeamStats() throws IOException
	{
		System.out.println("\nDisplay All League Records Alphabetically:\n");
		
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < numNHLTeams; i++)
		{
			teamArray[i].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________\n");
		bufferMenu();
		
	} // end of viewStandings()
	
	void viewOneTeamStats() throws IOException
	{
		System.out.println("\nDisplay One Team's Stats:\n");
		
		boolean validInput = false;
		String enteredTeam = "";
		int teamIndex = -1;
		
		boolean gameExists = false;
		
		while(!validInput)
		{
			System.out.print("Enter the home team (abbreviation): ");
			enteredTeam = br.readLine();
			enteredTeam = enteredTeam.toUpperCase();
			
			for(int i = 0; i < teamArray.length; i++)
			{
				if(enteredTeam.equals(teamArray[i].getTeamAbr()))
				{
					validInput = true;
					teamIndex = i;
					break;
				}
			}
			
			if (!validInput)
			{
				System.out.println("\nError. Team Abbreviation does not match any team in file.\n");
			}
		}
		
		System.out.println("\nDisplay " + teamArray[teamIndex].getTeamAbr() + " Stats:\n");
		
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");

		teamArray[teamIndex].printRecord();

		System.out.println("________________________________________________________________________________________________________________________\n");
		
		// Check if the team the user entered has played a game that is saved in the .txt file
		for (int i = 0; i < gameArray.size(); i++)
		{
			if ( gameArray.get(i).getHomeTeam().equalsIgnoreCase( teamArray[teamIndex].getTeamAbr()) || 
					gameArray.get(i).getAwayTeam().equalsIgnoreCase( teamArray[teamIndex].getTeamAbr() ) )
			{
				gameExists = true;
				break;
			}
		}
		
		System.out.println("\nDisplay " + teamArray[teamIndex].getTeamAbr() + " Games:\n");
		
		if (gameExists)
		{
			System.out.println("Away Team   Score    Home Team    Result       Date");
			System.out.println("_________________________________________________________________");
			
			
			for (int i = 0; i < gameArray.size(); i++)
			{
				if ( gameArray.get(i).getHomeTeam().equalsIgnoreCase( teamArray[teamIndex].getTeamAbr()) || 
						gameArray.get(i).getAwayTeam().equalsIgnoreCase( teamArray[teamIndex].getTeamAbr() ) )
				{
					gameArray.get(i).printGame();
				}
			}
			
			System.out.println("_________________________________________________________________\n");
		}
		else
			System.out.println("This Team has no saved games played this season.");
		
		bufferMenu();
	}
	
	void viewTiebreakingProcedure() throws IOException
	{
		System.out.println("\nReview Tiebreaking Procedure (2019-20 Season via NHL.com):\n");
		
		System.out.println("If two or more clubs are tied in points during the regular season, the standing of the clubs is "
				+ "\ndetermined in the following order:\n\n" + 
		
				"\t1. The fewer number of games played.\n" + 
				"\t2. The greater number of games won, excluding games won in Overtime or by Shootout (\"Regulation Wins\"). This figure \n" + 
				"\t   is reflected in the RW column.\n" + 
				"\t3. The greater number of games won, excluding games won by Shootout. This figure is reflected in the ROW column.\n" + 
				"\t4. The greater number of games won by the Club in any manner (\"Total Wins\"). This figure is reflected in the W column.\n" + 
				"\t5. The greater number of points earned in games against each other among two or more tied clubs. For the purpose of \n" +
				"\t   determining standing for two or more Clubs that have not played an even number of games with one or more of the other \n" +
				"\t   tied Clubs, the first game played in the city that has the extra game (the \"odd game\") shall not be included. When \n" +
				"\t   more than two Clubs are tied, the percentage of available points earned in games among each other (and not including \n" +
				"\t   any \"odd games\") shall be used to determine standing.\n" + 
				"\t6. The greater differential between goals for and against (including goals scored in Overtime or awarded for prevailing in Shootouts) \n" + 
				"\t   for the entire regular season. This figure is reflected in the DIFF column in the standings.\n" + 
				"\t7. The greater number of goals scored (including goals scored in Overtime or awarded for prevailing in Shootouts) for the entire \n" +
				"\t   regular season. This figure is reflected in the GF column.\n" + 
				
				"\n\tNOTE: In standings a victory in a shootout counts as one goal for, while a shootout loss counts as one goal against.\n");
		
		bufferMenu();
	}
	
	void viewLegend() throws IOException
	{
		System.out.println("\nView Legend:\n");
		
		System.out.println("Example:\n\nCity          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");

		System.out.printf("%-13s %-15s", "Example City", "Examples");
		System.out.println(" (" + "XPL" + ") \t" + 82 + "\t" + 53 + "\t" + 18 + "\t" + 11 + "\t" + 117 + "\t" + 
								43 + "\t" + 47 + "\t" + 267 + "\t" + 211 + "\t" + "+" + (267-211));
		
		System.out.println("________________________________________________________________________________________________________________________\n");
		
		System.out.println
				 ("\t GP   = Games Played \n"
				+ "\t W    = Total Wins (worth 2 points) \n"
				+ "\t L    = Total Losses (worth 0 points) \n"
				+ "\t OTL  = Overtime or Shootout Losses (worth 1 point) \n"
				+ "\t PTS  = Total Points \n"
				+ "\t RW   = Regulation Wins (games won without needing Overtime or Shootout) \n"
				+ "\t ROW  = Regulation and Overtime Wins (games won without needing a Shootout) \n"
				+ "\t GF   = Goals For (Goals Scored) \n"
				+ "\t GA   = Goals Against (Goals Allowed) \n"
				+ "\t DIFF = Goal Differential (GF minus GA) \n");
		
		bufferMenu();
	}
	
	void loadTeams() throws IOException
	{
		// Reading data from Team text file
		FileReader fr = new FileReader("Team.txt");
		BufferedReader br = new BufferedReader(fr);
		
		// File Variables
		String cityName = "";
		String teamName = "";
		String teamAbr = "";
		char division = 'x';
		int gp = 0;
		int w = 0;
		int l = 0;
		int otl = 0;
		int pts = 0;
		int rw = 0;
		int row = 0;
		int gf = 0;
		int ga = 0;	
		
		// Variables for each line
		String eachLine = "";
		StringTokenizer st;
		eachLine = br.readLine();
		int i = 0;
		
		while (eachLine != null)
		{
			st = new StringTokenizer(eachLine,",");
			while (st.hasMoreTokens())
			{
				cityName = st.nextToken();
				teamName = st.nextToken();
				teamAbr = st.nextToken();
				division = st.nextToken().charAt(0);
				gp = Integer.parseInt(st.nextToken());
				w = Integer.parseInt(st.nextToken());
				l = Integer.parseInt(st.nextToken());
				otl = Integer.parseInt(st.nextToken());
				pts = Integer.parseInt(st.nextToken());
				rw = Integer.parseInt(st.nextToken());
				row = Integer.parseInt(st.nextToken());
				gf = Integer.parseInt(st.nextToken());
				ga = Integer.parseInt(st.nextToken());
				
				teamArray[i] = new Team(cityName, teamName, teamAbr, division, gp, w, l, otl, pts, rw, row, gf, ga);
				i++;
				
				eachLine = br.readLine();
				
			} // end of reading a line
		} // end of reading file
		
		br.close();
		System.out.println("Teams Loaded");
	
	} // end of loadTeams()
	
	void loadGames() throws IOException
	{
		// Reading data from Appointment text file
		FileReader fr = new FileReader("Games.txt");
		BufferedReader br = new BufferedReader(fr);
		
		// File Variables
		String date = "";
		String homeTeam = "";
		String awayTeam = "";
		int homeScore = -1;
		int awayScore = -1;
		boolean ot = false;
		boolean so = false;
		
		// Variables for each line
		String eachLine = "";
		StringTokenizer st;
		eachLine = br.readLine();
		
		while (eachLine != null)
		{
			st = new StringTokenizer(eachLine,",");
			while (st.hasMoreTokens())
			{
				date = st.nextToken();
				homeTeam = st.nextToken();
				awayTeam = st.nextToken();
				homeScore = Integer.parseInt(st.nextToken());
				awayScore = Integer.parseInt(st.nextToken());
				ot = Boolean.parseBoolean(st.nextToken());
				so = Boolean.parseBoolean(st.nextToken());
				
				gameArray.add(new Game(date, homeTeam, awayTeam, homeScore, awayScore, ot, so));
				
				eachLine = br.readLine();
				
			} // end of reading a line
		} // end of reading file
		
		br.close();
		System.out.println("Games Loaded");
	
	} // end of loadGames()
	
	void loadPlayoffs() throws IOException
	{
		// Reading data from Playoffs text file
		FileReader fr = new FileReader("Playoffs.txt");
		BufferedReader br = new BufferedReader(fr);
		
		// File Variables
		int lowerSeed = -1;
		int higherSeed = -1;
		int lowerG = 0;
		int higherG = 0;
		int g1homeScore = 0;
		int g1awayScore = 0;
		int g1numOt = 0;
		int g2homeScore = 0;
		int g2awayScore = 0;
		int g2numOt = 0;
		int g3homeScore = 0;
		int g3awayScore = 0;
		int g3numOt = 0;
		int g4homeScore = 0;
		int g4awayScore = 0;
		int g4numOt = 0;
		int g5homeScore = 0;
		int g5awayScore = 0;
		int g5numOt = 0;
		int g6homeScore = 0;
		int g6awayScore = 0;
		int g6numOt = 0;
		int g7homeScore = 0;
		int g7awayScore = 0;
		int g7numOt = 0;
		boolean over = false;
		
		// Variables for each line
		String eachLine = "";
		StringTokenizer st;
		eachLine = br.readLine();
		int i = 0;
		
		while (eachLine != null)
		{
			st = new StringTokenizer(eachLine,",");
			while (st.hasMoreTokens())
			{
				lowerSeed = Integer.parseInt(st.nextToken());
				higherSeed = Integer.parseInt(st.nextToken());
				lowerG = Integer.parseInt(st.nextToken());
				higherG = Integer.parseInt(st.nextToken());
				g1homeScore = Integer.parseInt(st.nextToken());
				g1awayScore = Integer.parseInt(st.nextToken());
				g1numOt = Integer.parseInt(st.nextToken());
				g2homeScore = Integer.parseInt(st.nextToken());
				g2awayScore = Integer.parseInt(st.nextToken());
				g2numOt = Integer.parseInt(st.nextToken());
				g3homeScore = Integer.parseInt(st.nextToken());
				g3awayScore = Integer.parseInt(st.nextToken());
				g3numOt = Integer.parseInt(st.nextToken());
				g4homeScore = Integer.parseInt(st.nextToken());
				g4awayScore = Integer.parseInt(st.nextToken());
				g4numOt = Integer.parseInt(st.nextToken());
				g5homeScore = Integer.parseInt(st.nextToken());
				g5awayScore = Integer.parseInt(st.nextToken());
				g5numOt = Integer.parseInt(st.nextToken());
				g6homeScore = Integer.parseInt(st.nextToken());
				g6awayScore = Integer.parseInt(st.nextToken());
				g6numOt = Integer.parseInt(st.nextToken());
				g7homeScore = Integer.parseInt(st.nextToken());
				g7awayScore = Integer.parseInt(st.nextToken());
				g7numOt = Integer.parseInt(st.nextToken());
				over = Boolean.parseBoolean(st.nextToken());
				
				playoffArray[i] = new PlayoffSeries(lowerSeed, higherSeed, lowerG, higherG, g1homeScore, g1awayScore, g1numOt, g2homeScore, g2awayScore, g2numOt, 
						g3homeScore, g3awayScore, g3numOt, g4homeScore, g4awayScore, g4numOt, g5homeScore, g5awayScore, g5numOt, 
						g6homeScore, g6awayScore, g6numOt, g7homeScore, g7awayScore, g7numOt, over);
				
				i++;
				
				eachLine = br.readLine();
				
			} // end of reading a line
		} // end of reading file
		
		br.close();
		System.out.println("Playoffs Loaded");
	}
	
	void loadStandings() throws IOException
	{
		// Reading data from Standings text file
		FileReader fr = new FileReader("Standings.txt");
		BufferedReader br = new BufferedReader(fr);

		// Variables for each line
		String eachLine = "";
		StringTokenizer st;
		eachLine = br.readLine();
		int i = 0;
		
		while (eachLine != null)
		{
			st = new StringTokenizer(eachLine);
			while (st.hasMoreTokens())
			{
				standings[i] = Integer.parseInt(st.nextToken());
				
				i++;
				eachLine = br.readLine();
				
			} // end of reading a line
		} // end of reading file
		
		br.close();
		System.out.println("Standings Loaded\n");
	}
	
	void saveTeams() throws IOException
	{
		// FileWriter to OVERWRITE not APPEND the file
		FileWriter fw = new FileWriter("Team.txt", false);
		BufferedWriter bw = new BufferedWriter(fw);
		
		// Write one at a time into text file
		for (int i = 0; i < teamArray.length ; i++)
		{
			bw.write(
					teamArray[i].getCityName() + "," + 
					teamArray[i].getTeamName() + "," + 
					teamArray[i].getTeamAbr() + "," + 
					teamArray[i].getDivision() + "," + 
					teamArray[i].getGp() + "," + 
					teamArray[i].getW() + "," + 
					teamArray[i].getL() + "," + 
					teamArray[i].getOtl() + "," + 
					teamArray[i].getPts() + "," + 
					teamArray[i].getRw() + "," + 
					teamArray[i].getRow() + "," + 
					teamArray[i].getGf() + "," + 
					teamArray[i].getGa() + "\n");
		}
		
		bw.close();
				
	} // end of saveTeams()
	
	void saveGames() throws IOException
	{
		// FileWriter to OVERWRITE not APPEND the file
		FileWriter fw = new FileWriter("Games.txt", false);
		BufferedWriter bw = new BufferedWriter(fw);
		
		// Write one at a time into text file
		for (int i = 0; i < gameArray.size(); i++)
		{
			bw.write(gameArray.get(i).getDate() + "," + 
					gameArray.get(i).getHomeTeam() + "," +
					gameArray.get(i).getAwayTeam() + "," +
					gameArray.get(i).getHomeScore() + "," + 
					gameArray.get(i).getAwayScore() + "," +
					gameArray.get(i).isOt() + "," +
					gameArray.get(i).isSo() + "\n" );
		}
		
		bw.close();
				
	} // end of saveGames()
	
	void savePlayoffs() throws IOException
	{
		// FileWriter to OVERWRITE not APPEND the file
		FileWriter fw = new FileWriter("Playoffs.txt", false);
		BufferedWriter bw = new BufferedWriter(fw);
		
		// Write one at a time into text file
		for (int i = 0; i < playoffArray.length ; i++)
		{
			bw.write(
					playoffArray[i].getLowerSeed() + "," + 
					playoffArray[i].getHigherSeed() + "," + 
					playoffArray[i].getLowerGamesWon() + "," + 
					playoffArray[i].getHigherGamesWon() + "," + 
					playoffArray[i].getGame1().getHomeScore() + "," +
					playoffArray[i].getGame1().getAwayScore() + "," +
					playoffArray[i].getGame1().getNumOt() + "," +
					playoffArray[i].getGame2().getHomeScore() + "," +
					playoffArray[i].getGame2().getAwayScore() + "," +
					playoffArray[i].getGame2().getNumOt() + "," +
					playoffArray[i].getGame3().getHomeScore() + "," +
					playoffArray[i].getGame3().getAwayScore() + "," +
					playoffArray[i].getGame3().getNumOt() + "," +
					playoffArray[i].getGame4().getHomeScore() + "," +
					playoffArray[i].getGame4().getAwayScore() + "," +
					playoffArray[i].getGame4().getNumOt() + "," +
					playoffArray[i].getGame5().getHomeScore() + "," +
					playoffArray[i].getGame5().getAwayScore() + "," +
					playoffArray[i].getGame5().getNumOt() + "," +
					playoffArray[i].getGame6().getHomeScore() + "," +
					playoffArray[i].getGame6().getAwayScore() + "," +
					playoffArray[i].getGame6().getNumOt() + "," +
					playoffArray[i].getGame7().getHomeScore() + "," +
					playoffArray[i].getGame7().getAwayScore() + "," +
					playoffArray[i].getGame7().getNumOt() + "," +
					playoffArray[i].isOver() + "\n" );
		}
		
		bw.close();
	}
	
	void saveStandings() throws IOException
	{
		// FileWriter to OVERWRITE not APPEND the file
		FileWriter fw = new FileWriter("Standings.txt", false);
		BufferedWriter bw = new BufferedWriter(fw);
		
		// Write one at a time into text file
		for (int i = 0; i < standings.length ; i++)
		{
			bw.write(standings[i] + "\n" );
		}
		
		bw.close();
	}
	
	void resetSeason() 
	{
		// Reset the elements of the team Array (other than teamName, cityName, teamArreviation, division)
		for(int i = 0; i < teamArray.length; i++)
		{
			teamArray[i].setGp(0);
			teamArray[i].setW(0);
			teamArray[i].setL(0); 
			teamArray[i].setOtl(0);
			teamArray[i].setPts(0);
			teamArray[i].setRw(0);
			teamArray[i].setRow(0);
			teamArray[i].setGf(0);
			teamArray[i].setGa(0);
		}
		
		// Clear all the elements in the game Array
		gameArray.clear();
		
		// Reset all elements in playoffArray and standings Array
		resetPlayoffs();

			
	} // end of resetSeason()
	
	void updateDB()
	{
          // Step 1: Loading or registering JDBC driver class
        try 
        {         Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");	    }
        catch(ClassNotFoundException cnfex) 
        {
            System.out.println("Problem in loading or registering MS Access JDBC driver");
            cnfex.printStackTrace();
        }

        // SQL variables
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int totalrows = 0;
        
        // Step 2: Opening database connection
        try {
        	
            // Step 2.A: Create and get connection using DriverManager class
            connection = DriverManager.getConnection("jdbc:ucanaccess://TeamDB.accdb"); 

            // Step 2.B: Creating JDBC Statement 
            // It is scrollable so we can use next() and last()
            // It is updatable so we can enter new records
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            
            // DELETE all data currently in the table and replace it with the updated data
            statement.executeUpdate("DELETE * FROM TEAM");
            
            // Step 2.C: Executing SQL & retrieve data into ResultSet
            //Get the total rows in the table and create a primary key for the transactions
            //Data is sequential.  Row 1 also has a transid value of 1 etc.
            //The new record should create a transid based on the total number of rows existing.
            
            resultSet = statement.executeQuery("SELECT * FROM TEAM"); 
            resultSet.last();
            totalrows = resultSet.getRow();
        }
        catch(SQLException sqlex){
            sqlex.printStackTrace();
        }
        //UPDATE THE DATABASE   
      //Create the SQL string and pass the values to the database
		String sql = "INSERT INTO Team (teamId, cityName, teamName, teamAbr, division, gp , w, l, otl, pts, rw, row, gf, ga) VALUES  ";
		String fullsql =""; //sql + values
		String cityName = "";
		String teamName = "";
		String teamAbr = "";
		char division = 'x';
		int gp = 0;
		int w = 0;
		int l = 0;
		int otl = 0;
		int pts = 0;
		int rw = 0;
		int row = 0; // regulation OT wins
		int gf = 0;
		int ga = 0;	
		int rows = 0; // actual rows
		int teamId = totalrows; //primary key in the Sales table
        
        for (int i = 0; i < teamArray.length; i++) 
    	{	
        	teamId++;	//update to a unique value
        	
    		cityName = String.valueOf(teamArray[i].getCityName());
			teamName = String.valueOf(teamArray[i].getTeamName()); 
			teamAbr = String.valueOf(teamArray[i].getTeamAbr()); 
			division = Character.valueOf(teamArray[i].getDivision()); 
			gp = Integer.valueOf(teamArray[i].getGp());
			w = Integer.valueOf(teamArray[i].getW());
			l = Integer.valueOf(teamArray[i].getL()); 
			otl = Integer.valueOf(teamArray[i].getOtl());
			pts = Integer.valueOf(teamArray[i].getPts());
			rw = Integer.valueOf(teamArray[i].getRw());
			row = Integer.valueOf(teamArray[i].getRow());
			gf = Integer.valueOf(teamArray[i].getGf());
			ga = Integer.valueOf(teamArray[i].getGa());
    		
    		fullsql = sql + "(" + teamId + ",'" + cityName + "','" + teamName + "','" + teamAbr + "','" + division + "'," + gp + 
    				"," + w + "," + l + "," + otl + "," + pts + "," + rw + "," + row + "," + gf + 
    				"," + ga + ")"; //testing
    		
    		PreparedStatement preparedStatement = null;
			try 
			{
				preparedStatement = connection.prepareStatement(fullsql);   		
				rows = preparedStatement.executeUpdate();
				
				if (rows > 0) 
				{	
					fullsql = "";
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
        	
    	}
        
        // rows variable either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
        if (rows == 1)       	
        	System.out.println("\nAll rows in TEAM table have been inserted into TeamDB.accdb successfully.");
        
        
            // Step 3: Closing database connection
           
                    // cleanup resources, once after processing
                    try {
						resultSet.close();
					    statement.close();
					} 
                    catch (SQLException e)
                    {
						e.printStackTrace();
					}
                

                    // and then finally close connection
             
	} //end of updateDB() 
	
	void viewStandingsDB() throws IOException
	{
		 // Step 1: Loading or registering JDBC driver class
        try 
        {         Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");	    }
        catch(ClassNotFoundException cnfex) 
        {
            System.out.println("Problem in loading or registering MS Access JDBC driver");
            cnfex.printStackTrace();
        }

        // SQL variables
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        // Step 2: Opening database connection
        try {
        	
            // Step 2.A: Create and get connection using DriverManager class
            connection = DriverManager.getConnection("jdbc:ucanaccess://TeamDB.accdb"); 

            // Step 2.B: Creating JDBC Statement 
            // It is scrollable so we can use next() and last()
            // It is updatable so we can enter new records
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            
            // Step 2.C: Executing SQL & retrieve data into ResultSet
            resultSet = statement.executeQuery("SELECT * " + 
            						"FROM Team " + 
            						"ORDER BY PTS;");
            
          //  resultSet = statement.executeQuery("SELECT * INTO TeamOrder FROM Team ");
            
            System.out.println("\nView Current League Standings:\n");
            
            System.out.println("City          Team                     Division   \tGP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
    		System.out.println("_________________________________________________________________________________________________________________________________________");
            
            while(resultSet.next())
            {
                System.out.printf("%-13s %-15s (%-3s)    ", resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
                
        		if( resultSet.getString(4).charAt(0) == 'A' )
        			System.out.print("Atlantic ");
        		else if( resultSet.getString(4).charAt(0) == 'M' )
        			System.out.print("Metropolitan");
        		else if( resultSet.getString(4).charAt(0) == 'C' )
        			System.out.print("Central  ");
        		else if( resultSet.getString(4).charAt(0) == 'P' )
        			System.out.print("Pacific  ");
        		
        		System.out.print("\t" + resultSet.getInt(5) + "\t" + resultSet.getInt(6) + "\t" + resultSet.getInt(7) + "\t" + 
        				resultSet.getInt(8) + "\t" + resultSet.getInt(9) + "\t" + resultSet.getInt(10) + "\t" + resultSet.getInt(11) + "\t" + 
        				resultSet.getInt(12) + "\t" + resultSet.getInt(13) + "\t");
        		
        		if ( ((resultSet.getInt(12) - resultSet.getInt(13))) > 0 )
        			System.out.println( "+" + ((resultSet.getInt(12) - resultSet.getInt(13))));
        		else if ( ((resultSet.getInt(12) - resultSet.getInt(13))) == 0) // For spacing
        			System.out.println( " " + ((resultSet.getInt(12) - resultSet.getInt(13))));
        		else
        			System.out.println(((resultSet.getInt(12) - resultSet.getInt(13))));
            }
            
    		System.out.println("_________________________________________________________________________________________________________________________________________\n");
    		
        }
        catch(SQLException sqlex){
            sqlex.printStackTrace();
        }
        
        
        bufferMenu();
	}
	
	void viewStandingsMenu() throws IOException
	{
		int standingsMenuChoice = -1;
		System.out.println();
		
		do
		{
			System.out.print("Standings Menu: \n" +
				"\t1) Division Standings\n" +
				"\t2) Wild Card Standings\n" +
				"\t3) Conference Standings\n" +
				"\t4) League Standings\n" +
				"\t5) Return to Main Menu\n\n" +
				"Enter your choice (1-5): ");
			
			standingsMenuChoice = Integer.parseInt(br.readLine());
			
			switch(standingsMenuChoice) {
				
				case(1):
					viewDivStandings();
					break;
				
				case(2):
					viewWildCardStandings();
					break;
					
				case(3):
					viewConfStandings();
					break;	
					
				case(4):
					viewLeagueStandings();
					break;
					
				case(5): 
					// Returing to Main Menu...
					break;
				
				default:
					System.out.println("\nError. Please enter a choice between 1 and 5.\n");		
			}
			
		} while (standingsMenuChoice != 5);
		
		System.out.println();
	}
	
	void viewDivStandings() throws IOException
	{
		// Printing Standings based on the Division the team is in
		
		// Atlantic Teams Array
		int[] atlTeams = new int[numAtlanticTeams];
		int atlIndex = 0; // Increase every time an Atlantic team is saved into the array from the loop
		
		// Matro Teams Array
		int[] metroTeams = new int[numMetroTeams];
		int metroIndex = 0; // Increase every time a Metro team is saved into the array from the loop
		
		// Central Teams Array
		int[] cenTeams = new int[numCentralTeams];
		int cenIndex = 0; // Increase every time a Central team is saved into the array from the loop
		
		// Pacific Teams Array
		int[] pacTeams = new int[numAtlanticTeams];
		int pacIndex = 0; // Increase every time a Pacific team is saved into the array from the loop
		
		boolean[] teamPrint = new boolean[numNHLTeams]; // Determines which teams have not been printed ( false = not printed, true = printed )
		
		for (int i = 0; i < numNHLTeams; i++) // set all boolean array elements to false
			teamPrint[i] = false;
		
		int highestIndex = -1;
		int highestPts = -1;
		
		// Variable Used in Tiebreaker 5:
		int diffPts = 0; 	// difference of points between hIP and iP ( hIP - iP ):
							// 		POSITIVE if highestIndex wins tiebreaker
							//		NEGATIVE if highestIndex wins tiebreaker
							//		ZERO if still tied and need tiebreaker 6
		
		// Variables Used in Tiebreaker 6:
		int iDIFF = 0;  // i (challenger) Goal Differential (DIFF)
		int hIDIFF = 0; // highestIndex (current leader) Goal Differential (DIFF)
		
		
		for (int j = 0; j < numNHLTeams; j++)
		{
			highestIndex = -1;
			highestPts = -1;
			diffPts = 0;
			hIDIFF = 0;
			iDIFF = 0;
			
			// Determine a potential highest point total (based on teams remaining)
			for (int i = 0; i < numNHLTeams; i++)
			{
				if( (teamArray[i].getPts() > highestPts) && teamPrint[i] == false )
				{
					highestPts = teamArray[i].getPts();
					highestIndex = i;
				}
				else if ( (teamArray[i].getPts() == highestPts) && teamPrint[i] == false )
				{
					// Tiebreaker 1: Games Played
					
					if ( teamArray[i].getGp() < teamArray[highestIndex].getGp() )
					{
						highestIndex = i;
					}
					else if ( teamArray[i].getGp() == teamArray[highestIndex].getGp() )
					{
						// Tiebreaker 2: Regulation Wins (RW)
						
						if ( teamArray[i].getRw() > teamArray[highestIndex].getRw() )
						{
							highestIndex = i;
						}
						else if ( teamArray[i].getRw() == teamArray[highestIndex].getRw() )
						{
							// Tiebreaker 3: Regulation + Overtime Wins (ROW)
							
							if ( teamArray[i].getRow() > teamArray[highestIndex].getRow() )
							{
								highestIndex = i;
							}
							else if ( teamArray[i].getRow() == teamArray[highestIndex].getRow() )
							{
								// Tiebreaker 4: Total Wins (W)
								
								if ( teamArray[i].getW() > teamArray[highestIndex].getW() )
								{
									highestIndex = i;
								}
								else if ( teamArray[i].getW() == teamArray[highestIndex].getW() )
								{
									// Tiebreaker 5: Games played between the two teams needing tiebreaker
									
									diffPts = tiebreaker5(highestIndex, i); // diffPts is difference of points between hIP and iP ( hIP - iP ):
																				// 		POSITIVE if highestIndex wins tiebreaker
																				//		NEGATIVE if highestIndex wins tiebreaker
																				//		ZERO if still tied and need tiebreaker 6
									
									if ( diffPts < 0 )
									{
										highestIndex = i;
									}
									else if ( diffPts == 0)
									{
										// Tiebreaker 6: Goal Differential (DIFF)
										
										iDIFF = teamArray[i].getGf() - teamArray[i].getGf();
										hIDIFF = teamArray[highestIndex].getGf() - teamArray[highestIndex].getGf();
										
										if ( iDIFF > hIDIFF )
										{
											highestIndex = i;
										}
										else if ( iDIFF == hIDIFF )
										{
											// Tiebreaker 7: Goals For (GF)
											
											if ( teamArray[i].getGf() > teamArray[highestIndex].getGf() )
											{
												highestIndex = i;
											}
											
											// else if ( teamArray[i].getGf() == teamArray[highestIndex].getGf() )
											
										}
									}
								}
							}
						}
					}
				}
			}
		
			
			if ( teamArray[highestIndex].getDivision() == 'A' )
			{
				atlTeams[atlIndex] = highestIndex;
				atlIndex++;
			}
			else if ( teamArray[highestIndex].getDivision() == 'M' )
			{
				metroTeams[metroIndex] = highestIndex;
				metroIndex++;
			}
			else if ( teamArray[highestIndex].getDivision() == 'C' )
			{
				cenTeams[cenIndex] = highestIndex;
				cenIndex++;
			}
			else if ( teamArray[highestIndex].getDivision() == 'P' )
			{
				pacTeams[pacIndex] = highestIndex;
				pacIndex++;
			}
				
			teamPrint[highestIndex] = true;

		}
		
		System.out.println("\n\t\t\t\t\t\tAtlantic Division:\n");
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < numAtlanticTeams; i++)
		{
			teamArray[ atlTeams[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________");
		
		System.out.println("\n\t\t\t\t\t\tMetropolitan Division:\n");
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < numMetroTeams; i++)
		{
			teamArray[ metroTeams[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________");
		
		System.out.println("\n\t\t\t\t\t\tCentral Division:\n");
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < numCentralTeams; i++)
		{
			teamArray[ cenTeams[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________");
		
		System.out.println("\n\t\t\t\t\t\tPacific Division:\n");
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < numPacificTeams; i++)
		{
			teamArray[ pacTeams[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________\n");
		
		
		// Test if any teams have not been printed
		for (int i = 0; i < numNHLTeams; i++)
		{
			if (!teamPrint[i])
			{
				System.out.println("Error. Not all teams have been printed.");
				break;
			}
		}
	
		bufferStandingsMenu();
	}
	
	void viewWildCardStandings() throws IOException
	{
		// Atlantic Top 3 Array
		int[] atlTop3 = new int[3];
		int atl3Index = 0; // Increase every time an Atlantic team is saved into the array from the loop
		
		// Matro Top 3 Array
		int[] metroTop3 = new int[3];
		int metro3Index = 0; // Increase every time a Metro team is saved into the array from the loop
		
		// Central Top 3 Array
		int[] cenTop3 = new int[3];
		int cen3Index = 0; // Increase every time a Central team is saved into the array from the loop
		
		// Pacific Top 3 Array
		int[] pacTop3 = new int[3];
		int pac3Index = 0; // Increase every time a Pacific team is saved into the array from the loop
		
		// East Wildcard Array
		int[] eastWC = new int[2];
		int eastWCIndex = 0; // Increase every time a Eastern Wildcard team is saved into the array from the loop
		
		// West Wildcard Array
		int[] westWC = new int[2];
		int westWCIndex = 0; // Increase every time a Western Wildcard team is saved into the array from the loop

		// East Remaining Array (the remaining East teams that have not been saved to an array yet)
		int[] eastRemaining = new int[ numEastTeams - 8 ];
		int eastRemIndex = 0; // Increase every time a Eastern team outside the playoffs is saved into the array from the loop
		
		// West Remaining Array (the remaining West teams that have not been saved to an array yet)
		int[] westRemaining = new int[ numWestTeams - 8];
		int westRemIndex = 0; // Increase every time a Western team outside the playoffs is saved into the array from the loop
		
		boolean[] teamPrint = new boolean[numNHLTeams]; // Determines which teams have not been printed ( false = not printed, true = printed )
		
		for (int i = 0; i < numNHLTeams; i++) // set all boolean array elements to false
			teamPrint[i] = false;
		
		int highestIndex = -1;
		int highestPts = -1;
		
		// Variable Used in Tiebreaker 5:
		int diffPts = 0; 	// difference of points between hIP and iP ( hIP - iP ):
							// 		POSITIVE if highestIndex wins tiebreaker
							//		NEGATIVE if highestIndex wins tiebreaker
							//		ZERO if still tied and need tiebreaker 6
		
		// Variables Used in Tiebreaker 6:
		int iDIFF = 0;  // i (challenger) Goal Differential (DIFF)
		int hIDIFF = 0; // highestIndex (current leader) Goal Differential (DIFF)
		
		
		for (int j = 0; j < numNHLTeams; j++)
		{
			highestIndex = -1;
			highestPts = -1;
			diffPts = 0;
			hIDIFF = 0;
			iDIFF = 0;
			
			// Determine a potential highest point total (based on teams remaining)
			for (int i = 0; i < numNHLTeams; i++)
			{
				if( (teamArray[i].getPts() > highestPts) && teamPrint[i] == false )
				{
					highestPts = teamArray[i].getPts();
					highestIndex = i;
				}
				else if ( (teamArray[i].getPts() == highestPts) && teamPrint[i] == false )
				{
					// Tiebreaker 1: Games Played
					
					if ( teamArray[i].getGp() < teamArray[highestIndex].getGp() )
					{
						highestIndex = i;
					}
					else if ( teamArray[i].getGp() == teamArray[highestIndex].getGp() )
					{
						// Tiebreaker 2: Regulation Wins (RW)
						
						if ( teamArray[i].getRw() > teamArray[highestIndex].getRw() )
						{
							highestIndex = i;
						}
						else if ( teamArray[i].getRw() == teamArray[highestIndex].getRw() )
						{
							// Tiebreaker 3: Regulation + Overtime Wins (ROW)
							
							if ( teamArray[i].getRow() > teamArray[highestIndex].getRow() )
							{
								highestIndex = i;
							}
							else if ( teamArray[i].getRow() == teamArray[highestIndex].getRow() )
							{
								// Tiebreaker 4: Total Wins (W)
								
								if ( teamArray[i].getW() > teamArray[highestIndex].getW() )
								{
									highestIndex = i;
								}
								else if ( teamArray[i].getW() == teamArray[highestIndex].getW() )
								{
									// Tiebreaker 5: Games played between the two teams needing tiebreaker
									
									diffPts = tiebreaker5(highestIndex, i); // diffPts is difference of points between hIP and iP ( hIP - iP ):
																				// 		POSITIVE if highestIndex wins tiebreaker
																				//		NEGATIVE if highestIndex wins tiebreaker
																				//		ZERO if still tied and need tiebreaker 6
									
									if ( diffPts < 0 )
									{
										highestIndex = i;
									}
									else if ( diffPts == 0)
									{
										// Tiebreaker 6: Goal Differential (DIFF)
										
										iDIFF = teamArray[i].getGf() - teamArray[i].getGf();
										hIDIFF = teamArray[highestIndex].getGf() - teamArray[highestIndex].getGf();
										
										if ( iDIFF > hIDIFF )
										{
											highestIndex = i;
										}
										else if ( iDIFF == hIDIFF )
										{
											// Tiebreaker 7: Goals For (GF)
											
											if ( teamArray[i].getGf() > teamArray[highestIndex].getGf() )
											{
												highestIndex = i;
											}
											
											// else if ( teamArray[i].getGf() == teamArray[highestIndex].getGf() )
											
										}
									}
								}
							}
						}
					}
				}
			}
		
			
			if ( teamArray[highestIndex].getDivision() == 'A' )
			{
				if ( atl3Index < 3 )
				{
					atlTop3[atl3Index] = highestIndex;
					atl3Index++;
				}
				else if ( eastWCIndex < 2 )
				{
					eastWC[eastWCIndex] = highestIndex;
					eastWCIndex++;
				}
				else
				{
					eastRemaining[eastRemIndex] = highestIndex;
					eastRemIndex++;
				}
			}		
			
			if ( teamArray[highestIndex].getDivision() == 'M' )
			{
				if ( metro3Index < 3 )
				{
					metroTop3[metro3Index] = highestIndex;
					metro3Index++;
				}
				else if ( eastWCIndex < 2 )
				{
					eastWC[eastWCIndex] = highestIndex;
					eastWCIndex++;
				}
				else
				{
					eastRemaining[eastRemIndex] = highestIndex;
					eastRemIndex++;
				}
			}	
			
			if ( teamArray[highestIndex].getDivision() == 'C' )
			{
				if ( cen3Index < 3 )
				{
					cenTop3[cen3Index] = highestIndex;
					cen3Index++;
				}
				else if ( westWCIndex < 2 )
				{
					westWC[westWCIndex] = highestIndex;
					westWCIndex++;
				}
				else
				{
					westRemaining[westRemIndex] = highestIndex;
					westRemIndex++;
				}
			}	
			
			if ( teamArray[highestIndex].getDivision() == 'P' )
			{
				if ( pac3Index < 3 )
				{
					pacTop3[pac3Index] = highestIndex;
					pac3Index++;
				}
				else if ( westWCIndex < 2 )
				{
					westWC[westWCIndex] = highestIndex;
					westWCIndex++;
				}
				else
				{
					westRemaining[westRemIndex] = highestIndex;
					westRemIndex++;
				}
			}	
				
			teamPrint[highestIndex] = true;

		}
		
		// Print Eastern Conference
		
		System.out.println("\n\t\t\t\t\t\tEastern Conference:\n");
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");
		System.out.println("\tAtlantic Division:");
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < atlTop3.length; i++)
		{
			teamArray[ atlTop3[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________");
		System.out.println("\tMetropolitan Division:");
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < metroTop3.length; i++)
		{
			teamArray[ metroTop3[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________");
		System.out.println("\t    Wild Card:");
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < eastWC.length; i++)
		{
			teamArray[ eastWC[i] ].printRecord();
		}
		
		System.out.println("----------------------------------------------P-L-A-Y-O-F-F---L-I-N-E---------------------------------------------------");
		
		for (int i = 0; i < eastRemaining.length; i++)
		{
			teamArray[ eastRemaining[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________");
		
		
		// Print Western Conference
		
		System.out.println("\n\n\t\t\t\t\t\tWestern Conference:\n");
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");
		System.out.println("\tCentral Division:");
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < cenTop3.length; i++)
		{
			teamArray[ cenTop3[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________");
		System.out.println("\tPacific Division:");
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < pacTop3.length; i++)
		{
			teamArray[ pacTop3[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________");
		System.out.println("\t    Wild Card:");
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < westWC.length; i++)
		{
			teamArray[ westWC[i] ].printRecord();
		}
		
		System.out.println("----------------------------------------------P-L-A-Y-O-F-F---L-I-N-E---------------------------------------------------");
		
		for (int i = 0; i < westRemaining.length; i++)
		{
			teamArray[ westRemaining[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________");
		
		
		// Test if any teams have not been printed
		for (int i = 0; i < numNHLTeams; i++)
		{
			if (!teamPrint[i])
			{
				System.out.println("Error. Not all teams have been printed.");
				break;
			}
		}
	
		System.out.println();
		bufferStandingsMenu();
	}
	
	void viewConfStandings() throws IOException
	{
		// Printing Standings based on the Conference the team is in
		
		// Eastern Teams Array
		int[] eastTeams = new int[numEastTeams];
		int eastIndex = 0; // Increase every time a eastern team is saved into the array from the loop
		// Western Teams Array
		int[] westTeams = new int[numWestTeams];
		int westIndex = 0; // Increase every time a western team is saved into the array from the loop
		
		boolean[] teamPrint = new boolean[numNHLTeams]; // Determines which teams have not been printed ( false = not printed, true = printed )
		
		for (int i = 0; i < numNHLTeams; i++) // set all boolean array elements to false
			teamPrint[i] = false;
		
		int highestIndex = -1;
		int highestPts = -1;
		
		// Variable Used in Tiebreaker 5:
		int diffPts = 0; 	// difference of points between hIP and iP ( hIP - iP ):
							// 		POSITIVE if highestIndex wins tiebreaker
							//		NEGATIVE if highestIndex wins tiebreaker
							//		ZERO if still tied and need tiebreaker 6
		
		// Variables Used in Tiebreaker 6:
		int iDIFF = 0;  // i (challenger) Goal Differential (DIFF)
		int hIDIFF = 0; // highestIndex (current leader) Goal Differential (DIFF)
		
		
		for (int j = 0; j < numNHLTeams; j++)
		{
			highestIndex = -1;
			highestPts = -1;
			diffPts = 0;
			hIDIFF = 0;
			iDIFF = 0;
			
			// Determine a potential highest point total (based on teams remaining)
			for (int i = 0; i < numNHLTeams; i++)
			{
				if( (teamArray[i].getPts() > highestPts) && teamPrint[i] == false )
				{
					highestPts = teamArray[i].getPts();
					highestIndex = i;
				}
				else if ( (teamArray[i].getPts() == highestPts) && teamPrint[i] == false )
				{
					// Tiebreaker 1: Games Played
					
					if ( teamArray[i].getGp() < teamArray[highestIndex].getGp() )
					{
						highestIndex = i;
					}
					else if ( teamArray[i].getGp() == teamArray[highestIndex].getGp() )
					{
						// Tiebreaker 2: Regulation Wins (RW)
						
						if ( teamArray[i].getRw() > teamArray[highestIndex].getRw() )
						{
							highestIndex = i;
						}
						else if ( teamArray[i].getRw() == teamArray[highestIndex].getRw() )
						{
							// Tiebreaker 3: Regulation + Overtime Wins (ROW)
							
							if ( teamArray[i].getRow() > teamArray[highestIndex].getRow() )
							{
								highestIndex = i;
							}
							else if ( teamArray[i].getRow() == teamArray[highestIndex].getRow() )
							{
								// Tiebreaker 4: Total Wins (W)
								
								if ( teamArray[i].getW() > teamArray[highestIndex].getW() )
								{
									highestIndex = i;
								}
								else if ( teamArray[i].getW() == teamArray[highestIndex].getW() )
								{
									// Tiebreaker 5: Games played between the two teams needing tiebreaker
									
									diffPts = tiebreaker5(highestIndex, i); // diffPts is difference of points between hIP and iP ( hIP - iP ):
																				// 		POSITIVE if highestIndex wins tiebreaker
																				//		NEGATIVE if highestIndex wins tiebreaker
																				//		ZERO if still tied and need tiebreaker 6
									
									if ( diffPts < 0 )
									{
										highestIndex = i;
									}
									else if ( diffPts == 0)
									{
										// Tiebreaker 6: Goal Differential (DIFF)
										
										iDIFF = teamArray[i].getGf() - teamArray[i].getGf();
										hIDIFF = teamArray[highestIndex].getGf() - teamArray[highestIndex].getGf();
										
										if ( iDIFF > hIDIFF )
										{
											highestIndex = i;
										}
										else if ( iDIFF == hIDIFF )
										{
											// Tiebreaker 7: Goals For (GF)
											
											if ( teamArray[i].getGf() > teamArray[highestIndex].getGf() )
											{
												highestIndex = i;
											}
											
											// else if ( teamArray[i].getGf() == teamArray[highestIndex].getGf() )
											
										}
									}
								}
							}
						}
					}
				}
			}
		
			
			if ( teamArray[highestIndex].getDivision() == 'A' || teamArray[highestIndex].getDivision() == 'M')
			{
				eastTeams[eastIndex] = highestIndex;
				eastIndex++;
			}
			else if ( teamArray[highestIndex].getDivision() == 'C' || teamArray[highestIndex].getDivision() == 'P')
			{
				westTeams[westIndex] = highestIndex;
				westIndex++;
			}
				
			teamPrint[highestIndex] = true;

		}
		
		System.out.println("\n\t\t\t\t\t\tEastern Conference:\n");
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < numEastTeams; i++)
		{
			teamArray[ eastTeams[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________\n");
			
	
		System.out.println("\n\t\t\t\t\t\tWestern Conference:\n");
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");
		
		for (int i = 0; i < numWestTeams; i++)
		{
			teamArray[ westTeams[i] ].printRecord();
		}
		
		System.out.println("________________________________________________________________________________________________________________________\n");
		
		// Test if any teams have not been printed
		for (int i = 0; i < numNHLTeams; i++)
		{
			if (!teamPrint[i])
			{
				System.out.println("Error. Not all teams have been printed.");
				break;
			}
		}
	
		bufferStandingsMenu();
	}
	
	void viewLeagueStandings() throws IOException
	{
		// Displaying all teams records based on Points earned and (if necessary) tiebreakers
		
		boolean[] teamPrint = new boolean[numNHLTeams]; // Determines which teams have not been printed ( false = not printed, true = printed )
		
		for (int i = 0; i < numNHLTeams; i++) // set all boolean array elements to false
			teamPrint[i] = false;
		
		int highestIndex = -1;
		int highestPts = -1;
		
		// Variable Used in Tiebreaker 5:
		int diffPts = 0; 	// difference of points between hIP and iP ( hIP - iP ):
							// 		POSITIVE if highestIndex wins tiebreaker
							//		NEGATIVE if highestIndex wins tiebreaker
							//		ZERO if still tied and need tiebreaker 6
		
		// Variables Used in Tiebreaker 6:
		int iDIFF = 0;  // i (challenger) Goal Differential (DIFF)
		int hIDIFF = 0; // highestIndex (current leader) Goal Differential (DIFF)
		
		System.out.println("\n\t\t\t\t\t\tLeague Standings:\n");
		System.out.println("City          Team                      GP      W       L       OTL     PTS     RW      ROW     GF      GA      DIFF"); 
		System.out.println("________________________________________________________________________________________________________________________");

		
		for (int j = 0; j < numNHLTeams; j++)
		{
			highestIndex = -1;
			highestPts = -1;
			diffPts = 0;
			hIDIFF = 0;
			iDIFF = 0;
			
			// Determine a potential highest point total (based on teams remaining)
			for (int i = 0; i < numNHLTeams; i++)
			{
				if( (teamArray[i].getPts() > highestPts) && teamPrint[i] == false )
				{
					highestPts = teamArray[i].getPts();
					highestIndex = i;
				}
				else if ( (teamArray[i].getPts() == highestPts) && teamPrint[i] == false)
				{
					// Tiebreaker 1: Games Played
					
					if ( teamArray[i].getGp() < teamArray[highestIndex].getGp() )
					{
						highestIndex = i;
					}
					else if ( teamArray[i].getGp() == teamArray[highestIndex].getGp() )
					{
						// Tiebreaker 2: Regulation Wins (RW)
						
						if ( teamArray[i].getRw() > teamArray[highestIndex].getRw() )
						{
							highestIndex = i;
						}
						else if ( teamArray[i].getRw() == teamArray[highestIndex].getRw() )
						{
							// Tiebreaker 3: Regulation + Overtime Wins (ROW)
							
							if ( teamArray[i].getRow() > teamArray[highestIndex].getRow() )
							{
								highestIndex = i;
							}
							else if ( teamArray[i].getRow() == teamArray[highestIndex].getRow() )
							{
								// Tiebreaker 4: Total Wins (W)
								
								if ( teamArray[i].getW() > teamArray[highestIndex].getW() )
								{
									highestIndex = i;
								}
								else if ( teamArray[i].getW() == teamArray[highestIndex].getW() )
								{
									// Tiebreaker 5: Games played between the two teams needing tiebreaker
									
									diffPts = tiebreaker5(highestIndex, i); // diffPts is difference of points between hIP and iP ( hIP - iP ):
																				// 		POSITIVE if highestIndex wins tiebreaker
																				//		NEGATIVE if highestIndex wins tiebreaker
																				//		ZERO if still tied and need tiebreaker 6
									
									if ( diffPts < 0 )
									{
										highestIndex = i;
									}
									else if ( diffPts == 0)
									{
										// Tiebreaker 6: Goal Differential (DIFF)
										
										iDIFF = teamArray[i].getGf() - teamArray[i].getGf();
										hIDIFF = teamArray[highestIndex].getGf() - teamArray[highestIndex].getGf();
										
										if ( iDIFF > hIDIFF )
										{
											highestIndex = i;
										}
										else if ( iDIFF == hIDIFF )
										{
											// Tiebreaker 7: Goals For (GF)
											
											if ( teamArray[i].getGf() > teamArray[highestIndex].getGf() )
											{
												highestIndex = i;
											}
											
											// else if ( teamArray[i].getGf() == teamArray[highestIndex].getGf() )
											
										}
									}
								}
							}
						}
					}
				}
			}
			
			
			teamArray[highestIndex].printRecord();
			teamPrint[highestIndex] = true;
		}
		
		// Test if any teams have not been printed
		for (int i = 0; i < numNHLTeams; i++)
		{
			if (!teamPrint[i])
			{
				System.out.println("Error. Not all teams have been printed.");
				break;
			}
		}
		
		System.out.println("________________________________________________________________________________________________________________________\n");
		bufferStandingsMenu();	
	}
	
	int tiebreaker5(int highestIndex, int i)
	{
		// Tiebreaker 5: The greater number of points earned in games against each other among two or more tied clubs. For the purpose of
			//	  determining standing for two or more Clubs that have not played an even number of games with one or more of the other
			//    tied Clubs, the first game played in the city that has the extra game (the "odd game") shall not be included. When
			//	  more than two Clubs are tied, the percentage of available points earned in games among each other (and not including
			//	  any "odd games") shall be used to determine standing.
		
		int hIHome = 0; // number of Games that "highestIndex" team played at Home
		int iHome = 0; // number of Games that "i" team played at Home
		int hIPts = 0; // highestIndex (current leader) points earned between games
		int iPts = 0; // i (challenger) points earned between games
		
		boolean skipFirstHIGame = false;
		boolean skipFirstIGame = false;
		
		// Check if the teams (between the team representing "i" and the team representing "highestIndex")
		// 		have played a game between each other that is saved in the .txt file and count the number
		//		of games each team played at home
		for (int j = 0; j < gameArray.size(); j++)
		{
			if ( gameArray.get(j).getHomeTeam().equalsIgnoreCase( teamArray[highestIndex].getTeamAbr() ) && 
					gameArray.get(j).getAwayTeam().equalsIgnoreCase( teamArray[i].getTeamAbr()) )
			{
				hIHome++;
			}
			else if ( gameArray.get(j).getHomeTeam().equalsIgnoreCase( teamArray[i].getTeamAbr() ) && 
							gameArray.get(j).getAwayTeam().equalsIgnoreCase( teamArray[highestIndex].getTeamAbr()) )
			{
				iHome++;
			}
		}
		
		// If the games between both teams is not an EVEN number, skip the first game played in the city that has the extra game
		if ( (hIHome + iHome) % 2 != 0 )
		{
			if ( hIHome > iHome)
				skipFirstHIGame = true;
			else
				skipFirstIGame = true;
		}
	
		
		// No purpose of judging teams if zero or one game is played (the one game will be skipped)
		if ( (hIHome + iHome) >= 2 )
		{			
			for (int j = 0; j < gameArray.size(); j++)
			{
				// Team represented by "highestIndex" is HOME TEAM
				if ( gameArray.get(j).getHomeTeam().equalsIgnoreCase( teamArray[highestIndex].getTeamAbr() ) && 
						gameArray.get(j).getAwayTeam().equalsIgnoreCase( teamArray[i].getTeamAbr()) )
				{					
					// If true, SKIP this game when counting points
					if (skipFirstHIGame)
					{
						skipFirstHIGame = false;
						continue;
					}
						
					
					// Adding points based on outcome of the game
					
					// "highestIndex" team wins this game
					if ( gameArray.get(j).getHomeScore() > gameArray.get(j).getAwayScore() )
					{
						// "highestIndex" team wins this game in Overtime or in a Shootout
						if ( gameArray.get(j).isOt() || gameArray.get(j).isSo())
						{
							hIPts += 2;
							iPts += 1;
						}
						// "highestIndex" team wins this game in Regulation
						else
						{
							hIPts += 2;
						}
					}
					// "i" team wins this game
					else
					{
						// "i" team wins this game in Overtime or in a Shootout
						if ( gameArray.get(j).isOt() || gameArray.get(j).isSo())
						{
							hIPts += 1;
							iPts += 2;
						}
						// "i" team wins this game in Regulation
						else
						{
							iPts += 2;
						}
					}
					
				}
				// Team represented by "i" is HOME TEAM
				else if ( gameArray.get(j).getAwayTeam().equalsIgnoreCase( teamArray[highestIndex].getTeamAbr() ) && 
							gameArray.get(j).getHomeTeam().equalsIgnoreCase( teamArray[i].getTeamAbr()) )
				{
					// If true, SKIP this game when counting points
					if (skipFirstIGame)
					{
						skipFirstIGame = false;
						continue;
					}
						
					
					// Adding points based on outcome of the game
					
					// "i" team wins this game
					if ( gameArray.get(j).getHomeScore() > gameArray.get(j).getAwayScore() )
					{
						// "i" team wins this game in Overtime or in a Shootout
						if ( gameArray.get(j).isOt() || gameArray.get(j).isSo())
						{
							hIPts += 1;
							iPts += 2;
						}
						// "i" team wins this game in Regulation
						else
						{
							iPts += 2;
						}
					}
					// "highestIndex" team wins this game
					else
					{
						// "highestIndex" team wins this game in Overtime or in a Shootout
						if ( gameArray.get(j).isOt() || gameArray.get(j).isSo())
						{
							hIPts += 1;
							iPts += 2;
						}
						// "highestIndex" team wins this game in Regulation
						else
						{
							hIPts += 2;
						}
					}
					
				}
			}
		}
		
		// RETURN difference of points between hIP and iP ( hIP - iP ):
		// 		POSITIVE if highestIndex wins tiebreaker
		//		NEGATIVE if highestIndex wins tiebreaker
		//		ZERO if still tied and need tiebreaker 6
		
		return (hIPts - iPts);
	}
	
	void beginPlayoffs() throws IOException
	{
		//
		// FIRST ROUND   SECOND ROUND    CONF FINAL          FINAL         CONF FINAL   SECOND ROUND   FIRST ROUND
		//
		//  C1------\                                                                                   /------A1
		//  WC1/2----\                                                                                 /----WC1/2
		//            WEST SEMI-FINAL-\                                               /-EAST SEMI-FINAL
		//  C2-------/                 \                                             /                 \-------A2
		//  C3------/                   \                                           /                   \------A3
		//                               \                  STANLEY                /
		//                               WEST FINAL ----->    CUP   <----- EAST FINAL
		//                               /                   FINAL                 \
		//  P1------\                   /                                           \                   /------M1
		//  WC1/2----\                 /                                             \                 /----WC1/2
		//            WEST SEMI-FINAL-/                                               \-EAST SEMI-FINAL
		//  P2-------/                                                                                 \-------M2
		//  P3------/                                                                                   \------M3
		//
		// _______________________________________________________________________________________________________
		//
		// C1 = Central 1st Seed (1st Place in Central)
		// P1 = Pacific 1st Seed
		// M1 = Metro 1st Seed
		// A1 = Atlantic 1st Seed
		// WC1/2 = Wild Card 1 OR 2 (Determined based on Standings)
		//
		
		
		// Creating Variables to hold the INDEX of the correct team in the standings
		// Playoff Teams Index:
		int c1 = -1;
		int c2 = -1;
		int c3 = -1;
		int p1 = -1;
		int p2 = -1;
		int p3 = -1;
		int westWC1 = -1;
		int westWC2 = -1;
		int west1 = -1;
		
		int a1 = -1;
		int a2 = -1;
		int a3 = -1;
		int m1 = -1;
		int m2 = -1;
		int m3 = -1;
		int eastWC1 = -1;
		int eastWC2 = -1;
		int east1 = -1;
		
		
		//////////////////////////////
		////// Assign Seeding ////////
		//////////////////////////////
		
		boolean[] teamAssigned = new boolean[numNHLTeams]; // Determines which teams have not been assigned a seed in playoffs
																// ( false = not printed, true = printed )
		
		for (int i = 0; i < numNHLTeams; i++) // set all boolean array elements to false
			teamAssigned[i] = false;
		
		for (int k = 0; k < standings.length; k++) // set all integer array elements to -1
			standings[k] = -1;
		
		int highestIndex = -1;
		int highestPts = -1;
		
		// Variable Used in Tiebreaker 5:
		int diffPts = 0; 	// difference of points between hIP and iP ( hIP - iP ):
							// 		POSITIVE if highestIndex wins tiebreaker
							//		NEGATIVE if highestIndex wins tiebreaker
							//		ZERO if still tied and need tiebreaker 6
		
		// Variables Used in Tiebreaker 6:
		int iDIFF = 0;  // i (challenger) Goal Differential (DIFF)
		int hIDIFF = 0; // highestIndex (current leader) Goal Differential (DIFF)
		
		
		for (int j = 0; j < numNHLTeams; j++)
		{
			highestPts = -1;
			diffPts = 0;
			hIDIFF = 0;
			iDIFF = 0;
			
			// Determine a potential highest point total (based on teams remaining)
			for (int i = 0; i < numNHLTeams; i++)
			{
				if( (teamArray[i].getPts() > highestPts) && teamAssigned[i] == false)
				{
					highestPts = teamArray[i].getPts();
					highestIndex = i;
				}
				else if ( (teamArray[i].getPts() == highestPts) && teamAssigned[i] == false)
				{
					// Tiebreaker 1: Games Played
					
					if ( teamArray[i].getGp() < teamArray[highestIndex].getGp() )
					{
						highestIndex = i;
					}
					else if ( teamArray[i].getGp() == teamArray[highestIndex].getGp() )
					{
						// Tiebreaker 2: Regulation Wins (RW)
						
						if ( teamArray[i].getRw() > teamArray[highestIndex].getRw() )
						{
							highestIndex = i;
						}
						else if ( teamArray[i].getRw() == teamArray[highestIndex].getRw() )
						{
							// Tiebreaker 3: Regulation + Overtime Wins (ROW)
							
							if ( teamArray[i].getRow() > teamArray[highestIndex].getRow() )
							{
								highestIndex = i;
							}
							else if ( teamArray[i].getRow() == teamArray[highestIndex].getRow() )
							{
								// Tiebreaker 4: Total Wins (W)
								
								if ( teamArray[i].getW() > teamArray[highestIndex].getW() )
								{
									highestIndex = i;
								}
								else if ( teamArray[i].getW() == teamArray[highestIndex].getW() )
								{
									// Tiebreaker 5: Games played between the two teams needing tiebreaker
									
									diffPts = tiebreaker5(highestIndex, i); // diffPts is difference of points between hIP and iP ( hIP - iP ):
																				// 		POSITIVE if highestIndex wins tiebreaker
																				//		NEGATIVE if highestIndex wins tiebreaker
																				//		ZERO if still tied and need tiebreaker 6
									
									if ( diffPts < 0 )
									{
										highestIndex = i;
									}
									else if ( diffPts == 0)
									{
										// Tiebreaker 6: Goal Differential (DIFF)
										
										iDIFF = teamArray[i].getGf() - teamArray[i].getGf();
										hIDIFF = teamArray[highestIndex].getGf() - teamArray[highestIndex].getGf();
										
										if ( iDIFF > hIDIFF )
										{
											highestIndex = i;
										}
										else if ( iDIFF == hIDIFF )
										{
											// Tiebreaker 7: Goals For (GF)
											
											if ( teamArray[i].getGf() > teamArray[highestIndex].getGf() )
											{
												highestIndex = i;
											}
											
											// else if ( teamArray[i].getGf() == teamArray[highestIndex].getGf() )
											
										}
									}
								}
							}
						}
					}
				}
			}

			////////// LEAGUE SEEDING /////////////
			standings[j] = highestIndex;
			
			////////// EAST TOP SEED //////////////	
			if (east1 == -1)
				if ( (teamArray[highestIndex].getDivision() == 'A' ||  teamArray[highestIndex].getDivision() == 'M') && east1 == -1 )
					east1 = highestIndex;
			
			///////// ATLANTIC DIVISION TOP 3 ///////////
			if ( teamArray[highestIndex].getDivision() == 'A' && a1 == -1 )
				a1 = highestIndex;
			else if ( teamArray[highestIndex].getDivision() == 'A' && a2 == -1 )
				a2 = highestIndex;
			else if ( teamArray[highestIndex].getDivision() == 'A' && a3 == -1 )
				a3 = highestIndex;
			///////// METRO DIVISION TOP 3 ///////////
			else if ( teamArray[highestIndex].getDivision() == 'M' && m1 == -1 )
				m1 = highestIndex;
			else if ( teamArray[highestIndex].getDivision() == 'M' && m2 == -1 )
				m2 = highestIndex;
			else if ( teamArray[highestIndex].getDivision() == 'M' && m3 == -1 )
				m3 = highestIndex;
			////////// EAST WILD CARD ////////////
			else if ( (teamArray[highestIndex].getDivision() == 'A' ||  teamArray[highestIndex].getDivision() == 'M') && eastWC1 == -1 )
				eastWC1 = highestIndex;
			else if ( (teamArray[highestIndex].getDivision() == 'A' ||  teamArray[highestIndex].getDivision() == 'M') && eastWC2 == -1 )
				eastWC2 = highestIndex;
				
			
			////////// WEST TOP SEED //////////////
			if (west1 == -1)
				if ( (teamArray[highestIndex].getDivision() == 'C' ||  teamArray[highestIndex].getDivision() == 'P') && west1 == -1 )
					west1 = highestIndex;
			
			///////// CENTRcL DIVISION TOP 3 ///////////
			if ( teamArray[highestIndex].getDivision() == 'C' && c1 == -1 )
				c1 = highestIndex;
			else if ( teamArray[highestIndex].getDivision() == 'C' && c2 == -1 )
				c2 = highestIndex;
			else if ( teamArray[highestIndex].getDivision() == 'C' && c3 == -1 )
				c3 = highestIndex;
			///////// PcCIFIC DIVISION TOP 3 ///////////
			else if ( teamArray[highestIndex].getDivision() == 'P' && p1 == -1 )
				p1 = highestIndex;
			else if ( teamArray[highestIndex].getDivision() == 'P' && p2 == -1 )
				p2 = highestIndex;
			else if ( teamArray[highestIndex].getDivision() == 'P' && p3 == -1 )
				p3 = highestIndex;
			////////// WEST WILD CcRD ////////////
			else if ( (teamArray[highestIndex].getDivision() == 'C' ||  teamArray[highestIndex].getDivision() == 'P') && westWC1 == -1 )
				westWC1 = highestIndex;
			else if ( (teamArray[highestIndex].getDivision() == 'C' ||  teamArray[highestIndex].getDivision() == 'P') && westWC2 == -1 )
				westWC2 = highestIndex;
			
			teamAssigned[highestIndex] = true;
			
		}
		
		// Saving team seeds into playoffArray
		// If 1st in Central is 1st in West
				if( c1 == west1 )
				{
					playoffArray[0] = new PlayoffSeries( westWC2 , c1 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[1] = new PlayoffSeries( c3 , c2 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[2] = new PlayoffSeries( westWC1 , p1 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[3] = new PlayoffSeries( p3 , p2 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					
				}
				// If 1st in Pacific is 1st in West
				else
				{
					playoffArray[0] = new PlayoffSeries( westWC1 , c1 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[1] = new PlayoffSeries( c3 , c2 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[2] = new PlayoffSeries( westWC2 , p1 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[3] = new PlayoffSeries( p3 , p2 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
				}
				
				// If 1st in Atlantic in 1st in East
				if( a1 == east1 )
				{
					
					playoffArray[4] = new PlayoffSeries( eastWC2 , a1 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[5] = new PlayoffSeries( a3 , a2 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[6] = new PlayoffSeries( eastWC1 , m1 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[7] = new PlayoffSeries( m3 , m2 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);					
				}
				// If 1st in Metro in 1st in East
				else
				{
					playoffArray[4] = new PlayoffSeries( eastWC1 , a1 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[5] = new PlayoffSeries( a3 , a2 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[6] = new PlayoffSeries( eastWC2 , m1 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);
					playoffArray[7] = new PlayoffSeries( m3 , m2 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);	
				}
		
		for(int i = 8; i < numPlayoffSeries; i++)
			playoffArray[i] = new PlayoffSeries( -1 , -1 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,false);	
		
		System.out.println();
	}
	
	void resetPlayoffs()
	{
		for (int i = 0; i < playoffArray.length; i++)
		{
			playoffArray[i].setLowerSeed(-1);
			playoffArray[i].setHigherSeed(-1);
			playoffArray[i].setLowerGamesWon(0);
			playoffArray[i].setHigherGamesWon(0);
			playoffArray[i].getGame1().setAwayScore(0);
			playoffArray[i].getGame1().setHomeScore(0);
			playoffArray[i].getGame1().setNumOt(0);
			playoffArray[i].getGame2().setAwayScore(0);
			playoffArray[i].getGame2().setHomeScore(0);
			playoffArray[i].getGame2().setNumOt(0);
			playoffArray[i].getGame3().setAwayScore(0);
			playoffArray[i].getGame3().setHomeScore(0);
			playoffArray[i].getGame3().setNumOt(0);
			playoffArray[i].getGame4().setAwayScore(0);
			playoffArray[i].getGame4().setHomeScore(0);
			playoffArray[i].getGame4().setNumOt(0);
			playoffArray[i].getGame5().setAwayScore(0);
			playoffArray[i].getGame5().setHomeScore(0);
			playoffArray[i].getGame5().setNumOt(0);
			playoffArray[i].getGame6().setAwayScore(0);
			playoffArray[i].getGame6().setHomeScore(0);
			playoffArray[i].getGame6().setNumOt(0);
			playoffArray[i].getGame7().setAwayScore(0);
			playoffArray[i].getGame7().setHomeScore(0);
			playoffArray[i].getGame7().setNumOt(0);
			playoffArray[i].setOver(false);
		}
		
		// Reset standings Array
		resetStandings();
	}
	
	void resetStandings()
	{
		for (int i = 0; i < standings.length; i++)
		{
			standings[i] = -1;
		}
	}
	
	void viewPlayoffProcedure() throws IOException
	{
		System.out.println("\nReview Playoff Procedure (2019-20 Season via NHL.com):\n");
		
		System.out.println("\t16 teams will qualify for the Stanley Cup Playoffs. The format is a set bracket that is largely division-based with \n"
				+ "\twild cards. The top three teams in each division will make up the first 12 teams in the playoffs. The remaining four \n"
				+ "\tspots will be filled by the next two highest-placed finishers in each conference, based on regular-season record and \n"
				+ "\tregardless of division. It is possible for one division in each conference to send five teams to the postseason while \n"
				+ "\tthe other sends just three. In the First Round, the division winner with the best record in each conference will be \n"
				+ "\tmatched against the wild-card team with the lesser record; the wild card team with the better record will play the other \n"
				+ "\tdivision winner. The teams finishing second and third in each division will meet in the First Round within the bracket \n"
				+ "\theaded by their respective division winners. First-round winners within each bracket play one another in the Second Round \n"
				+ "\tto determine the four participants in the Conference Finals. Home-ice advantage through the first two rounds goes to the \n"
				+ "\tteam that placed higher in the regular-season standings. In the Conference Finals and Stanley Cup Final, home-ice advantage \n"
				+ "\tgoes to the team that had the better regular-season record -- regardless of the teams' final standing in their respective \n\tdivisions.\n");				
				
		bufferMenu();
	}
	
	void playoffMenu() throws IOException 
	{
		int playoffMenuChoice = -1;
		String beginPlayoffsChoice = "";
		boolean validInput = false;
		String resetConfirm = "";
		boolean validPlayoffs = true;
		System.out.println();
		
		// Test if playoffArray has correct elements to begin playoffs by testing if FIRST ROUND series has correct indexes
		for( int i = 0; i < 8; i++)
		{
			if ( playoffArray[i].getLowerSeed() == -1 || playoffArray[i].getHigherSeed() == -1 )
			{
				validPlayoffs = false;
				break;
			}
		}
		
		if (!validPlayoffs)
		{
			while(!validInput)
			{
				System.out.print("There is no saved Playoff progress. \nWould you like to begin a new Playoffs using the current standings? (Y/N) ");
				beginPlayoffsChoice = br.readLine();
				
				if(beginPlayoffsChoice.equalsIgnoreCase("Y"))
				{
					beginPlayoffs();
					validPlayoffs = true;
					validInput = true;
				}
				else if (beginPlayoffsChoice.equalsIgnoreCase("N"))
					validInput = true;
				else
					System.out.println("\nError. Please enter either \"Y\" for Yes or \"N\" for No");
			}
		}
		
		if (validPlayoffs)
		{
			do
			{
				validInput = false;
				
				System.out.print("Playoff Menu: \n" +
					"\t1) First Round\n" +
					"\t2) Second Round\n" +
					"\t3) Conference Final\n" +
					"\t4) Stanley Cup Final\n" +
					"\t5) Draft Lottery (at end of Playoffs)\n" +
					"\t6) Reset Playoffs\n" +
					"\t7) Return to Main Menu\n\n" +
					"Enter your choice (1-7): ");
				
				playoffMenuChoice = Integer.parseInt(br.readLine());
				
				switch(playoffMenuChoice) {
					
					case(1):
						firstRound();
						break;
					
					case(2):
						secondRound();
						break;
						
					case(3):
						confFinal();
						break;	
						
					case(4):
						cupFinal();
						break;
						
					case(5):
						lotteryMenu();
						break;
						
					case(6):
						while(!validInput)
						{
							System.out.print("\nAre you sure you would like to reset the current playoff progress and begin a new playoffs \n"
													+ "with the current standings? (Y/N) ");
							resetConfirm = br.readLine();
							
							if (resetConfirm.equalsIgnoreCase("Y"))
							{
								validInput = true;
								
								resetPlayoffs();
								beginPlayoffs();

								System.out.println("\nPlayoff Progress has been Reset.\n");
							}
							else if (resetConfirm.equalsIgnoreCase("N"))
							{
								validInput = true;
								System.out.println("\nReset has been cancelled.\n");
							}
							else
							{
								System.out.print("\nError. Please enter either \"Y\" for Yes or \"N\" for No\n");
							}
						}						
					case(7): 
						// Returing to Main Menu...
						break;
					
					default:
						System.out.println("\nError. Please enter a choice between 1 and 7.\n");		
				}
				
			} while (playoffMenuChoice != 7);
		}
		
		System.out.println();
	}
	
	void firstRound() throws IOException
	{
		int firstRMenuChoice = -1;
		int seriesIndex = -1;
		int gamesPlayed = -1;
		int winningGame = 7;
		int seriesMenuChoice = -1;
		
		do
		{
			System.out.print("\nFirst Round Menu (Choose a Series): \n\n" +
				"\t      West:\n" +
				"\t1) " + teamArray[ playoffArray[0].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[0].getHigherSeed() ].getTeamAbr() + "\n" +
				"\t2) " + teamArray[ playoffArray[1].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[1].getHigherSeed() ].getTeamAbr() + "\n" +
				"\t3) " + teamArray[ playoffArray[2].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[2].getHigherSeed() ].getTeamAbr() + "\n" +
				"\t4) " + teamArray[ playoffArray[3].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[3].getHigherSeed() ].getTeamAbr() + "\n" +
				"\n\t      East:\n" +
				"\t5) " + teamArray[ playoffArray[4].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[4].getHigherSeed() ].getTeamAbr() + "\n" +
				"\t6) " + teamArray[ playoffArray[5].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[5].getHigherSeed() ].getTeamAbr() + "\n" +
				"\t7) " + teamArray[ playoffArray[6].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[6].getHigherSeed() ].getTeamAbr() + "\n" +
				"\t8) " + teamArray[ playoffArray[7].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[7].getHigherSeed() ].getTeamAbr() + "\n" +
				"\n\n\t9) Return to Playoff Menu\n\n" +
				"Enter your choice (1-9): ");
			
			firstRMenuChoice = Integer.parseInt(br.readLine());
			System.out.println();
			
			if (firstRMenuChoice >= 1 && firstRMenuChoice <= 8) 
			{
				// Set series index based on menu choice
				seriesIndex = firstRMenuChoice - 1;
				
				do
				{
					// Determine how many games in series have been played so far
					gamesPlayed = playoffArray[seriesIndex].getHigherGamesWon() + playoffArray[seriesIndex].getLowerGamesWon();
					
					// Determine the game where the series was decided (if it has already), and do not show the games that do not need to be played
					// Example: A Team wins a series 4-2 in Game 6, so there is no reason to display Game 7 if it will never be played
					if ( playoffArray[seriesIndex].getHigherGamesWon() == 4 || playoffArray[seriesIndex].getLowerGamesWon() == 4 )
						winningGame = playoffArray[seriesIndex].getHigherGamesWon() + playoffArray[seriesIndex].getLowerGamesWon();
					else // if series has not been won by either team, display all 7 games
						winningGame = 7;
					
					System.out.print("    ");
					
					// Display current series status
					if ( playoffArray[seriesIndex].isOver() )
					{
						if ( playoffArray[seriesIndex].getHigherGamesWon() > playoffArray[seriesIndex].getLowerGamesWon() )
							System.out.println(teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " WINS " + 
								playoffArray[seriesIndex].getHigherGamesWon() + "-" + playoffArray[seriesIndex].getLowerGamesWon());
						
						else
							System.out.println(teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " WINS " + 
									playoffArray[seriesIndex].getLowerGamesWon() + "-" + playoffArray[seriesIndex].getHigherGamesWon());
					}
					else if ( playoffArray[seriesIndex].getHigherGamesWon() > playoffArray[seriesIndex].getLowerGamesWon() )
							System.out.println(teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " LEADS " + 
								playoffArray[seriesIndex].getHigherGamesWon() + "-" + playoffArray[seriesIndex].getLowerGamesWon());
					
					else if ( playoffArray[seriesIndex].getHigherGamesWon() < playoffArray[seriesIndex].getLowerGamesWon() )
							System.out.println(teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " LEADS " + 
								playoffArray[seriesIndex].getLowerGamesWon() + "-" + playoffArray[seriesIndex].getHigherGamesWon());
					
					else if ( playoffArray[seriesIndex].getHigherGamesWon() == playoffArray[seriesIndex].getLowerGamesWon() )
							System.out.println("SERIES TIED " + playoffArray[seriesIndex].getLowerGamesWon() + "-" + 
								playoffArray[seriesIndex].getHigherGamesWon());
					
					System.out.println("  -------------------------------------");
					
					// Display Game 1
					if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() == 0 )
						System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
											teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL\n" );
					else if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() == 1 )
						System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
											teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL (OT)\n" );
					else if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() > 1 )
						System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
											teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL (" + 
											playoffArray[seriesIndex].getGame1().getNumOt() + "OT)\n" );
					else
						System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
								teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
					
					// Display Game 2
					if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() == 0 )
						System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
											teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL\n" );
					else if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() == 1 )
						System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
											teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL (OT)\n" );
					else if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() > 1 )
						System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
											teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL (" + 
											playoffArray[seriesIndex].getGame2().getNumOt() + "OT)\n" );
					else
						System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
								teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
					
					// Display Game 3
					if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() == 0 )
						System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
											teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL\n" );
					else if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() == 1 )
						System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
											teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL (OT)\n" );
					else if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() > 1 )
						System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
											teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
											playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL (" + 
											playoffArray[seriesIndex].getGame3().getNumOt() + "OT)\n" );
					else
						System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
								teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
				
					// Display Game 4
						if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() == 0 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() == 1 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() > 1 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame4().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
					
					// Display Game 5
					if (winningGame > 4)
					{
						if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() == 0 )
							System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() == 1 )
							System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() > 1 )
							System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame5().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
					}
					
					// Display Game 6
					if (winningGame > 5)
					{
						if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() == 0 )
							System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() == 1 )
							System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() > 1 )
							System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame6().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
					}
					
					// Display Game 7
					if (winningGame == 7)
					{
						if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() == 0 )
							System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() == 1 )
							System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() > 1 )
							System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame7().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
					}
					
												
					System.out.print("\nSeries Menu:\n"
							+ "\t1) Play Next Game\n"
							+ "\t2) Return to First Round Menu"
							+ "\n\nEnter your choice (1-2): ");
					
					seriesMenuChoice = Integer.parseInt(br.readLine());
					
					switch(seriesMenuChoice) {
					
						case(1):
							if ( !playoffArray[seriesIndex].isOver() )
								enterPlayoffGame(seriesIndex, (gamesPlayed + 1) );
							else
								System.out.println("\nThe Series has ended and no more games can be played in this series.\n");
							break;
						
						case(2):
							// Returning to First Round Menu...
							break;
						
						default:
							System.out.println("\nError. Please enter a choice between 1 and 2.\n");		
					}		
				} while (seriesMenuChoice != 2);
			}
			else if (firstRMenuChoice != 9)
					System.out.println("\nError. Please enter a choice between 1 and 9.\n");		
			
		} while (firstRMenuChoice != 9);
		
		System.out.println();
	}
		
	void secondRound() throws IOException
	{		
		int secondRMenuChoice = -1;
		boolean validSecondRound = true;
		boolean seedingSet = true;
		int seriesIndex = -1;
		int gamesPlayed = -1;
		int winningGame = 7;
		int seriesMenuChoice = -1;
		
		// Check if all First Round Series have ended
		for( int i = 0; i < 8; i++)
		{
			if ( !playoffArray[i].isOver() )
			{
				validSecondRound = false;
				break;
			}
		}
		
		// Check if seeding for Second Round has been set
		for( int i = 8; i < 11; i++)
		{
			if ( playoffArray[i].getHigherSeed() == -1 || playoffArray[i].getLowerSeed() == -1 )
			{
				seedingSet = false;
				break;
			}
		}
		
		if (validSecondRound)
		{
			// Set seeding for second round if needed
			if (!seedingSet)
			{
				secondRoundSeeding();
				seedingSet = true;
			}
			
			do
			{
				System.out.print("\nSecond Round Menu (Choose a Series): \n\n" +
					"\t      West:\n" +
					"\t1) " + teamArray[ playoffArray[8].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[8].getHigherSeed() ].getTeamAbr() + "\n" +
					"\t2) " + teamArray[ playoffArray[9].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[9].getHigherSeed() ].getTeamAbr() + "\n" +
					"\n\t      East:\n" +
					"\t3) " + teamArray[ playoffArray[10].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[10].getHigherSeed() ].getTeamAbr() + "\n" +
					"\t4) " + teamArray[ playoffArray[11].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[11].getHigherSeed() ].getTeamAbr() + "\n" +
					"\n\n\t5) Return to Playoff Menu\n\n" +
					"Enter your choice (1-5): ");
				
				secondRMenuChoice = Integer.parseInt(br.readLine());
				System.out.println();
				
				if (secondRMenuChoice >= 1 && secondRMenuChoice <= 4) 
				{
					// Set series index based on menu choice
					seriesIndex = secondRMenuChoice + 7;
					
					do
					{
						// Determine how many games in series have been played so far
						gamesPlayed = playoffArray[seriesIndex].getHigherGamesWon() + playoffArray[seriesIndex].getLowerGamesWon();
						
						// Determine the game where the series was decided (if it has already), and do not show the games that do not need to be played
						// Example: A Team wins a series 4-2 in Game 6, so there is no reason to display Game 7 if it will never be played
						if ( playoffArray[seriesIndex].getHigherGamesWon() == 4 || playoffArray[seriesIndex].getLowerGamesWon() == 4 )
							winningGame = playoffArray[seriesIndex].getHigherGamesWon() + playoffArray[seriesIndex].getLowerGamesWon();
						else // if series has not been won by either team, display all 7 games
							winningGame = 7;
						
						System.out.print("    ");
						
						// Display current series status
						if ( playoffArray[seriesIndex].isOver() )
						{
							if ( playoffArray[seriesIndex].getHigherGamesWon() > playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println(teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " WINS " + 
									playoffArray[seriesIndex].getHigherGamesWon() + "-" + playoffArray[seriesIndex].getLowerGamesWon());
							
							else
								System.out.println(teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " WINS " + 
										playoffArray[seriesIndex].getLowerGamesWon() + "-" + playoffArray[seriesIndex].getHigherGamesWon());
						}
						else if ( playoffArray[seriesIndex].getHigherGamesWon() > playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println(teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " LEADS " + 
									playoffArray[seriesIndex].getHigherGamesWon() + "-" + playoffArray[seriesIndex].getLowerGamesWon());
						
						else if ( playoffArray[seriesIndex].getHigherGamesWon() < playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println(teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " LEADS " + 
									playoffArray[seriesIndex].getLowerGamesWon() + "-" + playoffArray[seriesIndex].getHigherGamesWon());
						
						else if ( playoffArray[seriesIndex].getHigherGamesWon() == playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println("SERIES TIED " + playoffArray[seriesIndex].getLowerGamesWon() + "-" + 
									playoffArray[seriesIndex].getHigherGamesWon());
						
						System.out.println("  -------------------------------------");
						
						// Display Game 1
						if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() == 0 )
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() == 1 )
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() > 1 )
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame1().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						
						// Display Game 2
						if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() == 0 )
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() == 1 )
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() > 1 )
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame2().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						
						// Display Game 3
						if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() == 0 )
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() == 1 )
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() > 1 )
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame3().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
					
						// Display Game 4
						if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() == 0 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() == 1 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() > 1 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame4().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
						
						// Display Game 5
						if (winningGame > 4)
						{
							if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() == 0 )
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL\n" );
							else if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() == 1 )
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL (OT)\n" );
							else if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() > 1 )
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL (" + 
													playoffArray[seriesIndex].getGame5().getNumOt() + "OT)\n" );
							else
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
										teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						}
						
						// Display Game 6
						if (winningGame > 5)
						{
							if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() == 0 )
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL\n" );
							else if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() == 1 )
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL (OT)\n" );
							else if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() > 1 )
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL (" + 
													playoffArray[seriesIndex].getGame6().getNumOt() + "OT)\n" );
							else
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
										teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
						}
						
						// Display Game 7
						if (winningGame == 7)
						{
							if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() == 0 )
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL\n" );
							else if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() == 1 )
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL (OT)\n" );
							else if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() > 1 )
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL (" + 
													playoffArray[seriesIndex].getGame7().getNumOt() + "OT)\n" );
							else
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
										teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						}
													
						System.out.print("\nSeries Menu:\n"
								+ "\t1) Play Next Game\n"
								+ "\t2) Return to Second Round Menu"
								+ "\n\nEnter your choice (1-2): ");
						
						seriesMenuChoice = Integer.parseInt(br.readLine());
						
						switch(seriesMenuChoice) {
						
							case(1):
								if ( !playoffArray[seriesIndex].isOver() )
									enterPlayoffGame(seriesIndex, (gamesPlayed + 1) );
								else
									System.out.println("\nThe Series has ended and no more games can be played in this series.\n");
								break;
							
							case(2):
								// Returning to First Round Menu...
								break;
							
							default:
								System.out.println("\nError. Please enter a choice between 1 and 2.\n");		
						}	
						
					} while (seriesMenuChoice != 2);
				}
				else if (secondRMenuChoice != 5)
						System.out.println("\nError. Please enter a choice between 1 and 5.\n");		
			} while (secondRMenuChoice != 5);
		}
		else
			System.out.println("\nAll playoff series in the First Round must conclude before the Second Round can begin.");
		
		System.out.println();
	}
	
	void confFinal() throws IOException
	{
		int confFinalMenuChoice = -1;
		boolean validConfFinal = true;
		boolean seedingSet = true;
		int seriesIndex = -1;
		int gamesPlayed = -1;
		int winningGame = 7;
		int seriesMenuChoice = -1;
		
		for( int i = 8; i < 12; i++)
		{
			if ( !playoffArray[i].isOver() )
			{
				validConfFinal = false;
				break;
			}
		}
		
		// Check if seeding for Conference Final has been set
		for( int i = 12; i < 14; i++)
		{
			if ( playoffArray[i].getHigherSeed() == -1 || playoffArray[i].getLowerSeed() == -1 )
			{
				seedingSet = false;
				break;
			}
		}
		
		if (validConfFinal)
		{
			// Set seeding for second round if needed
			if (!seedingSet)
			{
				confFinalSeeding();
				seedingSet = true;
			}
			
			do
			{
				System.out.print("\nConference Final Menu (Choose a Series): \n\n" +
					"\t      West Final:\n" +
					"\t1) " + teamArray[ playoffArray[12].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[12].getHigherSeed() ].getTeamAbr() + "\n" +
					"\n\t      East Final:\n" +
					"\t2) " + teamArray[ playoffArray[13].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[13].getHigherSeed() ].getTeamAbr() + "\n" +
					"\n\n\t3) Return to Playoff Menu\n\n" +
					"Enter your choice (1-3): ");
				
				confFinalMenuChoice = Integer.parseInt(br.readLine());
				System.out.println();
				
				if (confFinalMenuChoice == 1 || confFinalMenuChoice == 2) 
				{
					// Set series index based on menu choice
					seriesIndex = confFinalMenuChoice + 11;						
					
					do
					{
						// Determine how many games in series have been played so far
						gamesPlayed = playoffArray[seriesIndex].getHigherGamesWon() + playoffArray[seriesIndex].getLowerGamesWon();
						
						// Determine the game where the series was decided (if it has already), and do not show the games that do not need to be played
						// Example: A Team wins a series 4-2 in Game 6, so there is no reason to display Game 7 if it will never be played
						if ( playoffArray[seriesIndex].getHigherGamesWon() == 4 || playoffArray[seriesIndex].getLowerGamesWon() == 4 )
							winningGame = playoffArray[seriesIndex].getHigherGamesWon() + playoffArray[seriesIndex].getLowerGamesWon();
						else // if series has not been won by either team, display all 7 games
							winningGame = 7;
						
						System.out.print("    ");
						
						// Display current series status
						if ( playoffArray[seriesIndex].isOver() )
						{
							if ( playoffArray[seriesIndex].getHigherGamesWon() > playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println(teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " WINS " + 
									playoffArray[seriesIndex].getHigherGamesWon() + "-" + playoffArray[seriesIndex].getLowerGamesWon());
							
							else
								System.out.println(teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " WINS " + 
										playoffArray[seriesIndex].getLowerGamesWon() + "-" + playoffArray[seriesIndex].getHigherGamesWon());
						}
						else if ( playoffArray[seriesIndex].getHigherGamesWon() > playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println(teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " LEADS " + 
									playoffArray[seriesIndex].getHigherGamesWon() + "-" + playoffArray[seriesIndex].getLowerGamesWon());
						
						else if ( playoffArray[seriesIndex].getHigherGamesWon() < playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println(teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " LEADS " + 
									playoffArray[seriesIndex].getLowerGamesWon() + "-" + playoffArray[seriesIndex].getHigherGamesWon());
						
						else if ( playoffArray[seriesIndex].getHigherGamesWon() == playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println("SERIES TIED " + playoffArray[seriesIndex].getLowerGamesWon() + "-" + 
									playoffArray[seriesIndex].getHigherGamesWon());
						
						System.out.println("  -------------------------------------");
						
						// Display Game 1
						if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() == 0 )
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() == 1 )
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() > 1 )
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame1().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						
						// Display Game 2
						if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() == 0 )
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() == 1 )
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() > 1 )
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame2().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						
						// Display Game 3
						if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() == 0 )
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() == 1 )
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() > 1 )
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame3().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
					
						// Display Game 4
						if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() == 0 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() == 1 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() > 1 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame4().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
						
						// Display Game 5
						if (winningGame > 4)
						{
							if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() == 0 )
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL\n" );
							else if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() == 1 )
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL (OT)\n" );
							else if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() > 1 )
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL (" + 
													playoffArray[seriesIndex].getGame5().getNumOt() + "OT)\n" );
							else
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
										teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						}
						
						// Display Game 6
						if (winningGame > 5)
						{
							if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() == 0 )
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL\n" );
							else if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() == 1 )
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL (OT)\n" );
							else if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() > 1 )
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL (" + 
													playoffArray[seriesIndex].getGame6().getNumOt() + "OT)\n" );
							else
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
										teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
						}
						
						// Display Game 7
						if (winningGame == 7)
						{
							if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() == 0 )
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL\n" );
							else if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() == 1 )
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL (OT)\n" );
							else if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() > 1 )
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL (" + 
													playoffArray[seriesIndex].getGame7().getNumOt() + "OT)\n" );
							else
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
										teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						}
													
						System.out.print("\nSeries Menu:\n"
								+ "\t1) Play Next Game\n"
								+ "\t2) Return to Conference Final Menu"
								+ "\n\nEnter your choice (1-2): ");
						
						seriesMenuChoice = Integer.parseInt(br.readLine());
						
						switch(seriesMenuChoice) {
						
							case(1):
								if ( !playoffArray[seriesIndex].isOver() )
									enterPlayoffGame(seriesIndex, (gamesPlayed + 1) );
								else
									System.out.println("\nThe Series has ended and no more games can be played in this series.\n");
								break;
							
							case(2):
								// Returning to First Round Menu...
								break;
							
							default:
								System.out.println("\nError. Please enter a choice between 1 and 2.\n");		
						}
					} while (seriesMenuChoice != 2);	
				}
				else if (confFinalMenuChoice != 3)
						System.out.println("\nError. Please enter a choice between 1 and 3.\n");		
			} while (confFinalMenuChoice != 3);
		}
		else
			System.out.println("\nAll playoff series in the Second Round must conclude before the Conference Final can begin.");
		
		System.out.println();
	}
	
	void cupFinal() throws IOException
	{
		int scFinalMenuChoice = -1;
		boolean validSCFinal = true;
		boolean seedingSet = true;
		int seriesIndex = -1;
		int gamesPlayed = -1;
		int winningGame = 7;
		int seriesMenuChoice = -1;
		
		for( int i = 12; i < 14; i++)
		{
			if ( !playoffArray[i].isOver() )
			{
				validSCFinal = false;
				break;
			}
		}
		

		if ( playoffArray[14].getHigherSeed() == -1 || playoffArray[14].getLowerSeed() == -1 )
			seedingSet = false;
		
				
		if (validSCFinal)
		{
			// Set seeding for second round if needed
			if (!seedingSet)
			{
				cupFinalSeeding();
				seedingSet = true;
			}
		
			do
			{
				System.out.print("\nStanley Cup Final Menu (Choose a Series): \n\n" +
					"\t      Stanley Cup Final:\n" +
					"\t1) " + teamArray[ playoffArray[14].getLowerSeed() ].getTeamAbr() + " vs. " + teamArray[ playoffArray[14].getHigherSeed() ].getTeamAbr() + "\n" +
					"\n\n\t2) Return to Playoff Menu\n\n" +
					"Enter your choice (1-2): ");
				
				scFinalMenuChoice = Integer.parseInt(br.readLine());
				System.out.println();
				
				if (scFinalMenuChoice == 1) 
				{
					// Set series index
					seriesIndex = 14;
					
					do
					{
						// Determine how many games in series have been played so far
						gamesPlayed = playoffArray[seriesIndex].getHigherGamesWon() + playoffArray[seriesIndex].getLowerGamesWon();
						
						// Determine the game where the series was decided (if it has already), and do not show the games that do not need to be played
						// Example: A Team wins a series 4-2 in Game 6, so there is no reason to display Game 7 if it will never be played
						if ( playoffArray[seriesIndex].getHigherGamesWon() == 4 || playoffArray[seriesIndex].getLowerGamesWon() == 4 )
							winningGame = playoffArray[seriesIndex].getHigherGamesWon() + playoffArray[seriesIndex].getLowerGamesWon();
						else // if series has not been won by either team, display all 7 games
							winningGame = 7;
						
						System.out.print("    ");
						
						// Display current series status
						if ( playoffArray[seriesIndex].isOver() )
						{
							if ( playoffArray[seriesIndex].getHigherGamesWon() > playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println(teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " WINS " + 
									playoffArray[seriesIndex].getHigherGamesWon() + "-" + playoffArray[seriesIndex].getLowerGamesWon());
							
							else
								System.out.println(teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " WINS " + 
										playoffArray[seriesIndex].getLowerGamesWon() + "-" + playoffArray[seriesIndex].getHigherGamesWon());
						}
						else if ( playoffArray[seriesIndex].getHigherGamesWon() > playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println(teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " LEADS " + 
									playoffArray[seriesIndex].getHigherGamesWon() + "-" + playoffArray[seriesIndex].getLowerGamesWon());
						
						else if ( playoffArray[seriesIndex].getHigherGamesWon() < playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println(teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " LEADS " + 
									playoffArray[seriesIndex].getLowerGamesWon() + "-" + playoffArray[seriesIndex].getHigherGamesWon());
						
						else if ( playoffArray[seriesIndex].getHigherGamesWon() == playoffArray[seriesIndex].getLowerGamesWon() )
								System.out.println("SERIES TIED " + playoffArray[seriesIndex].getLowerGamesWon() + "-" + 
									playoffArray[seriesIndex].getHigherGamesWon());
						
						System.out.println("  -------------------------------------");
						
						// Display Game 1
						if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() == 0 )
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() == 1 )
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 1 && playoffArray[seriesIndex].getGame1().getNumOt() > 1 )
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame1().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame1().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 1: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						
						// Display Game 2
						if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() == 0 )
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() == 1 )
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 2 && playoffArray[seriesIndex].getGame2().getNumOt() > 1 )
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame2().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame2().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 2: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						
						// Display Game 3
						if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() == 0 )
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() == 1 )
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 3 && playoffArray[seriesIndex].getGame3().getNumOt() > 1 )
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame3().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame3().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 3: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
					
						// Display Game 4
						if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() == 0 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL\n" );
						else if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() == 1 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL (OT)\n" );
						else if (gamesPlayed >= 4 && playoffArray[seriesIndex].getGame4().getNumOt() > 1 )
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getAwayScore() + " @ " + 
												teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
												playoffArray[seriesIndex].getGame4().getHomeScore() + " - FINAL (" + 
												playoffArray[seriesIndex].getGame4().getNumOt() + "OT)\n" );
						else
							System.out.print("   Game 4: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
									teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
						
						// Display Game 5
						if (winningGame > 4)
						{
							if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() == 0 )
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL\n" );
							else if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() == 1 )
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL (OT)\n" );
							else if (gamesPlayed >= 5 && playoffArray[seriesIndex].getGame5().getNumOt() > 1 )
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame5().getHomeScore() + " - FINAL (" + 
													playoffArray[seriesIndex].getGame5().getNumOt() + "OT)\n" );
							else
								System.out.print("   Game 5: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
										teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						}
						
						// Display Game 6
						if (winningGame > 5)
						{
							if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() == 0 )
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL\n" );
							else if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() == 1 )
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL (OT)\n" );
							else if (gamesPlayed >= 6 && playoffArray[seriesIndex].getGame6().getNumOt() > 1 )
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame6().getHomeScore() + " - FINAL (" + 
													playoffArray[seriesIndex].getGame6().getNumOt() + "OT)\n" );
							else
								System.out.print("   Game 6: " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
										teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + "\n" );
						}
						
						// Display Game 7
						if (winningGame == 7)
						{
							if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() == 0 )
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL\n" );
							else if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() == 1 )
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL (OT)\n" );
							else if (gamesPlayed == 7 && playoffArray[seriesIndex].getGame7().getNumOt() > 1 )
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getAwayScore() + " @ " + 
													teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " " + 
													playoffArray[seriesIndex].getGame7().getHomeScore() + " - FINAL (" + 
													playoffArray[seriesIndex].getGame7().getNumOt() + "OT)\n" );
							else
								System.out.print("   Game 7: " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
										teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + "\n" );
						}
													
						System.out.print("\nSeries Menu:\n"
								+ "\t1) Play Next Game\n"
								+ "\t2) Return to Stanley Cup Final Menu"
								+ "\n\nEnter your choice (1-2): ");
						
						seriesMenuChoice = Integer.parseInt(br.readLine());
						
						switch(seriesMenuChoice) {
						
							case(1):
								if ( !playoffArray[seriesIndex].isOver() )
									enterPlayoffGame(seriesIndex, (gamesPlayed + 1) );
								else
									System.out.println("\nThe Series has ended and no more games can be played in this series.\n");
								break;
							
							case(2):
								// Returning to First Round Menu...
								break;
							
							default:
								System.out.println("\nError. Please enter a choice between 1 and 2.\n");		
						}					
					} while (seriesMenuChoice != 2);
				}
				else if (scFinalMenuChoice != 2)
						System.out.println("\nError. Please enter a choice between 1 and 2.\n");		
			} while (scFinalMenuChoice != 2);
		}
		else
			System.out.println("\nAll playoff series in the Conference Final must conclude before the Stanley Cup Final can begin.");
		
		System.out.println();
	}

	void enterPlayoffGame(int seriesIndex, int gameNumber) throws IOException
	{
		int homeScore = -1;
		int awayScore = -1;
		int numOT = -1;	
		String userOT = "";
		boolean validInput = false;
		boolean validScore = false;
		boolean validOT = false;
		int difference = -20;
		
		if ( gameNumber == 1 || gameNumber == 2 || gameNumber == 5 || gameNumber == 7 )
			System.out.println("\n" + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " @ " + 
					teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " - Game " + gameNumber);
		else
			System.out.println("\n" + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " @ " + 
					teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " - Game " + gameNumber);
		
		// Did the game reach overtime
		while(!validInput)
		{
			System.out.print("\nDid the game reach Overtime? (Y/N) ");
			userOT = br.readLine();
			
			if(userOT.equalsIgnoreCase("Y"))
			{
				validInput = true;
				
				while(!validOT)
				{
					System.out.print("How many Overtime periods did the Game last? ");
					numOT = Integer.parseInt(br.readLine());
					
					if (numOT > 0)
						validOT = true;
					else
						System.out.println("\nError. Invalid number of OT periods (must be greater than 0)");
				}
				
			}
			else if (userOT.equalsIgnoreCase("N"))
			{
				validInput = true;
				numOT = 0;
			}
			else
			{
				System.out.println("\nError. Please enter either \"Y\" for Yes or \"N\" for No");
			}
		}
		
		// Enter both teams score
		while (!validScore)
		{
			validInput = false;
			
			// Final away team score
			while(!validInput)
			{
				if (gameNumber == 1 || gameNumber == 2 || gameNumber == 5 || gameNumber == 7)
					System.out.print("\nEnter final " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " score: ");
				else
					System.out.print("\nEnter final " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " score: ");
				
				awayScore = Integer.parseInt(br.readLine());
				
				if(awayScore >= 0)
				{
					validInput = true;
				}
				else
				{
					System.out.println("\nError. Invalid Score - must be greater than or equal to zero\n");
				}
			}
			
			validInput = false;
			
			// Final home team score
			while(!validInput)
			{
				if (gameNumber == 1 || gameNumber == 2 || gameNumber == 5 || gameNumber == 7)
					System.out.print("Enter final " + teamArray[ playoffArray[seriesIndex].getHigherSeed() ].getTeamAbr() + " score: ");
				else
					System.out.print("Enter final " + teamArray[ playoffArray[seriesIndex].getLowerSeed() ].getTeamAbr() + " score: ");
				
				homeScore = Integer.parseInt(br.readLine());
				
				if(homeScore >= 0)
				{
					validInput = true;
				}
				else
				{
					System.out.println("\nError. Invalid Score (must be greater than 0)\n");
				}
			}
			
			difference = homeScore - awayScore;
			
			if (homeScore == 0 && awayScore == 0)
			{
				System.out.println("\nError. Invalid Score - both teams cannot have zero goals");
			}
			else if (homeScore == awayScore)
			{
				System.out.println("\nError. Invalid Score - both teams cannot have the same amount of goals");
			}
			else if ( numOT >= 1 && difference != 1 && difference != -1)
			{
				System.out.println("\nError. Invalid Score - Since the game entered OT the final score between both teams must be one goal");
			}
			else
			{
				validScore = true;
				
				// All inputs are valid, enter data into playoffArray
				if (gameNumber == 1)
				{
					playoffArray[seriesIndex].getGame1().setAwayScore(awayScore);
					playoffArray[seriesIndex].getGame1().setHomeScore(homeScore);
					playoffArray[seriesIndex].getGame1().setNumOt(numOT);
				}
				else if (gameNumber == 2)
				{
					playoffArray[seriesIndex].getGame2().setAwayScore(awayScore);
					playoffArray[seriesIndex].getGame2().setHomeScore(homeScore);
					playoffArray[seriesIndex].getGame2().setNumOt(numOT);
				}
				else if (gameNumber == 3)
				{
					playoffArray[seriesIndex].getGame3().setAwayScore(awayScore);
					playoffArray[seriesIndex].getGame3().setHomeScore(homeScore);
					playoffArray[seriesIndex].getGame3().setNumOt(numOT);
				}
				else if (gameNumber == 4)
				{
					playoffArray[seriesIndex].getGame4().setAwayScore(awayScore);
					playoffArray[seriesIndex].getGame4().setHomeScore(homeScore);
					playoffArray[seriesIndex].getGame4().setNumOt(numOT);
				}
				else if (gameNumber == 5)
				{
					playoffArray[seriesIndex].getGame5().setAwayScore(awayScore);
					playoffArray[seriesIndex].getGame5().setHomeScore(homeScore);
					playoffArray[seriesIndex].getGame5().setNumOt(numOT);
				}
				else if (gameNumber == 6)
				{
					playoffArray[seriesIndex].getGame6().setAwayScore(awayScore);
					playoffArray[seriesIndex].getGame6().setHomeScore(homeScore);
					playoffArray[seriesIndex].getGame6().setNumOt(numOT);
				}
				else if (gameNumber == 7)
				{
					playoffArray[seriesIndex].getGame7().setAwayScore(awayScore);
					playoffArray[seriesIndex].getGame7().setHomeScore(homeScore);
					playoffArray[seriesIndex].getGame7().setNumOt(numOT);
				}
				
				
				// Home team wins
				if (homeScore > awayScore)
				{
					// Add 1 to number of games won to home team in playoffArray
					if (gameNumber == 1 || gameNumber == 2 || gameNumber == 5 || gameNumber == 7)
						playoffArray[seriesIndex].setHigherGamesWon( playoffArray[seriesIndex].getHigherGamesWon() + 1 );
					else
						playoffArray[seriesIndex].setLowerGamesWon( playoffArray[seriesIndex].getLowerGamesWon() + 1 );					
				}
				// Away team wins
				else
				{
					// Add 1 to number of games won to away team in playoffArray
					if (gameNumber == 1 || gameNumber == 2 || gameNumber == 5 || gameNumber == 7)
						playoffArray[seriesIndex].setLowerGamesWon( playoffArray[seriesIndex].getLowerGamesWon() + 1 );
					else
						playoffArray[seriesIndex].setHigherGamesWon( playoffArray[seriesIndex].getHigherGamesWon() + 1 );	
				}
			}
		}

		// Check if this game decided the series
		if ( playoffArray[seriesIndex].getHigherGamesWon() == 4 || playoffArray[seriesIndex].getLowerGamesWon() == 4 )
			playoffArray[seriesIndex].setOver(true);
		
		System.out.println();
		bufferSeriesMenu();
	}
	
	void secondRoundSeeding()
	{
		// Note on PLAYOFF seeding: 
				//	 - in ROUND 1 and ROUND 2 the HIGHER SEED has home ice
				//	 - in CONFERENCE FINAL and STANLEY CUP FINAL the team with more regular season PTS has home ice
		
		// Series 8 Seeding (West Semi-Final 1 - Central)
		
		if ( playoffArray[0].getHigherGamesWon() == 4 )
		{
			playoffArray[8].setHigherSeed( playoffArray[0].getHigherSeed() );
			
			if ( playoffArray[1].getHigherGamesWon() == 4 )
			{
				playoffArray[8].setLowerSeed( playoffArray[1].getHigherSeed() );
			}
			else if ( playoffArray[1].getLowerGamesWon() == 4 )
			{
				playoffArray[8].setLowerSeed( playoffArray[1].getLowerSeed() );
			}
		}
		else if ( playoffArray[0].getLowerGamesWon() == 4 )
		{
			playoffArray[8].setLowerSeed( playoffArray[0].getLowerSeed() );
			
			if ( playoffArray[1].getHigherGamesWon() == 4 )
			{
				playoffArray[8].setHigherSeed( playoffArray[1].getHigherSeed() );
			}
			else if ( playoffArray[1].getLowerGamesWon() == 4 )
			{
				playoffArray[8].setHigherSeed( playoffArray[1].getLowerSeed() );
			}
		}
		
		// Series 9 Seeding (West Semi-Final 2 - Pacific)
		
		if ( playoffArray[2].getHigherGamesWon() == 4 )
		{
			playoffArray[9].setHigherSeed( playoffArray[2].getHigherSeed() );
			
			if ( playoffArray[3].getHigherGamesWon() == 4 )
			{
				playoffArray[9].setLowerSeed( playoffArray[3].getHigherSeed() );
			}
			else if ( playoffArray[3].getLowerGamesWon() == 4 )
			{
				playoffArray[9].setLowerSeed( playoffArray[3].getLowerSeed() );
			}
		}
		else if ( playoffArray[2].getLowerGamesWon() == 4 )
		{
			playoffArray[9].setLowerSeed( playoffArray[2].getLowerSeed() );
			
			if ( playoffArray[3].getHigherGamesWon() == 4 )
			{
				playoffArray[9].setHigherSeed( playoffArray[3].getHigherSeed() );
			}
			else if ( playoffArray[3].getLowerGamesWon() == 4 )
			{
				playoffArray[9].setHigherSeed( playoffArray[3].getLowerSeed() );
			}
		}
		
		// Series 10 Seeding (East Semi-Final 1 - Atlantic)
		
		if ( playoffArray[4].getHigherGamesWon() == 4 )
		{
			playoffArray[10].setHigherSeed( playoffArray[4].getHigherSeed() );
			
			if ( playoffArray[5].getHigherGamesWon() == 4 )
			{
				playoffArray[10].setLowerSeed( playoffArray[5].getHigherSeed() );
			}
			else if ( playoffArray[5].getLowerGamesWon() == 4 )
			{
				playoffArray[10].setLowerSeed( playoffArray[5].getLowerSeed() );
			}
		}
		else if ( playoffArray[4].getLowerGamesWon() == 4 )
		{
			playoffArray[10].setLowerSeed( playoffArray[4].getLowerSeed() );
			
			if ( playoffArray[10].getHigherGamesWon() == 4 )
			{
				playoffArray[10].setHigherSeed( playoffArray[5].getHigherSeed() );
			}
			else if ( playoffArray[5].getLowerGamesWon() == 4 )
			{
				playoffArray[10].setHigherSeed( playoffArray[5].getLowerSeed() );
			}
		}
		
		// Series 11 Seeding (East Semi-Final 2 - Metro)
		
		if ( playoffArray[6].getHigherGamesWon() == 4 )
		{
			playoffArray[11].setHigherSeed( playoffArray[6].getHigherSeed() );
			
			if ( playoffArray[7].getHigherGamesWon() == 4 )
			{
				playoffArray[11].setLowerSeed( playoffArray[7].getHigherSeed() );
			}
			else if ( playoffArray[7].getLowerGamesWon() == 4 )
			{
				playoffArray[11].setLowerSeed( playoffArray[7].getLowerSeed() );
			}
		}
		else if ( playoffArray[6].getLowerGamesWon() == 4 )
		{
			playoffArray[11].setLowerSeed( playoffArray[6].getLowerSeed() );
			
			if ( playoffArray[7].getHigherGamesWon() == 4 )
			{
				playoffArray[11].setHigherSeed( playoffArray[7].getHigherSeed() );
			}
			else if ( playoffArray[7].getLowerGamesWon() == 4 )
			{
				playoffArray[11].setHigherSeed( playoffArray[7].getLowerSeed() );
			}
		}
			
	}
	
	void confFinalSeeding()
	{
		// Note on PLAYOFF seeding: 
		//	 - in ROUND 1 and ROUND 2 the HIGHER SEED has home ice
		//	 - in CONFERENCE FINAL and STANLEY CUP FINAL the team with more regular season PTS has home ice
		
		int westSeed1 = -1, 
			westSeed2 = -1,
			eastSeed1 = -1,
			eastSeed2 = -1;
		
		// Series 12 Seeding (West Final)
		
		if ( playoffArray[8].getHigherGamesWon() == 4 )
			westSeed1 = playoffArray[8].getHigherSeed();
		else if ( playoffArray[8].getLowerGamesWon() == 4 )
			westSeed1 = playoffArray[8].getLowerSeed();
		
		if ( playoffArray[9].getHigherGamesWon() == 4 )
			westSeed2 = playoffArray[9].getHigherSeed();
		else if ( playoffArray[9].getLowerGamesWon() == 4 )
			westSeed2 = playoffArray[9].getLowerSeed();
		
		for (int i = 0; i < standings.length; i++)
		{
			// Determine which index appears in the array first (that team in the higher seed)
			if ( standings[i] == westSeed1 )
			{
				playoffArray[12].setHigherSeed(westSeed1);
				playoffArray[12].setLowerSeed(westSeed2);
				break;
			}
			else if ( standings[i] == westSeed2 )
			{
				playoffArray[12].setHigherSeed(westSeed2);
				playoffArray[12].setLowerSeed(westSeed1);
				break;
			}
		}
		
		// Series 13 Seeding (East Final)
		
		if ( playoffArray[10].getHigherGamesWon() == 4 )
			eastSeed1 = playoffArray[10].getHigherSeed();
		else if ( playoffArray[10].getLowerGamesWon() == 4 )
			eastSeed1 = playoffArray[10].getLowerSeed();
		
		if ( playoffArray[11].getHigherGamesWon() == 4 )
			eastSeed2 = playoffArray[11].getHigherSeed();
		else if ( playoffArray[11].getLowerGamesWon() == 4 )
			eastSeed2 = playoffArray[11].getLowerSeed();
		
		for (int i = 0; i < standings.length; i++)
		{
			// Determine which index appears in the array first (that team in the higher seed)
			if ( standings[i] == eastSeed1 )
			{
				playoffArray[13].setHigherSeed(eastSeed1);
				playoffArray[13].setLowerSeed(eastSeed2);
				break;
			}
			else if ( standings[i] == eastSeed2 )
			{
				playoffArray[13].setHigherSeed(eastSeed2);
				playoffArray[13].setLowerSeed(eastSeed1);
				break;
			}
		}
	}

	void cupFinalSeeding()
	{
		// Note on PLAYOFF seeding: 
				//	 - in ROUND 1 and ROUND 2 the HIGHER SEED has home ice
				//	 - in CONFERENCE FINAL and STANLEY CUP FINAL the team with more regular season PTS has home ice
				
		int seed1 = -1, 
			seed2 = -1;
		
		// Series 12 Seeding (West Final)
		
		if ( playoffArray[12].getHigherGamesWon() == 4 )
			seed1 = playoffArray[12].getHigherSeed();
		else if ( playoffArray[12].getLowerGamesWon() == 4 )
			seed1 = playoffArray[12].getLowerSeed();
		
		// Series 13 Seeding (East Final)
		
		if ( playoffArray[13].getHigherGamesWon() == 4 )
			seed2 = playoffArray[13].getHigherSeed();
		else if ( playoffArray[13].getLowerGamesWon() == 4 )
			seed2 = playoffArray[13].getLowerSeed();
		
		for (int i = 0; i < standings.length; i++)
		{
			// Determine which index appears in the array first (that team in the higher seed)
			if ( standings[i] == seed1 )
			{
				playoffArray[14].setHigherSeed(seed1);
				playoffArray[14].setLowerSeed(seed2);
				break;
			}
			else if ( standings[i] == seed2 )
			{
				playoffArray[14].setHigherSeed(seed2);
				playoffArray[14].setLowerSeed(seed1);
				break;
			}
		}
	}
	
	void lotteryMenu() throws IOException
	{
		int playoffMenuChoice = -1;
		System.out.println();

		if ( playoffArray[14].isOver() )
		{
			do
			{
				System.out.print("Lottery Menu: \n" +
					"\t1) Simulate a Lottery (Results NOT Saved)\n" +
					"\t2) View Each Team's Odds\n" +
					"\t3) How is the Draft Order Determined?\n" +
					"\t4) Return to Playoff Menu\n\n" +
					"Enter your choice (1-3): ");
				
				playoffMenuChoice = Integer.parseInt(br.readLine());
				
				switch(playoffMenuChoice) {
					
					case(1):
						lotterySim();
						break;
					
					case(2):
						viewDraftOdds();
						break;
						
					case(3):
						viewDraftProcess();
						break;
						
					case(4): 
						// Returing to Playoff Menu...
						break;
					
					default:
						System.out.println("\nError. Please enter a choice between 1 and 4.\n");		
				}
				
			} while (playoffMenuChoice != 4);
		}
		else
		{
			System.out.println("The Stanley Cup Final must conclude before the Draft Order can be decided");
		}
		
		System.out.println();
	}
	
	void lotterySim() throws IOException
	{		  
		System.out.println();
		
        // create instance of Random class 
        Random rand = new Random(); 
        
        // Lottery array to have all teams that missed playoffs enter the lottery, [0] has highest odds of 1st overall pick (worst team in the standings)
        int[] lottery = new int[numNHLTeams - 16];
        
        boolean madePlayoffs = false;
        
        int lotteryWinner1 = -1; // Winner of 1st Overall Pick
        int lotteryWinner2 = -1; // Winner of 2nd Overall Pick
        int lotteryWinner3 = -1; // Winner of 3rd Overall Pick
        
        int k = 0;
		
		// Enter teams that missed playoffs into lottery array
		for(int i = (numNHLTeams - 1); i >= 0; i--)
		{
			// Determine if team made the playoffs (if yes, they have 0% lottery odds)
			for(int j = 0; j < 8; j++)
			{
				if( playoffArray[j].getHigherSeed() == standings[i] || playoffArray[j].getLowerSeed() == standings[i] )
				{
					madePlayoffs = true;
					break;
				}
				else
					madePlayoffs = false;
			}
			
			if (!madePlayoffs)
			{
				lottery[k] = standings[i];
				k++;
			}
			
			if (k > 14)
				break;
		}
  
		// 1st Overall Pick
		
        // Generate random integer in range 0 to 999 to determine 1st Overall Lottery Winner
        int randInt1 = rand.nextInt(1000); 
        
        // 1st Overall Lottery Odds:
        // (All of the following teams have missed the playoffs:)
		//  Last Place Team ----------	18.5%
		//			2					13.5%
		//			3					11.5%
		//			4					 9.5%
		//			5					 8.5%
		//			6					 7.5%
		//			7					 6.5%
		//			8					 6.0%
		//			9					 5.0%
		//			10					 3.5%
		//			11					 3.0%
		//			12					 2.5%
        //			13					 2.0%
		//			14					 1.5%
		//	Best Team to miss Playoffs - 1.0%


        // For the purpose of getting a random integer, remove the decimal from the
        // above odds to determine a lottery winner ( 18.5% --> 185/1000 )
        if ( randInt1 < 185 )
        	lotteryWinner1 = lottery[0];
        else if ( randInt1 < 320 ) // 185 + 135
        	lotteryWinner1 = lottery[1];
        else if ( randInt1 < 435 ) // 320 + 115
        	lotteryWinner1 = lottery[2];
        else if ( randInt1 < 530 ) // 435 + 95
        	lotteryWinner1 = lottery[3];
        else if ( randInt1 < 615 ) // 530 + 85
        	lotteryWinner1 = lottery[4];
        else if ( randInt1 < 690 ) // 615 + 75
        	lotteryWinner1 = lottery[5];
        else if ( randInt1 < 755 ) // 690 + 65
        	lotteryWinner1 = lottery[6];
        else if ( randInt1 < 815 ) // 755 + 60
        	lotteryWinner1 = lottery[7];
        else if ( randInt1 < 865 ) // 815 + 50
        	lotteryWinner1 = lottery[8];
        else if ( randInt1 < 900 ) // 865 + 35
        	lotteryWinner1 = lottery[9];
        else if ( randInt1 < 930 ) // 900 + 30
        	lotteryWinner1 = lottery[10];
        else if ( randInt1 < 955 ) // 930 + 25
        	lotteryWinner1 = lottery[11];
        else if ( randInt1 < 975 ) // 955 + 20
        	lotteryWinner1 = lottery[12];
        else if ( randInt1 < 990 ) // 975 + 15
        	lotteryWinner1 = lottery[13];
        else if ( randInt1 < 1000 ) // 990 + 10
        	lotteryWinner1 = lottery[14];
        
        // 2nd Overall Pick
        int randInt2 = -1;
        boolean valid2 = false; // determines if 2nd overall pick is valid (not same team as 1st overall pick)
        
        while( !valid2 )
        {
        	// Generate random integer in range 0 to 1000 to determine 2nd Overall Lottery Winner		
	        randInt2 = rand.nextInt(1001); 
	        
	        /////////////////////////////////////////////////////////////////////////////////
	        // all websites displaying odds for 2nd overall pick add to 100.1% not 100% so
	        // the extra number (1000 + 1) is needed
	        /////////////////////////////////////////////////////////////////////////////////
	        
	        // 2nd Overall Lottery Odds:
	        // (All of the following teams have missed the playoffs:)
			//  Last Place Team ----------	16.5%
			//			2					13.0%
			//			3					11.3%
			//			4					 9.6%
			//			5					 8.7%
			//			6					 7.8%
			//			7					 6.8%
			//			8					 6.3%
			//			9					 5.3%
			//			10					 3.8%
			//			11					 3.3%
			//			12					 2.7%
	        //			13					 2.2%
			//			14					 1.7%
			//	Best Team to miss Playoffs - 1.1%
	        // 
	        // If the same team that won the 1st Overall pick is chosen we will
	        // reset the random number until it is valid
	
	
	        if ( randInt2 < 165 )
	        	lotteryWinner2 = lottery[0];
	        else if ( randInt2 < 295 ) // 165 + 130
	        	lotteryWinner2 = lottery[1];
	        else if ( randInt2 < 408 ) // 295 + 113
	        	lotteryWinner2 = lottery[2];
	        else if ( randInt2 < 504 ) // 408 + 96
	        	lotteryWinner2 = lottery[3];
	        else if ( randInt2 < 591 ) // 504 + 87
	        	lotteryWinner2 = lottery[4];
	        else if ( randInt2 < 669 ) // 591 + 78
	        	lotteryWinner2 = lottery[5];
	        else if ( randInt2 < 737 ) // 669 + 68
	        	lotteryWinner2 = lottery[6];
	        else if ( randInt2 < 800 ) // 737 + 63
	        	lotteryWinner2 = lottery[7];
	        else if ( randInt2 < 853 ) // 800 + 53
	        	lotteryWinner2 = lottery[8];
	        else if ( randInt2 < 891 ) // 853 + 38
	        	lotteryWinner2 = lottery[9];
	        else if ( randInt2 < 924 ) // 891 + 33
	        	lotteryWinner2 = lottery[10];
	        else if ( randInt2 < 951 ) // 924 + 27
	        	lotteryWinner2 = lottery[11];
	        else if ( randInt2 < 973 ) // 951 + 22
	        	lotteryWinner2 = lottery[12];
	        else if ( randInt2 < 990 ) // 973 + 17
	        	lotteryWinner2 = lottery[13];
	        else if ( randInt2 < 1001 ) // 990 + 11?
	        	lotteryWinner2 = lottery[14];
	        
	        if ( lotteryWinner2 == lotteryWinner1 )
	        	valid2 = false;
	        else 
	        	valid2 = true;
        }
	
        // 3rd Overall Pick
        int randInt3 = -1;
        boolean valid3 = false; // determines if 2nd overall pick is valid (not same team as 1st overall pick)
        
        while( !valid3 )
        {
        	// Generate random integer in range 0 to 999 to determine 2nd Overall Lottery Winner		
	        randInt3 = rand.nextInt(1000); 
	        
			// 3rd Overall Lottery Odds:
	        // (All of the following teams have missed the playoffs:)
			//  Last Place Team ----------	14.4%
			//			2					12.3%
			//			3					11.1%
			//			4					 9.7%
			//			5					 8.9%
			//			6					 8.0%
			//			7					 7.1%
			//			8					 6.7%
			//			9					 5.7%
			//			10					 4.1%
			//			11					 3.6%
			//			12					 3.0%
	        //			13					 2.4%
			//			14					 1.8%
			//	Best Team to miss Playoffs - 1.2%
	        // 
	        // If the same team that won the 1st or 2nd Overall pick is chosen we will
	        // reset the random number until it is valid
	
	
	        if ( randInt3 < 144 )
	        	lotteryWinner3 = lottery[0];
	        else if ( randInt3 < 267 ) // 144 + 124
	        	lotteryWinner3 = lottery[1];
	        else if ( randInt3 < 378 ) // 267 + 111
	        	lotteryWinner3 = lottery[2];
	        else if ( randInt3 < 475 ) // 378 + 97
	        	lotteryWinner3 = lottery[3];
	        else if ( randInt3 < 564 ) // 475 + 89
	        	lotteryWinner3 = lottery[4];
	        else if ( randInt3 < 644 ) // 564 + 80
	        	lotteryWinner3 = lottery[5];
	        else if ( randInt3 < 715 ) // 644 + 71
	        	lotteryWinner3 = lottery[6];
	        else if ( randInt3 < 782 ) // 715 + 67
	        	lotteryWinner3 = lottery[7];
	        else if ( randInt3 < 839 ) // 782 + 57
	        	lotteryWinner3 = lottery[8];
	        else if ( randInt3 < 880 ) // 839 + 41
	        	lotteryWinner3 = lottery[9];
	        else if ( randInt3 < 916 ) // 880 + 36
	        	lotteryWinner3 = lottery[10];
	        else if ( randInt3 < 946 ) // 916 + 30
	        	lotteryWinner3 = lottery[11];
	        else if ( randInt3 < 970 ) // 946 + 24
	        	lotteryWinner3 = lottery[12];
	        else if ( randInt3 < 988 ) // 970 + 18
	        	lotteryWinner3 = lottery[13];
	        else if ( randInt3 < 1000 ) // 988 + 12
	        	lotteryWinner3 = lottery[14];
	        
	        if ( lotteryWinner3 == lotteryWinner1 || lotteryWinner3 == lotteryWinner2 )
	        	valid3 = false;
	        else 
	        	valid3 = true;
        }   
                
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        // Getting indexes for teams that lost in Conference Final, lost in Stanley Cup Final, and Stanley Cup Champion
        //		(these will be needed later to determine draft order of picks 28-31)
        
        int westFinalLoser = -1;
        int eastFinalLoser = -1;
        int cupFinalLoser = -1;
        int cupChampion = -1;
        int cenWinner = -1;
        int pacWinner = -1;
        int atlWinner = -1;
        int metWinner = -1;
                
        // Series 12 (West Final)
		
 		if ( playoffArray[12].getHigherGamesWon() == 4 )
 			westFinalLoser = playoffArray[12].getLowerSeed();
 		else if ( playoffArray[12].getLowerGamesWon() == 4 )
 			westFinalLoser = playoffArray[12].getHigherSeed();
 		
 		// Series 13 (East Final)
 		
 		if ( playoffArray[13].getHigherGamesWon() == 4 )
 			eastFinalLoser = playoffArray[13].getLowerSeed();
 		else if ( playoffArray[13].getLowerGamesWon() == 4 )
 			eastFinalLoser = playoffArray[13].getHigherSeed();
 		
 		// Series 14 (Stanley Cup Final)
 		
 		if ( playoffArray[14].getHigherGamesWon() == 4 )
 		{
 			cupFinalLoser = playoffArray[14].getLowerSeed();
 			cupChampion = playoffArray[14].getHigherSeed();
 		}	
 		else if ( playoffArray[14].getLowerGamesWon() == 4 )
 		{
 			cupFinalLoser = playoffArray[14].getHigherSeed();
 			cupChampion = playoffArray[14].getLowerSeed();
 		}	
     		
 		for (int i = 0; i < numNHLTeams; i++)
 		{
 			if ( teamArray[standings[i]].getDivision() == 'C' && cenWinner == -1 )
 				cenWinner = standings[i];
 			else if ( teamArray[standings[i]].getDivision() == 'P' && pacWinner == -1 )
 				pacWinner = standings[i];
 			else if ( teamArray[standings[i]].getDivision() == 'A' && atlWinner == -1 )
 				atlWinner = standings[i];
 			else if ( teamArray[standings[i]].getDivision() == 'M' && metWinner == -1 )
 				metWinner = standings[i];
 			
 			if (cenWinner != -1 && pacWinner != -1 && atlWinner != -1 && metWinner != -1)
 				break;
 		}
     	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        // Print Draft Order (Round 1)
        System.out.println("Draft Order (Round 1):");
        
        int draftPosition = 1;
        
        System.out.println("\t" + draftPosition + ".  " + teamArray[lotteryWinner1].getTeamAbr());
        draftPosition++;
        System.out.println("\t" + draftPosition + ".  " + teamArray[lotteryWinner2].getTeamAbr());
        draftPosition++;
        System.out.println("\t" + draftPosition + ".  " + teamArray[lotteryWinner3].getTeamAbr());
        draftPosition++;
        
        
        // Print teams that didn't win lottery (picks 4-15)
        for (int i = 0; i < lottery.length; i++)
        {
        	// Print team only if they did not win lottery (prevent printing twice)
        	if ( lottery[i] != lotteryWinner1 && lottery[i] != lotteryWinner2 && lottery[i] != lotteryWinner3 )
        	{
        		// Spacing changes
        		if (draftPosition <= 9)
        			System.out.println("\t" + draftPosition + ".  " + teamArray[lottery[i]].getTeamAbr());
        		else
        			System.out.println("\t" + draftPosition + ". " + teamArray[lottery[i]].getTeamAbr());
        			
	        	draftPosition++;
        	}
        }
        
        boolean teamPrinted = false; // if team has been printed in the lottery section
        boolean divWinner = false; // if team has won their division
        boolean finalsTeam = false; // if team has advanced to conference final (gets a later pick)
        
        
        // Print picks 16 to 23-27 (Not printing divWinners or finalsTeams)
        for (int i = (numNHLTeams - 1); i >= 0; i--)
        {
        	for (int j = 0; j < lottery.length; j++)
        	{
        		if (lottery[j] == standings[i] )
        		{
        			teamPrinted = true;
        			break;
        		}
        		else
        			teamPrinted = false;
        	}
        	
        	if ( 	standings[i] == westFinalLoser ||
        			standings[i] == eastFinalLoser ||
        			standings[i] == cupFinalLoser  ||
        			standings[i] == cupChampion )
        	{
        		finalsTeam = true;
        	}
        	else
        		finalsTeam = false;
        	
        	if ( 	standings[i] == cenWinner ||
        			standings[i] == pacWinner ||
        			standings[i] == atlWinner  ||
        			standings[i] == metWinner )
        	{
        		divWinner = true;
        	}
        	else
        		divWinner = false;
        	
        	// Print team only if they did not win lottery, make conference final, or win their division (prevent printing twice)
        	if ( !teamPrinted && !finalsTeam && !divWinner )
        	{
	        	System.out.println("\t" + draftPosition + ". " + teamArray[standings[i]].getTeamAbr());
	        	draftPosition++;
        	}
        }
        
        // Print picks 24-27 (these picks are occupied by division winners -- unless they advanced to the conference finals)
        for (int i = (numNHLTeams - 1); i >= 0; i--)
        {        	
        	if ( 	standings[i] == westFinalLoser ||
        			standings[i] == eastFinalLoser ||
        			standings[i] == cupFinalLoser  ||
        			standings[i] == cupChampion )
        	{
        		finalsTeam = true;
        	}
        	else
        		finalsTeam = false;
        	
        	if ( 	standings[i] == cenWinner ||
        			standings[i] == pacWinner ||
        			standings[i] == atlWinner ||
        			standings[i] == metWinner )
        	{
        		divWinner = true;
        	}
        	else
        		divWinner = false;
        	
        	// Print team if they did make conference final but won their division
        	if ( !finalsTeam && divWinner )
        	{
	        	System.out.println("\t" + draftPosition + ". " + teamArray[standings[i]].getTeamAbr());
	        	draftPosition++;
        	}
        }
        
        // Print picks 28-31 (these picks are determined by playoff outcome)
        // Conference final losers get picks 28,29 (based on standings who goes first)
        // Stanley Cup final loser get pick 30
        // Stanley Cup Champion gets pick 31

        
        // Determining which conference final loser gets to pick first
        // (lower team in standings picks first)
        // The team that appears in the standings array first is the higher seed and gets the lower (later) pick
        for (int i = 0; i < standings.length; i++)
        {
        	if ( eastFinalLoser == standings[i] )
        	{
        		System.out.println("\t" + draftPosition + ". " + teamArray[westFinalLoser].getTeamAbr());
                draftPosition++;
                System.out.println("\t" + draftPosition + ". " + teamArray[eastFinalLoser].getTeamAbr());
                draftPosition++;
        		break;
        	}
        	
        	if ( westFinalLoser == standings[i] )
        	{
        		System.out.println("\t" + draftPosition + ". " + teamArray[eastFinalLoser].getTeamAbr());
                draftPosition++;
                System.out.println("\t" + draftPosition + ". " + teamArray[westFinalLoser].getTeamAbr());
                draftPosition++;
        		break;
        	}
        		
        }

        System.out.println("\t" + draftPosition + ". " + teamArray[cupFinalLoser].getTeamAbr());
        draftPosition++;
        System.out.println("\t" + draftPosition + ". " + teamArray[cupChampion].getTeamAbr() + "\n");
        
        // Print draft order for later rounds (rounds 2-7 order not affected by lottery, but are affected by teams that make conference final,
        //																							so only TOP 3 teams change based on standings)
        System.out.println("Draft Order (Rounds 2-7):");
        draftPosition = 1;
        
        // Print teams that missed the playoffs
        for (int i = 0; i < lottery.length; i++)
        {
        	// Spacing changes
    		if (draftPosition <= 9)
    			System.out.println("\t" + draftPosition + ".  " + teamArray[lottery[i]].getTeamAbr());
    		else
    			System.out.println("\t" + draftPosition + ". " + teamArray[lottery[i]].getTeamAbr());
    				
    		draftPosition++;
        }
                
        // Print picks 16 to 24-27
        for (int i = (numNHLTeams - 1); i >= 0; i--)
        {
        	for (int j = 0; j < lottery.length; j++)
        	{
        		if (lottery[j] == standings[i] )
        		{
        			teamPrinted = true;
        			break;
        		}
        		else
        			teamPrinted = false;
        	}
        	
        	if ( 	standings[i] == westFinalLoser ||
        			standings[i] == eastFinalLoser ||
        			standings[i] == cupFinalLoser  ||
        			standings[i] == cupChampion )
        	{
        		finalsTeam = true;
        	}
        	else
        		finalsTeam = false;
        	
        	if ( 	standings[i] == cenWinner ||
        			standings[i] == pacWinner ||
        			standings[i] == atlWinner  ||
        			standings[i] == metWinner )
        	{
        		divWinner = true;
        	}
        	else
        		divWinner = false;
        	
        	// Print team only if they did not win lottery, make conference final, or win their division (prevent printing twice)
        	if ( !teamPrinted && !finalsTeam && !divWinner )
        	{
	        	System.out.println("\t" + draftPosition + ". " + teamArray[standings[i]].getTeamAbr());
	        	draftPosition++;
        	}
        }
        
        // Print picks 24-27
        for (int i = (numNHLTeams - 1); i >= 0; i--)
        {        	
        	if ( 	standings[i] == westFinalLoser ||
        			standings[i] == eastFinalLoser ||
        			standings[i] == cupFinalLoser  ||
        			standings[i] == cupChampion )
        	{
        		finalsTeam = true;
        	}
        	else
        		finalsTeam = false;
        	
        	if ( 	standings[i] == cenWinner ||
        			standings[i] == pacWinner ||
        			standings[i] == atlWinner ||
        			standings[i] == metWinner )
        	{
        		divWinner = true;
        	}
        	else
        		divWinner = false;
        	
        	// Print team if they did make conference final but won their division
        	if ( !finalsTeam && divWinner )
        	{
	        	System.out.println("\t" + draftPosition + ". " + teamArray[standings[i]].getTeamAbr());
	        	draftPosition++;
        	}
        }
        
        // Print picks 28-31 (these picks are determined by playoff outcome)
        
        // Determining which conference final loser gets to pick first
        // (lower team in standings picks first)
        // The team that appears in the standings array first is the higher seed and gets the lower (later) pick
        for (int i = 0; i < standings.length; i++)
        {
        	if ( eastFinalLoser == standings[i] )
        	{
        		System.out.println("\t" + draftPosition + ". " + teamArray[westFinalLoser].getTeamAbr());
                draftPosition++;
                System.out.println("\t" + draftPosition + ". " + teamArray[eastFinalLoser].getTeamAbr());
                draftPosition++;
        		break;
        	}
        	
        	if ( westFinalLoser == standings[i] )
        	{
        		System.out.println("\t" + draftPosition + ". " + teamArray[eastFinalLoser].getTeamAbr());
                draftPosition++;
                System.out.println("\t" + draftPosition + ". " + teamArray[westFinalLoser].getTeamAbr());
                draftPosition++;
        		break;
        	}
        		
        }

        System.out.println("\t" + draftPosition + ". " + teamArray[cupFinalLoser].getTeamAbr());
        draftPosition++;
        System.out.println("\t" + draftPosition + ". " + teamArray[cupChampion].getTeamAbr() + "\n");
        
        
        bufferLotteryMenu();
	}
	
	void viewDraftOdds() throws IOException
	{
		// Will need to update draft odds if odds change or when Seattle enters league
		// (as 16 teams will now enter lottery instead of only 15 teams)
		
		
		String[] odds = new String[numNHLTeams - 16];
		boolean madePlayoffs = false;
		System.out.println();
		
		System.out.println("Lottery Odds by Team:\n\n"
				+ "\tTeam\t1st Pick  2nd Pick  3rd Pick\n\t______________________________________");
		
		
		// Draft Lottery Odds ( 2019-2020 Season )
		odds[0] = "\t18.5% \t  16.5%\t    14.4%";
		odds[1] = "\t13.5% \t  13.0%\t    12.3%";
		odds[2] = "\t11.5% \t  11.3%\t    11.1%";
		odds[3] = "\t9.5% \t  9.6% \t    9.7%";
		odds[4] = "\t8.5% \t  8.7% \t    8.9%";
		odds[5] = "\t7.5% \t  7.8% \t    8.0%";
		odds[6] = "\t6.5% \t  6.8% \t    7.1%";
		odds[7] = "\t6.0% \t  6.3% \t    6.7%";
		odds[8] = "\t5.0% \t  5.3% \t    5.7%";
		odds[9] = "\t3.5% \t  3.8% \t    4.1%";
		odds[10] = "\t3.0% \t  3.3% \t    3.6%";
		odds[11] = "\t2.5% \t  2.7% \t    3.0%";
		odds[12] = "\t2.0% \t  2.2% \t    2.4%";
		odds[13] = "\t1.5% \t  1.7% \t    1.8%";
		odds[14] = "\t1.0% \t  1.1% \t    1.2%";
		
		int k = 0;
		
		// Display each teams odds
		for(int i = (numNHLTeams - 1); i >= 0; i--)
		{
			// Determine if team made the playoffs (if yes, they have 0% lottery odds)
			for(int j = 0; j < 8; j++)
			{
				if( playoffArray[j].getHigherSeed() == standings[i] || playoffArray[j].getLowerSeed() == standings[i] )
				{
					madePlayoffs = true;
					break;
				}
				else
					madePlayoffs = false;
			}
			
			if (!madePlayoffs)
			{
				System.out.println("\t" + teamArray[standings[i]].getTeamAbr() + odds[k]);
				k++;
			}
		}
		
		System.out.println();
		bufferLotteryMenu();
	}
	
	void viewDraftProcess() throws IOException
	{
		System.out.println("\nDetermining the Draft Order:\n\n");
		System.out.println("\t1.  All teams missing the playoffs are in the Lottery\n" + 
				"\t2.  Teams with the least points get more chances at winning the Lottery\n" + 
				"\t3.  The 1st overall pick is awarded by a drawing of ping pong balls (this program will not use ping pong balls, and will use a RNG)\n" + 
				"\t4.  The 2nd overall pick is awarded by a drawing of ping pong balls (RNG)\n" + 
				"\t5.  The 3rd overall pick is awarded by a drawing of ping pong balls (RNG)\n" + 
				"\t6.  Remaining lottery teams, sorted by points, fill out picks 4-15\n" + 
				"\t7.  Playoff teams that did not win their divisions and did not make the conference finals, sorted by points, are assigned the next picks\n" + 
				"\t8.  Playoff teams that won their divisions and did not make the conference finals, sorted by points, are assigned the next picks\n" + 
				"\t9.  Conference finals losers sorted by points are assigned picks 28 and 29\n" + 
				"\t10. Stanley Cup runner-up is assigned pick 30\n" + 
				"\t11. Stanley Cup champion is assigned pick 31\n" + 
				"\t12. The Draft Order of Rounds 2-7 is not impacted by the lottery results, thus the last team in the standings will have the first pick" + 
				"  \n\t\tin Rounds 2-7 even if that team did not win the lottery. However, the teams that made the conference final will remain at the end" +
				"  \n\t\tof the round and select within the final 4 picks of Rounds 2-7.\n");
		
		bufferLotteryMenu();
	}
	
} // end of Simulation class
