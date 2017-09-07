package controllers;

import models.Player;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import views.html.profile;

import java.util.UUID;

import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class Profiles {

    public static Result profile(String input) {
        Player player;

        if (input.length() == 36) {
            player = new Player(UUID.fromString(input));
        } else if (input.length() <= 16 && input.matches("^[a-zA-Z0-9_]*$")) {
            player = new Player(input);
        } else {
            return ok("Invalid username/UUID");
        }

        if (!player.isLoaded()) {
            return ok("Player doesn't exist!");
        }

        Logger.info("Rendering profile for " + player.getName());

        return ok(profile.render(player));
    }

    public static Result search() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String input = requestData.get("input");

        return redirect("profile/" + input);

    }

}
