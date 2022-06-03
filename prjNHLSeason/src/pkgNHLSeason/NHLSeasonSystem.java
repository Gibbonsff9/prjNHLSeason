package pkgNHLSeason;

import java.io.*;

public class NHLSeasonSystem {

	public static void main(String[] args) throws IOException 
	{
		NHLSeason nhl2020 = new NHLSeason();
		
		try
		{
			nhl2020.mainMenu();
		}
		catch (FileNotFoundException e)
		{	
			System.out.println("Error. File Not Found");
			System.exit(100);
		}
		catch (IOException e)
		{	
			System.out.println("Error Loading data");
			System.exit(101);
		}
		catch (NumberFormatException e)
		{	
			System.out.println("Error. Invalid number format");
			System.exit(102);
		}
		finally
		{
			System.out.println("\nProgram Exited.");
		}

	}

}
