package MOVIE;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.io.*;
import org.json.*;

import HALL.BigHall;
import HALL.Hall;
import HALL.SmallHall;
/**
 * Class Movie
 * 儲存movie的資料
 * 存取、比較Movie內資料的函數
 *
 */
public class Movie {
	private static final String date = new SimpleDateFormat("yyMMdd").format(new Date()); //系統日期
	public static ArrayList<Movie> initial_allmovie = new ArrayList(10); //存所有Movie物件的ArrayList
	private static final String Tab_name = 'a' + date; // Movie db 的 Table name
	private String movie_name; //電影名稱
	private String id; //電影id
	// private String resp_id;
	private String url; // 連結
	private String descri; // 簡述
	private Hall hall; //廳位(型態為Hall)
	private String hallname_s; // 廳位名稱
	private String hall_id; // 廳位的Table name
	private String mov_clas_s; // 電影分級
	private Classification mov_clas; // 電影分級(enum Classification)
	private Time starttime; // 電影開場時間(型態為Time)
	private String starttime_s; //電影開場時間(型態為String)
	private String score_s; // 電影評價
	private double score;  // 電影分數
	// private String infor_s;
	private int infor; //片長
	private boolean isbig; // 電影聽位的大小

	/**
	 * 取系統時間
	 * @return String 
	 */
	public static String getDateTime() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("HH:mm");
		Date date = new Date();
		String strDate = sdFormat.format(date);
		// System.out.println(strDate);
		return strDate;
	}

	/**
	 * 把所有Movie的物件存入initial_allmovie
	 */
	public static void setmov_arr() {
		int i = 0;
		String name;
		String st;
		Time st_time;
		Connection c = null;
		Statement stmt2 = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Movie.db");
			c.setAutoCommit(false);
			stmt2 = c.createStatement();
			ResultSet rs1 = stmt2.executeQuery("SELECT *  FROM " + Tab_name);
			// name = rs1.getString("moviename");
			// st = rs1.getString("starttime");
			// st_time = new Time(st);
			// initial_allmovie.add( new Movie(name, st_time));
			// rs1.next();
			while (rs1.next()) {
				name = rs1.getString("moviename");
				// System.out.println(name);
				st = rs1.getString("starttime");
				// System.out.println(st);
				st_time = new Time(st);
				Movie a = new Movie(name, st_time);
				initial_allmovie.add(a);

			}

			rs1.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Constructor
	 * @param mov_name 
	 * @param st_t
	 * @throws IOException
	 */
	Movie(String mov_name, Time st_t) throws IOException {

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Movie.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + Tab_name + " WHERE MOVIENAME = '" + mov_name + "' "
					+ "and STARTTIME = '" + st_t.TimetoString() + "' ;");
			// System.out.println(mov_name);
			// System.out.println(st_t.TimetoString());
			if (rs.isClosed() == true) {
				System.out.println("Movie Not Found");
				// System.out.println("Operation done successfully");
			} else {
				id = rs.getString("id");
				movie_name = rs.getString("moviename");
				url = rs.getString("url");
				descri = rs.getString("descri");
				hallname_s = rs.getString("hallname");
				hall_id = rs.getString("hall_id");
				switch (hallname_s) {
				case ("武當"):
				case ("少林"):
				case ("華山"):
					hall = new BigHall(hall_id, movie_name, st_t.TimetoString(), hallname_s);
					isbig = true;
					break;
				default:
					hall = new SmallHall(hall_id, movie_name, st_t.TimetoString(), hallname_s);
					isbig = false;
					break;

				}
				mov_clas_s = rs.getString("mov_clas");
				switch (mov_clas_s) {
				case "輔導":
					mov_clas = Classification.PG_15;
					// System.out.println(1);
					break;
				case "普遍":
					mov_clas = Classification.G;
					break;
				case "保護":
					mov_clas = Classification.PG_6;
					break;
				default:
					mov_clas = Classification.R_18;
				}
				starttime_s = rs.getString("starttime");
				starttime = new Time(starttime_s);
				/*
				 * int x = starttime_s.indexOf("："); if (x < 0) { String[] b =
				 * starttime_s.split(":"); String hour = b[0]; hour =
				 * hour.replaceAll(" ", ""); String minute = b[1]; minute =
				 * minute.replaceAll(" ", ""); starttime_s = hour + "：" +
				 * minute; }
				 */
				score = rs.getDouble("score");
				infor = rs.getInt("infor");
				/*
				 * System.out.println(); System.out.println("ID = " + id);
				 * System.out.println("movie_name = " + movie_name);
				 * System.out.println("starttime_s = " + starttime_s);
				 * System.out.println("url = " + url);
				 * System.out.println("descri = " + descri);
				 * System.out.println("hallname_s = " + hallname_s);
				 * System.out.println("hall_id = " + hall_id);
				 * System.out.println("mov_clas_s = " + mov_clas_s);
				 * System.out.println("score = " + score);
				 * System.out.println("infor = " + infor); System.out.println();
				 */

			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		// System.out.println("Operation done successfully");
	}

	/**
	 * 找出該場電影所有有效的放映時間
	 * @param mov_name 電影名稱
	 * @return ArrayList<String> 放映時間
	 */
	public static ArrayList<String> ShowAllTime(String mov_name) {

		Connection c = null;
		Statement stmt2 = null;
		ArrayList<String> arrlist = new ArrayList(10);
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Movie.db");
			c.setAutoCommit(false);
			// System.out.println("Opened database successfully HIGHER!!!! ");

			Time now = new Time(Movie.getDateTime());
			// now.StringtoTime(Movie.getDateTime());
			stmt2 = c.createStatement();

			ResultSet rs1 = stmt2
					.executeQuery("SELECT STARTTIME  FROM " + Tab_name + " WHERE MOVIENAME='" + mov_name + "';");
			while (rs1.next()) {
				String t = rs1.getString("starttime");
				Time obt = new Time(t);

				// obt.StringtoTime(t);
				if (now.isEarly(obt)) {
					arrlist.add(t);

				}
			}

			rs1.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return arrlist;
	}

	/**
	 * 利用電影ID找該部電影的資料
	 * @param ID 電影 ID
	 */
	public static void SearchMovieID(String ID) {
		Connection c = null;
		Statement stmt2 = null;
		ArrayList<String> arrlist = new ArrayList<String>(10);
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Movie.db");
			c.setAutoCommit(false);
			// ("MOVIENAME,ID, URL, DESCRI, HALLNAME, HALL_ID, MOV_CLAS,
			// STARTTIME, SCORE, INFOR)
			stmt2 = c.createStatement();
			ResultSet rs1 = stmt2.executeQuery("SELECT *  FROM " + Tab_name + " WHERE ID='" + ID + "';");
			System.out.println("電影時間: " + rs1.getString("moviename"));
			System.out.println("分級: " + rs1.getString("mov_clas"));
			System.out.println("廳位: " + rs1.getString("hallname"));
			System.out.print("播放時間: " + rs1.getString("starttime"));
			rs1.next();
			while (rs1.next()) {
				System.out.print("," + rs1.getString("starttime"));
			}
			System.out.println();
			rs1.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	/**
	 * 利用電影名稱搜尋電影分級
	 * @param name 要搜尋的電影名稱
	 * @return String 分級
	 */
	public static String SearchMovieName(String name) {
		Connection c = null;
		Statement stmt2 = null;
		String clas = "";
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Movie.db");
			c.setAutoCommit(false);
			// ("MOVIENAME,ID, URL, DESCRI, HALLNAME, HALL_ID, MOV_CLAS,
			// STARTTIME, SCORE, INFOR)
			stmt2 = c.createStatement();
			ResultSet rs1 = stmt2.executeQuery("SELECT *  FROM " + Tab_name + " WHERE MOVIENAME='" + name + "';");
			clas = rs1.getString("mov_clas");
			// System.out.println(clas);
			rs1.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return clas;
	}

	/**
	 * 顯示所有符合時間限制的電影
	 * @return ArrayLisr<String>
	 */
	public static ArrayList<String> ShowAll() {

		Connection c = null;
		Statement stmt = null;
		Statement stmt2 = null;
		ArrayList<String> arrlist = new ArrayList<String>(10);
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Movie.db");
			c.setAutoCommit(false);
			// System.out.println("Opened database successfully HIGHER!!!! ");

			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT DISTINCT MOVIENAME  FROM " + Tab_name + ";");
			while (rs.next()) {
				String mov_name = rs.getString("moviename");
				Time now = new Time(Movie.getDateTime());
				// now.StringtoTime(Movie.getDateTime());
				// System.out.println("! "+now.TimetoString());
				stmt2 = c.createStatement();
				ResultSet rs1 = stmt2
						.executeQuery("SELECT STARTTIME  FROM " + Tab_name + " WHERE MOVIENAME='" + mov_name + "';");
				while (rs1.next()) {
					String t = rs1.getString("starttime");
					Time obt = new Time(t);

					if (now.isEarly(obt)) {
						// System.out.println(t);
						mov_name = mov_name + "/" + t;
						break;
					}
				}
				// System.out.println(mov_name);
				rs1.close();
				arrlist.add(mov_name);

			}
			// System.out.println("Finish database successfully HIGHER!!!! ");

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return arrlist;
	}

	/**
	 * 選分數較傳入參數高的電影
	 * @param Score 要求的分數下限
	 */
	public static void Higher_Score(double Score) {
		String[] a = new String[0];
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Movie.db");
			c.setAutoCommit(false);
			// System.out.println("Opened database successfully HIGHER!!!! ");

			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT DISTINCT ID, MOVIENAME FROM " + Tab_name + " WHERE SCORE >= " + Score + ";");
			while (rs.next()) {
				String id = rs.getString("ID");
				String name = rs.getString("moviename");
				System.out.println(id + " (" + name + ")");
				// System.out.println(id);

			}
			// System.out.println("Finish database successfully HIGHER!!!! ");

			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// return a;
	}

	/**
	 * 搜尋在時間範圍內符合片長需求的電影
	 * @param t1 時間區間的起始時間
	 * @param t2 時間區間的結束時間
	 * @param len 片長
	 */
	public static void Time_Len_Search(Time t1, Time t2, int len) {
		Connection c = null;
		Statement stmt2 = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Movie.db");
			c.setAutoCommit(false);
			stmt2 = c.createStatement();
			ResultSet rs1 = stmt2.executeQuery("SELECT *  FROM " + Tab_name + " WHERE STARTTIME >= '"
					+ t1.TimetoString() + "' " + "AND STARTTIME <= '" + t2.TimetoString() + "'AND INFOR <= " + len);
			System.out.println("電影: " + rs1.getString("moviename"));
			System.out.println("ID: " + rs1.getString("id"));
			// System.out.println("分級: "+rs1.getString("mov_clas"));
			// System.out.println("廳位: "+rs1.getString("hallname"));
			System.out.println("播放時間: " + rs1.getString("starttime"));
			System.out.println("片長：" + rs1.getString("infor"));
			System.out.println();
			rs1.next();
			while (rs1.next()) {
				System.out.println("電影: " + rs1.getString("moviename"));
				System.out.println("播放時間: " + rs1.getString("starttime"));
				System.out.println("ID: " + rs1.getString("id"));
				System.out.println("片長：" + rs1.getString("infor") + "分鐘");
				System.out.println();
			}

			rs1.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * 將電影資料加入Movie db
	 * @param MovieName
	 * @param ID
	 * @param Url
	 * @param Descri
	 * @param Hallname
	 * @param Hall_id
	 * @param Mov_clas
	 * @param Starttime
	 * @param Score
	 * @param Infor
	 */
	private static void Insert(String MovieName, String ID, String Url, String Descri, String Hallname, String Hall_id,
			String Mov_clas, String Starttime, double Score, int Infor) {

		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Movie.db");
			c.setAutoCommit(false);
			// System.out.println("Opened database successfully");

			stmt = c.createStatement();
			// System.out.println(9);
			String sql = "INSERT INTO " + Tab_name + "(MOVIENAME,ID,  "
					+ "URL, DESCRI, HALLNAME, HALL_ID, MOV_CLAS, STARTTIME, SCORE, INFOR) " + "VALUES ('" + MovieName
					+ "' ,'" + ID + "' ,'" + Url + "' ,'" + Descri + "','" + Hallname + "','" + Hall_id + "' ,'"
					+ Mov_clas + "' ,'" + Starttime + "' ," + Score + " ,'" + Infor + "');";

			// System.out.println(10);
			stmt.executeUpdate(sql);
			// System.out.println(11);

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// System.out.println("Records created successfully");
	}
	
	/**
	 * 把Movie的json檔的資料讀入
	 */
	private static void Setmovie() {
		String movie_name;
		String id;
		// String resp_id;
		String url;
		String descri;
		// Hall hallname;
		String hallname_s;
		String hall_id;
		String mov_clas_s;
		Classification mov_clas;
		Time starttime;
		String starttime_s;
		String score_s;
		double score;
		String infor_s;
		int infor;
		String str = "";

		FileReader fin;
		try {
			fin = new FileReader("movie_info.json");
			int word;

			while (fin.ready()) {
				word = fin.read();
				// System.out.print((char) word);
				str = str + (char) word;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject j;
		try {
			JSONArray jsonArr = new JSONArray(str);

			for (int i = 0; i < jsonArr.length(); i++) {
				starttime_s = jsonArr.getJSONObject(i).getString("time");
				starttime_s = starttime_s.replaceAll(" ", "");
				String[] a = starttime_s.split("、");
				for (int k = 0; k < a.length; k++) {
					a[k] = a[k].replaceAll(" ", "");
					int x = a[k].indexOf("：");
					if (x < 0) {
						String[] b = a[k].split(":");
						String hour = b[0];
						hour = hour.replaceAll(" ", "");
						String minute = b[1];
						minute = minute.replaceAll(" ", "");
						a[k] = hour + "：" + minute;
					}
					// System.out.println(a[k]);
				}
				for (int iter = 0; iter < a.length; iter++) {
					id = jsonArr.getJSONObject(i).getString("id");
					// System.out.println(id);
					movie_name = jsonArr.getJSONObject(i).getString("movie");
					movie_name = movie_name.trim();
					// movie_name = movie_name.replaceAll(" ", "");
					// System.out.println(movie_name);
					url = jsonArr.getJSONObject(i).getString("url");
					// System.out.println(url);
					descri = jsonArr.getJSONObject(i).getString("descri");
					// System.out.println(descri);
					mov_clas_s = jsonArr.getJSONObject(i).getString("classification");
					// System.out.println(mov_clas_s);
					score_s = jsonArr.getJSONObject(i).getString("score");
					score_s = score_s.replaceAll(" ", "");
					// System.out.print(score_s);
					// System.out.println(score_s);
					infor_s = jsonArr.getJSONObject(i).getString("infor");
					infor_s = infor_s.replaceAll(" ", "");
					int infor_x = infor_s.indexOf(":");
					if (infor_x < 0) {
						String[] infor_a = infor_s.split("：");
						int length_infor_a1 = infor_a[1].length();
						infor = Integer.parseInt(infor_a[1].substring(0, length_infor_a1 - 1));
					} else {
						String[] infor_a = infor_s.split(":");
						int length_infor_a1 = infor_a[1].length();
						infor = Integer.parseInt(infor_a[1].substring(0, length_infor_a1 - 1));
					}
					// String[] c = infor.split("：");
					// infor = c[0] + c[1];
					// System.out.print(infor);
					// System.out.println(infor);
					hallname_s = jsonArr.getJSONObject(i).getString("hall");
					hallname_s = hallname_s.replaceAll(" ", "");
					// System.out.println(hallname_s);
					String[] b = score_s.split("/");
					score = Double.parseDouble(b[0]);
					// System.out.println(score);
					starttime = new Time(a[iter]);
					// System.out.println(0);
					hall_id = "a" + id + starttime.getHour() + starttime.getMinute() + "_" + date;
					// System.out.println(1);
					// System.out.println(resp_id);
					String hallnameid = hallname_s + starttime.getHour() + starttime.getMinute();
					// System.out.println(hallnameid);
					// hallname = new Hall(hallnameid);
					/*
					 * if (mov_clas_s.equals("輔導")){ mov_clas =
					 * Classification.PG_15; System.out.println(2); } else if
					 * (mov_clas_s.equals("普遍")) mov_clas = Classification.G;
					 * else if (mov_clas_s.equals("保護")) mov_clas =
					 * Classification.PG_6; else mov_clas = Classification.R_18;
					 */
					/*
					 * switch (mov_clas_s) { case "輔導": mov_clas =
					 * Classification.PG_15; // System.out.println(1); break;
					 * case "普遍": mov_clas = Classification.G; break; case "保護":
					 * mov_clas = Classification.PG_6; break; default: mov_clas
					 * = Classification.R_18; }
					 */
					// System.out.println(3);
					// hallname = new Hall(resp_id);
					// System.out.println(2);
					// System.out.println(iter);
					Movie.Insert(movie_name, id, url, descri, hallname_s, hall_id, mov_clas_s, a[iter], score, infor);
					// System.out.println(5);
				}

			}

		} catch (Exception e) {
			System.err.println("Error1: " + e.getMessage());
		}
	}

	/**
	 * 創MOVIE DB
	 */
	public static void Creat_Mov_Db() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Movie.db");
			// System.out.println("Opened database successfully");

			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM sqlite_master WHERE name= '" + Tab_name + "' and type='table' ;");
			if (rs.next()) {
				// System.out.println("Hey "+rs.getString(2));
				stmt.close();
			} else {
				// System.out.println("Hello");
				String sql = "CREATE TABLE if not exists " + Tab_name + " (MOVIENAME           TEXT    NOT NULL, "
						+ "ID            TEXT          NOT NULL," + " HALL_ID           TEXT    NOT NULL, "
						+ " URL            TEXT     NOT NULL, " + " DESCRI             TEXT    NOT NULL, "
						+ " HALLNAME             TEXT    NOT NULL, " + " MOV_CLAS             TEXT    NOT NULL,"
						+ " STARTTIME             TIME    NOT NULL, " + " SCORE        DOUBLE, "
						+ " INFOR             INTEGER)";
				stmt.executeUpdate(sql);
				Movie.Setmovie();
				stmt.close();
				c.close();

			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * 取得電影名稱
	 * @return String moviename
	 */
	public String getMovie_name() {
		return movie_name;
	}

	/**
	 * 取得電影ID
	 * @return String ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 取得電影連結
	 * @return String url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 取的電影簡述
	 * @return String descri
	 */
	public String getDescri() {
		return descri;
	}
	
	/**
	 * 取得廳位資料
	 * @return Hall hall
	 */
	public Hall getHall() {
		return hall;
	}

	/**
	 * 取得廳位名稱
	 * @return String hallname_s
	 */
	public String getHallname_s() {
		return hallname_s;
	}

	/**
	 * 取得廳位資料的table name
	 * @return String hall_id
	 */
	public String getHall_id() {
		return hall_id;
	}

	/**
	 * 取得電影分級
	 * @return String mov_clas_s
	 */
	public String getMov_clas_s() {
		return mov_clas_s;
	}

	/**
	 * 取得電影分級(Classification)
	 * @return Classification mov_clas
	 */
	public Classification getMov_clas() {
		return mov_clas;
	}

	/**
	 * 取得電影放映時間
	 * @return Time starttime
	 */
	public Time getStarttime() {
		return starttime;
	}

	/**
	 * 取的放映時間(字串)
	 * @return String starttime_s
	 */
	public String getStarttime_s() {
		return starttime_s;
	}

	/**
	 * 取的分數
	 * @return double score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * 取得電影片長
	 * @return int infor
	 */
	public int getInfor() {
		return infor;
	}

	/**
	 * 取得廳位大小
	 * @return boolean isbig
	 */
	public boolean isIsbig() {
		return isbig;
	}

	/**
	 * 改變廳位大小
	 * @param isbig
	 */
	public void setIsbig(boolean isbig) {
		this.isbig = isbig;
	}

	/*
	 * public static void main(String[] args) throws IOException { //
	 * Hall.InitialBigHall();：20 Creat_Mov_Db(); setmov_arr(); //Movie a = new
	 * Movie("攻殼機動隊1995GHOSTINTHESHELL", new Time("17：50"));
	 * //System.out.println(a.id); //Movie.Higher_Score(9);
	 * Movie.SearchMovieID("x7GCv22RgYGfd5l8YdbVqYhZ");
	 * 
	 * 
	 * 
	 * }
	 */
}
