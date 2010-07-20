package org.desking.model;

public interface ILogger {
	public static final ILogger DEFAULT = new ILogger() {

		@Override
		public void logInfo(String message) {
			System.out.println(message);
		}

		@Override
		public void logError(String message) {
			System.err.println(message);
		}
		
	};
	
	public void logInfo(String message);
	public void logError(String message);
}
