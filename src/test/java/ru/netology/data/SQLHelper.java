package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
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
    public static DataHelper.PaymentEntityTableInfo getPaymentEntityTableInfo() {
        Connection conn = getConn();
        var requestSQL = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1";
        return runner.query(conn, requestSQL, new BeanHandler<>(DataHelper.PaymentEntityTableInfo.class));
    }

    @SneakyThrows
    public static DataHelper.CreditRequestEntityTableInfo getCreditRequestEntityTableInfo() {
        var requestSQL = "SELECT * FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, requestSQL, new BeanHandler<>(DataHelper.CreditRequestEntityTableInfo.class));
    }

    @SneakyThrows
    public static DataHelper.OrderEntityTableInfo getOrderEntityTableInfo() {
        var requestSQL = "SELECT * FROM order_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, requestSQL, new BeanHandler<>(DataHelper.OrderEntityTableInfo.class));
    }
}
