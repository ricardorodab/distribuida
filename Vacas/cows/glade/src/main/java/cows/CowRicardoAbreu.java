package cows;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


import main.Action;
import main.ActionType;
import main.Config;
import main.GlobalRandom;
import main.ICow;
import main.LocalInfo;

public class CowRicardoAbreu implements ICow{

    public Color getColor() {
	return new Color(100, 6, 200);
    }	
    class Cell{int di,dj; Cell(int di, int dj){this.di = di; this.dj = dj;}}
    
    public Action make_turn(LocalInfo li){
	//Determine free cells
	List<Cell> freeCells = new ArrayList<Cell>();
	for(int i = 0; i < 3; i++)
	    for(int j = 0; j < 3; j++){
		if(li.nc[i][j] != null && li.nc[i][j].cowID == null){
		    freeCells.add(new Cell(i,j));
		}
	    }	
	//can't move, can't breed, so try to eat until the end...
	if(freeCells.isEmpty())
	    return new Action(ActionType.EAT,0,0);
	Cell freeCell;
	freeCell= freeCells.get(GlobalRandom.getInstance().rand.nextInt(freeCells.size()));
	if(li.grassEaten >= 15)
	    return new Action(ActionType.BREED,freeCell.di-1,freeCell.dj-1);
	if(li.nc[1][1].grass > 0)
	    return new Action(ActionType.EAT,0,0);	
	return new Action(ActionType.MOVE,freeCell.di-1,freeCell.dj-1);
	
    }

    public Integer getSTUDENTid(){
	return 309216139;
    }

    public String getName(){
	return "Ricardo-Abreu";
    }

    public ICow clone(){
	return new CowRicardoAbreu();
    }

    public boolean skipPopulation(){
	return false;
    }
}
