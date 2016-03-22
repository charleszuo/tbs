package com.zcwyx.tbs.registercenter.client;

public class Util {

	public static VersionRange validateVersion(String version){
		VersionRange versionRange = new VersionRange();
		if(version.matches("\\d+")){
			int ver = Integer.parseInt(version);
			versionRange.setRange(false);
			versionRange.setLow(ver);
		}else if(version.matches("\\d+\\+")){
			int ver = Integer.parseInt(version.substring(0, version.length() -1));
			versionRange.setRange(true);
			versionRange.setLow(ver);
			versionRange.setHigh(Integer.MAX_VALUE);
		}else if(version.matches("\\d+-\\d+")){
			String[] lh = version.split("-");
			versionRange.setRange(true);
			versionRange.setLow(Integer.parseInt(lh[0]));
			versionRange.setHigh(Integer.parseInt(lh[1]));
		}
		
		return versionRange;
	}
	
	public static class VersionRange {
		private int low;
		private int high;
		private boolean isValid = true;
		private boolean isRange;

		public int getLow() {
			return low;
		}

		public void setLow(int low) {
			this.low = low;
		}

		public int getHigh() {
			return high;
		}

		public void setHigh(int high) {
			this.high = high;
		}

		public boolean isValid() {
			return isValid;
		}

		public void setValid(boolean isValid) {
			this.isValid = isValid;
		}

		public boolean isRange() {
			return isRange;
		}

		public void setRange(boolean isRange) {
			this.isRange = isRange;
		}

	}
}
