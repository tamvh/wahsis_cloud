package com.wahsis.iot.database;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
*
* @author khiemnv
*/
public class PartnerServerModelFactory { 
	public static PartnerServerModel getDaoRedis() { 
		return null; 
	}
	public static PartnerServerModel getDaoSQL() throws IOException {
            return PartnerServerModelSQL.getInstance(); // Logger.getLogger(PartnerServerModelFactory.class.getName()).log(Level.SEVERE, null, ex);
//		return null;
	}
}

