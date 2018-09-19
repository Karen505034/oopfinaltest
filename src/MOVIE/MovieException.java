package MOVIE;

/**
 * Class MovieException
 * 接訂票 退票 查詢可能會出的錯
 *
 */
public class MovieException extends Exception {
	/**
	 * Constructor
	 * @param message 錯誤的訊息
	 */
	public MovieException(String message){
		super(message);
	}
}
