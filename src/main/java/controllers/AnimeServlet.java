package controllers;

import models.AnimEntity;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.UserEntity;
import utility.database.GetDataFromDB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Servlets.AnimeServlet", urlPatterns = "/title")
public class AnimeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = req.getServletContext();
        if(servletContext.getAttribute("myAnim") != null) {
            AnimEntity currentAnim = (AnimEntity) servletContext.getAttribute("myAnim");
            req.setAttribute("myAnimEntity", servletContext.getAttribute("myAnim"));
            UserEntity currentUser = (UserEntity) req.getSession().getAttribute("currentUser");
            if(currentUser.getListViewed().size() > 0) {
                boolean flagToUnic = true;
//                AnimEntity currentAnim = (AnimEntity) servletContext.getAttribute("myAnim");
                for(int i = 0; i < currentUser.getListViewed().size(); i++) {
                    if (currentUser.getListViewed().get(i).getId() == currentAnim.getId()) {
                        flagToUnic = false;
                        break;
                    }
                }
                if(flagToUnic) {
                    addAnimToCurrentUser(req, servletContext, currentAnim, currentUser);
                }
            } else {
                addAnimToCurrentUser(req, servletContext, currentAnim, currentUser);
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/anime_page.jsp").forward(req, resp);
    }

    private void addAnimToCurrentUser(HttpServletRequest req, ServletContext servletContext, AnimEntity currentAnim, UserEntity currentUser) {
        currentUser.getListViewed().add((AnimEntity) servletContext.getAttribute("myAnim"));
        Long id = currentAnim.getId();
        List<Integer> listId = currentUser.getListViewedId();
        List<Integer> listIdCopy = new ArrayList<>(listId);
        listIdCopy.add(id.intValue());
        req.getSession().setAttribute("currentListViewedId", listIdCopy);
//        System.out.println(req.getSession().getAttribute("currentListViewedId"));
//        System.out.println(req.getSession().getAttribute("currentUser"));

        try {
            GetDataFromDB.setListViewed((List<Integer>) req.getSession().getAttribute("currentListViewedId"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String myAnimId = req.getParameter("myTitle");
        AnimEntity myAnim = (AnimEntity) req.getSession().getAttribute(myAnimId);
        req.getSession().removeAttribute(myAnimId);
        ServletContext servletContext = req.getServletContext();
        servletContext.setAttribute("myAnim", myAnim);
        resp.sendRedirect("/title");
    }
}
