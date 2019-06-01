package dk.nielshvid.intermediary;

import java.time.LocalDate;
public class Entities {
	public static class Sample {
		public int serialnumber;
		public String owner;
		public LocalDate accessed;
	}
	public static class Blood {
		public Sample sampledata;
		public String bloodtype;
	}

	public static class Physicalsets {
		public String containerSize;
	}
}
