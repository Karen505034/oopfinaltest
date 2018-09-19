package HALL;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Class Hall
 * 廳位的資料
 *
 */
public abstract class  Hall {
	protected String HallID;
	protected String MovieName;
	protected String StartTime;
	protected String Hallname;
	
	/**
	 * 指定區域
	 * @param Amount
	 * @param region
	 * @return boolean
	 */
	abstract public boolean RegionBook(int Amount, String region);
	//public Hall(){}
	/**
	 * 一般訂票 輸入需要張數
	 * @param Amount
	 * @return boolean 是否訂票成功
	 */
	abstract public boolean GeneralBook(int Amount);
	
	/**
	 * 輸入需要張數 是否需要連續，條件訂票
	 * @param Amount 需要張數
	 * @param cont 是否需要連續
	 * @return boolean 是否訂票成功
	 */
	abstract public boolean ConditionBook(int Amount,boolean cont);
	
	/**
	 * 輸入需要張數 是否需要連續 指定座位區 條件式訂票
	 * @param Amount 張數
	 * @param cont 是否連續
	 * @param region 區域
	 * @return boolean 是否訂票成功
	 */
	abstract public boolean ConditionBook(int Amount,boolean cont,String region);
	//abstract boolean RegionBook(int Amount,String region);
	
	/**
	 * 輸入張數 是否需要連續 條件式訂票
	 * @param Amount 張數
	 * @param cont 是否需要連續
	 * @return boolean 是否訂票成功
	 */
	abstract public boolean SearchEmptySeat(int Amount,boolean cont);
	
	/**
	 * 搜尋是否還有空位
	 * @param Amount 張數
	 * @param row 第幾排
	 * @return boolean 是否還有空位
	 */
	abstract public boolean SearchEmptySeat(int Amount,char row);
	
	/**
	 * 顯示目前的訂位狀況
	 */
	abstract public void Show();
	
	/**
	 * 利用座位ID進行退票
	 * @param ID 
	 * @return boolean 是否退票成功
	 */
	abstract public boolean Cancel(String ID);
	
	
	
	/**
	 * 確定大廳與小廳的資料是否讀取成功
	 * @return boolean
	 * @throws IOException
	 */
	public static boolean InitialHall() throws IOException
	{
		return InitialBigHall()&&InitialSmallHall();
	}
	
