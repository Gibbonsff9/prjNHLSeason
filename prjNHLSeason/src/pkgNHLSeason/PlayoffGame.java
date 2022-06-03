package pkgNHLSeason;

public class PlayoffGame 
{
	private int homeScore;
	private int awayScore;
	private int numOt; // amount of overtimes in the game (playoffs have no shootout and can have multiple overtime periods)
						// if game did not go to overtime, this variable is ZERO
	
	// Default Constructor
	public PlayoffGame()
	{
		homeScore = 0;
		awayScore = 0;
		numOt = 0;
	}
	
	// Constructor
	public PlayoffGame(int homeScore, int awayScore, int numOt) 
	{
		super();
		this.homeScore = homeScore;
		this.awayScore = awayScore;
		this.numOt = numOt;
	}

	public int getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}

	public int getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(int awayScore) {
		this.awayScore = awayScore;
	}
	
	public int getNumOt() {
		return numOt;
	}

	public void setNumOt(int numOt) {
		this.numOt = numOt;
	}
	
	
}
