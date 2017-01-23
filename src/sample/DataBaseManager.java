package sample;
import java.sql.*;

/**
 * Created by belikov.a on 17.01.2017.
 */
public class DataBaseManager {

    private static String urlDB = "jdbc:mysql://localhost:3306/checkDB";
    private static String user = "root";
    private static String pass = "";

    private static Connection connect = null;
    private static Statement statement = null;
    private static ResultSet result;

    DataBaseManager(String urlDB, String user, String pass){
        this.urlDB = urlDB;
        this.user = user;
        this.pass = pass;
    }
    DataBaseManager(){

    }

    public static ResultSet getResult(String query){
        try{

            if(connect == null) {
                connect = DriverManager.getConnection(urlDB, user, pass);
            }

            System.out.println(connect);

            statement = connect.createStatement();

            result = statement.executeQuery(query);

        }catch(Exception sqlEx){
            sqlEx.printStackTrace();
        }finally {
            return result;
        }
    }

    public void updateDB(String query){
        try{

            if(connect == null) {
                connect = DriverManager.getConnection(urlDB, user, pass);
            }

            if(statement == null) {
                statement = connect.createStatement();
            }

            statement.executeUpdate(query);

        }catch(Exception sqlEx) {
            sqlEx.printStackTrace();
        }finally {
            try{
                connect.close();
                statement.close();

            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    protected void finalize(){
        try{connect.close();}catch(Exception ex){ex.printStackTrace();}
        try{statement.close();}catch(Exception ex){ex.printStackTrace();}
        try{result.close();}catch(Exception ex){ex.printStackTrace();}
    }
}
