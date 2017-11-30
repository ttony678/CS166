-- SQL for Project 3

/*	SCHEMA
	Passenger	(pID int, passNum txt, fullName txt, bdate date, country txt)
	Flight		(airId int, flightNum txt, origin txt, destination txt, plane txt, seats int, duration int)
	Rating		(rID, int, pID int, flightNum txt, score int, comment txt)
	Booking		(bookRef txt, departure date, flightNum txt, pID int) */

/*	1. AddPassenger Function
	Add a new passenger into the database. You should provide an interface that takes as
	input the information of a new passenger (i.e. passport number, full name, birth date e.t.c)
	and checks if the provided information are valid based on the constrains of the database
	schema. */
	
	INSERT INTO Passenger (pID, passNum, fullName, bdate, country)
		VALUES (strPID, strPassNum, strFullName, strBdate, strcountry);
	
	
/*	2. BookFlight Function
	Book a flight for an existing passenger. This function will enable you to book a flight
	from a given origin to a given destination for an existing customer. You need to provide
	an interface that accepts the necessary information for booking a flight and checks if all
	inputs given by the user are valid based on the defined schema and the information stored
	in the database */
	
	INSERT INTO Booking (bookRef, departure, flightNum, pID)
		VALUES (strBookRef, strDeparture, strFlightNum, strPID);
	
	
/*	3. TakeCustomerReview Function
	This function will allow you, as a travel agent to note down the reviews of
	passengers. You should provide an interface that allows you to insert a new record of a
	rating for a given flight. Make sure to check for all the necessary constraints before
	performing the insert. */
	
	-- prompt customere if they'd like to leave review
	-- if so,
	--		get pID (as strPID)
	--		prompt cust for score,
	--		retrieve score (as strScore)
	--		prompt cust for comment,
	--		retrieve comment (as strComment)
	-- else,
	--		do nothing
	
	INSERT INTO Ratings (rID, pID, flightNum, score, comment)
		VALUES (strRID, strPID, strFlightNum, strScore, strComment);
	
	
	
/*	4. ListAvailableFlightsBetweenOriginAndDestination Function
	This function will allow you to list all available flights between two cities. A booking
	agent uses this information to make an informed decision when booking a given flight.
	You should print flight number, origin, destination, plane, and duration of flight. */
	
	SELECT	F.flightNum, F.origin, F.destination, F.plane, F.duration
	FROM 	Flight F
	WHERE	F.origin = strOrigin AND F.destination = str.Destination;
	
/*5. ListMostPopularDestinations Function
	This function will return a list of the k-most popular destinations depending on the
	number of flights offered to that specific destination. You should print out the name of
	the destination city and the number of distinct flights offered to that destination. The user
	should provide the value of k during runtime. */
	
	
/* 	6. ListHighestRatedRoutes Function
	This function will return a list of the k-highest rated routes based on the user ratings.
	You should print out the airline name, flight number, origin, destination, plane, and
	avg_score. The user should provide the value of k during runtime. */
	
	
/* 	7. ListFlightFromOriginToDestinationInOrderOfDuration Function
	This function will return a list of a k flights for a given origin and destination in order
	of duration. You should print the airline, flight number, origin, destination, plane, and
	duration. The user should give the value of k during runtime. */


/*	8. FindNumberOfAvailableSeatsForFlight
	Find the number of empty seats for a given flight on a given date. You should print flight
	number, origin, destination, departure date, booked seats, total number of seats, and
	number of available seats. */
	
	SELECT 	F.flightNum, F.origin, F.destination. B.departure, F.seats, 
			500 AS total seats, 500 - F.seats AS available seats
			
	FROM 	Flights F, Booking B
	WHERE 	F.flightNum = strFlightNum AND 
			B.flightNum = F.flightNum AND
			B.departure = strDeparture;
	
	