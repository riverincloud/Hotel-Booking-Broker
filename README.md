HotelBookingBroker
==================

The Hotel Booking Broker is an online system that allows users to search for, compare rates, check availability and make bookings at three fictional hotels connected to the service.

-
The system is a typical 3-tier system with presentation, logic and database handled separately. It implements a client and multiple servers using Transmission Control Protocol (TCP).

The system, implementing multiple servers, is capable of handling three hotel servers, two in one city and one in another, as well as at least two clients concurrently. 

-
The clients use a text-based interface, reading from System.in and writing to System.out.

-
The client is able to make the following requests to the Hotel Booking Broker server: 

•	What cities are currently serviced by the system? 

•	For a given city, what hotels are currently serviced by the system? 

•	For a particular hotel, what is the room rate?

•	For a particular hotel, does it have a vacancy between given check-in and check-out dates?

•	Book a room at a hotel. 

-
The Hotel Booking Broker server will forward certain requests (such as rates, vacancies etc.) directly to the hotel's server rather than maintaining information on its own server. 

-
All databases are managed by a Java DB (Derby) database in NetBeans IDE.
