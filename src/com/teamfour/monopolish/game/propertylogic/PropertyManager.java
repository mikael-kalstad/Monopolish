package com.teamfour.monopolish.game.propertylogic;

import java.util.ArrayList;

public class PropertyManager {
    private ArrayList<Property> properties;
    private PropertyDAO dao = new PropertyDAO();

    public PropertyManager(int game_id){
        properties = dao.getAllProperties(game_id);
    }

    public ArrayList<Property> getAvailableProteries(){
        ArrayList<Property> available = getPlayerProperties(0);
        return (available);
    }

    public ArrayList<Property> getPlayerProperties(int o_id){
        ArrayList<Property> available =null;
        for(int i = 0; i<properties.size(); i++){
            if(properties.get(i).getOwner() == o_id){
                available.add(properties.get(i));
            }
        }
        return (available);
    }

    public Property getPropertyAt(int pos){

        for(int i = 0; i<properties.size(); i++){
            if(properties.get(i).getPosition() == pos){
                return(properties.get(i));
            }
        }
        return (null);
    }

    public Property getPropertyById(int p_id){
        for(int i = 0; i<properties.size(); i++){
            if(p_id == properties.get(i).getId()){
                return(properties.get(i));
            }
        }
        return(null);
    }

    public void setOwner(int p_id, int owner){
        Property prop = getPropertyById(p_id);
        prop.setOwner(owner);
        dao.updateProperty(prop);
    }

    public void pawn(int p_id){
        Property prop = getPropertyById(p_id);
        prop.setPawned(true);
        dao.updateProperty(prop);
    }

    public void unPawn(int p_id){
        Property prop = getPropertyById(p_id);
        prop.setPawned(false);
        dao.updateProperty(prop);
    }

    public int getPropertyValueByPlayer(int player_id){
        ArrayList<Property> player_prop = getPlayerProperties(player_id);
        int money = 0;
        for(int i = 0; i<player_prop.size(); i++){
            money += player_prop.get(i).getPrice();
        }
        return(money);
    }

    public void terminateGame(){
        dao.endGame();
    }
}
