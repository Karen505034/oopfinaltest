package MOVIE;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Time
 * 形式 => hour:minute
 *
 */
public class Time {
	private String hour;
	private String minute;
	
	/**
	 * Constructor
	 * 沒有傳入參數
	 */
	public Time(){
		hour = null;
		minute = null;
	}
	/**
	 * Constructor
	 * @param h 小時
	 * @param m 分鐘
	 */
	public Time(String h, String m){
		hour = h;
		minute = m;
	}
	/**
	 * Constructor
	 * @param time 資料型態為Time的物件
	 */
	public Time(String time){
		this.StringtoTime(time);
	}
	/**
	 * 取hour
	 * @return String 小時
	 */
	public String getHour() {
		return hour.replaceAll(" ", "");
	}
	/**
	 * 取minute
	 * @return String 分鐘
	 */
	public String getMinute() {
		return minute.replaceAll(" ", "");
	}
	/**
	 * 把物件Time用字串表示出來
	 * @return String 小時:分鐘
	 */
	public String TimetoString(){
		return hour + "：" + minute;
	}
	/**
	 * 把字串轉乘Time的物件
	 * @param time 要被轉成字串的物件
	 */
	public void StringtoTime(String time){
		int x = time.indexOf(":");
		if(x >= 0){
			String[] a = time.split(":");
			hour = a[0];
			hour.replaceAll(" ", "");
			minute = a[1];
			minute.replaceAll(" ", "");
		}
		else{
			String[] a = time.split("：");
			hour = a[0];
			hour.replaceAll(" ", "");
			minute = a[1];
			minute.replaceAll(" ", "");
		}
		
	}
	/**
	 * 比較時間的早晚
	 * @param time2 要被比較的物件
	 * @return boolean 如果time2比calling obj早=>retunr true; 反之return false
	 */
	public boolean isEarly(Time time2){
		//If time2 is early, return true.
		int a = time2.TimetoString().compareTo(this.TimetoString());
		if (a > 0)
			return true;
		else
			return false;
	}
	/**
	 * 用來測試的main function
	 * @param args
	 */
	public static void main(String[] args){
		Time time = new Time();
		System.out.println(time.TimetoString());
		time.StringtoTime("09:12");
		System.out.println(time.TimetoString());
		Time time2 = new Time("09:13");
		boolean a = time2.isEarly(time);
		System.out.println(a);
		System.out.print(time2.getHour());
	}
}
