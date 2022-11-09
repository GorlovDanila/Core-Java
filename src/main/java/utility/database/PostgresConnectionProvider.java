package utility.database;

import java.sql.*;

public class PostgresConnectionProvider {

    private static boolean flagExist;
    private static Statement statement;
    private static int count = 0;
    private static Connection connection;
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(PostgresDBConnectionData.DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to find driver class :(");
        }

        try {
            connection = DriverManager.getConnection(PostgresDBConnectionData.URL,
                    PostgresDBConnectionData.USERNAME,
                    PostgresDBConnectionData.PASSWORD);
            statement = connection.createStatement();
            if(count == 0) {
                count++;
                if (connection.getMetaData().getMaxColumnsInSelect() != 9) {
                    String sql = "CREATE DATABASE Semestrwork";
                    statement.executeUpdate(sql);
                    connection = DriverManager.getConnection(PostgresDBConnectionData.URL + "Semestrwork",
                            PostgresDBConnectionData.USERNAME,
                            PostgresDBConnectionData.PASSWORD);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Unable to receive connection :(");
        }
        return connection;
    }
}
