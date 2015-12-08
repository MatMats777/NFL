package com.example.x61224.nfl.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by x61221 on 12/7/2015.
 */
public class Player implements Serializable {

    public String name;
    public String position;
    public String original_position;
    public String number;
    public String status;
    public String id;

    public Player(String name, String position, String original_position, String number,String status,String id) {
        super();
        this.name = name;
        this.position = position;
        this.original_position = original_position;
        this.number = number;
        this.status = status;
        this.id=id;
    }

    public Player(JSONObject player,JSONObject position) throws JSONException {

        this.original_position = position.getString("name");
        this.name = player.getString("name");
        this.position = player.getString("position");
        this.number = player.getString("jersey_number");
        this.status = player.getString("status");
    }

}