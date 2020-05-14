/*
 *  MekWars - Copyright (C) 2007 
 * 
 *  original author - Nathan Morris (urgru@users.sourceforge.net)
 *  Change by Jason Tighe (torren@users.sourceforge.net)
 * 
 *  This program is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU General Public License as published by the Free 
 *  Software Foundation; either version 2 of the License, or (at your option) 
 *  any later version.
 * 
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 *  for more details.
 *  
 *  Sort used for Armies
 */
 
 package client.util;
 
 import java.util.Comparator;

import client.campaign.CArmy;

 public class CArmyComparator implements Comparator<Object> {

 	//NOTE: This order must match order of
 	//sort options in CPLayer.sortArmies()'s
 	//"choices" array.
 	public static final int ARMYSORT_NAME = 0;
 	public static final int ARMYSORT_BV = 1;
 	public static final int ARMYSORT_ID = 2;
 	public static final int ARMYSORT_TONNAGE = 3;
 	public static final int ARMYSORT_AVGMPWALK = 4;
 	public static final int ARMYSORT_AVGMPJUMP = 5;
 	public static final int ARMYSORT_UNITS = 6;
 	public static final int ARMYSORT_NONE = 7;
 	
 	private int sortOrder;
 	
 	public CArmyComparator(int sortOrder) {
 		this.sortOrder = sortOrder;
 	}
 	
 	public int compare(Object obj1, Object obj2) {
 		
 		CArmy army1 = (CArmy)obj1;
 		CArmy army2 = (CArmy)obj2;
 		
 		switch (sortOrder) {
 		
 			case ARMYSORT_NAME : //the name
				return army1.getName().compareTo(army2.getName());
 		
 			case ARMYSORT_BV : //self evident
 				Integer army1BV = new Integer(army1.getBV());
 				Integer army2BV = new Integer(army2.getBV());
 				return army1BV.compareTo(army2BV);
 				
 			case ARMYSORT_ID : //the unique unit ID
 				Integer army1ID = new Integer(army1.getID());
 				Integer army2ID = new Integer(army2.getID());
 				return army1ID.compareTo(army2ID);	

 			case ARMYSORT_TONNAGE: //Total tonnage of the army
 				Float army1Ton = new Float(army1.getTotalTonnage());
 				Float army2Ton = new Float(army2.getTotalTonnage());
 				return army1Ton.compareTo(army2Ton);

 			case ARMYSORT_AVGMPWALK : //average walk MP for the army
 				Integer army1MP = new Integer(army1.getAverageWalk());
 				Integer army2MP = new Integer(army2.getAverageWalk());
 				return army1MP.compareTo(army2MP);	

 			case ARMYSORT_AVGMPJUMP : //average jump mp of the army
 				Integer army1JP = new Integer(army1.getAverageJump());
 				Integer army2JP = new Integer(army2.getAverageJump());
 				return army1JP.compareTo(army2JP);	

 			case ARMYSORT_UNITS:
 				Integer army1Size = new Integer(army1.getUnits().size());
 				Integer army2Size = new Integer(army2.getUnits().size());
 				return army1Size.compareTo(army2Size);
 				
 			default :
 				return 0;
 		}//end switch
 	}//end compare()

}