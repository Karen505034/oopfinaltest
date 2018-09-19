package MOVIE;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class User
 * 使用者資訊
 *
 */
public class User {
	private long index;
	private String name;
	private int age;
	private static int counting=0; 

	/**
	 * constructor
	 * @param age
	 * @param name
	 */
	public User(int age,String name){
		this.name=name;
		this.age=age;
		this.index=counting;
		counting++;
		
	}
	
	/**
	 * 建立User db檔
	 */
	public static void Creat_User_Db() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:User.db");
			// System.out.println("Opened database successfully");

			stmt = c.createStatement();
			
				String sql = "CREATE TABLE if not exists user_table (IND    INT , "
						+ " NAME    TEXT      NOT NULL, AGE      INT )";
				stmt.executeUpdate(sql);
				stmt.close();
				c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
	
	/**
	 * 讀進json的user資料
	 */
	public static void Setuser(){
		Creat_User_Db();
		int index;
		String name;
		int age;
		
		String str = "";

		FileReader fin;
		try {
			fin = new FileReader("user.json");
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
				index = jsonArr.getJSONObject(i).getInt("index");
				name = jsonArr.getJSONObject(i).getString("name");
				age = jsonArr.getJSONObject(i).getInt("age");
				User.Insert(index, name, age);
			}

		} catch (Exception e) {
			System.err.println("Error1: " + e.getMessage());
		}
	}
	
	/**
	 * 將user的資料寫入user db
	 * @param index
	 * @param name
	 * @param age
	 */
	private static void Insert(int index, String name, int age){
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:User.db");
			c.setAutoCommit(false);
			//System.out.println("Opened database successfully");
			stmt = c.createStatement();
			
			String sql = "INSERT INTO user_table (IND, NAME, AGE) " 
					+ "VALUES (" + index + ",'" + name + "'," + age + ");";
			
			
			//System.out.println(sql);
			//System.out.println("name = " + name);
			//System.out.println("age = " + age);
			
			stmt.executeUpdate(sql);

			stmt.close();
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		//System.out.println("Records created successfully");
	}
	
	/**
	 * 輸入user index回傳user年紀
	 * @param ind 使用者index
	 * @return int user年紀
	 */
	public static int SearchIndex(int ind){
		Connection c = null;
		Statement stmt2 = null;
		int age = 0;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:User.db");
			c.setAutoCommit(false);
			//("MOVIENAME,ID, URL, DESCRI, HALLNAME, HALL_ID, MOV_CLAS, STARTTIME, SCORE, INFOR)
			stmt2 = c.createStatement();
			ResultSet rs1 = stmt2.executeQuery("SELECT *  FROM user_table WHERE IND=" +ind+ ";");
			age = rs1.getInt("age");
			rs1.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return age;
	}
	
	/**
	 * 取得user的index
	 * @return long index
	 */
	public long getIndex() {
		return index;
	}
	
	
	/*public void setId(long index) {
		this.index = index;
	}*/
	
	/**
	 * 取得user名字
	 * @return String 客戶名字
	 */
	public String getName() {
		String a = new String(name);
		return a;
	}
	/*public void setName(String name) {
		this.name = name;
	}*/
	
	/**
	 * 取得user年紀
	 * @return int 年紀
	 */
	public int getAge() {
		return age;
	}
	/*public void setAge(int age) {
		this.age = age;
	}*/
	
	
	/*public static void main(String args[]){
		Setuser();
	}*/
}
