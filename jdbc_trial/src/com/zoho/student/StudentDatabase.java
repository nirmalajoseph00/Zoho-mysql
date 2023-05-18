package com.zoho.student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class StudentDatabase 
{
	private static Connection connection = null; //Connection is an Interface, static since we need one connection for whole pjt
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) 
	{
		try{  
			Class.forName("com.mysql.cj.jdbc.Driver");  //load & register the driver
			
			String dbURL = "jdbc:mysql://localhost:3306/STUDENT";
			String username = "root";
			String password = "";
			connection = DriverManager.getConnection(dbURL,username,password);  //establised the connection
			System.out.println("Connection established.......");
			//here STUDENT is database name, root is username and no password 
			
			char ch='Y';
			int choice;
	
			do
			{
				System.out.println("-----------------------------------");
				System.out.println("1. Insert record");
				System.out.println("2. Select a Record (procedure with paramter)");
				System.out.println("3. Select all Records (procedure without paramter)");
				System.out.println("4. Update record");
				System.out.println("5. Delete record");
				System.out.println("6. Students Born in 2000");
				System.out.println("7. Student with max total marks");
				System.out.println("8. Mark>90 in any subject of current academic year");
				System.out.println("9. Select all Records Mark Table");
				System.out.println("10.Quit");
				System.out.println("-----------------------------------");
				System.out.println("Enter your choice: ");
				choice=sc.nextInt();
				
				switch (choice)
				{
				case 1:
					insertRecord();
					break;
				case 2:
					System.out.println("Enter student ID: ");
					int studentID=sc.nextInt();
					selectRecord(studentID);
					break;
				case 3:
					selectAllRecord();
					break;
				case 4:
					updateRecord();
					break;
				case 5:
					deleteRecord();
					break;
				case 6:
					studentsBornIn2000();
					break;
				case 7:
					maxTotalMarks();
					break;
				case 8:
					mark90Above();
					break;
				case 9:
					selectAllMark();
					break;
				case 10:
					System.out.println("Do you want to continue(Y/N): ");
					ch=sc.next().charAt(0);
					break;
				default:
					System.out.println("Wrong Choice ");
					break;
				}
				
			}while(ch=='Y');
			

			connection.close(); 
			System.out.println("Goodbye");
			}
		catch(Exception e)
		{
			System.out.println(e);
		}  

	}



	private static void insertRecord() throws SQLException //just throws since it is already handled in main function
	{
		
		String sqlQuery = "INSERT INTO STUDENTDETAILS(FirstName,LastName,STD,Branch,Gender,Email,DateOfBirth,YearOfAdmission) VALUES (?,?,?,?,?,?,?,?)"; //? refers to corresponding columns
		
		//platform to perform sql statement
		PreparedStatement preparedStatement=connection.prepareStatement(sqlQuery); 
		
		//METHOD 3 - TAKE USER INPUT
		//add user input values to all columns, 1 => no. corresponding to '?'
		
		System.out.println("Enter first name: ");
		preparedStatement.setString(1, sc.next());
		System.out.println("Enter last name: ");
		preparedStatement.setString(2, sc.next());
		System.out.println("Enter STD: ");
		preparedStatement.setInt(3, sc.nextInt());
		System.out.println("Enter Branch: ");
		preparedStatement.setString(4, sc.next());
		System.out.println("Enter gender(M,F,O): ");
		preparedStatement.setString(5, sc.next());
		System.out.println("Enter email: ");
		preparedStatement.setString(6, sc.next());
		System.out.println("Enter DOB: ");
		preparedStatement.setDate(7, Date.valueOf(sc.next()));
		System.out.println("Enter year of admission: ");
		preparedStatement.setString(8, sc.next());
		
		int rows = preparedStatement.executeUpdate(); //performs the operation & returns an integer value = no of rows updated
		
		if(rows == 1)
		{
			System.out.println("Record inserted successfully");
		}
		
	}
	
	private static void selectRecord(int studentID) throws SQLException
	{
		CallableStatement call = connection.prepareCall("{ call GET_RECORD(?) }");
		call.setInt(1, studentID);
		
		//trigger the query
		ResultSet result = call.executeQuery(); //returns a Result set(interface from java.sql)
		
		while(result.next())
		{
			int ID = result.getInt("ID");
			String fname = result.getString("FirstName");
			String lname = result.getString("LastName");
			int std = result.getInt("STD");
			String branch = result.getString("Branch");
			String gender = result.getString("Gender");
			String email = result.getString("Email");
			Date dob = result.getDate("DateOfBirth");
			String yoa = result.getString("YearOfAdmission");

			System.out.println("ID: "+ ID +" FNAME: "+fname+" LNAME: "+lname + " STD: " + std + " Branch: "+ branch +" Gender: "
					+gender +" Email: "+ email+ " DOB: " + dob + " Year of Adm: "+yoa.substring(0,4));
		}
										
	}
	
	private static void selectAllRecord() throws SQLException
	{
		CallableStatement call = connection.prepareCall("{ call GET_ALL() }");
		ResultSet result = call.executeQuery();
		
		while(result.next())
		{
			int ID = result.getInt("ID");
			String fname = result.getString("FirstName");
			String lname = result.getString("LastName");
			int std = result.getInt("STD");
			String branch = result.getString("Branch");
			String gender = result.getString("Gender");
			String email = result.getString("Email");
			Date dob = result.getDate("DateOfBirth");
			String yoa = result.getString("YearOfAdmission");
			
			System.out.println("ID: "+ ID +" FNAME: "+fname+" LNAME: "+lname + " STD: " + std + " Branch: "+ branch +" Gender: "
					+gender +" Email: "+ email + " DOB: " + dob + " Year of Adm: "+yoa.substring(0,4));
		}
	}
	
	private static void updateRecord() throws SQLException
	{
		Statement statement = connection.createStatement(); //platform to create statment
		//createStatement returns an instance of Statement interface
		
		System.out.println("Enter student ID: ");
		int studentID=sc.nextInt();
		
		String sql = "select * from STUDENTDETAILS where ID = " + studentID;
		ResultSet result = statement.executeQuery(sql);
		
		if(result.next())
		{
			selectRecord(studentID);
			
			System.out.println("What to update?");
        	System.out.println("1. First name");
        	System.out.println("2. STD");
        	int option=sc.nextInt();
        	
        	String sqlQuery1 = "UPDATE STUDENTDETAILS SET ";
        	String sqlQuery2 = "WHERE ID = " + studentID;
        	
        	switch (option) 
        	{
        	case 1:
        		System.out.println("Enter new first name to update: ");
        		String newName=sc.next();
        		
        		sql = sqlQuery1 + "FirstName = ? " + sqlQuery2;
        		PreparedStatement preparedStatement = connection.prepareStatement(sql);
        		preparedStatement.setString(1, newName);
        	
        		int rows = preparedStatement.executeUpdate();
        		if (rows > 0) 
        		{
        		    System.out.println(studentID+ " updated successfully...");
        		    selectRecord(studentID);
        		}
        		break;
        		
        	case 2:
        		System.out.println("Enter new STD to update: ");
        		int newSTD=sc.nextInt();
        		System.out.println("Enter new Branch to update: ");
        		String newBranch=sc.next();
        		
        		sql = sqlQuery1 + "STD = ?, Branch = ? " + sqlQuery2;
        		PreparedStatement preparedStatement2 = connection.prepareStatement(sql);
        		preparedStatement2.setInt(1, newSTD);
        		preparedStatement2.setString(2, newBranch);
        		
        		rows = preparedStatement2.executeUpdate();
        		if (rows > 0) 
        		{
        			System.out.println(studentID+ " updated successfully...");
        		    selectRecord(studentID);
        		}
        		break;
        	}
		}
		else
		{
			System.out.println("Record not found...");
		}
		
	}
	
	private static void deleteRecord() throws SQLException 
	{
		System.out.println("Enter the ID of student record to be deleted: " );
    	int studentID=sc.nextInt();
    	
        String sql = "DELETE FROM STUDENTDETAILS WHERE ID = " + studentID;
        Statement statement = connection.createStatement();
    	
    	int rows = statement.executeUpdate(sql);
 		if (rows > 0) 
 		{
 			System.out.println(studentID+ " deleted successfully... ");
 			selectAllRecord();
 		}
 		else 
 		{
 			System.out.println(" No such student");
 		}
	}
	
	private static void studentsBornIn2000() throws SQLException
	{
		CallableStatement call = connection.prepareCall("{ call GET_2000() }");
		ResultSet result = call.executeQuery();
		
		while(result.next())
		{
			System.out.println("Count of students born in 2000: " + result.getInt("StudentCountBornIn2000"));
		}
	}


	private static void maxTotalMarks() throws SQLException 
	{
		CallableStatement call = connection.prepareCall("{ call GET_MAXMARK() }");
		ResultSet result = call.executeQuery();
		
		while(result.next())
		{
			String fname = result.getString("FirstName");
			int totalMark = result.getInt("TOTAL_MARK");
			
			System.out.println("FNAME: "+fname+ " --- Total Max Mark: " + totalMark );
		}
	}



	private static void mark90Above() throws SQLException
	{
		String sql = "SELECT * "
				+ " FROM(SELECT FIRSTNAME,'Maths' as Subject,MATH AS Mark,STD FROM STUDENTDETAILS AS S INNER JOIN MARK AS M WHERE S.ID=M.ID"
				+ " UNION ALL "
				+ " SELECT FIRSTNAME,'Phy' as Subject,PHY AS Mark,STD FROM STUDENTDETAILS AS S INNER JOIN MARK AS M WHERE S.ID=M.ID"
				+ " UNION ALL"
				+ " SELECT FIRSTNAME,'Chem' as Subject,CHEM AS Mark,STD FROM STUDENTDETAILS AS S INNER JOIN MARK AS M WHERE S.ID=M.ID"
				+ " UNION ALL"
				+ " SELECT FIRSTNAME,'Eng' as Subject,ENG AS Mark,STD FROM STUDENTDETAILS AS S INNER JOIN MARK AS M WHERE S.ID=M.ID"
				+ " UNION ALL"
				+ " SELECT FIRSTNAME,'Bio' as Subject,BIO AS Mark,STD FROM STUDENTDETAILS AS S INNER JOIN MARK AS M WHERE S.ID=M.ID"
				+ " UNION ALL"
				+ " SELECT FIRSTNAME,'Eco' as Subject,ECO AS Mark,STD FROM STUDENTDETAILS AS S INNER JOIN MARK AS M WHERE S.ID=M.ID"
				+ " UNION ALL"
				+ " SELECT FIRSTNAME,'History' as Subject,HIS AS Mark,STD FROM STUDENTDETAILS AS S INNER JOIN MARK AS M WHERE S.ID=M.ID) "
				+ " AS SUB_MARK"
				+ " WHERE Mark>90";
		
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(sql);
		
		while(results.next())
		{
			String fname = results.getString("FirstName");
			String sub = results.getString("Subject");
			int mark = results.getInt("Mark");
			int std = results.getInt("STD");
			
			System.out.println("FNAME: " + fname +" Subject: "+ sub +" Mark: "+mark + " STD: " + std);
		}
	}

	private static void selectAllMark() throws SQLException 
	{
		String sql = "SELECT * FROM MARK";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		
		ResultSet result = preparedStatement.executeQuery();
		System.out.println("ID	MATH	PHY	CHEM	ENG	BIO	ECO	HIS");
		while(result.next())
		{
			int id = result.getInt(1); //column index => 1
			int mat= result.getInt("MATH");
			int phy= result.getInt("PHY");
			int chem= result.getInt("CHEM");
			int eng = result.getInt("ENG");
			int bio = result.getInt("BIO");
			int eco = result.getInt("ECO");
			int his = result.getInt("HIS");
			System.out.println(id+"\t"+mat+"\t"+phy+"\t"+chem+"\t"+eng+"\t"+bio+"\t"+eco+"\t"+his);
			
		}
		
	}





}
