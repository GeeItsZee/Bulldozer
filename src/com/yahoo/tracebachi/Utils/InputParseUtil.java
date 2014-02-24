package com.yahoo.tracebachi.Utils;

public class InputParseUtil
{
	public static int parseSafeInt( String toParse, int minVal, int maxVal,
		int defaultVal )
	{
		// Method variables
		int result = defaultVal;
		
		// Try to parse
		try
		{
			// Ensure non null string
			if( toParse != null )
			{
				// Parse
				result = Integer.parseInt( toParse );
				
				// Check if in bounds
				if( result < minVal )
				{
					return minVal;
				}
				else if( result > maxVal )
				{
					return maxVal;
				}
				
				// Return result
				return result;
			}
		}
		catch( NumberFormatException excep ){}
		
		// Return default
		return defaultVal;
	}
	
	public static int[] parseSafeIntPair( String toParse, String splitAround, 
		int firstMin, int firstMax, int firstDefault, 
		int secondMin, int secondMax, int secondDefault )
	{
		// Method variables
		int[] results = new int[2];
		String[] splitResult = toParse.split( splitAround );
		
		// Verify there are at least 2 splits
		if( splitResult.length > 1 )
		{
			// Parse
			results[0] = parseSafeInt( splitResult[0], 
				firstMin, firstMax, firstDefault );
			
			// Parse
			results[1] = parseSafeInt( splitResult[1],
				secondMin, secondMax, secondDefault );
		}
		else if( splitResult.length == 1 )
		{
			// Parse only the first
			results[0] = parseSafeInt( splitResult[0], 
				firstMin, firstMax, firstDefault );
		}
		
		// Return
		return results;
	}
}
