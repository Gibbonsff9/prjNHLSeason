package pkgNHLSeason;

public class PlayoffSeries 
{
	private int lowerSeed;
	private int higherSeed;
	private int lowerGamesWon;
	private int higherGamesWon;
	private PlayoffGame game1;
	private PlayoffGame game2;
	private PlayoffGame game3;
	private PlayoffGame game4;
	private PlayoffGame game5;
	private PlayoffGame game6;
	private PlayoffGame game7;
	private boolean over;
	
	public PlayoffSeries()
	{
		lowerSeed = -1;
		higherSeed = -1;
		lowerGamesWon = 0;
		higherGamesWon = 0;
		game1 = new PlayoffGame(0,0,0);
		game2 = new PlayoffGame(0,0,0);
		game3 = new PlayoffGame(0,0,0);
		game4 = new PlayoffGame(0,0,0);
		game5 = new PlayoffGame(0,0,0);
		game6 = new PlayoffGame(0,0,0);
		game7 = new PlayoffGame(0,0,0);
		over = false;
	}
	
	public PlayoffSeries( int lower, int higher, int lowerG, int higherG ,int g1h, int g1a, int g1o, int g2h, int g2a, int g2o, int g3h, int g3a, int g3o, int g4h, int g4a, 
			int g4o, int g5h, int g5a, int g5o, int g6h, int g6a, int g6o, int g7h, int g7a, int g7o, boolean ovr)
	{
		lowerSeed = lower;
		higherSeed = higher;
		lowerGamesWon = lowerG;
		higherGamesWon = higherG;
		
		game1 = new PlayoffGame(g1h,g1a,g1o);
		game2 = new PlayoffGame(g2h,g2a,g2o);
		game3 = new PlayoffGame(g3h,g3a,g3o);
		game4 = new PlayoffGame(g4h,g4a,g4o);
		game5 = new PlayoffGame(g5h,g5a,g5o);
		game6 = new PlayoffGame(g6h,g6a,g6o);
		game7 = new PlayoffGame(g7h,g7a,g7o);
		over = ovr;
	}
	
	public int getHigherSeed() {
		return higherSeed;
	}
	public void setHigherSeed(int higherSeed) {
		this.higherSeed = higherSeed;
	}
	public int getLowerSeed() {
		return lowerSeed;
	}
	public void setLowerSeed(int lowerSeed) {
		this.lowerSeed = lowerSeed;
	}
	public PlayoffGame getGame1() {
		return game1;
	}
	public void setGame1(PlayoffGame game1) {
		this.game1 = game1;
	}
	public PlayoffGame getGame2() {
		return game2;
	}
	public void setGame2(PlayoffGame game2) {
		this.game2 = game2;
	}
	public PlayoffGame getGame3() {
		return game3;
	}
	public void setGame3(PlayoffGame game3) {
		this.game3 = game3;
	}
	public PlayoffGame getGame4() {
		return game4;
	}
	public void setGame4(PlayoffGame game4) {
		this.game4 = game4;
	}
	public PlayoffGame getGame5() {
		return game5;
	}
	public void setGame5(PlayoffGame game5) {
		this.game5 = game5;
	}
	public PlayoffGame getGame6() {
		return game6;
	}
	public void setGame6(PlayoffGame game6) {
		this.game6 = game6;
	}
	public PlayoffGame getGame7() {
		return game7;
	}
	public void setGame7(PlayoffGame game7) {
		this.game7 = game7;
	}

	public boolean isOver() {
		return over;
	}

	public void setOver(boolean over) {
		this.over = over;
	}

	public int getLowerGamesWon() {
		return lowerGamesWon;
	}

	public void setLowerGamesWon(int lowerGamesWon) {
		this.lowerGamesWon = lowerGamesWon;
	}

	public int getHigherGamesWon() {
		return higherGamesWon;
	}

	public void setHigherGamesWon(int higherGamesWon) {
		this.higherGamesWon = higherGamesWon;
	}
	
	
	
	
}
