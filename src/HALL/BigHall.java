package HALL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.io.*;
import org.json.*;

import MOVIE.Movie;
import MOVIE.MovieException;
import MOVIE.Ticket;

/**
 * Class BigHall
 * 電影大廳
 * 繼承Hall
 */
public class BigHall extends Hall {


	/**
	 * constructor
	 * @param HallName
	 * @param MovieName
	 * @param StartTime
	 * @param Hallname
	 */
	public BigHall(String HallName, String MovieName, String StartTime, String Hallname) {
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
			String sql = "CREATE TABLE if not exists " + HallID + " AS SELECT * FROM BIG_HALL";
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
	 * 輸入張數一般訂票
	 */
	public boolean GeneralBook(int Amount) {
		Connection c = null;
		Statement stmt = null;
		Statement stmt2 = null;
		Statement stmt3 = null;
		boolean booked = false;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);
			// System.out.println("Opened database successfully");

			stmt = c.createStatement();
			stmt2 = c.createStatement();
			stmt3 = c.createStatement();

			ResultSet rs = null;
			ResultSet rs1 = stmt.executeQuery("SELECT REGION,COUNT(region) FROM " + this.HallID
					+ " WHERE occupied='false' GROUP BY REGION ORDER BY CASE REGION WHEN 'red' THEN 0 WHEN 'yellow' THEN 1 WHEN 'blue' THEN 2 WHEN 'gray' THEN 3 END;");

			boolean isbook = false;

			while (rs1.next()) {

				if (rs1.getInt(2) > Amount) {
					rs = stmt.executeQuery("SELECT * FROM " + this.HallID + " WHERE occupied='false' AND region='"
							+ rs1.getString(1) + "' LIMIT " + Amount + " ;");
					while (rs.next()) {
						String id = rs.getString("ID");

						stmt2.executeUpdate("UPDATE " + this.HallID + " SET occupied='true' WHERE ID='" + id + "';");

						System.out.println("Seat_ID = " + rs.getString("ID"));
						//System.out.println("ROW = " + rs.getString("ROW"));
						//System.out.println("SEATNUMBER = " + rs.getString("SEATNUMBER"));
						System.out.println("REGION = " + rs.getString("REGION"));
						String seatnumber_s;
						System.out.println("MovieName = " + this.MovieName);
						System.out.println("HALLNAME = " + this.Hallname);
						System.out.println("Starttime = " + this.StartTime);
						
						if(rs.getInt("SEATNUMBER") < 10)
							seatnumber_s = "0" + rs.getString("SEATNUMBER");
						else
							seatnumber_s = Integer.toString(rs.getInt("SEATNUMBER"));
						System.out.println("Seat = " + rs.getString("ROW") + "_" + seatnumber_s);

						
						System.out.println("Ticket_ID = " + Ticket.Insert(MovieName, StartTime,
								rs.getString("ROW").charAt(0), rs.getInt("SEATNUMBER"), this.Hallname));
						System.out.println("-------------------------------");
						isbook = true;
					}
					c.commit();
					booked = true;

					break;
				}
			}
			ResultSet rs2 = stmt3
					.executeQuery("SELECT COUNT(occupied) FROM " + this.HallID + " WHERE occupied='false' ;");
			System.out.println(this.MovieName + "在" + this.StartTime + "剩餘空位 " + rs2.getInt(1));
			System.out.println();
			// System.out.println(isbook);
			if (isbook == false) {
				throw new MovieException(this.MovieName + "於" + this.StartTime + "座位數量不足");
			}
			stmt2.close();
			stmt.close();
			c.close();
		} catch (MovieException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// System.out.println("Operation done successfully");

		return booked;
	}

