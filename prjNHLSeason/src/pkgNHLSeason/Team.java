package pkgNHLSeason;

public class Team 
{
	// Variables that WILL NOT change between seasons
	private String cityName;
	private String teamName;
	private String teamAbr; // Team Abbreviation
	private char division; // Pacific, Central, Metro, Atlantic
	
	// Variables that WILL change between seasons
	private int gp;		// Games Played
	private int w;		// Wins
	private int l;		// Losses
	private int otl;	// Overtime losses
	private int pts;	// Points
	private int rw;		// Regulation Wins
	private int row;	// Regulation and Overtime Wins
	private int gf;		// Goals For (Scored)
	private int ga;		// Goals Against (Allowed)
	
	// Deafault Constructor
	public Team()
	{
		cityName = "";
		teamName = "";
		teamAbr = "";
		division = 'x';
		
		gp = 0;
		w = 0;
		l = 0;
		otl = 0;
		pts= 0;
		rw = 0;
		row = 0;
		gf = 0;
		ga = 0;
	}

	// Constructor
	public Team(String cityName, String teamName, String teamAbr, char division, int gp, int w, int l, int otl, int pts,
			int rw, int row, int gf, int ga) 
	{
		super();
		this.cityName = cityName;
		this.teamName = teamName;
		this.teamAbr = teamAbr;
		this.division = division;
		
		this.gp = gp;
		this.w = w;
		this.l = l;
		this.otl = otl;
		this.pts = pts;
		this.rw = rw;
		this.row = row;
		this.gf = gf;
		this.ga = ga;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamAbr() {
		return teamAbr;
	}

	public void setTeamAbr(String teamAbr) {
		this.teamAbr = teamAbr;
	}

	public int getGp() {
		return gp;
	}

	public void setGp(int gp) {
		this.gp = gp;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public int getOtl() {
		return otl;
	}

	public void setOtl(int otl) {
		this.otl = otl;
	}

	public int getPts() {
		return pts;
	}

	public void setPts(int pts) {
		this.pts = pts;
	}

	public int getRw() {
		return rw;
	}

	public void setRw(int rw) {
		this.rw = rw;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getGf() {
		return gf;
	}

	public void setGf(int gf) {
		this.gf = gf;
	}

	public int getGa() {
		return ga;
	}

	public void setGa(int ga) {
		this.ga = ga;
	}

	public char getDivision() {
		return division;
	}

	public void setDivision(char division) {
		this.division = division;
	}
	
	public void printRecord()
	{
		System.out.printf("%-13s %-15s", cityName, teamName);
		System.out.print(" (" + teamAbr + ") \t" + gp + "\t" + w + "\t" + l + "\t" + otl + "\t" + pts + "\t" + rw + "\t" + row + "\t" + gf + "\t" + ga + "\t");
		
		if ( (gf-ga) > 0 )
			System.out.println( "+" + (gf-ga));
		else if ( (gf-ga) == 0) // For spacing
			System.out.println( " " + (gf-ga));
		else
			System.out.println((gf-ga));
	}
	
}
