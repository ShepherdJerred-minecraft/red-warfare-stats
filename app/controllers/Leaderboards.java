package controllers;

import models.Mode;
import models.Stats;
import org.apache.commons.lang3.EnumUtils;
import play.Logger;
import play.mvc.Result;

import static play.mvc.Results.ok;


public class Leaderboards {

    public static Result selector() {
        return ok(views.html.leaderboards.selector.render());
    }

    public static Result view(String mode) {
        if (!EnumUtils.isValidEnum(Mode.ModeName.class, mode)) {
            return ok("Not a valid mode!");
        }

        Logger.info("Rendering " + mode + " leaderboard");

        return ok(views.html.leaderboards.viewer.render(new Mode(Mode.ModeName.valueOf(mode))));
    }

    public static Result viewStat(String mode, String stat) {
        if (!EnumUtils.isValidEnum(Mode.ModeName.class, mode)) {
            return ok("Not a valid mode!");
        }

        if (!EnumUtils.isValidEnum(Stats.Type.class, stat)) {
            return ok("Not a valid stat!");
        }

        Logger.info("Rendering " + mode + " leaderboard");

        return ok(views.html.leaderboards.viewer.render(new Mode(Mode.ModeName.valueOf(mode), Stats.Type.valueOf(stat))));
    }

    public static Result viewSort(String mode, String stat, String sort) {
        if (!EnumUtils.isValidEnum(Mode.ModeName.class, mode)) {
            return ok("Not a valid mode!");
        }

        if (!EnumUtils.isValidEnum(Stats.Type.class, stat)) {
            return ok("Not a valid stat!");
        }

        if (!EnumUtils.isValidEnum(Sort.class, sort)) {
            return ok("Not a valid sort!");
        }

        Logger.info("Rendering " + mode + " leaderboard");

        return ok(views.html.leaderboards.viewer.render(new Mode(Mode.ModeName.valueOf(mode), Stats.Type.valueOf(stat), Sort.valueOf(sort))));
    }

    public static Result viewPage(String mode, String stat, String sort, String page) {
        if (!EnumUtils.isValidEnum(Mode.ModeName.class, mode)) {
            return ok("Not a valid mode!");
        }

        if (!EnumUtils.isValidEnum(Stats.Type.class, stat)) {
            return ok("Not a valid stat!");
        }

        if (!EnumUtils.isValidEnum(Sort.class, sort)) {
            return ok("Not a valid sort!");
        }

        Logger.info("Rendering " + mode + " leaderboard");

        return ok(views.html.leaderboards.viewer.render(new Mode(Mode.ModeName.valueOf(mode), Stats.Type.valueOf(stat), Sort.valueOf(sort), Integer.valueOf(page))));
    }

    public enum Sort {
        ASC, DESC
    }

}
