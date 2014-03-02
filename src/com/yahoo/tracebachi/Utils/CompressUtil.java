package com.yahoo.tracebachi.Utils;

public class CompressUtil
{	
	public static int getSignedIntFrom( long data, int numBitsToLoad )
	{
		// Method variables
		int result = 0;
		int mask = (1 << numBitsToLoad) - 1;
		
		// Get the bits
		result = (int) (data & mask);
		
		// Shift them to the edge
		result <<= (32 - numBitsToLoad);
		
		// Sign extend shift them back
		result >>= (32 - numBitsToLoad);
		
		return result;
	}
	
	public static short getSignedShortFrom( long data, int numBitsToLoad )
	{
		// Method variables
		short result = 0;
		int mask = (1 << numBitsToLoad) - 1;
		
		// Get the bits
		result = (short) (data & mask);
		
		// Shift them to the edge
		result <<= (16 - numBitsToLoad);
		
		// Sign extend shift them back
		result >>= (16 - numBitsToLoad);
		
		return result;
	}
	
	public static byte getSignedByteFrom( long data, int numBitsToLoad )
	{
		// Method variables
		byte result = 0;
		int mask = (1 << numBitsToLoad) - 1;
		
		// Get the bits
		result = (byte) (data & mask);
		
		// Shift them to the edge
		result <<= (8 - numBitsToLoad);
		
		// Sign extend shift them back
		result >>= (8 - numBitsToLoad);
		
		return result;
	}
	
	public static long trimBits( long data, int numBitsToStore )
	{
		// Method variables
		long mask = (1 << numBitsToStore) - 1;
		
		// Return the shifted integer
		return (data & mask);
	}
}
