package utility.database;

import models.AnimEntity;
import models.AuthorEntity;
import models.GenreEntity;
import models.UserEntity;
import utility.listeners.SessionListener;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GetDataFromDB {

    public static List<AnimEntity> getAnimTableData() throws SQLException {
        Connection myConnection = PostgresConnectionProvider.getConnection();
        List<AnimEntity> anime = new ArrayList<>();
        String sqlQuery = "SELECT * FROM animes";
        PreparedStatement statement = myConnection.prepareStatement(sqlQuery);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            AnimEntity animEntity = new AnimEntity();
            animEntity.setId(resultSet.getLong("id"));
            animEntity.setTitle(resultSet.getString("title"));
            animEntity.setGenre(resultSet.getString("genre"));
            animEntity.setAuthor(resultSet.getString("author"));
            animEntity.setLink(resultSet.getString("link"));
            animEntity.setLinkToImage(resultSet.getString("link_to_image"));
            animEntity.setYear(resultSet.getString("year"));
            animEntity.setType(resultSet.getString("type"));
            animEntity.setStatus(resultSet.getString("status"));
            anime.add(animEntity);
        }
        return anime;
    }

    public static List<AuthorEntity> getAuthorsTableData() throws SQLException {
        Connection myConnection = PostgresConnectionProvider.getConnection();
        List<AuthorEntity> authorEntities = new ArrayList<>();
        String sqlQuery = "SELECT * FROM authors";
        PreparedStatement statement = myConnection.prepareStatement(sqlQuery);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            AuthorEntity authorEntity = new AuthorEntity();
            authorEntity.setId(resultSet.getLong("id"));
            authorEntity.setName(resultSet.getString("name"));
            authorEntity.setInfo(resultSet.getString("info"));
            authorEntities.add(authorEntity);
        }
        return authorEntities;
    }

    public static List<GenreEntity> getGenresTableData() throws SQLException {
        Connection myConnection = PostgresConnectionProvider.getConnection();
        List<GenreEntity> genreEntities = new ArrayList<>();
        String sqlQuery = "SELECT * FROM genres";
        PreparedStatement statement = myConnection.prepareStatement(sqlQuery);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            GenreEntity genreEntity = new GenreEntity();
            genreEntity.setId(resultSet.getLong("id"));
            genreEntity.setName(resultSet.getString("name"));
            genreEntity.setInfo(resultSet.getString("info"));
            genreEntities.add(genreEntity);
        }
        return genreEntities;
    }

    public static List<UserEntity> getAllUsers() throws SQLException {
        Connection myConnection = PostgresConnectionProvider.getConnection();
        List<UserEntity> allUsers = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users";
        PreparedStatement statement = myConnection.prepareStatement(sqlQuery);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(resultSet.getLong("id"));
            userEntity.setLogin(resultSet.getString("login"));
            userEntity.setPassword(resultSet.getString("password"));
            userEntity.setRole(resultSet.getString("role"));
            userEntity.setListViewedId((List<Integer>) resultSet.getArray("viewed_id"));
            allUsers.add(userEntity);
        }
        return allUsers;
    }

    public static AnimEntity getAnimById(long id) throws SQLException {
        Connection myConnection = PostgresConnectionProvider.getConnection();
        AnimEntity result = new AnimEntity();
        String sqlQuery = "SELECT * FROM animes WHERE id = ?";
        PreparedStatement statement = myConnection.prepareStatement(sqlQuery);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            AnimEntity animEntity = new AnimEntity();
            animEntity.setId(resultSet.getLong("id"));
            animEntity.setTitle(resultSet.getString("title"));
            animEntity.setGenre(resultSet.getString("genre"));
            animEntity.setAuthor(resultSet.getString("author"));
            animEntity.setLink(resultSet.getString("link"));
            animEntity.setLinkToImage(resultSet.getString("link_to_image"));
            animEntity.setYear(resultSet.getString("year"));
            animEntity.setType(resultSet.getString("type"));
            animEntity.setStatus(resultSet.getString("status"));
            result = animEntity;
        }
        return result;
    }

    public static void setListViewed(List<Integer> list) throws SQLException {
        Integer[] arrayId = list.toArray(new Integer[0]);
        Connection myConnection = PostgresConnectionProvider.getConnection();
        Array arrayIdSql = myConnection.createArrayOf("integer", arrayId);
        String sqlQuery = "UPDATE users SET viewed_id = ? WHERE id = ?";
        PreparedStatement statement = myConnection.prepareStatement(sqlQuery);
        statement.setArray(1, arrayIdSql);
        statement.setLong(2, SessionListener.getMyCurrentUser().getId());
        statement.execute();
    }
}