	/**
	 * 輸入張數 位置區域訂票
	 * @param Amount
	 * @param region
	 * @return boolean 電影訂票是否成功
	 */
	public boolean RegionBook(int Amount, String region) {
		Connection c = null;
		Statement stmt = null;
		Statement stmt2 = null;
		Statement stmt3 = null;

		try {
			boolean isbook = false;
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);
			// System.out.println("Opened database successfully");

			stmt = c.createStatement();
			stmt2 = c.createStatement();
			ResultSet rs = null;
			ResultSet rs1 = stmt.executeQuery(
					"SELECT COUNT(region) FROM " + this.HallID + " WHERE occupied='false' AND region= "+region+" ;");

			// System.out.println(rs1.getString(1));

			if (rs1.getInt(1) > Amount) {
				rs = stmt.executeQuery("SELECT * FROM " + this.HallID + " WHERE occupied='false' AND region='" + region
						+ "' LIMIT " + Amount + " ;");
				while (rs.next()) {
					String id = rs.getString("ID");
					System.out.println(id);
					
					stmt2.executeUpdate("UPDATE " + this.HallID + " SET occupied='true' WHERE ID='" + id + "';");

					System.out.println("Seat_ID = " + rs.getString("ID"));
					//System.out.println("ROW = " + rs.getString("ROW"));
					//System.out.println("SEATNUMBER = " + rs.getString("SEATNUMBER"));
					System.out.println("REGION = " + rs.getString("REGION"));
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
				isbook = true;
				c.commit();

			}
			ResultSet rs2 = stmt3
					.executeQuery("SELECT COUNT(occupied) FROM " + this.HallID + " WHERE occupied='false' ;");
			System.out.println(this.MovieName + "在" + this.StartTime + "剩餘空位 " + rs2.getInt(1));
			System.out.println();
			if (isbook == false)
				throw new MovieException("電影" + this.MovieName + "於" + this.StartTime + "座位數量不足");

			stmt2.close();
			stmt.close();
			c.close();
		} catch (MovieException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// System.out.println("Operation done successfully");

		return true;
	}

	/***
	 * 指定座位訂票
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
			System.out.println("REGION = " + rs.getString("REGION"));
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
			System.out.println();
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
	 * 取消訂位
	 * @param ID 座位ID
	 */
	public boolean Cancel(String ID) {
		Connection c = null;
		Statement stmt = null;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			stmt.executeUpdate("UPDATE " + this.HallID + " SET occupied='false' WHERE ID='" + ID + "';");
			c.commit();

			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// System.out.println("Operation done successfully");

		return true;
	}

	/**
	 * 輸入張數 區域定位 與是否需要連續
	 * @param Amount
	 * @param cont
	 * @param region
	 */
	public boolean ConditionBook(int Amount, boolean cont, String region) {
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
						+ "AND R.region='" + region + "' AND E.region='" + region + "' "
						+ "AND R.seatnumber<=E.seatnumber And R.seatnumber+" + Amount + ">E.seatnumber "
						+ "GROUP BY R.row, R.seatnumber " + "HAVING Count(R.seatnumber)=" + Amount;

				/*str2 = "SELECT R.ID ,R.row, R.seatnumber,Count(R.seatnumber) DAYS " + "FROM " + this.HallID + " R,"
						+ this.HallID + " E " + "WHERE  R.row = E.row AND R.occupied='false' AND E.occupied='false' "
						+ "AND R.region='" + region + "' AND E.region='" + region + "' "
						+ "AND R.seatnumber<=E.seatnumber And R.seatnumber+" + Amount + ">E.seatnumber "
						+ "GROUP BY R.row, R.seatnumber " + "HAVING Count(R.seatnumber)=" + Amount;*/
				
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery(str2);
				if (rs.isClosed()) {
					System.out.println("座位數量不足");
					return false;
				}

				// System.out.println( "row = " + rs.getString(2) );
				String row = rs.getString(2);
				// System.out.println( "seatnumber = " + rs.getString(3) );
				int seatnumber = rs.getInt(3);

				stmt.close();
				c.commit();
				c.close();
				for (int i = 0; i < Amount; i++) {
					this.SeatNumberBook(row, seatnumber + i);
				}
			} else {
				this.RegionBook(Amount, region);
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return false;
	}

	/**
	 * 條件式訂票 輸入張數與是否需要連續
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
				 * rs.getString(3) ); System.out.println( "REGION = " +
				 * rs.getString(4) );
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
	 * 顯示座位訂位狀況
	 */
	public void Show() {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Hall.db");
			c.setAutoCommit(false);
			// System.out.println("Opened database successfully");

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + this.HallID + " ;");

			String row = "";

			while (rs.next()) {
				String oldrow = row;
				String id = rs.getString("id");
				String occupied = rs.getString("occupied");
				String region = rs.getString("region");
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
			System.out.println("---------------------------------------------------------");
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println();
		// System.out.println("Operation done successfully");

	}

	/**
	 * 搜尋指定位置指定電影票張數的空位
	 * @param Amount
	 * @param row
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
	 * 測試的main function
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {

		//String str = "R7XhLqnLWef6HU155ek0940";
		//Hall.InitialHall();
		// Hall A=new Hall("a3R47wXhjjLqnLWef6HU155ek0940_170601",true);
		// BigHall A=new BigHall("AAA0950");
		// A.Show();
		// System.out.println(A.GeneralBook(11));

		// A.ConditionBook(10, true,"gray");
		// A.RegionBook(3, "red");
		// A.Show();
	}
	
	@Override
	/**
	 * 查詢是否有空位 且是否連續
	 * @param Amount
	 * @param cont
	 */
	public boolean SearchEmptySeat(int Amount, boolean cont) {
		return false;
	}
}
