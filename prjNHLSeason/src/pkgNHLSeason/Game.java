package pkgNHLSeason;

public class Game 
{	
	private String date;
	private String homeTeam;
	private String awayTeam;
	private int homeScore;
	private int awayScore;
	private boolean ot; // if game went to Overtime
	private boolean so; // if game went to a Shootout
	
	// Default Constructor
	public Game()
	{
		date = "";
		homeTeam = "";
		awayTeam = "";
		homeScore = 0;
		awayScore = 0;
		ot = false;
		so = false;
	}
	
	// Constructor
	public Game(String date, String homeTeam, String awayTeam, int homeScore, int awayScore, boolean ot, boolean so) 
	{
		super();
		this.date = date;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.homeScore = homeScore;
		this.awayScore = awayScore;
		this.ot = ot;
		this.so = so;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
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

	public boolean isOt() {
		return ot;
	}

	public void setOt(boolean ot) {
		this.ot = ot;
	}

	public boolean isSo() {
		return so;
	}

	public void setSo(boolean so) {
		this.so = so;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public void printGame()
	{
		System.out.print( "   " + awayTeam + "      " + awayScore + " - " + homeScore + "       " + homeTeam + "\t  " );
		
		if (so)
			System.out.print("Final (SO)   " + date + "\n");
		else if (ot && !so)
			System.out.print("Final (OT)   " + date + "\n");
		else
			System.out.print("Final        " + date + "\n");
	}
	
	
	
	
}

