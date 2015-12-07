package com.example.x61224.nfl.model;

import android.content.ContentValues;

import com.example.x61224.nfl.data.NFLContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by x61221 on 12/6/2015.
 */
public class Roster {

    private String team_id;
    public Vector<Player> offense;
    public Vector<Player> defense;
    public Vector<Player> special_team;

    public Roster() {
        Vector<Player> offense = new Vector<>();
        Vector<Player> defense = new Vector<>();
        Vector<Player> special_team = new Vector<>();

    }

    public Roster(String team_id,JSONObject rosterJson) throws JSONException {

        Vector<Player> offense = new Vector<>();
        Vector<Player> defense = new Vector<>();
        Vector<Player> special_team = new Vector<>();
        this.team_id=team_id;
        JSONObject offenseObject = rosterJson.getJSONObject("offense");
        JSONArray offensiveLineArray = offenseObject.getJSONArray("positions");
        JSONObject defenseObject = rosterJson.getJSONObject("defense");
        JSONArray defensiveLineArray = defenseObject.getJSONArray("positions");
        JSONObject specialObject = rosterJson.getJSONObject("special_teams");
        JSONArray specialLineArray = specialObject.getJSONArray("positions");

        for (int i = 0; i < offensiveLineArray.length(); i++) {
            JSONObject offensePositionJson = offensiveLineArray.getJSONObject(i);
            JSONArray playerArray = offensePositionJson.getJSONArray("players");

            for (int j = 0; j < playerArray.length(); j++) {
                Player entry = new Player();
                entry.position_name = offensePositionJson.getString("name");
                entry.position_desc = offensePositionJson.getString("desc");
                entry.defense_type = "";
                JSONObject playerJson = playerArray.getJSONObject(j);
                entry.player_id = playerJson.getString("id");
                entry.player_name = playerJson.getString("name");
                entry.player_position = playerJson.getString("position");
                entry.player_jersey_number = playerJson.getString("jersey_number");
                entry.player_status = playerJson.getString("status");
                entry.player_depth = playerJson.getString("depth");
                offense.add(entry);
            }
        }

        for (int i = 0; i < defensiveLineArray.length(); i++) {
            JSONObject defensePositionJson = offensiveLineArray.getJSONObject(i);
            JSONArray playerArray = defensePositionJson.getJSONArray("players");

            for (int j = 0; j < playerArray.length(); j++) {
                Player entry = new Player();
                entry.position_name = defensePositionJson.getString("name");
                entry.position_desc = defensePositionJson.getString("desc");
                entry.defense_type = defenseObject.getString("type");
                JSONObject playerJson = playerArray.getJSONObject(j);
                entry.player_id = playerJson.getString("id");
                entry.player_name = playerJson.getString("name");
                entry.player_position = playerJson.getString("position");
                entry.player_jersey_number = playerJson.getString("jersey_number");
                entry.player_status = playerJson.getString("status");
                entry.player_depth = playerJson.getString("depth");
                defense.add(entry);
            }
        }

        for (int i = 0; i < specialLineArray.length(); i++) {
            JSONObject specialPositionJson = specialLineArray.getJSONObject(i);
            JSONArray playerArray = specialPositionJson.getJSONArray("players");

            for (int j = 0; j < playerArray.length(); j++) {
                Player entry = new Player();
                entry.position_name = specialPositionJson.getString("name");
                entry.position_desc = specialPositionJson.getString("desc");
                entry.defense_type = "";
                JSONObject playerJson = playerArray.getJSONObject(j);
                entry.player_id = playerJson.getString("id");
                entry.player_name = playerJson.getString("name");
                entry.player_position = playerJson.getString("position");
                entry.player_jersey_number = playerJson.getString("jersey_number");
                entry.player_status = playerJson.getString("status");
                entry.player_depth = playerJson.getString("depth");
                special_team.add(entry);
            }
        }
    }
}