package sqlite_crash;

import org.sqlite.SQLiteConnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Main
{
    public static void main(String[] args) throws IOException, SQLException
    {
        List<Double> values = new ArrayList<>();

        Random r = new Random();
        for (int i = 0; i < 30_000; i++) {
            values.add((double) (r.nextInt(1_400_000)));
        }
        values.sort((o1, o2) -> -o1.compareTo(o2));

        SQLiteConnection connection = (SQLiteConnection) DriverManager.getConnection("jdbc:sqlite::memory:");
        connection.setAutoCommit(true);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE table_0(\"num\" real NOT NULL)");
        }

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO table_0 VALUES (?)")) {
            for(Double value: values) {
                stmt.setDouble(1, value);
                stmt.execute();
            }
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SELECT MEDIAN(\"num\") FROM table_0");
        }
    }
}