	/**
	 * 讀進大廳的json 資料
	 * @return boolean 資料是否讀取成功
	 * @throws IOException
	 */
	private static boolean InitialBigHall( ) throws IOException
	  {
		  	String str="";
		  	if(Hall.NewBigHallTable("BIG_HALL")==false)
		  		return false;
		  	else
		  	{
		
					FileReader fin;
					try {
						fin = new FileReader("big_room.json");
						int word;
					while (fin.ready()){
							word = fin.read();
							str=str+(char)word;							
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				 try {
				      JSONArray jsonArr = new JSONArray(str);
				      int seatNum;
				      String id;
				      String row;
				      String region;
				      String occupied;
				  
				      Hall.NewBigHallTable("BIG_HALL");
				      
				      for(int i=0;i <jsonArr.length();i++)
				      {
				    	  id=jsonArr.getJSONObject(i).getString("id");
				    	  row=jsonArr.getJSONObject(i).getString("row");
				    	  seatNum=jsonArr.getJSONObject(i).getInt("seatNum");
				    	  occupied=jsonArr.getJSONObject(i).get("occupied").toString();
				    	  region=jsonArr.getJSONObject(i).getString("region");
				    	  
				    	  Hall.InsertBigHall(id,row.charAt(0),seatNum,occupied,region,"BIG_HALL");
				      }		
				    }catch(Exception e){
				    System.err.println("Error: " + e.getMessage());
				    }
				 	return true;
				 }
	  }
		

	/**
	 * 把大廳的資料寫入Hall db檔
	 * @param ID
	 * @param Row
	 * @param SeatNum
	 * @param Occupied
	 * @param Region
	 * @param HallName
	 */
	private static void InsertBigHall(String ID,char Row,int SeatNum,String Occupied,String Region,String HallName)
	{
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			String sql = "INSERT INTO "+ HallName +"(ID,ROW,SEATNUMBER,OCCUPIED,REGION) " +
					"VALUES ('"+ID+"','"+Row+"',"+SeatNum+",'"+Occupied+"' ,'"+Region+"' );"; 
			stmt.executeUpdate(sql);

			stmt = c.createStatement();

			stmt.close();
			c.commit();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}


	/**
	 * 利用HallID創不同時間不同電影的廳位的table
	 * @param HallID
	 * @return boolean 是否創立成功
	 */
	private static boolean NewBigHallTable(String HallID)
	{
		Connection c = null;
		Statement stmt = null;
		Statement stmt2 = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			//System.out.println("Opened database successfully");

			stmt2=c.createStatement();
			ResultSet rs = stmt2.executeQuery( "SELECT * FROM sqlite_master WHERE name='BIG_HALL' and type='table' ;" );
			if(rs.next())
			{
				stmt2.close();
				return false;
			}
			else
			{
			stmt = c.createStatement();
			String sql = "CREATE TABLE if not exists "+ HallID +
					" (ID TEXT NOT NULL," +
					" ROW           CHAR(1)    NOT NULL, " + 
					" SEATNUMBER    INT        NOT NULL, " + 
					" OCCUPIED      BOOLEAN    NOT　NULL, " + 
					" REGION         TEXT)"; 
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
			return true;
			}
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		//System.out.println("Table created successfully");
		return true;
	}
	
	/**
	 * 讀入小廳的json資料
	 * @return boolean 是否讀入成功
	 * @throws IOException
	 */
	private static boolean InitialSmallHall( ) throws IOException
	  {
		  	String str="";
		  	if(Hall.NewSmallHallTable("SMALL_HALL")==false)
		  	{
		  		return false;
		  	}
		  	else
		  	{
		
					FileReader fin;
					try {
						fin = new FileReader("small_room.json");
						int word;
					while (fin.ready()){
							word = fin.read();
							str=str+(char)word;							
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				 try {
				      JSONArray jsonArr = new JSONArray(str);
				      int seatNum;
				      String id;
				      String row;			  
				      String occupied;
				  
				      Hall.NewSmallHallTable("SMALL_HALL");
				      
				      for(int i=0;i <jsonArr.length();i++)
				      {
				    	  id=jsonArr.getJSONObject(i).getString("id");
				    	  row=jsonArr.getJSONObject(i).getString("row");
				    	  seatNum=jsonArr.getJSONObject(i).getInt("seatNum");
				    	  occupied=jsonArr.getJSONObject(i).get("occupied").toString();
				    	  
				    	  Hall.InsertSmallHall(id,row.charAt(0),seatNum,occupied,"SMALL_HALL");
				      }		
				    }catch(Exception e){
				    System.err.println("Error: " + e.getMessage());
				    }
				 	return true;
				 }
	  }
		

	/**
	 * 將小廳的資料輸入Hall db
	 * @param ID
	 * @param Row
	 * @param SeatNum
	 * @param Occupied
	 * @param HallName
	 */
	private static void InsertSmallHall(String ID,char Row,int SeatNum,String Occupied,String HallName)
	{
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			String sql = "INSERT INTO "+ HallName +"(ID,ROW,SEATNUMBER,OCCUPIED) " +
					"VALUES ('"+ID+"','"+Row+"',"+SeatNum+",'"+Occupied+"' );"; 
			stmt.executeUpdate(sql);

			stmt.close();
			c.commit();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	/**
	 * 利用HallIDr建立不同時間不同電影的小廳table
	 * @param HallID
	 * @return boolean 是否創立成功
	 */
	private static boolean NewSmallHallTable(String HallID)
	{
		Connection c = null;
		Statement stmt = null;
		Statement stmt2 = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			//System.out.println("Opened database successfully");

			stmt2=c.createStatement();
			ResultSet rs = stmt2.executeQuery( "SELECT * FROM sqlite_master WHERE name='SMALL_HALL' and type='table' ;" );
			if(rs.next())
			{
				stmt2.close();
				return false;
			}
			else
			{
			stmt = c.createStatement();
			String sql = "CREATE TABLE if not exists "+ HallID +
					" (ID TEXT NOT NULL," +
					" ROW           CHAR(1)    NOT NULL, " + 
					" SEATNUMBER    INT        NOT NULL, " + 
					" OCCUPIED      BOOLEAN    NOT　NULL); " ; 
					 
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
			return true;
			}
			
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		//System.out.println("Table created successfully");
		return true;
	}
	
	
	
	
	
}
