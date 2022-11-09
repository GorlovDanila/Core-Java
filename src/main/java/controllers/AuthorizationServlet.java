package controllers;

import models.AnimEntity;
import utility.database.GetDataFromDB;
import utility.database.PostgresConnectionProvider;
import models.UserEntity;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utility.cryptography.BCrypt;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "Servlets.AuthorizationServlet", urlPatterns = "/authorization")
public class AuthorizationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = req.getServletContext();
        if(servletContext.getAttribute("userIsExists") != null) {
            req.setAttribute("userIsExists", servletContext.getAttribute("userIsExists"));
        }
        req.getRequestDispatcher("/WEB-INF/views/authorization_page.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //Connection connection = PostgresConnectionProvider.getConnection();
        boolean flagUserIsExists = false;
        UserEntity currentUser = new UserEntity();
        try {
            Connection connection = PostgresConnectionProvider.getConnection();
            if (req.getParameter("personLogin") != null && req.getParameter("personPassword") != null && !req.getParameter("personLogin").equals("") && !req.getParameter("personPassword").equals("")) {
                List<UserEntity> users = new ArrayList<>();
                boolean flagPasswordTrue = false;
                boolean flagLoginTrue = false;
                String sqlQuery = "SELECT id, password, role, viewed_id FROM users WHERE login = ?";
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, req.getParameter("personLogin"));
                ResultSet resultSet = statement.executeQuery();
                currentUser.setLogin(req.getParameter("personLogin"));
                while (resultSet.next()) {
                    flagLoginTrue = true;
                    UserEntity user = new UserEntity();
                    user.setLogin(req.getParameter("personLogin"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(resultSet.getString("role"));
                    user.setId(resultSet.getLong("id"));
                    if(resultSet.getArray("viewed_id") != null) {
                        Array arrayIdSql = resultSet.getArray("viewed_id");
                        Integer[] arrayId = (Integer[]) arrayIdSql.getArray();
                        user.setListViewedId(Arrays.stream(arrayId).toList());
                       // user.setListViewedId((List<Integer>) resultSet.getArray("viewed_id"));
                    } else {
                        user.setListViewedId(new ArrayList<>());
                    }
                    users.add(user);
                }
                if (users.size() > 0) {
                    for (UserEntity user : users) {
                        if (BCrypt.passwordDecryptHash(req.getParameter("personPassword"), user.getPassword())) {
                            flagPasswordTrue = true;
                            currentUser.setId(user.getId());
                            currentUser.setRole(user.getRole());
                            currentUser.setPassword(req.getParameter("personPassword"));
                            List<AnimEntity> animes = GetDataFromDB.getAnimTableData();
                            List<Integer> listViewedId;
                            if(user.getListViewedId() != null) {
                                listViewedId = user.getListViewedId();
                                currentUser.setListViewedId(user.getListViewedId());
                            } else {
                                listViewedId = new ArrayList<>();
                                currentUser.setListViewedId(new ArrayList<>());
                            }
                            List<AnimEntity> listViewed = new ArrayList<>();
                            if(listViewedId != null && listViewedId.size() > 0) {
                                for (AnimEntity anim : animes) {
                                    for (Integer i : listViewedId) {
                                        if (anim.getId() == i) {
                                            listViewed.add(GetDataFromDB.getAnimById(i));
                                        }
                                    }
                                }
                            }
                            currentUser.setListViewed(listViewed);
                            break;
                        }
                    }
                }
                if (flagLoginTrue && flagPasswordTrue) {
                    req.getSession().setAttribute("currentId", currentUser.getId());
                    req.getSession().setAttribute("currentListViewedId", currentUser.getListViewedId());
                    req.getSession().setAttribute("currentListViewed", currentUser.getListViewed());
                    req.getSession().setAttribute("currentLogin", req.getParameter("personLogin"));
                    req.getSession().setAttribute("currentPassword", req.getParameter("personPassword"));
                    req.getSession().setAttribute("currentRole", currentUser.getRole());
                    flagUserIsExists = true;
                }
                ServletContext servletContext = req.getServletContext();
                servletContext.setAttribute("userIsExists", flagUserIsExists);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        if(flagUserIsExists && currentUser.getRole().equals("default")) {
            resp.sendRedirect("/");
        } else if(flagUserIsExists && currentUser.getRole().equals("admin")) {
            resp.sendRedirect("/admin");
        } else {
            resp.sendRedirect("/registration");
        }
    }
}
