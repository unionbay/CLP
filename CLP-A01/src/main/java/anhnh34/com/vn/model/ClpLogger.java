package anhnh34.com.vn.model;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ClpLogger extends Logger{
	
	private static final String FQCN = ClpLogger.class.getName() + ".";

	
	protected ClpLogger(String name) {
		super(name);	
	}

	
	public void info(final Object msg) {
	    super.log(FQCN, Level.INFO, msg, null);
	}
	
}
