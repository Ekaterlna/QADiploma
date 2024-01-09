package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private static final QueryRunner runner = new QueryRunner();

    private SQLHelper() {
    }

    @SneakyThrows
    private static Connection getConn() throws SQLException {
        var url = System.getProperty("url");
        var username = System.getProperty("username");
        var password = System.getProperty("password");
        return DriverManager.getConnection(url, username, password);
    }

    @SneakyThrows
    public static void cleanDatabase() {
        var connection = getConn();
        runner.execute(connection, "DELETE FROM payment_entity ");
        runner.execute(connection, "DELETE FROM credit_request_entity");
        runner.execute(connection, "DELETE FROM order_entity");
    }

    @SneakyThrows
    public static String getPaymentStatus() {
        var statusSQL = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, statusSQL, new ScalarHandler<String>());
    }

    @SneakyThrows
    public static String getCreditStatus() {
        var statusSQL = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, statusSQL, new ScalarHandler<String>());
    }

    @SneakyThrows
    public static String getPaymentID() {
        var idSQL = "SELECT transaction_id FROM payment_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, idSQL, new ScalarHandler<String>());
    }

    @SneakyThrows
    public static String getCreditID() {
        var idSQL = "SELECT bank_id FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, idSQL, new ScalarHandler<String>());
    }

    @SneakyThrows
    public static String getPaymentOrderID() {
        var idSQL = "SELECT payment_id FROM order_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, idSQL, new ScalarHandler<String>());
    }

    @SneakyThrows
    public static String getCreditOrderID() {
        var idSQL = "SELECT credit_id FROM order_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, idSQL, new ScalarHandler<String>());
    }
}
