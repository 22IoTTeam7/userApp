package com.example.userapplication;

import java.util.ArrayList;

public class CheckFloor {

    private int checkF2 = 0;
    private int checkF4 = 0;
    private int checkF5 = 0;

    private Floor2List listF2 = new Floor2List();
    private Floor4List listF4 = new Floor4List();
    private Floor5List listF5 = new Floor5List();

    public int getFloor(ArrayList<String> listAP){
        for(int index = 0; index < listAP.size(); index++){
            if(listF2.getList().contains(listAP.get(index))){ checkF2++; }
            if(listF4.getList().contains(listAP.get(index))){ checkF4++; }
            if(listF5.getList().contains(listAP.get(index))){ checkF5++; }
        }

        if((checkF2 > checkF4) && (checkF2 > checkF4)){
            return 2;
        }
        else if((checkF4 > checkF2) && (checkF4 > checkF5)){
            return 4;
        }
        else{
            return 5;
        }
    }
}
