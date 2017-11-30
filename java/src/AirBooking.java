/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class AirBooking{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public AirBooking(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + AirBooking.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		AirBooking esql = null;
		
		try{
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new AirBooking (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Passenger");
				System.out.println("2. Book Flight");
				System.out.println("3. Review Flight");
				System.out.println("4. List Flights From Origin to Destination");
				System.out.println("5. List Most Popular Destinations");
				System.out.println("6. List Highest Rated Destinations");
				System.out.println("7. List Flights to Destination in order of Duration");
				System.out.println("8. Find Number of Available Seats on a given Flight");
				System.out.println("9. < EXIT");
				
				switch (readChoice()){
					case 1: AddPassenger(esql); break;
					case 2: BookFlight(esql); break;
					case 3: TakeCustomerReview(esql); break;
					case 4: ListAvailableFlightsBetweenOriginAndDestination(esql); break;
					case 5: ListMostPopularDestinations(esql); break;
					case 6: ListHighestRatedRoutes(esql); break;
					case 7: ListFlightFromOriginToDestinationInOrderOfDuration(esql); break;
					case 8: FindNumberOfAvailableSeatsForFlight(esql); break;
					case 9: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
    }//end readChoice
    
    /*************************DATABASE FUNCTIONS*******************************/
    
    // USER INPUT DONE
	public static void AddPassenger(AirBooking esql){//1
        //Add a new passenger to the database
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String passport;
            do {
                System.out.print("Enter your 10-character Passport Number: ");
                passport = br.readLine();
                if (passport.length() != 10) {
                    System.out.println("Invalid length, all Passport Numbers must be 10 characters long.");
                }
                else {
                    break;
                }
            } while (true);

            System.out.print("Enter your full name: ");
            String fullName = br.readLine();

            int m;
			do { 
                System.out.print("Enter your birth month: ");
				String month = br.readLine();
				m = Integer.parseInt(month);
				if (m < 1 || m > 12) {
					System.out.println("\nPlease enter a value between 1 and 12.\n");	
				}
			} while (m < 1 || m > 12);
			
			int d;
			do { 
                System.out.print("Enter your birth day: ");
				String day = br.readLine();
				d = Integer.parseInt(day);
				if (d < 1 || d > 31) {
					System.out.println("\nPlease enter a value between 1 and 31.\n");	
				}
			} while (d < 1 || d > 31);
	
			int y;
			do { 
                System.out.print("Enter your birth year: ");
				String year = br.readLine();
				y = Integer.parseInt(year);
				if (y < 1900 || y > 2020) {
					System.out.println("\nPlease enter a value greater than 1900 and less than 2020.\n");
				}
			} while (y < 1900 || y > 2020);

			String date = m + "/" + d + "/" + y;

            System.out.print("Enter your country of origin:  ");
            String country = br.readLine();

            String query = "";
			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println("total row(s): " + rowCount);
			if (rowCount == 0) {
				System.out.println();
			}

		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
    
    // USER INPUT DONE. Confused about bookRef
	public static void BookFlight(AirBooking esql){//2
        //Book Flight for an existing customer
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the booking reference number: ");
            String bookRef = br.readLine();

            int m;
			do { 
                System.out.print("Enter the departure month: ");
				String month = br.readLine();
				m = Integer.parseInt(month);
				if (m < 1 || m > 12) {
					System.out.println("\nPlease enter a value between 1 and 12.\n");	
				}
			} while (m < 1 || m > 12);
			
			int d;
			do { 
                System.out.print("Enter the departure day: ");
				String day = br.readLine();
				d = Integer.parseInt(day);
				if (d < 1 || d > 31) {
					System.out.println("\nPlease enter a value between 1 and 31.\n");	
				}
			} while (d < 1 || d > 31);
	
			int y;
			do { 
                System.out.print("Enter the departure year: ");
				String year = br.readLine();
				y = Integer.parseInt(year);
				if (y < 1900 || y > 2020) {
					System.out.println("\nPlease enter a value greater than 1900 and less than 2020.\n");
				}
			} while (y < 1900 || y > 2020);

            String date = m + "/" + d + "/" + y;
            
            System.out.print("Enter the flight number: ");
            String flightNum = br.readLine();

            System.out.print("Enter the passenger ID: ");
            String pID = br.readLine();


        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
	}
    
    // USER INPUT DONE. Confused about rID
	public static void TakeCustomerReview(AirBooking esql){//3
        //Insert customer review into the ratings table
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter the rating ID: ");
            String rID = br.readLine();

            System.out.print("Enter the passenger ID: ");
            String pID = br.readLine();

            System.out.print("Enter the flight number: ");
            String flightNum = br.readLine();

            String score;
            do {
                System.out.print("Enter a score on a scale from 0-5: ");
                try {
                    score = br.readLine();
                    int s = Integer.parseInt(score);
                    if (s < 0 || s > 5) {
                        System.out.print("Invalid input\n");
                        continue;
                    }
                    else {
                        break;
                    }

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            } while(true);

            System.out.print("Enter a comment: ");
            String comment;

            String query;


        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
	}
    
    // EXTRA CREDIT
	public static void InsertOrUpdateRouteForAirline(AirBooking esql){//4
        //Insert a new route for the airline
        try {
            
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
	}
    
    // DONE
	public static void ListAvailableFlightsBetweenOriginAndDestination(AirBooking esql) throws Exception{//5
		//List all flights between origin and distination (i.e. flightNum,origin,destination,plane,duration) 
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter an origin: ");
			String origin = br.readLine();

			System.out.print("Enter a destination: ");
			String destination = br.readLine();

			String query = "SELECT F.flightNum, F.origin, F.destination, F.plane, F.duration ";
			query += "FROM Flight F ";
			query += "WHERE F.origin = '" + origin + "' ";
			query += "AND F.destination = '" + destination + "';";

			int rowCount = esql.executeQueryAndPrintResult(query);
			// System.out.println("total row(s): " + rowCount);
			if (rowCount == 0) {
				System.out.println("There are no flights between " + origin + " and " + destination + ".");
			}

		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
    
    // DONE
	public static void ListMostPopularDestinations(AirBooking esql){//6
		//Print the k most popular destinations based on the number of flights offered to them (i.e. destination, choices)
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Please enter the number of results you would like to see: ");
			String results = br.readLine();

			String query = "SELECT COUNT(F.destination), F.destination ";
			query += "FROM Flight F ";
			query += "GROUP BY F.destination ";
			query += "ORDER BY COUNT(F.destination) DESC ";
			query += "LIMIT " + results + ";";

			int rowCount = esql.executeQueryAndPrintResult(query);
			// System.out.println("total row(s): " + rowCount);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
    
    // TODO needs to be tested
	public static void ListHighestRatedRoutes(AirBooking esql){//7
        //List the k highest rated Routes (i.e. Airline Name, flightNum, Avg_Score)
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please enter the number of results you would like to see: ");
            String results = br.readLine();

            String query = "SELECT A.name, F.flightNum, F.origin, F.destination, ";
            query += "F.plane, AVERAGE(R.score) ";
            query += "FROM Airline A, Flight F, Ratings R ";
            query += "WHERE A.airId = F.airId AND F.flightNum = R.flightNum ";
            query += "GROUP BY F.destination ";
            query += "ORDER BY AVERAGE(R.score) DESC ";
            query += "LIMIT " + results + ";";

            int rowCount = esql.executeQueryAndPrintResult(query);
            // System.out.println("total row(s): " + rowCount);
        } catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void ListFlightFromOriginToDestinationInOrderOfDuration(AirBooking esql){//8
        //List flight to destination in order of duration (i.e. Airline name, flightNum, origin, destination, duration, plane)
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter an origin: ");
			String origin = br.readLine();

			System.out.print("Enter a destination: ");
            String destination = br.readLine();
            
            System.out.print("Please enter the number of results you would like to see: ");
            String results = br.readLine();


        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
	}
	
	public static void FindNumberOfAvailableSeatsForFlight(AirBooking esql){//9
		//
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter a flight number: ");
			String flightNum = br.readLine();
			
			int m;
			do { 
                System.out.print("Enter a departure month: ");
				String month = br.readLine();
				m = Integer.parseInt(month);
				if (m < 1 || m > 12) {
					System.out.println("\nPlease enter a value between 1 and 12.\n");
				}
			} while (m < 1 || m > 12);
			
			int d;
			do { 
                System.out.print("Enter a departure day: ");
				String day = br.readLine();
				d = Integer.parseInt(day);
				if (d < 1 || d > 31) {
					System.out.println("\nPlease enter a value between 1 and 31.\n");	
				}
			} while (d < 1 || d > 31);
	
			int y;
			do { 
                System.out.print("Enter a departure year: ");
				String year = br.readLine();
				y = Integer.parseInt(year);
				if (y < 1900 || y > 2020) {
					System.out.println("\nPlease enter a value greater than 1900 and less than 2020.\n");	
				}
			} while (y < 1900 || y > 2020);

			String date = m + "/" + d + "/" + y;
			System.out.println(date);

			String query = "SELECT DISTINCT F.flightNum, F.origin, F.destination, B.departure, ";
			query += "F.seats AS \"total seats\" ";
			query += "FROM Booking B, Flight F ";
			query += "WHERE F.flightNum = '" + flightNum + "' AND ";
			query += "B.flightNum = F.flightNum AND ";
			query += "B.departure = '" + date + "' ";
			//query += "GROUP BY B.departure";

			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println("total row(s): " + rowCount);
			// if (rowCount == 0) {
			// 	System.out.println("There are no flights between " + flightNum + " and " + destination + ".");
			// }

		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
	}
}
