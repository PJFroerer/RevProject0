package com.Revature

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, SQLException, Statement}

object JDBC1 {
//trying to get TT CustomerID working, considering
// dropping CustomerID off of TT but would risk not knowing repeat customers.
// add a menu to let the user choose what they want to do from testing or querying
  def main(args: Array[String]) {
    // connect to the database named "test" on the localhost

    val driver = "com.mysql.cj.jdbc.Driver"
    val url = "jdbc:mysql://localhost:3306/breadwars"
    val username = "root"
    val password = "Fluffymonkies35753#%&%#"

    val conn: Connection = DriverManager.getConnection(url, username, password)

    val stmt: Statement = conn.createStatement()

    def menu(): Unit = {
      println("Please select an option by typing the corresponding number:" +
        "\n1. Sign up and test some bread!" +
        "\n2. Update your survey info" +
        "\n3. Search survey results" +
        "\n4. Exit")
      val x: Int = scala.io.StdIn.readInt()
      x match {
        case 1 => getTesterInfo()
                chooseTest()
        case 2 => updateMenu()
        case 3 => println("3")
        case 4 => System.exit(0)
      }
    }
    def getTesterInfo(): Unit = {

      val Name = scala.io.StdIn.readLine("Welcome to the Breadwars, Please input full name and age separated by spaces\n").split(" ")
      val fName = Name(0)
      val lName = Name(1)
      val age = Name(2)
      stmt.executeUpdate(s"INSERT INTO Customers(LastName,FirstName,Age) VALUES('$lName','$fName','$age')")
      println(s"Thank you $fName!")

    }

    def chooseTest(): Unit = { //gets input on bread/cheese choice, then asks for ratings and comments
      println("Now please choose 1 bread and cheese from each list as it appears.")

      val rsb: ResultSet = stmt.executeQuery("SELECT GROUP_CONCAT(DISTINCT bread ORDER BY bread ASC SEPARATOR ', ') FROM Choices;")

      while (rsb.next()) {
        println(rsb.getString(1))
      }
      val bChoice = scala.io.StdIn.readLine()
      val rsc: ResultSet = stmt.executeQuery("SELECT GROUP_CONCAT(DISTINCT cheese ORDER BY cheese ASC SEPARATOR ', ') FROM Choices;")

      while (rsc.next()) {
        println(rsc.getString(1))
      }
      val cChoice = scala.io.StdIn.readLine()

      val ttInsert = "INSERT INTO TT(CustomerID,Bread,Cheese,bRate,cRate,bcRate,comments) VALUES(?,?,?,?,?,?);"
      val pstmt: PreparedStatement = conn.prepareStatement(ttInsert)

      //        val rsID: ResultSet = stmt.executeQuery(s"SELECT CustomerID FROM Customers WHERE LastName='$lName;")
      //
      //        val rsIDI = rsID.getString(1)
      println(s"How was the $bChoice? Rate from 1 to 10.")
      val bRate = scala.io.StdIn.readInt()
      println(s"and the $cChoice?")
      val cRate = scala.io.StdIn.readInt()
      println("What about when they're combined?")
      val bcRate = scala.io.StdIn.readInt()
      println("Do you have any comments about the flavors or textures?")
      val comms = scala.io.StdIn.readLine()
      pstmt.setString(1, bChoice)
      pstmt.setString(2, cChoice)
      pstmt.setInt(3, bRate)
      pstmt.setInt(4, cRate)
      pstmt.setInt(5, bcRate)
      pstmt.setString(6, comms)

      val rowAffected = pstmt.executeUpdate
    }

    def updateMenu(): Unit = {
      println("What would you like to update? " +
        "\n1. Name" +
        "\n2. Ratings" +
        "\n3. Available bread/cheese" +
        "\n4. return")
      val x: Int = scala.io.StdIn.readInt()
      x match {
        case 1 => println("1")
        case 2 => println("2")
        case 3 => println("3")
        case 4 => menu()
      }

    }
    def updateName(): Unit = {
      println("Alright, what is your actual name?")

    }
  menu()
  conn.close()
  }
}


// val Dinfo = statement.executeUpdate(s"DELETE FROM Customers WHERE '$input'='$OtherInput'")
