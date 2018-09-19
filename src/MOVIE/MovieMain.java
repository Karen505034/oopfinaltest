package MOVIE;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import HALL.*;
//import javafx.scene.layout.Region;
import HALL.*
;/**
 * Class MovieMain
 * 使用此專案的函數
 * 
 *
 */
public class MovieMain {

	/**
	 * main function
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Movie.Creat_Mov_Db();
		Hall.InitialHall();
		Ticket.InitializeDB();
		User.Setuser();
		boolean RepeatUser = true;
		boolean RepeatBooking = true;
		boolean RepeatSearch = true;
		User Consumer;

		while (RepeatUser) {
			System.out.println("Welcome to the Movie system, what would you like to do? ");
			System.out.println("1.Booking 2.Searching 3.Cancel?");
			Scanner scanner = new Scanner(System.in);
			int Mode = 0;
			Mode = scanner.nextInt();
			if (Mode == 2) {   // 查詢
				while (RepeatSearch) {
					System.out.println("---------------Search Mode------------------");
					System.out.println(
							"1.Judgement 2.Movie Time or Length 3.Tiket_ID 4.Movie_ID 5.Region/Row Empty ,or 0 to Main Page");
					int SearchMode = scanner.nextInt();
					switch (SearchMode) {
					case 1:
						SJudgement();
						break;
					case 2:
						SMovieTimeLength();
						break;
					case 3:
						STicketID();
						break;
					case 4:
						SMovieID();
						break;
					case 5:
						SRegionRow();
						break;
					case 0:
						RepeatSearch = false;
						break;
					}

				}
			} else if(Mode == 1) {   // 訂票
				/*
				 * System.out.
				 * println("Please enter Consumer's LastName and Age (with space between) "
				 * ); Scanner scanner = new Scanner(System.in);
				 * System.out.printf("Age = "); int age=scanner.nextInt();
				 * System.out.printf("Name = "); String name=scanner.next();
				 * 
				 * if(age>0&&age<150) Consumer=new User(age,name); else
				 * continue;
				 */
				while (RepeatBooking) {
					System.out.println("-----------------------------");
					System.out.println("user_index:");
					int user_index = scanner.nextInt();

					System.out.println("Movie to play today:");
					int i = 1;
					ArrayList<String> MovieList = Movie.ShowAll();
					for (String e : Movie.ShowAll()) {
						System.out.println(i + "." + e);
						i++;
					}

					System.out.printf("Which movie?  ");
					int Choice = scanner.nextInt();
					if (Choice > MovieList.size()) {
						System.out.println("No such Movie! ");
						continue;
					}
					String MovieName = MovieList.get(Choice - 1);
					String[] split = MovieName.split("/");
					MovieName = split[0];
					// System.out.println("Movie! "+MovieName);

					String clas = Movie.SearchMovieName(MovieName);
					int user_age = User.SearchIndex(user_index);
					boolean can_book = false;
					switch (clas) {
					case ("限制"):
						if (user_age >= 18)
							can_book = true;
						break;
					case ("保護"):
						if (user_age >= 6)
							can_book = true;
						break;
					case ("輔導"):
						if (user_age >= 15)
							can_book = true;
						break;
					default:
						can_book = true;

					}
					try {
						if (can_book) {
							ArrayList<String> Session = Movie.ShowAllTime(MovieName);
							System.out.println(MovieName + "1");
							if (Session.size() == 0) {
								System.out.println("No session today ");
								continue;
							}
							System.out.println("Sessions today ");
							i = 1;
							for (String e : Session) {
								System.out.println(i + ". " + e);
								i++;
							}
							System.out.printf("Which session? ");
							int SessionChoice = scanner.nextInt();
							if (SessionChoice > Session.size()) {
								System.out.println("Not in Range ");
								continue;
							}
							String ChoiceTime = Session.get(SessionChoice - 1);
							Time MovieTime = new Time();
							MovieTime.StringtoTime(ChoiceTime);
							Movie Mov = new Movie(MovieName, MovieTime);

							Mov.getHall().Show();

							System.out.printf("How many people? ");
							int Amounts = scanner.nextInt();
							System.out.println();
							System.out.printf("Sit together? Y/N ");
							String Cont = scanner.next();
							System.out.println();
							String Region;
							if (Mov.isIsbig()) {
								System.out.printf("Specific Region? N/Red/Yellow/Blue/Gray ");
								Region = scanner.next();
								boolean cont = false;
								if (Cont.charAt(0) == 'N' || Cont.charAt(0) == 'n'){
									if(Region.charAt(0) == 'N' || Region.charAt(0) == 'n')
										Mov.getHall().GeneralBook(Amounts);
									else
										Mov.getHall().RegionBook(Amounts, Region);
								}
									
								else if (Cont.charAt(0) == 'Y' || Cont.charAt(0) == 'y') {
									cont = true;
									if (Region.charAt(0) == 'N' || Region.charAt(0) == 'n')
										Mov.getHall().ConditionBook(Amounts, cont);
									else
										Mov.getHall().ConditionBook(Amounts, cont, Region);
								}
							} else {
								boolean cont = false;
								if (Cont.charAt(0) == 'N' || Cont.charAt(0) == 'n')
									Mov.getHall().GeneralBook(Amounts);
								else if (Cont.charAt(0) == 'Y' || Cont.charAt(0) == 'y') {
									Mov.getHall().ConditionBook(Amounts, cont);
								}
							}

							Mov.getHall().Show();

							RepeatBooking = false;
							RepeatUser = false;
						} else
							throw new MovieException("失敗，該電影分級為" + clas + "，" + user_age + "歲無法購買");
					} catch (MovieException e) {
						System.out.println(e.getMessage());
					}

				}

			}
			else{ // 退票
				System.out.println("Ticket ID?");
				int ticket_id = scanner.nextInt();
				Ticket.Delete(ticket_id);
			}

		}
	}

	/**
	 * 查詢評價
	 */
	static void SJudgement() {
		System.out.println("Input the score you wish:");
		Scanner scanner = new Scanner(System.in);
		int Score = scanner.nextInt();
		Movie.Higher_Score(Score);
	}

	/**
	 * 查詢符合電影時間要求與放映長度要求的電影
	 */
	static void SMovieTimeLength() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Input the Seats you want:");
		int Seats = scanner.nextInt();
		System.out.println("Input the Movie Time you want,");
		System.out.println("From:");
		String Time_1 = scanner.next();
		System.out.println("to:");
		String Time_2 = scanner.next();
		Time time_1 = new Time(Time_1);
		Time time_2 = new Time(Time_2);
		System.out.println("最大片長 (分鐘)");
		int length = scanner.nextInt();
		Movie.Time_Len_Search(time_1, time_2, length);

	}

	/**
	 * 利用Ticket ID查詢電影票的資料
	 */
	static void STicketID() {
		System.out.println("Input the Ticket ID:");
		Scanner scanner = new Scanner(System.in);
		int ID = scanner.nextInt();
		Ticket.Search(ID);
	}

	/**
	 * 利用電影ID查詢電影資料
	 */
	static void SMovieID() {
		System.out.println("Input the Movie ID you want:");
		Scanner scanner = new Scanner(System.in);
		String ID = scanner.next();
		Movie.SearchMovieID(ID);

	}

	/**
	 * 查詢座位資料
	 */
	static void SRegionRow() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Input the Amount of Seats you want:");
		int Seats = scanner.nextInt();
		System.out.println("Input the Row you want,");
		String R = scanner.next();
		
		char Row = R.charAt(0);
		Row = Character.toUpperCase(Row);
		Movie.setmov_arr();
		ArrayList<Movie> MovieArrList = Movie.initial_allmovie;
		for (Movie e : MovieArrList) {
			if (e.getHall().SearchEmptySeat(Seats, Row) == true)
				if(e.getStarttime().isEarly(new Time(Movie.getDateTime()))!=true)
						System.out.println(e.getId() + " " + e.getStarttime_s() + " (" + e.getMovie_name() + ")");
		}
	}
}
