/*
 * SpringMapEdit -- A 3D map editor for the Spring engine
 *
 * Copyright (C) 2008-2009  Heiko Schmitt <heikos23@web.de>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
/**
 * HeightBrush.java 
 * Created on 10.10.2008
 * by Heiko Schmitt
 */
package frontend.render.brushes;

import backend.SpringMapEdit;
import backend.map.Heightmap;
import backend.math.Vector2Int;

/**
 * @author Heiko Schmitt
 */
public class HeightBrush extends Brush
{
	public enum HeightMode
	{
		Raise,
		Smooth,
		Set,
		Erode,
		Randomize,
		CopyPaste
	}
	
	public BrushPattern[] pattern = new BrushPattern[1];
	
	//Current Brush mode
	public int brushMode;
	
	//Brush settings
	public float[] strength;
	
	//Brush in repeat mode, or stamp mode?
	public boolean[] moduloMode;
	
	public int[] width;
	public int[] height;
	
	public HeightBrush(BrushDataManager<BrushPattern> brushPatternManager, int patternID, int newWidth, int newHeight, SpringMapEdit smeX)
	{
		this.brushPatternManager = brushPatternManager;
		
		brushMode = HeightMode.Raise.ordinal();
		int count = HeightMode.values().length;

		strength = new float[count];
		strength[HeightMode.Raise.ordinal()] = 0.001f;
		strength[HeightMode.Smooth.ordinal()] = 0.1f;
		strength[HeightMode.Set.ordinal()] = 10f;
		strength[HeightMode.Erode.ordinal()] = 100f;
		strength[HeightMode.Randomize.ordinal()] = 0.003f;
		strength[HeightMode.CopyPaste.ordinal()] = 1f;

		moduloMode = new boolean[count];
		moduloMode[HeightMode.Raise.ordinal()] = false;
		moduloMode[HeightMode.Smooth.ordinal()] = false;
		moduloMode[HeightMode.Set.ordinal()] = false;
		moduloMode[HeightMode.Erode.ordinal()] = false;
		moduloMode[HeightMode.Randomize.ordinal()] = false;
		moduloMode[HeightMode.CopyPaste.ordinal()] = false;  
		
		//Set up pattern and texture
		pattern = new BrushPattern[count];
		pattern[HeightMode.Raise.ordinal()] = brushPatternManager.getScaledBrushData(patternID, newWidth, newHeight, true);
		pattern[HeightMode.Smooth.ordinal()] = brushPatternManager.getScaledBrushData(patternID, newWidth, newHeight, true);
		pattern[HeightMode.Set.ordinal()] = brushPatternManager.getScaledBrushData(patternID, newWidth, newHeight, true);
		pattern[HeightMode.Erode.ordinal()] = brushPatternManager.getScaledBrushData(patternID, newWidth, newHeight, true);
		pattern[HeightMode.Randomize.ordinal()] = brushPatternManager.getScaledBrushData(patternID, newWidth, newHeight, true);
		pattern[HeightMode.CopyPaste.ordinal()] = brushPatternManager.getScaledBrushData(patternID, newWidth, newHeight, true);  
		
		width = new int[count];
		width[HeightMode.Raise.ordinal()] = newWidth;
		width[HeightMode.Smooth.ordinal()] = newWidth;
		width[HeightMode.Set.ordinal()] = newWidth;
		width[HeightMode.Erode.ordinal()] = newWidth;
		width[HeightMode.Randomize.ordinal()] = newWidth;
		width[HeightMode.CopyPaste.ordinal()] = newWidth;
		
		height = new int[count];
		height[HeightMode.Raise.ordinal()] = newHeight;
		height[HeightMode.Smooth.ordinal()] = newHeight;
		height[HeightMode.Set.ordinal()] = newHeight;
		height[HeightMode.Erode.ordinal()] = newHeight;
		height[HeightMode.Randomize.ordinal()] = newHeight;
		height[HeightMode.CopyPaste.ordinal()] = newHeight;
		
		//Set size
		if (pattern[brushMode] != null)
		{
			width[brushMode] = pattern[brushMode].width;
			height[brushMode] = pattern[brushMode].height;
		}
		else
		{
			width[brushMode] = newWidth;
			height[brushMode] = newHeight;
		}
		sme = smeX;
	}
	
	public int getWidth()
	{
		return pattern[brushMode].width;
	}
	
	public int getHeight()
	{
		return pattern[brushMode].height;
	}

	public void setSize(int width, int height)
	{
		this.width[brushMode] = width;
		this.height[brushMode] = height;
		setPattern(pattern[brushMode].patternID);
	}
	
	public int getStrengthInt()
	{
		int result = 0;
		switch (brushMode)
		{
		case 0: result = (int)strength[brushMode] * 10000 + 1; break;
		case 1: result = (int)strength[brushMode] * 10000 + 1; break;
		case 2: result = (int)strength[brushMode] * 6; break;
		case 3: result = (int)strength[brushMode]; break;
		case 4: result = (int)strength[brushMode] * 10000 + 1; break;
		case 5: result = (int)strength[brushMode]; break;
		}
		return result;
	}
	
	public void setStrengthInt(int newStrength)
	{
		switch (brushMode)
		{
		case 0:
			this.strength[brushMode] = (newStrength + 1) / 10000f; break;
		case 1:
			this.strength[brushMode] = (newStrength + 1) / 10000f; break;
		case 2:
			this.strength[brushMode] = newStrength / 6f; break;
		case 3:
			this.strength[brushMode] = newStrength; break;
		case 4:
			this.strength[brushMode] = (newStrength + 1) / 10000f; break;
		case 5:
			this.strength[brushMode] = newStrength; break;
		}
	}
	
	public float getStrength()
	{
		return strength[brushMode];
	}
	
	public boolean getModuloMode()
	{	
		return moduloMode[brushMode];
	}

	public boolean isVertexOriented()
	{
		return true;
	}
	
	public BrushPattern getPattern()
	{
		return this.pattern[brushMode];
	}
	
	public void setPattern(int patternID)
	{
		pattern[brushMode] = brushPatternManager.getScaledBrushData(patternID, width[brushMode], height[brushMode], true);
		width[brushMode] = pattern[brushMode].width;
		height[brushMode] = pattern[brushMode].height;
	}
	
	public void mirror(boolean horizontal)
	{
		pattern[brushMode].mirror(horizontal);
	}
	
	public void rotate(boolean counterClockWise)
	{
		pattern[brushMode].rotate(counterClockWise);
		width[brushMode] = pattern[brushMode].width;
		height[brushMode] = pattern[brushMode].height;
	}
	
	public void applyBrush(Vector2Int position, boolean invert)
	{
		Heightmap heightMap = sme.map.heightmap;
		switch (brushMode)
		{
		case 0: heightMap.modifyHeight(position.x(), position.y(), this, invert); break;
		case 1: heightMap.smoothHeight(position.x(), position.y(), this, getStrength()); break;
		case 2: heightMap.setHeight(position.x(), position.y(), this, getStrength() / sme.map.maxHeight, invert); break;
		case 3: heightMap.erodeMapWet(position.x(), position.y(), getWidth(), getHeight(), sme.mes.getErosionSetup()); break;
		case 4: heightMap.randomizeHeight(position.x(), position.y(), this, getStrength()); break;
		case 5: // This is disabled in the editor dialog
		{
			if (invert)
				heightMap.paste(position.x(), position.y(), this);
			else
				heightMap.copy(position.x(), position.y(), getWidth(), getHeight());
			break;
		}
		default:;
		}
	}
}