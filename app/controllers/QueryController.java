package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.Query;
import models.User;
import play.Logger;
import play.mvc.*;
import utils.InvalidUrlException;

import java.util.Map;

import static controllers.Application.getLocalUser;

public class QueryController extends Controller {

    public static Result index() {
        final User currentUser = getLocalUser(session());
        if (currentUser == null)
            return redirect(routes.Application.login());
        return ok(
                views.html.Query.index.render(Query.getAllQueriesByUser(currentUser))
        );
    }

    public static Result create() {
        Logger.debug("create a query");
        final User currentUser = getLocalUser(session());
        if (currentUser == null) {
            Logger.debug("we could not find the current user.");
            return unauthorized("you need to login");
        } else {
            Map<String, String[]> body = request().body().asFormUrlEncoded();
            String url = body.get("url")[0];
            try {
                Query q = new Query(currentUser, url);
                q.save();
                return ok("registered");
            } catch (InvalidUrlException e) {
                e.printStackTrace();
                return internalServerError();
            }
        }
    }

    public static Result delete(Long id) {
        Logger.debug("delete a query: " + id);
        final User currentUser = getLocalUser(session());
        if (currentUser == null) {
            Logger.debug("we could not find the current user.");
            return unauthorized("you need to login");
        } else {
            Query q = Query.findById(id);
            q.delete();
            return ok("deleted");
        }
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result newQuery() {
        return ok(views.html.Query.newQuery.render());
    }
}
