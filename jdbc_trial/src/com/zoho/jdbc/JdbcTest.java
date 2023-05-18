package com.zoho.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class JdbcTest 
{
	private static Connection connection = null; //Connection is an Interface, static since we need one connection for whole pjt
	private static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) 
	{
		try{  
			Class.forName("com.mysql.cj.jdbc.Driver");  //load & register the driver
			
			String dbURL = "jdbc:mysql://localhost:3306/NIRMALA";
			String username = "root";
			String password = "";
			connection = DriverManager.getConnection(dbURL,username,password);  //establised the connection
			//here NIRMALA is database name, root is username and no password 
			
			int choice;
			char c='Y';
			
			do
			{
				System.out.println("1. Insert record");
				System.out.println("2. Select a Record");
				System.out.println("3. Select all Records");
				System.out.println("4. Updatable ResultSet");
				System.out.println("5. Quit");
				System.out.println("Enter your choice: ");
				choice=sc.nextInt();
				
				switch (choice)
				{
				case 1:
					insertRecord();
					break;
				case 2:
					selectRecord();
					break;
				case 3:
					selectAllRecord();
					break;
				case 4:
					updatableResultSet();
					break;
				case 5:
					c='N';
					break;
				default:
					System.out.println("Wrong Choice ");
					break;
				}
			}while(c=='Y');
			System.out.println("Goodbye....");
			connection.close();  
			}
		catch(Exception e)
		{
			System.out.println(e);
		}  

	}
	
	private static void updatableResultSet()
	{
		try 
		{
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			String sql = "SELECT * FROM STUDENTS";
			ResultSet rs = statement.executeQuery(sql);
			
			int recordNo;
			
			System.out.println("What to update in Result Set?");
        	System.out.println("1. Delete last row");
        	System.out.println("2. Delete first row");
        	System.out.println("3. Delete specified row");
        	System.out.println("4. Update course code & fee at specified row");
        	System.out.println("5. Insert new record");
        	int option=sc.nextInt();
        	
        	switch (option) 
        	{
        	case 1:
        		rs.last(); //cursor moves to last row in the table
        		rs.deleteRow(); //deleted in result set & database
        		System.out.println("deleted....");
        		selectAllRecord();
        		break;
        	case 2:
        		rs.first();
        		rs.deleteRow();
        		System.out.println("deleted....");
        		selectAllRecord();
        		break;
        	case 3:
        		System.out.println("Enter record no. to delete: ");
        		recordNo = sc.nextInt();
        		rs.absolute(recordNo);
        		rs.deleteRow();
        		System.out.println("deleted....");
        		selectAllRecord();
        		break;
        	case 4:
        		System.out.println("Enter record no. to update fee: ");
        		recordNo = sc.nextInt();
        		System.out.println("Enter new course code: ");
        		String newCourse = sc.next();
        		System.out.println("Enter new fee: ");
        		int newFee = sc.nextInt();
        		
        		rs.absolute(recordNo); // go to specified record no.
        		rs.updateString(9, newCourse); //update course name 9=>column no.
        		rs.updateInt(8, newFee); //update fee
        		rs.updateRow(); //new contents updated in database
        		System.out.println("updated....");
        		selectAllRecord();
        		break;
        		
        	case 5:
        		rs.moveToInsertRow(); //move cursor to where you want to insert row. creates an empty row
        		
        		System.out.println("Enter ID: ");
        		rs.updateString(1, sc.next()); //1=> column no
        		System.out.println("Enter roll no: ");
        		rs.updateInt(2, sc.nextInt());
        		System.out.println("Enter first name: ");
        		rs.updateString(3, sc.next());
        		System.out.println("Enter last name: ");
        		rs.updateString(4, sc.next());
        		System.out.println("Enter DOB: ");
        		rs.updateDate(5, Date.valueOf(sc.next()));
        		System.out.println("Enter DEPT CODE: ");
        		rs.updateString(6, sc.next());
        		System.out.println("Enter gender(M,F,O): ");
        		rs.updateString(7, sc.next());
        		System.out.println("Enter fee: ");
        		rs.updateInt(8, sc.nextInt());
        		System.out.println("Enter course ID: ");
        		rs.updateString(9, sc.next());
        		rs.insertRow(); //insert new row into database
        		System.out.println("inserted....");
        		selectAllRecord();
        	}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	

	private static void selectAllRecord() throws SQLException
	{
		CallableStatement call = connection.prepareCall("{ call GET_ALL() }");
		ResultSet result = call.executeQuery();
		
		while(result.next())
		{
			String id = result.getString("ID");
			int roll = result.getInt("ROLL_NO");
			String fname = result.getString("FNAME");
			String lname = result.getString("LNAME");
			Date dob = result.getDate("DOB");
			String deptCode = result.getString("DEPT_CODE");
			String gender = result.getString("GENDER");
			int fee = result.getInt("FEEs");
			String course = result.getString("COURSE");
			
			System.out.println("ID: "+id+" ROLL_NO: "+roll+" FNAME: "+fname+" LNAME: "+lname+" DOB: "+dob+" DEPT_CODE: "+deptCode+" Gender: "
					+gender+" FEE: "+fee+" COURSE: "+course);
		}
	}


	private static void insertRecord() throws SQLException //just throws since it is already handled in main function
	{
		//METHOD 1 - Hardcode values into query
		//System.out.println("Insert record");
		//String sqlQuery = "INSERT INTO STUDENTS VALUES ('SO4', 1005, 'Dearaj', 'Mehta' , '1997-06-12', 'EC02', 'M', 7000, 'EC-203')";
		
		String sqlQuery = "INSERT INTO STUDENTS VALUES (?,?,?,?,?,?,?,?,?)"; //? refers to corresponding columns
		
		//platform to perform sql statement
		PreparedStatement preparedStatement=connection.prepareStatement(sqlQuery); 
		
		//METHOD 2 - Hardcode into preparedStatment
//		preparedStatement.setString(1, "S05"); //add values to all columns, 1 => column index in table
//		preparedStatement.setInt(2, 1006);
//		preparedStatement.setString(3, "Divya");
//		preparedStatement.setString(4, "Sharma");
//		Date date = new Date(0000-00-00);
//		preparedStatement.setDate(5, date.valueOf("1999-04-02"));
//		preparedStatement.setString(6, "CS03");
//		preparedStatement.setString(7, "F");
//		preparedStatement.setInt(8, 7500);
//		preparedStatement.setString(9, "CS-315");
		
		//METHOD 3 - TAKE USER INPUT
		System.out.println("Enter ID: ");
		preparedStatement.setString(1, sc.next()); //add user input values to all columns, 1 => column index in table
		System.out.println("Enter roll no: ");
		preparedStatement.setInt(2, sc.nextInt());
		System.out.println("Enter first name: ");
		preparedStatement.setString(3, sc.next());
		System.out.println("Enter last name: ");
		preparedStatement.setString(4, sc.next());
		System.out.println("Enter DOB: ");
		preparedStatement.setDate(5, Date.valueOf(sc.next()));
		System.out.println("Enter DEPT CODE: ");
		preparedStatement.setString(6, sc.next());
		System.out.println("Enter gender(M,F,O): ");
		preparedStatement.setString(7, sc.next());
		System.out.println("Enter fee: ");
		preparedStatement.setInt(8, sc.nextInt());
		System.out.println("Enter course ID: ");
		preparedStatement.setString(9, sc.next());
		
		
		int rows = preparedStatement.executeUpdate(); //performs the operation & returns an integer value = no of rows updated
		if(rows > 0)
		{
			System.out.println("Record inserted successfully");
		}
		
	}
	
	private static void selectRecord() throws SQLException
	{
		Statement statement = connection.createStatement(); //platform to create statment
												//createStatement returns an instance of Statement interface
		System.out.println("Enter student roll no: ");
		int studentRoll=sc.nextInt();
		String sql = "select * from students where ROLL_NO = " + studentRoll;
		
		//trigger the query
		ResultSet result = statement.executeQuery(sql); //returns a Result set(interface from java.sql)
		
		while(result.next())
		{
			String id = result.getString("ID");
			int roll = result.getInt("ROLL_NO");
			String fname = result.getString("FNAME");
			String lname = result.getString("LNAME");
			Date dob = result.getDate("DOB");
			String deptCode = result.getString("DEPT_CODE");
			String gender = result.getString("GENDER");
			int fee = result.getInt("FEEs");
			String course = result.getString("COURSE");
			
			System.out.println("ID: "+id+" ROLL_NO: "+roll+" FNAME: "+fname+" LNAME: "+lname+" DOB: "+dob+" DEPT_CODE: "+deptCode+" Gender: "
					+gender+" FEE: "+fee+" COURSE: "+course);
		}
										
	}
}
