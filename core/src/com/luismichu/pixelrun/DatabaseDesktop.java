package com.luismichu.pixelrun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.sql.*;

public class DatabaseDesktop extends Database {
    static Connection connection;

    @Override
    void drop(){
        try {
            loadDatabase();

            String sql = "DROP TABLE IF EXISTS scores";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            sql = "CREATE TABLE IF NOT EXISTS scores (id integer primary key autoincrement, name text, score int)";
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    void insertar(Score score){
        try {
            loadDatabase();

            String sql = "CREATE TABLE IF NOT EXISTS scores (id integer primary key autoincrement, name text, score int)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            sql = "INSERT INTO scores (name, score) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, score.name);
            statement.setInt(2, score.score);
            statement.executeUpdate();

            statement.close();
            if (connection != null)
                connection.close();
        } catch (SQLException cnfe) {
            cnfe.printStackTrace();
        }
    }

    @Override
    Array<Score> leer(){
        try {
            loadDatabase();

            String sql = "SELECT name, score FROM scores ORDER BY score DESC LIMIT 5";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            Array<Score> scores = new Array<>();
            Score score;
            while (result.next()) {
                score = new Score();
                score.name = result.getString("name");
                score.score = result.getInt("score");
                scores.add(score);
            }

            statement.close();
            result.close();
            connection.close();

            return scores;

        } catch (SQLException cnfe) {
            cnfe.printStackTrace();
        }

        return new Array<>();
    }

    private static void loadDatabase(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + Gdx.files.internal("scores.db"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
