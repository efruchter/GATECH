package efruchter.runinfo;

public class Result {
	
	public double HC_Res;
	public long HC_Time;

	public double SA_Res;
	public long SA_Time;

	public double GA_Res;
	public long GA_Time;

	public double MM_Res;
	public long MM_Time;

	public String toString() {
		StringBuffer buff = new StringBuffer();

		buff.append("HC Result: ").append(HC_Res).append("\n");
		buff.append("HC Time (ms): ").append(HC_Time).append("\n");

		buff.append("SA Result: ").append(SA_Res).append("\n");
		buff.append("SA Time (ms): ").append(SA_Time).append("\n");

		buff.append("GA Result: ").append(GA_Res).append("\n");
		buff.append("GA Time (ms): ").append(GA_Time).append("\n");

		buff.append("MM Result: ").append(MM_Res).append("\n");
		buff.append("MM Time (ms): ").append(MM_Time).append("\n");

		return buff.toString();
	}
	
	
}
