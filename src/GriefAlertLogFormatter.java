import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

class GriefAlertLogFormatter extends Formatter{
	// Version 13 : 07/11 11h25 GMT+2
	// for servermod123-124+

	public String format(LogRecord rec){
		return calcDate(rec.getMillis())+"[INFO] "+formatMessage(rec)+"\n";	
	}

	private String calcDate(long millisecs){
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");	//	2010-10-09 07:52:09
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}

	public String getHead(Handler h){
		return "";
	}

	public String getTail(Handler h){
		return "";
	}
}	//	GriefAlertLogFormatter
