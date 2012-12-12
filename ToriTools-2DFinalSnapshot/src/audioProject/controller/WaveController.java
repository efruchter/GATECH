package audioProject.controller;

import static java.lang.Math.min;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import toritools.additionaltypes.HistoryQueue;

/**
 * Wave analysis engine. Maps are of size milliseconds.
 * @author toriscope
 *
 */
public class WaveController {

	private boolean beat = false;
	private float feel = 0.0f;
	
	final List<Float> feelArray = new ArrayList<Float>(), beatArray = new ArrayList<Float>();
	
	final HistoryQueue<Float> history;
	
	private final float averageFeel;
	
	int bossTime, victoryTime;
	
	long lastTime = 0;
	
	//private float beatScalar = 0;
	
	public WaveController(final String songName, final int historyLength) throws FileNotFoundException {
		Scanner feelScan = new Scanner(new File("audioProject/" + songName + "_feels.kres"));
		while(feelScan.hasNextFloat()) {
			float entry = feelScan.nextFloat(); 
			feelArray.add(entry);
		}		
		Scanner beatScan = new Scanner(new File("audioProject/" + songName + "_beats.kres"));
		while(beatScan.hasNextFloat()) {
			beatArray.add(beatScan.nextFloat());
		}
		history = new HistoryQueue<Float>(historyLength + 1);
		for(int i = 0; i < historyLength; i++) {
			history.push(0f);
		}
		
		// Find average.
		
		float a = 0;
		for (float b: feelArray) {
			a += b;
		}
		averageFeel =  a / feelArray.size();

		victoryTime = (int) (feelArray.size() * .99);

		/*
		ArrayList<Integer> beatIndex = new ArrayList<Integer>();
		for (int i = 0; i < beatArray.size(); i++) {
			if (beatArray.get(i) == 1.0f)
				beatIndex.add(i);
		}
		
		int beatFalloff = 200000;
		
		// Add bells, sweep right
		for (Integer index: beatIndex) {
			for (int i = index + 1; i < index + beatFalloff; i++) {
				if (i >= beatArray.size())
					continue;
				float newBeat = (float) Math.pow(.99, i - (index));
				beatArray.set(i, newBeat);
			}
		}*/
		
		bossTime = feelArray.size() / 2;
		for (int i = feelArray.size() / 4; i < feelArray.size(); i++) {
			if (feelArray.get(i) > feelArray.get(bossTime))
				bossTime = i;
		}
		System.out.println("Boss Time: " + (float) bossTime / feelArray.size());
	}
	
	public void setTime44100(final long newTime) {
		
		beat = false;
		for(int i = (int) lastTime; i < newTime; i++) {
			if (beat = beatArray.get(i) == 1)
				break;
		}
		
		//System.out.println(beatScalar = (!beat) ? beatArray.get((int) newTime) : 1);
		
		lastTime = newTime;
		
		feel = min(1, feelArray.get((int) newTime));
		
		history.push(feel);
	}
	
	public List<Float> pastN () {
		return history;
	}

	public boolean isBeat() {
		return beat;
	}

	public float getFeel() {
		return feel;
	}
	
	public float getAverageFeel() {
		return averageFeel;
	}
	
	public float getBossTime() {
		return bossTime;
	}

	public long getVictoryTime() {
		return victoryTime;
	}
	
	public int getPercentage() {
		return (int) (((float) lastTime / feelArray.size()) * 100);
	}

//	public float getBeatScalar() {
//		return beatScalar;
//	}
}
