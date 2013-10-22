import java.util.Calendar;
import java.text.SimpleDateFormat;

class DateUtils {
	
	private String start_seconds;
	private String start_milliseconds;
	private String start_mins;


  public static String now(String dateFormat) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    return sdf.format(cal.getTime());

  }


	

  public static void  main(String arg[]) {
  	
  	String data = DateUtils.now("mm:ss:SSS");
  	System.out.println(data);
  	String mins = data.substring(0,2);
  	String seconds = data.substring(3,5);
  	String milli = data.substring(7,9);
  	System.out.println(mins+" "+seconds+" "+milli);

    
  }
}
