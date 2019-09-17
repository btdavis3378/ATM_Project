import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class main {

  public static void main(String[] args) throws IOException {

    Connection con = null;

    try {

      //connection path to database
      String uri = "jdbc:sqlite:ATM_Management.db";
      con = DriverManager.getConnection(uri);
      System.out.println("Connected to the ClientDB \n"); //If successful

      //2.1 Create view to show total balance in each bank's accounts
      //Bank I.D: 1958 and 4123 have no accounts. There total balance will not show
      System.out.println("2.1");
      String totalBalance = "CREATE VIEW IF NOT EXISTS total AS\n"
          + "    SELECT Bank.bank_name AS [Bank Name],\n"
          + "           Bank.bank_id AS [Bank ID],\n"
          + "           sum(Account.balance) + sum(ATM.balance) AS [Total Balance]\n"
          + "      FROM Bank\n"
          + "           INNER JOIN\n"
          + "           Account ON Account.bank_id = Bank.bank_id\n"
          + "           INNER JOIN\n"
          + "           ATM ON ATM.bank_id = Bank.bank_id\n"
          + "     GROUP BY Bank.Bank_id;\n";

      //Execute the query for total balance in banks
      Statement stmt = con.createStatement();
      stmt.execute(totalBalance);
      System.out.println("Create view for total balance in banks (ATM and Accounts) successful \n");

      //2.2 Begin transaction to insert new ATM from user input
      System.out.println("2.2a");

      boolean moreATMs = true;
      boolean addingATM;
      Scanner sc = new Scanner(System.in);

      //while loop to add multiple ATMs unless input is N
      while(moreATMs = true) {
        System.out.println("Would you like to add an ATM? (y/n)");
        System.out.println("(Enter 'n' to continue to adding member)");
        String addATM = sc.nextLine().trim().toLowerCase();

        //break out of while loop you don't want to add atm
        if(addATM.equals("n") || addATM.equals("no")) {
          moreATMs = false;
          break;
        } else if(addATM.equals("y") || addATM.equals("yes")) {

          //Begin process of adding atm
          addingATM = true;

          while(addingATM = true) {

            try {
              //Get user input for ATM information
              System.out.println("Enter the ATM I.D (Int): ");
              int atm_id = sc.nextInt();

              System.out.println("Enter the bank I.D (Int): ");
              int bank_id = sc.nextInt();

              System.out.println("Enter the ATM location (Int): ");
              int atm_location = sc.nextInt();
              sc.nextLine();

              System.out.println("Enter the location name (String): ");
              String location_name = sc.nextLine();

              System.out.println("Enter the account balance (Int): ");
              int balance = sc.nextInt();

              System.out.println("Enter the number of transactions (Int): ");
              int num_of_tran = sc.nextInt();
              sc.nextLine();

              //Print input data and confirm information or
              System.out.println("ATM I.D: " + atm_id
                  + "\nBank id: " + bank_id
                  + "\nATM location: " + atm_location
                  + "\nLocation name: " + location_name
                  + "\nAccount balance: " + balance
                  + "\nNumber of transactions: " + num_of_tran
                  + "\nIf this information is correct, enter 'y' to proceed or 'n' to restart"
                  + "\nEnter 'c' or to cancel adding an atm");

              String confirm = sc.nextLine().trim().toLowerCase();

              //If information is correct, create transaction that inserts data
              if (confirm.equals("y") || confirm.equals("yes")) {
                String startTran = "BEGIN TRANSACTION;";
                stmt.execute(startTran);

                String insertATM = "INSERT OR IGNORE INTO ATM "
                    + "(atm_id, bank_id, atm_location, location_name, balance, num_of_tran)"
                    + "VALUES(?, ?, ?, ?, ?, ?);";

                PreparedStatement pstmt = con.prepareStatement(insertATM);
                pstmt.setInt(1, atm_id);
                pstmt.setInt(2, bank_id);
                pstmt.setInt(3, atm_location);
                pstmt.setString(4, location_name);
                pstmt.setInt(5, balance);
                pstmt.setInt(6, num_of_tran);
                pstmt.executeUpdate();

                String commit = "COMMIT TRANSACTION;";
                stmt.execute(commit);

                System.out.println("Successfully added ATM to database\n");

                addingATM = false;
                break;
              } else if (confirm.equals("n") || confirm.equals("no")) {
                continue; //If no, restarts adding atm
              } else if (confirm.equals("c") || confirm.equals("cancel")) {
                addingATM = false; //cancels to start of adding or not adding atm
                break;
              } else {
                //If input is incorrect, restart adding ATM process
                System.out.println("Wrong key entered, please try again");
                continue;
              }
            } catch(InputMismatchException e) {
              System.out.println("User Input Failure: " + e.getMessage() + ", Please try again");
              sc.next();
            }
          }
        }else {
          //If input is incorrect, restart
          System.out.println("Wrong key entered, please try again");
          continue;
        }
      }


      System.out.println("2.2a Successful\n");

      //Transaction which inserts new members and calculates new totals for money in banks
      System.out.println("2.2b");

      boolean newMembers = true;
      boolean correctDateFormat;
      boolean addingMember;

      //while loop to add multiple members unless input is N
      while(newMembers = true) {
        System.out.println("Would you like to add a member? (y/n)");
        System.out.println("(Enter 'n' to continue to account withdraw process)");
        String addMem = sc.nextLine().trim().toLowerCase();

        //break out of while loop you don't want to add member
        if(addMem.equals("n") || addMem.equals("no")) {
          newMembers = false;
          break;
        } else if(addMem.equals("y") || addMem.equals("yes")) {

          //Begin process of adding member
          addingMember = true;
          correctDateFormat = false;

          while(addingMember = true) {

            try {
              //Get user input for member information
              System.out.println("Enter the member I.D (Int): ");
              int mem_id = sc.nextInt();

              System.out.println("Enter the account I.D (Int): ");
              int acct_id = sc.nextInt();
              sc.nextLine();

              System.out.println("Enter the member's first name (String): ");
              String mem_fname = sc.nextLine();

              System.out.println("Enter the member's last name (String): ");
              String mem_lname = sc.nextLine();

              System.out.println("Enter the member's SSN (Int): ");
              int ssn = sc.nextInt();

              System.out.println("Enter the member's phone number (Long): ");
              Long phone = sc.nextLong();
              sc.nextLine();

              System.out.println("Enter the member's email (String): ");
              String email = sc.nextLine();

              System.out.println("Enter the member's address (String): ");
              String address = sc.nextLine();

              System.out.println("Enter the member's birthdate (yyyy-MM-dd): ");
              String birthdatetemp = sc.next();
              String birthdate;

              SimpleDateFormat dateSQLFormatTwo = new SimpleDateFormat("yyyy-MM-dd");

              //checks to see if date input is in correct format
              do {
                try {
                  Date birthdatetempTwo = dateSQLFormatTwo.parse(birthdatetemp);
                  correctDateFormat = true;
                  break;
                } catch (ParseException e) {
                  System.out.println("User input date incorrect: " + e.getMessage() + " Please enter correct date format (yyyy-MM-dd)");
                  birthdatetemp = sc.next();
                }
              } while(correctDateFormat != true);

              //If date input is correct, set the birthdate to it
              birthdate = birthdatetemp;

              sc.nextLine();

              //Print input data and confirm information or
              System.out.println("Member I.D: " + mem_id
                  + "\nAccount I.D: " + acct_id
                  + "\nFirst name: " + mem_fname
                  + "\nLast name: " + mem_lname
                  + "\nSocial Security Number: " + ssn
                  + "\nPhone number: " + phone
                  + "\nEmail: " + email
                  + "\nAddress: " + address
                  + "\nBirthdate: " + birthdate
                  + "\nIf this information is correct, enter 'y' to proceed or 'n' to restart"
                  + "\nEnter 'c' or to cancel adding new member");

              String confirm = sc.nextLine().trim().toLowerCase();

              //If information is correct, create transaction that inserts data
              if (confirm.equals("y") || confirm.equals("yes")) {
                String startTran = "BEGIN TRANSACTION;";
                stmt.execute(startTran);

                String insertMem = "INSERT OR IGNORE INTO Member "
                    + "(mem_id, acct_id, mem_fname, mem_lname, ssn, phone, email, address, birthdate)"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";

                PreparedStatement pstmt = con.prepareStatement(insertMem);
                pstmt.setInt(1, mem_id);
                pstmt.setInt(2, acct_id);
                pstmt.setString(3, mem_fname);
                pstmt.setString(4, mem_lname);
                pstmt.setInt(5, ssn);
                pstmt.setLong(6, phone);
                pstmt.setString(7, email);
                pstmt.setString(8, address);
                pstmt.setString(9, birthdate);
                pstmt.executeUpdate();

                /*
                Creates default account in the database for the member
                 */
                int bank_id = 1111;
                String acct_type = "checking";
                int balance = 100;
                Boolean is_active = true;

                String insertDefaultAccount = "INSERT OR IGNORE INTO Account "
                    + "(acct_id, bank_id, acct_type, balance, is_active)"
                    + "VALUES(?, ?, ?, ?, ?);";

                pstmt = con.prepareStatement(insertDefaultAccount);
                pstmt.setInt(1, acct_id);
                pstmt.setInt(2, bank_id);
                pstmt.setString(3, acct_type);
                pstmt.setInt(4, balance);
                pstmt.setBoolean(5, is_active);
                pstmt.executeUpdate();

                /*
                Creates default bank in the database for the created member
                 */
                String bank_name = "default bank";
                String street_address = "Default street 123 lane";
                String state = "DF";
                int zip = 11111;
                int location_id = 0123;
                String insertDefaultBank = "INSERT OR IGNORE INTO Bank "
                    + "(bank_id, bank_name, street_address, state, zip, location_id)"
                    + "VALUES(?, ?, ?, ?, ?, ?);";

                pstmt = con.prepareStatement(insertDefaultBank);
                pstmt.setInt(1, bank_id);
                pstmt.setString(2, bank_name);
                pstmt.setString(3, street_address);
                pstmt.setString(4, state);
                pstmt.setInt(5, zip);
                pstmt.setInt(6, location_id);
                pstmt.executeUpdate();

                String commit = "COMMIT TRANSACTION;";
                stmt.execute(commit);

                System.out.println("Successfully added member to database\n");

                //Process to show new bank totals with select statement
                System.out.println("Would you like to see new bank totals? (y/n)");
                String seeNewTotal = sc.nextLine().trim().toLowerCase();
                if (seeNewTotal.equals("y") || seeNewTotal.equals("yes")) {
                  String newTotals = "SELECT Bank.bank_name AS [Bank Name],\n"
                      + "           Bank.bank_id AS [Bank ID],\n"
                      + "           sum(Account.balance) + (ATM.balance) AS [Total Balance]\n"
                      + "      FROM Bank\n"
                      + "           INNER JOIN\n"
                      + "           Account ON Account.bank_id = Bank.bank_id\n"
                      + "           INNER JOIN\n"
                      + "           ATM ON ATM.bank_id = Bank.bank_id\n"
                      + "     GROUP BY Bank.Bank_id;";

                  ResultSet rs = stmt.executeQuery(newTotals);
                  System.out.println("1 = Bank name\n2 = Bank I.D\n3 = Total Balance in each bank (in ATM and Accounts");

                  //While loop to traverse and print the banks
                  while (rs.next()) {
                    System.out
                        .println("--------------------------------------------------------------");
                    for (int i = 1; i <= 3; i++) {
                      System.out.println(i + " = " + rs.getString(i));
                    }
                    System.out
                        .println("--------------------------------------------------------------");
                  }
                }

                addingMember = false;
                break;
              } else if (confirm.equals("n") || confirm.equals("no")) {
                continue; //If no, restarts adding member
              } else if (confirm.equals("c") || confirm.equals("cancel")) {
                addingMember = false; //cancels to start of adding or not adding member
                break;
              } else {
                //If input is incorrect, restart adding member process
                System.out.println("Wrong key entered, please try again");
                continue;
              }
            } catch(InputMismatchException e) {
              System.out.println("User Input Failure: " + e.getMessage() + ", Please try again");
              sc.next();
            }
          }
        }else {
          //If input is incorrect, restart
          System.out.println("Wrong key entered, please try again");
          continue;
        }
      }

      System.out.println("2.2b Successful\n");
      System.out.println("3.a-d");

      boolean withdrawing = true;

      //Process to withdraw from an account multiple times
      while (withdrawing == true) {

        System.out.println("Would you like to select and account then withdraw from balance? (y/n)");
        System.out.println("If yes, then list of accounts will appear");
        System.out.println("(Enter 'n' to exit the program)");
        String withdrawAnswer = sc.nextLine().trim().toLowerCase();

        //break out of while loop you don't want to withdraw
        if (withdrawAnswer.equals("n") || withdrawAnswer.equals("no")) {
          withdrawing = false;
          break;
        } else if (withdrawAnswer.equals("y") || withdrawAnswer.equals("yes")) {

          //Prints out member names and account IDs
          boolean beginWithdraw = true;
          while(beginWithdraw == true) {
            String showAccounts = "SELECT Member.mem_fname AS [Member fName],\n"
                + "           Member.mem_lname AS [Member lName],\n"
                + "           Member.acct_id AS [Account I.D]\n"
                + "      FROM Member\n"
                + "     GROUP BY Member.acct_id;";

            ResultSet rs = stmt.executeQuery(showAccounts);
            System.out.println("1 = First Name\n2 = Last Name\n3 = Account I.D");
            while (rs.next()) {
              System.out
                  .println("--------------------------------------------------------------");
              for (int i = 1; i <= 3; i++) {
                System.out.println(i + " = " + rs.getString(i));
              }
              System.out
                  .println("--------------------------------------------------------------");
            }

            System.out.println("Please select an account I.D");
            System.out.println("(May only contain numbers in list)");
            int selectedAcct = sc.nextInt();
            sc.nextLine();

            //Prints Account I.D and account balance from selected acct_id
            String showMoney = "Select Account.acct_id, Account.balance FROM Account WHERE acct_id = " + selectedAcct + ";";
            System.out.println("Account I.D and Account Balance:");
            rs = stmt.executeQuery(showMoney);
            for (int i = 1; i <= 2; i++) {
              System.out.println(i + " = " + rs.getString(i));
            }

            //ask user input if they want to withdraw
            System.out.println("Would you like to withdraw ('yes' or 'cancel')");
            System.out.println("(If yes, will show all ATM I.D's)");
            String withdrawFromBalance = sc.nextLine().trim().toLowerCase();

            //If user doesn't want to withdraw, then cancel loop
            if (withdrawFromBalance.equals("cancel")) {
              beginWithdraw = false;
              break;
            } else if (withdrawFromBalance.equals("y") || withdrawFromBalance.equals("yes")) {

              //prints ATMs for user to select to withdraw from
              String showATMs = "SELECT ATM.atm_id AS [ATM I.D]\n"
                  + "      FROM ATM;";

              rs = stmt.executeQuery(showATMs);

              System.out.println("1 = ATM I.D");
              while (rs.next()) {
                System.out
                    .println("----------------------------");
                for (int i = 1; i <= 1; i++) {
                  System.out.println(i + " = " + rs.getString(i));
                }
                System.out
                    .println("----------------------------");
              }

              //user input to select atm
              System.out.println("Please select an ATM to withdraw from. Enter ATM I.D number");
              int selectATM = sc.nextInt();

              System.out.println("Please enter amount to withdraw from ATM (Int)");
              int withdrawAMT = sc.nextInt();
              sc.nextLine();

              //Confirm withdrawal
              System.out.println("Are you sure you want to withdraw " + withdrawAMT + " from ATM ID number: " + selectATM + "? (y/n)");
              String confirmWithdraw = sc.nextLine().trim().toLowerCase();

              if (confirmWithdraw.equals("n") || confirmWithdraw.equals("no")) {
                break;
              } else if (confirmWithdraw.equals("y") || confirmWithdraw.equals("yes")) {

                //Begin withdrawal process and Begin transaction in SQL
                String startTran = "BEGIN TRANSACTION;";
                stmt.execute(startTran);

                //Update the fields in the database
                String subtractAmtFromAcct = "Update Account set balance = balance - " + withdrawAMT +  " where acct_id = " + selectedAcct + ";";
                String subtractAmtFromATM = "Update ATM set balance = balance - " + withdrawAMT +  " where atm_id = " + selectATM + ";";

                //execute the update
                stmt.execute(subtractAmtFromAcct);
                stmt.execute(subtractAmtFromATM);

                System.out.println("Withdraw successful");
                System.out.print("New Balance in account: ");

                //Prints new balance in account
                String showNewBalance = "SELECT Account.balance AS [Account Balance]\n"
                    + "      FROM Account WHERE Account.acct_id = " + selectedAcct + ";";
                rs = stmt.executeQuery(showNewBalance);
                System.out.println(rs.getString(1));

                //Asks user to see updated bank balance from the bank
                System.out.println("Would you like to see the updated bank balance associated with this account? (yes/y)");
                System.out.println("(Other than yes will continue)");
                String seeBankBalance = sc.nextLine().trim().toLowerCase();
                String selectBankID = "SELECT Account.bank_id FROM Account Where Account.acct_id = " + selectedAcct + ";";

                //Get the bank ID from the account and into a variable
                ResultSet getBankId = stmt.executeQuery(selectBankID);
                int realbank_id = getBankId.getInt(1);

                //User input to see the bank total
                if (seeBankBalance.equals("yes") || seeBankBalance.equals("y")) {

                  //Print the bank total
                  String newBankTotal = "SELECT Bank.bank_name AS [Bank Name],\n"
                      + "           Bank.bank_id AS [Bank ID],\n"
                      + "           sum(Account.balance) AS [Total Balance]\n"
                      + "      FROM Bank\n"
                      + "           INNER JOIN\n"
                      + "           Account ON Account.bank_id = Bank.bank_id\n"
                      + "     WHERE Bank.bank_id = " + realbank_id + ";";

                  rs = stmt.executeQuery(newBankTotal);
                  System.out.println("1 = Bank Name\n2 = Bank I.D\n3 = New Bank Balance Total in bank ");

                  while (rs.next()) {
                    System.out
                        .println("----------------------------");
                    for (int i = 1; i <= 3; i++) {
                      System.out.println(i + " = " + rs.getString(i));
                    }
                    System.out
                        .println("----------------------------");
                  }

                }

                //commit the transaction in SQL
                String commit = "COMMIT TRANSACTION;";
                stmt.execute(commit);
              } else {
                System.out.println("Wrong key input, restarting");
              }

            } else {
              System.out.println("Wrong key entered, please try again");
              continue;
            }
            beginWithdraw = false;
            break;
          }
        } else {
          //If input is incorrect, withdraw process
          System.out.println("Wrong key entered, please try again");
          continue;
        }
      }

      //exception handling
    } catch(SQLException e) {
      System.out.println("Standard Failure: " + e.getMessage());
    } finally {
      try {
        if (con != null) {
          con.close();
        }
      } catch (SQLException extra) {
        System.out.println("Extra Failure Here: " + extra.getMessage());
      }
    }
  }
}
