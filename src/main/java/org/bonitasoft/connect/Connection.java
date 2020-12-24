package org.bonitasoft.connect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.identity.ContactData;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserCriterion;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.util.APITypeManager;

public class Connection {

    public static void main(String[] args) {

        final Logger logger = Logger.getLogger("org.bonitasoft.connection");
        if (args.length < 4) {
            logger.info("usage: <Url> <applicationname> <user> <password> [action:USERS]");
            logger.info("example: http://localhost:8080 bonita walter.bates bpm USERS");
            return;
        }
        String url = args[0];
        String application = args[1];
        String username = args[2];
        String password = args[3];
        String action = args.length > 4 ? args[4] : null;
        logger.info("Connection url[" + url + "] Application[" + application + "] user[" + username + "] password[" + password + "]");
        try {
            long beg= System.currentTimeMillis();
            final Map<String, String> map = new HashMap<String, String>();
            map.put("server.url", url);
            map.put("application.name", application);
            APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, map);

            // Set the username and password
            // get the LoginAPI using the TenantAPIAccessor
            final LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
            // log in to the tenant to create a session
            final APISession session = loginAPI.login(username, password);
            long end= System.currentTimeMillis();
            logger.info("    *****************  Successful connection in "+(end-beg)+" ms ***************** ");

            if ("USERS".equalsIgnoreCase(action)) {
                // get the identityAPI bound to the session created previously.
                final IdentityAPI identity = TenantAPIAccessor.getIdentityAPI(session);

                // get the list of users
                final List<User> user = identity.getUsers(0, 5, UserCriterion.FIRST_NAME_ASC);

                // display the list of userNames 

                for (int i = 0; i < user.size(); i++) {
                    ContactData contact = identity.getUserContactData(user.get(i).getId(), false);
                    logger.info(user.get(i).getUserName() + " email:" + (contact == null ? "no email" : contact.getEmail()));
                }
            }

            loginAPI.logout(session);
        } catch (final Exception e) {
            logger.severe(" **************  ERROR during connection url[" + url + "] Application[" + application + "] user[" + username + "] password[" + password + "] : " + e.getMessage());
        }
    }

}
