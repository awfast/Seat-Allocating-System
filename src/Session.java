import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Session {
	
	protected Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs;
	private Statement stmt2;
	private Statement stmt3;
	private int lastDayOfTheMonth = 0;

	public void printDate(String dateFrom, String dateTo) {
		String[] fromDates = dateFrom.split("/");
		String[] toDates = dateTo.split("/");
		String fromDay = fromDates[0];
		String fromMonth = fromDates[1];
		String fromYear = fromDates[2];
		String toDay = toDates[0];
		String toMonth = toDates[1];
		String toYear = toDates[2];
		
		int fD = Integer.valueOf(fromDay);
		int fM = Integer.valueOf(fromMonth);
		int fY = Integer.valueOf(fromYear);
		int toD = Integer.valueOf(toDay);
		int toM = Integer.valueOf(toMonth);
		int toY = Integer.valueOf(toYear);
		getLastDayOfTheMonth(fM, fY);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		String examFromDay1 = new StringBuilder().append(fD).append("/").append(fM).append("/").append(fY).toString();
		String examToDay1 = new StringBuilder().append(toD).append("/").append(toM).append("/").append(toY).toString();
		long sessionN = 0;
		try {
			Date date = sdf.parse(dateFrom);
			Date dat1 = sdf.parse(examFromDay1);
			Date dat2 = sdf.parse(examToDay1);

			long mili1 = dat1.getTime();
			long mili2 = dat2.getTime();

			//calculate the difference in millisecond between two dates
			long diffInMilli = mili2-mili1;
			long diffInDays = diffInMilli / (24* 60 * 60 * 1000);
			sessionN = diffInDays;
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		try {
			for (int i = 0; i < sessionN; i++) {
				String examFromDay = new StringBuilder().append(fD).append("/").append(fM).append("/").append(fY).toString();
				String examToDay = new StringBuilder().append(toD).append("/").append(toM).append("/").append(toY).toString();
				int y = i++;
				int x=i;
				int z = x++;
				Date date = sdf.parse(dateFrom);
				Date dat1 = sdf.parse(examFromDay);
				Date dat2 = sdf.parse(examToDay);
				
				System.out.println("Diff in Days "+sessionN);
				//System.out.println(dat2);

				String[] splitDate1 = dat1.toString().split(" ");
				String dayOfTheWeek1 = splitDate1[0];
				String dayOfTheMonth1 = splitDate1[1];
				System.out.println(dayOfTheWeek1 + dayOfTheMonth1);
				System.out.println("dayOfTheWeek1 " + dayOfTheWeek1);
				System.out.println("test" + fD);
				String[] splitDate2 = dat2.toString().split(" ");
				String dayOfTheWeek2 = splitDate2[0];
				String dayOfTheMonth2 = splitDate2[1];
				System.out.println(dayOfTheMonth1);
				System.out.println(dayOfTheMonth2);
				if (fD == lastDayOfTheMonth) {
					if(x==0){
						return;
					} else {
						if(fM == 12) {
							fM = 1;
						} else {
							lastDayOfTheMonth = 1;
							fD=lastDayOfTheMonth;
							i--;
							z--;
							System.out.println("here------");
						}
					}
				}
				else if(fD == 1) {
					int o = i+1;
					int specialCase = fD;
					String firstOfTheMonth = new StringBuilder().append(specialCase).append("/").append(fM).append("/").append(fY).toString();
					System.out.println("here");
					System.out.println(y + "-Y");
					String am = "am";
					String pm = "pm";
					String insertSessionsAm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + o + "', + '"+ firstOfTheMonth + "', + '" + am + "')";
					String insertSessionsPm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + z + "', + '"+ firstOfTheMonth + "', + '" + pm + "')";

					stmt2 = conn.createStatement();
					stmt3 = conn.createStatement();
					stmt2.executeUpdate(insertSessionsAm);
					stmt3.executeUpdate(insertSessionsPm);
					fD++;
					System.out.println("Inserting into SESSION.." + "[" + i + "]" + "[" + firstOfTheMonth + "]" + "[" + am + "]");
					System.out.println("Inserting into SESSION.." + "[" + y + "]" + "[" + firstOfTheMonth + "]" + "[" + pm + "]");
					System.out.println("now here-------");
				} else if(dayOfTheWeek1.equals("Sat") || dayOfTheWeek1.equals("Sun")) {
					if (dayOfTheWeek1.equals("Sat")) {
						fD = fD+2;
						i--;
						y--;
						System.out.println("Skipping Saturday");
						System.out.println("....");
					}
					else if(dayOfTheWeek1.equals("Sun")) {
						fD++;
						i--;
						y--;
						System.out.println("Skipping Sunday");
						System.out.println("!!!!!!!!");
					}
					}
					else {
						y = i + 1;
						System.out.println(y + "-Y");
						String am = "am";
						String pm = "pm";
						String insertSessionsAm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + i + "', + '"+ examFromDay + "', + '" + am + "')";
						String insertSessionsPm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + y + "', + '"+ examFromDay + "', + '" + pm + "')";
	
						stmt2 = conn.createStatement();
						stmt3 = conn.createStatement();
						stmt2.executeUpdate(insertSessionsAm);
						stmt3.executeUpdate(insertSessionsPm);
						fD++;
						System.out.println("Inserting into SESSION.." + "[" + i + "]" + "[" + examFromDay + "]" + "[" + am + "]");
						System.out.println("Inserting into SESSION.." + "[" + y + "]" + "[" + examFromDay + "]" + "[" + pm + "]");
						System.out.println("--------");
					}
				}
		} catch (ParseException e) {
		} catch (SQLException e) {
			System.out.println(e);
		}
		finally {
			String asd = "SELECT * FROM SESSION";
			try {
				rs = stmt.executeQuery(asd);
				rs.afterLast();
				while(rs.previous()){
				String id = rs.getString("ID");
				String lastSession = rs.getString("MorningAfternoon");
				String lastExam = rs.getString("date");
				System.out.println("Last session" + lastSession);
					if(lastSession.equals("am")) {
						lastSession = "am";
						int i = Integer.valueOf(id);
						i++;
						String insertSessionsAm = "INSERT INTO SESSION(ID, Date, MorningAfternoon) VALUES ('" + i + "', + '"+ lastExam + "', + '" + lastSession + "')";
						stmt3 = conn.createStatement();
						stmt3.executeUpdate(insertSessionsAm);
						
					}
					else {
						return;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void getLastDayOfTheMonth(int month, int year) {
		   Calendar calendar = Calendar.getInstance();
		    // passing month-1 because 0-->jan, 1-->feb... 11-->dec
		    calendar.set(year, month - 1, 1);
		    calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
		    Date date = calendar.getTime();
		    DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
		    String[] splitDate = DATE_FORMAT.format(date).split("/");
		    String lastDayStr = splitDate[0];
		    int lastDayOfTheMonth = Integer.valueOf(lastDayStr);
		    this.lastDayOfTheMonth = lastDayOfTheMonth;
	}
}
