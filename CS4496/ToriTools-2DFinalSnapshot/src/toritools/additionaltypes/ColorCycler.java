package toritools.additionaltypes;

import static audioProject.AudioProject.getFloat;
import static java.lang.Math.*;

import java.awt.Color;

public class ColorCycler {
	private int red = 0, green = 0, blue = 0;
	private boolean redUp = false, greenUp = false, blueUp = false;
	private final int lr, hr, lg, hg, lb, hb;
	
	private Color currColor = Color.BLACK;

	public ColorCycler(final int lr, final int hr, final int lg, final int hg, final int lb, final int hb) {
		this.lr = max(0, lr);
		this.hr = min(255, hr);
		
		this.lg = max(0, lg);
		this.hg = min(255, hg);
		
		this.lb = max(0, lb);
		this.hb = min(255, hb);
	}
	
	public void cycleColors() {
		cycleColors(2);
	}

	public void cycleColors(final float rate) {
		red = (int) (red + getFloat() * rate * (redUp ? 1 : -1));
		if ((red = max(lr, red)) == lr)
			redUp = true;
		else if ((red = min(hr, red)) == hr)
			redUp = false;

		green = (int) (green + getFloat() * rate * (greenUp ? 1 : -1));
		if ((green = max(lg, green)) == lg)
			greenUp = true;
		else if ((green = min(hg, green)) == hg)
			greenUp = false;

		blue = (int) (blue + getFloat() * rate * (blueUp ? 1 : -1));
		if ((blue = max(lb, blue)) == lb)
			blueUp = true;
		else if ((blue = min(hb, blue)) == hb)
			blueUp = false;
		
		currColor = new Color(red, green, blue);
	}

	public Color getColor() {
		return currColor;
	}
}
