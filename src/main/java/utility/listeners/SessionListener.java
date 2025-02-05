package utility.listeners;

import models.AnimEntity;
import models.UserEntity;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;

import java.util.List;

@WebListener
public class SessionListener implements HttpSessionAttributeListener {
    private static final UserEntity myCurrentUser = new UserEntity();

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        userCreate(event);
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (event.getSession().getAttribute("currentUser") == null) {
            event.getSession().setAttribute("currentLogin",null);
            event.getSession().setAttribute("currentPassword", null);
            event.getSession().setAttribute("currentRole", null);
            event.getSession().setAttribute("currentListViewed", null);
            event.getSession().setAttribute("currentListViewedId", null);
            event.getSession().setAttribute("currentId", null);
            myCurrentUser.setLogin(null);
            myCurrentUser.setPassword(null);
            myCurrentUser.setRole(null);
            myCurrentUser.setListViewed(null);
            myCurrentUser.setListViewedId(null);
            myCurrentUser.setId(null);
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        userCreate(event);
    }

    private void userCreate(HttpSessionBindingEvent event) {
        if (event.getSession().getAttribute("currentUser") == null && event.getSession().getAttribute("currentLogin") != null && event.getSession().getAttribute("currentPassword") != null && event.getSession().getAttribute("currentRole") != null && event.getSession().getAttribute("currentListViewed") != null && event.getSession().getAttribute("currentListViewedId") != null && event.getSession().getAttribute("currentId") != null) {
            myCurrentUser.setLogin((String) event.getSession().getAttribute("currentLogin"));
            myCurrentUser.setPassword((String) event.getSession().getAttribute("currentPassword"));
            myCurrentUser.setRole((String) event.getSession().getAttribute("currentRole"));
            myCurrentUser.setListViewed((List<AnimEntity>) event.getSession().getAttribute("currentListViewed"));
            myCurrentUser.setListViewedId((List<Integer>) event.getSession().getAttribute("currentListViewedId"));
            myCurrentUser.setId((Long) event.getSession().getAttribute("currentId"));
//            if(event.getSession().getAttribute("currentListViewedId") != null) {
//
//            }
            event.getSession().setAttribute("currentUser", myCurrentUser);
        }
    }

    public static UserEntity getMyCurrentUser() {
        return myCurrentUser;
    }
}
