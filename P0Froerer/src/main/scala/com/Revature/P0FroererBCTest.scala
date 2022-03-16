package com.Revature

import org.apache.parquet.format.LogicalType.JSON

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, SQLException, Statement}
import scala.io.Source

object P0FroererBCTest {
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
        "\n4. Delete entry" +
        "\n5. Exit")
      val x: Int = scala.io.StdIn.readInt()
      x match {
        case 1 => getTesterInfo()
                chooseTest()
        case 2 => updateMenu()
        case 3 => searchMenu()
        case 4 => delEntry()
        case 5 => System.exit(0)
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

      val ttInsert = "INSERT INTO TT(Bread,Cheese,bRate,cRate,bcRate,comments) VALUES(?,?,?,?,?,?);"
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
      println("Thanks for testing!")
      menu()
    }

    def updateMenu(): Unit = {
      println("What would you like to update? " +
        "\n1. Name/Age" +
        "\n2. Ratings" +
        "\n3. return")
      val x: Int = scala.io.StdIn.readInt()
      x match {
        case 1 => updateNameAge()
        case 2 => updateRatings()
        case 3 => menu()
      }

    }
    def updateNameAge(): Unit = {
      val Name = scala.io.StdIn.readLine("Alright, what is the new full name and age you would like to input?\n").split(" ")
        val fName1 = Name(0)
        val lName1 = Name(1)
        val age1 = Name(2)
      val Name2 = scala.io.StdIn.readLine("And the full name and age you initially input?\n").split(" ")
        val fName2 = Name(0)
        val lName2 = Name(1)
      stmt.executeUpdate(s"UPDATE Customers SET LastName='$lName1', FirstName='$fName1', Age=$age1 WHERE LastName='$lName2' OR FirstName='$fName2';")
      println("Your survey has been updated.")
      menu()
    }
    def updateRatings(): Unit = {
      println()
        val Info = scala.io.StdIn.readLine("Alright, please enter the bread and cheese you tried separated by a space\n").split(" ")
          val bread = Info(0)
          val cheese = Info(1)
        println(s"Please enter your new score(1-10) for the $bread,")
          val bRate = scala.io.StdIn.readInt()
        println(s"the $cheese,")
          val cRate = scala.io.StdIn.readInt()
        println(s"the combined score,")
          val bcRate = scala.io.StdIn.readInt()
        println(s"and any new comments you have,")
          val comms = scala.io.StdIn.readLine()
      stmt.executeUpdate(s"UPDATE TT SET bRate=$bRate, cRate=$cRate, bcRate=$bcRate, comments='$comms' WHERE bread='$bread' AND Cheese='$cheese';")
    println("your ratings have been updated.")
    menu()
    }

    def searchMenu(): Unit = {
      println("What would you like to view? " +
        "\n1. My Entry" +
        "\n2. Available breads and cheeses" +
        "\n3. return")
      val x: Int = scala.io.StdIn.readInt()
      x match {
        case 1 => searchEntry()
        case 2 => searchChoices()
        case 3 => menu()
      }

    }
    def searchEntry(): Unit = {
      val Name = scala.io.StdIn.readLine("Please input full name to pull up test results\n").split(" ")
      val fName = Name(0)
      val lName = Name(1)
      println("Here is what you tested and how you rated it")
      val rsEntry: ResultSet = stmt.executeQuery(s"SELECT Customers.FirstName, Customers.LastName,Bread,Cheese,bRate,cRate,bcRate,comments FROM TT INNER JOIN Customers ON TT.TestNum = Customers.CustomerID WHERE FirstName='$fName' AND LastName='$lName';")

      while (rsEntry.next()) {
        val fName = rsEntry.getString(1)
        val lName = rsEntry.getString(2)
        val bread = rsEntry.getString(3)
        val cheese = rsEntry.getString(4)
        val bRate = rsEntry.getInt(5)
        val cRate = rsEntry.getInt(6)
        val bcRate = rsEntry.getInt(7)
        val comms = rsEntry.getString(8)
        println(s"You ($fName $lName) gave your $bread a $bRate, your $cheese a $cRate, \nthe bread/cheese combo $bcRate, and had these comments: $comms")
      }

      menu()
    }
    def searchChoices(): Unit = {
      println("Would you like to see all of the bread or cheese options?")
      val choice = scala.io.StdIn.readLine
      val rsbc: ResultSet = stmt.executeQuery(s"SELECT $choice FROM Choices;")

      while (rsbc.next()) {
        println(rsbc.getString(1))
        menu()
      }

    }
    def delEntry(): Unit = {
      val Name = scala.io.StdIn.readLine("Please input full name of the survey you would like to delete\n").split(" ")
      val fName = Name(0)
      val lName = Name(1)
      stmt.executeUpdate(s"DELETE FROM TT WHERE TestNum IN (SELECT customerID FROM customers where LastName='$lName' AND FirstName='$fName');")
      stmt.executeUpdate(s"DELETE FROM Customers WHERE FirstName='$fName' AND LastName='$lName';")
      println("Your entry has been deleted.")
      menu()
    }

    menu()
    conn.close()

  }
}


// val Dinfo = statement.executeUpdate(s"DELETE FROM Customers WHERE '$input'='$OtherInput'")
// def jsonRead(): Unit = {
// val int = "[0-9]+".r
// val str = "[a-z]+".r
//val CustomerImport = Source.fromFile("C://Revature//Projects//P0Froerer//src//test//test.json").getLines.toList

//CustomerImport.foreach{x => var insert = stmt.executeUpdate("INSERT INTO TT(bread,cheese,bRate,cRate,bcRate,comments) VALUES("+str.findAllIn(x).toList.apply(1)+", "+str.findAllIn(x).toList.apply(1)+" "+int.findAllIn(x).toList.apply(0)+" "+int.findAllIn(x).toList.apply(0)+" "+str.findAllIn(x).toList.apply(1)+" "+str.findAllIn(x).toList.apply(1)+");")}
// stmt.executeUpdate(s"INSERT INTO TT(bread) bRate=$bRate, cRate=$cRate, bcRate=$bcRate, comments='$comms' WHERE bread='$bread' AND Cheese='$cheese';")
//}