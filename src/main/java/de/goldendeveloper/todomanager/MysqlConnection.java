package de.goldendeveloper.todomanager;

import de.goldendeveloper.mysql.MYSQL;
import de.goldendeveloper.mysql.entities.Database;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.mysql.exceptions.NoConnectionException;
import de.goldendeveloper.todomanager.discord.utility.TodoTypes;

import java.sql.SQLException;

public class MysqlConnection {

    private final MYSQL mysql;
    public static String dbName = "GD-TodoManager";
    public static String settingTable = "settings";
    public static String clmGuildID = "guild";
    public static String clmPermRole = "role";

    public MysqlConnection(String hostname, String username, String password, int port) throws NoConnectionException, SQLException {
        mysql = new MYSQL(hostname, username, password, port);
        if (!mysql.existsDatabase(dbName)) {
            mysql.createDatabase(dbName);
        }
        Database db = mysql.getDatabase(dbName);
        if (!db.existsTable(settingTable)) {
            db.createTable(settingTable);
        }
        Table table = db.getTable(settingTable);
        if (!table.existsColumn(clmGuildID)) {
            table.addColumn(clmGuildID);
        }
        if (!table.existsColumn(clmPermRole)) {
            table.addColumn(clmPermRole);
        }
        TodoTypes.getAllTodoTypes().forEach(todoType -> {
            if (!table.existsColumn(todoType.getColumnName())) {
                table.addColumn(todoType.getColumnName());
            }
        });
        System.out.println("MYSQL Finished");
    }

    public MYSQL getMysql() {
        return mysql;
    }
}
