package com.teamfour.monopolish.game.PropertyLogic;

import java.util.ArrayList;

public class PropertyManager {
    private ArrayList<Property> properties;
    private PropertyDAO dao = new PropertyDAO();

    public PropertyManager(){
        properties = dao.getAllProperties();
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

    public Property getPropertById(int p_id){
        for(int i = 0; i<properties.size(); i++){
            if(p_id == properties.get(i).getP_id()){
                return(properties.get(i));
            }
        }
        return(null);
    }

    public void setOwner(int p_id, int owner){
        Property prop = getPropertById(p_id);
        prop.setOwner(owner);
        dao.updateProperty(prop);
    }

    public void pawn(int p_id){
        Property prop = getPropertById(p_id);
        prop.setPawned(true);
        dao.updateProperty(prop);
    }

    public void unPawn(int p_id){
        Property prop = getPropertById(p_id);
        prop.setPawned(false);
        dao.updateProperty(prop);
    }
}
