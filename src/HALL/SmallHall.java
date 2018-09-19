package HALL;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import MOVIE.MovieException;
import MOVIE.Ticket;
/**
 * Class SmallHall
 * 電影院小廳
 * 繼承Hall
 */
public class SmallHall extends Hall {

	/**
	 * 輸入張數 指定區域 是否需要連續，條件訂票
	 * @param Amount
	 * @param cont
	 * @param region
	 */
	public boolean ConditionBook(int Amount, boolean cont, String region) {
		return false;
	}

	/**
	 * 指定區域
	 */
	public boolean RegionBook(int Amount, String region) {
		return false;
	}
	/**
	 * Constructor
	 * @param HallName
	 * @param MovieName
	 * @param StartTime
	 * @param Hallname
	 */
	public SmallHall(String HallName, String MovieName, String StartTime, String Hallname) {
		super.StartTime = StartTime;
		super.MovieName = MovieName;
		super.HallID = HallName;
		super.Hallname = Hallname;
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			// System.out.println("Opened database successfully");

			stmt = c.createStatement();
			String sql = "CREATE TABLE if not exists " + HallID + " AS SELECT * FROM SMALL_HALL";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// System.out.println("Table created successfully");

	}

	/**
	 * 輸入張數，一般訂票
	 * @param Amount
	 */
	public boolean GeneralBook(int Amount) {
		Connection c = null;
		Statement stmt = null;
		Statement stmt2 = null;
		Statement stmt3 = null;
		boolean booked = false;
		try {
			boolean isbook = false;
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			stmt2 = c.createStatement();
			stmt3 = c.createStatement();
			ResultSet rs = null;
			ResultSet rs1 = stmt.executeQuery("SELECT COUNT(ID) FROM " + this.HallID + " WHERE occupied='false' ;");

			if (rs1.getInt(1) > Amount) {
				rs = stmt.executeQuery(
						"SELECT * FROM " + this.HallID + " WHERE occupied='false' LIMIT " + Amount + " ;");
				while (rs.next()) {
					String id = rs.getString("ID");

					stmt2.executeUpdate("UPDATE " + this.HallID + " SET occupied='true' WHERE ID='" + id + "';");

					System.out.println("Seat_ID = " + rs.getString("ID"));
					//System.out.println("ROW = " + rs.getString("ROW"));
					//System.out.println("SEATNUMBER = " + rs.getString("SEATNUMBER"));
					System.out.println("MovieName = " + this.MovieName);
					System.out.println("HALLNAME = " + this.Hallname);
					System.out.println("Starttime = " + this.StartTime);
					
					String seatnumber_s;
					if(rs.getInt("SEATNUMBER") < 10)
						seatnumber_s = "0" + rs.getString("SEATNUMBER");
					else
						seatnumber_s = Integer.toString(rs.getInt("SEATNUMBER"));
					System.out.println("Seat = " + rs.getString("ROW") + "_" + seatnumber_s);

					System.out.println("Ticket_ID = " + Ticket.Insert(MovieName, StartTime,
							rs.getString("ROW").charAt(0), rs.getInt("SEATNUMBER"), this.Hallname));
					System.out.println("-------------------------------");
				}
				c.commit();
				booked = true;
				isbook = true;
			}
			ResultSet rs2 = stmt3
					.executeQuery("SELECT COUNT(occupied) FROM " + this.HallID + " WHERE occupied='false' ;");
			System.out.println(this.MovieName + "在" + this.StartTime + "剩餘空位 " + rs2.getInt(1));
			if (isbook == false)
				throw new MovieException(rs.getString("ID") + "於" + rs.getString("starttime") + "座位數量不足");

			stmt2.close();
			stmt.close();
			c.close();
		} catch (MovieException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return booked;
	}

	/**
	 * 輸入指定座位訂票
	 * @param row
	 * @param seatnumber
	 */
	private void SeatNumberBook(String row, int seatnumber) {
		Connection c = null;
		Statement stmt = null;
		Statement stmt2 = null;
		Statement stmt3 = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			stmt2 = c.createStatement();
			stmt3 = c.createStatement();
			stmt.executeUpdate("UPDATE " + this.HallID + " SET occupied='true' WHERE row='" + row + "' AND seatnumber="
					+ seatnumber + ";");
			c.commit();
			ResultSet rs = stmt2.executeQuery(
					"Select * fROM " + this.HallID + " WHERE row='" + row + "' AND seatnumber=" + seatnumber + ";");

			System.out.println("Seat_ID = " + rs.getString("ID"));
			//System.out.println("ROW = " + rs.getString("ROW"));
			//System.out.println("SEATNUMBER = " + rs.getString("SEATNUMBER"));
			System.out.println("MovieName = " + this.MovieName);
			System.out.println("HALLNAME = " + this.Hallname);
			System.out.println("Starttime = " + this.StartTime);
			
			String seatnumber_s;
			if(rs.getInt("SEATNUMBER") < 10)
				seatnumber_s = "0" + rs.getString("SEATNUMBER");
			else
				seatnumber_s = Integer.toString(rs.getInt("SEATNUMBER"));
			System.out.println("Seat = " + rs.getString("ROW") + "_" + seatnumber_s);

			System.out.println("Ticket_ID = " + Ticket.Insert(MovieName, StartTime, rs.getString("ROW").charAt(0),
					rs.getInt("SEATNUMBER"), this.Hallname));
			ResultSet rs2 = stmt3
					.executeQuery("SELECT COUNT(occupied) FROM " + this.HallID + " WHERE occupied='false' ;");
			System.out.println(this.MovieName + "在" + this.StartTime + "剩餘空位 " + rs2.getInt(1));

			System.out.println("-------------------------------");
			stmt.close();
			stmt2.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * 利用座位ID退票
	 * @param ID
	 */
	public boolean Cancel(String ID) {
		Connection c = null;
		Statement stmt = null;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			stmt.executeUpdate("UPDATE " + this.HallID + " SET occupied='false' WHERE ID='" + ID + "';");
			c.commit();

			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return true;
	}

	/**
	 * 條件式訂票，輸入張數 與是否需要連續
	 * @param Amount
	 * @param cont
	 */
	public boolean ConditionBook(int Amount, boolean cont) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);
			String str2 = "";
			if (cont == true) {
				str2 = "SELECT R.ID ,R.row, R.seatnumber,Count(R.seatnumber) DAYS " + "FROM " + this.HallID + " R,"
						+ this.HallID + " E " + "WHERE  R.row = E.row AND R.occupied='false' AND E.occupied='false' "
						+ "AND R.seatnumber<=E.seatnumber And R.seatnumber+" + Amount + ">E.seatnumber "
						+ "GROUP BY R.row, R.seatnumber " + "HAVING Count(R.seatnumber)=" + Amount;

				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery(str2);
				if (rs.isClosed()) {
					System.out.println("座位數量不足");
					return false;
				}

				String row = rs.getString(2);
				int seatnumber = rs.getInt(3);

				/*
				 * while(rs.next()) { System.out.println( "ID = " +
				 * rs.getString(1) ); System.out.println( "ROW = " +
				 * rs.getString(2) ); System.out.println( "SEATNUMBER = " +
				 * rs.getString(3) );
				 * 
				 * System.out.println(); }
				 */

				stmt.close();
				c.commit();
				c.close();
				for (int i = 0; i < Amount; i++) {
					this.SeatNumberBook(row, seatnumber + i);
				}
			} else {
				this.GeneralBook(Amount);
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return false;
	}

	/**
	 * 判斷是否還有座位，輸入張數 與是否需要連續
	 * @param Amount
	 */
	public boolean SearchEmptySeat(int Amount, boolean cont) {

		return false;
	}

	/**
	 * 顯示訂位狀況
	 */
	public void Show() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + this.HallID + " ;");

			String row = "";

			while (rs.next()) {
				String oldrow = row;
				String id = rs.getString("id");
				String occupied = rs.getString("occupied");
				int seatnumber = rs.getInt("seatnumber");
				row = rs.getString("row");
				if (!row.equals(oldrow)) {
					System.out.println();
					System.out.print(row);

				}
				if (occupied.equals("false"))
					System.out.print(" O");
				else if (occupied.equals("null"))
					System.out.print("  ");
				else
					System.out.print(" X");

			}
			System.out.println();
			System.out.println("-------------------------------");
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println();

	}

	/**
	 * 輸入張數，判斷是否還有空座位
	 */
	public boolean SearchEmptySeat(int Amount, char row) {
		Connection c = null;
		Statement stmt = null;
		Statement stmt2 = null;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);
			// System.out.println("Opened database successfully");

			stmt = c.createStatement();
			stmt2 = c.createStatement();
			ResultSet rs = null;
			ResultSet rs1 = stmt.executeQuery(
					"SELECT COUNT(row) FROM " + this.HallID + " WHERE occupied='false' AND row='" + row + "'");

			// System.out.println(rs1.getString(1));

			int comp = rs1.getInt(1);
			stmt.close();
			c.close();
			if (comp >= Amount)
				return true;
			else
				return false;

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return false;

	}

	/**
	 * 測試的main funtion
	 * @param args
	 * @throws IOException
	 */
	/*public static void main(String[] args) throws IOException {
		String str = "R7XhLqnLWef6HU155ek0940";
		System.out.println("initial=" + Hall.InitialHall());
		// Hall A=new Hall("a3R47wXhjjLqnLWef6HU155ek0940_170601",true);

		SmallHall A = new SmallHall("AAA0950S", "R7XhLqnLWef6HU155ek0940", "09:30", "廳位");
		A.Show();
		System.out.println(A.SearchEmptySeat(8, 'A'));

		// A.ConditionBook(10, true);
		// A.RegionBook(3, "red");
		A.Show();

	}*/

}
