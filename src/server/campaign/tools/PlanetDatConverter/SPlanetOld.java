/*
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */

package server.campaign.tools.PlanetDatConverter;

import java.awt.Dimension;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

//import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.data.TimeUpdatePlanet;

import common.CampaignData;
import common.Continent;
import common.House;
import common.Influences;
import common.Terrain;
import common.util.Position;
import common.util.TokenReader;

public class SPlanetOld extends TimeUpdatePlanet implements Serializable, Comparable<Object> {
/**
     * 
     */
    private static final long serialVersionUID = -2266871107987235842L;
    private SHouse owner = null;

    @Override
    public String toString() {
               return "";
    }

    
    /**
     * 
     */
    public String fromString(String s, Random r, CampaignData data) {
        // debug

        boolean singleFaction = true;
        CampaignData.mwlog.mainLog(s);
        s = s.substring(3);
        StringTokenizer ST = new StringTokenizer(s, "#");
        setName(TokenReader.readString(ST));
        setCompProduction(TokenReader.readInt(ST));
        //TODO figure out what this missing bit is and add it.        // Read Factories

        int hasMF = TokenReader.readInt(ST);
        for (int i = 0; i < hasMF; i++) {
            SUnitFactoryOld mft = new SUnitFactoryOld();
            mft.fromString(TokenReader.readString(ST), this, r);
            getUnitFactories().add(mft);
        }
        int Infcount = 0;
        double xcoord = TokenReader.readDouble(ST);
        double ycoord = TokenReader.readDouble(ST);
        setPosition(new Position(xcoord,ycoord));
        try {
            HashMap<Integer, Integer> influence = new HashMap<Integer, Integer>();
            {
                StringTokenizer influences = new StringTokenizer(TokenReader.readString(ST), "$");

                while (influences.hasMoreElements()) {
                    String HouseName = TokenReader.readString(influences);
                    SHouse h = (SHouse) data.getHouseByName(HouseName);
                    int HouseInf = TokenReader.readInt(influences);
                    Infcount += HouseInf;
                    if (h != null)
                        influence.put(h.getId(), HouseInf);
                    else
                        CampaignData.mwlog.errLog("House not found: " + HouseName);
                }
            }
            // getInfluence().setInfluence(influence);
            setInfluence(new Influences(influence));
        } catch (RuntimeException ex) {
            CampaignData.mwlog.errLog("Problem on Planet: " + this.getName());
            CampaignData.mwlog.errLog(ex);
        }
        int Envs = TokenReader.readInt(ST);
        for (int i = 0; i < Envs; i++) {
            int size = TokenReader.readInt(ST);
            String terrain = TokenReader.readString(ST);
            int terrainNumber = 0;
            Terrain planetEnvironment = new Terrain();
            planetEnvironment.setName(terrain);
            String oldterrainstring = TokenReader.readString(ST);

            Continent PE = new Continent(size, planetEnvironment, null);
            getEnvironments().add(PE);
        }

        setDescription(TokenReader.readString(ST));

        this.setBaysProvided(TokenReader.readInt(ST));

        setConquerable(TokenReader.readBoolean(ST));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            setTimestamp(sdf.parse(TokenReader.readString(ST)));
        } catch (Exception ex) {
            // No biggy, but will cause senseless Data transfer, so:
            CampaignData.mwlog.errLog("The following excepion is not critical, but will cause useless bandwith usage: please fix!");
            CampaignData.mwlog.errLog(ex);
            setTimestamp(new Date(System.currentTimeMillis()));
        }

        int id = TokenReader.readInt(ST);
        if (id == -1) {
        	id = CampaignData.cd.getUnusedPlanetID();
        }
        setId(id);
//        setId(-1);
        int x = (TokenReader.readInt(ST));
        int y = (TokenReader.readInt(ST));
        setMapSize(new Dimension(x, y));

        x = (TokenReader.readInt(ST));
        y = (TokenReader.readInt(ST));
        setBoardSize(new Dimension(x, y));

        x = (TokenReader.readInt(ST));
        y = (TokenReader.readInt(ST));
        
        TokenReader.readDouble(ST); //read gravity

        TokenReader.readBoolean(ST); //read vacuum
        int chance = (TokenReader.readInt(ST));
        int mod = (TokenReader.readInt(ST));

        setMinPlanetOwnerShip(TokenReader.readInt(ST));

        setHomeWorld(TokenReader.readBoolean(ST));

        setOriginalOwner(TokenReader.readString(ST));

        StringTokenizer str = new StringTokenizer(TokenReader.readString(ST), "^");
        TreeMap<String, String> map = new TreeMap<String, String>();
        while (str.hasMoreTokens()) {
            String key = TokenReader.readString(str);
        }
        this.setPlanetFlags(map);

        this.setConquestPoints(TokenReader.readInt(ST));

        updateInfluences();

        if (singleFaction) {

            if (isNullOwner()) {
                this.setConquestPoints(100);
                //this.setCompProduction(0);
                this.setBaysProvided(0);
                SHouse house = new SHouse();
                house.setName("SOL");
                this.getInfluence().moveInfluence(house, house, 100, 100);
            }
        }

        setOwner(null, checkOwner(), false);

        return s;
    }

    /**
     * Use the other constructor as soon as you do not need the manual serialization support through fromString() anymore.
     */
    public SPlanetOld() {
        // super(CampaignMain.cm.getData().getUnusedPlanetID(),"", new
        // Position(0,0), null);
        super();
        setTimestamp(new Date(0));
        setOriginalOwner("SOL");
    }

    public SPlanetOld(int id, String name, Influences flu, int income, int CompProd, double xcood, double ycood) {
        super(id, name, new Position(xcood, ycood), flu);
        setCompProduction(CompProd);
        setTimestamp(new Date(0));
        setOriginalOwner("SOL");
    }


    public Vector<SUnitFactoryOld> getFactoriesByName(String s) {
        Vector<SUnitFactoryOld> result = new Vector<SUnitFactoryOld>(getUnitFactories().size(), 1);
        for (int i = 0; i < getUnitFactories().size(); i++) {
            SUnitFactoryOld MF = (SUnitFactoryOld) getUnitFactories().get(i);
            if (MF.getName().equals(s))
                result.add(MF);
        }
        return result;
    }

    public Vector<SUnitFactoryOld> getFactoriesOfWeighclass(int weightclass) {
        Vector<SUnitFactoryOld> result = new Vector<SUnitFactoryOld>(getUnitFactories().size(), 1);
        for (int i = 0; i < getUnitFactories().size(); i++) {
            SUnitFactoryOld MF = (SUnitFactoryOld) getUnitFactories().get(i);
            if (MF.getWeightclass() == weightclass)
                result.add(MF);
        }
        return result;
    }

    /**
     * @param Attacker -
     *            attacking faction
     * @return potential defending houses (ie - those with territory on the world)
     */
    public Vector<House> getDefenders(SHouse Attacker) {
        Vector<House> result = new Vector<House>(getInfluence().getHouses());
        result.trimToSize();
        /*
         * Iterator it = getInfluence().getHouses().iterator(); while (it.hasNext()) { SHouse h = (SHouse) it.next(); //if (!h.equals(Attacker) || Attacker.isInHouseAttacks()) result.add(h); }
         */
        return result;
    }

    @Override
    public boolean equals(Object o) {

        SPlanetOld p = null;
        try {
            p = (SPlanetOld) o;
        } catch (ClassCastException e) {
            return false;
        }

        if (o == null)
            return false;

        return p.getId() == this.getId();
    }


    public String getSmallStatus(boolean useHTML) {

        StringBuilder result = new StringBuilder();
        if (useHTML)
            result.append(this.getNameAsColoredLink());
        else
            result.append(getName());

        for (int i = 0; i < getUnitFactories().size(); i++) {
            SUnitFactoryOld MF = (SUnitFactoryOld) getUnitFactories().get(i);
            result.append(" [" + MF.getSize() + "," + MF.getFounder() + "," + MF.getTypeString() + "]");
        }

        result.append(":");
        for (House h : getInfluence().getHouses()) {
            result.append(h.getName() + "(" + getInfluence().getInfluence(h.getId()) + "cp)");
            result.append(", ");

        }
        if (useHTML)
            result.replace(result.length() - 2, result.length(), "<br>");
        return result.toString();
    }

    public SHouse checkOwner() {

        if (getInfluence() == null) {
            CampaignData.mwlog.errLog("getINF == null Planet: " + getName());
            return null;
        }

        SHouse h = null;
        Integer houseID = this.getInfluence().getOwner();

        if (houseID == null)
            return null;


        return h;
    }

    public SHouse getOwner() {
        /*
         * Null owner is possible, but should be uncommon. Check the owner again to make sure the this is true before returning.
         */
        if (owner == null)
            checkOwner();
        return owner;
    }

    public void setOwner(SHouse oldOwner, SHouse newOwner, boolean sendHouseUpdates) {

      owner = newOwner;
    }

    public int doGainInfluence(SHouse winner, SHouse loser, int amount, boolean adminExchange) {

        if (!winner.isConquerable() && !adminExchange)
            return 0;

        int infgain = getInfluence().moveInfluence(winner, loser, amount, this.getConquestPoints());
        // dont bother with updates if land has not changed hands.
        if (infgain > 0) {

            // winner.updated();
            // loser.updated();
            this.updated();

            SHouse oldOwner = owner;
            SHouse newOwner = checkOwner();
            setOwner(oldOwner, newOwner, true);
        }
        return infgain;
    }

    public String getShortDescription(boolean withTerrain) {
        StringBuilder result = new StringBuilder(getName());
        if (withTerrain) {
            Continent p = getEnvironments().getBiggestEnvironment();
            Terrain pe = p.getEnvironment();
            if (pe != null && pe.getEnvironments().size() > 0)
                result.append(" " + pe.getEnvironments().get(0).toImageDescription());

            if (this.getUnitFactories().size() > 0) {
                for (int i = 0; i < this.getUnitFactories().size(); i++) {
                    SUnitFactoryOld MF = ((SUnitFactoryOld) this.getUnitFactories().get(i));
                    result.append(MF.getIcons());
                }
            }
            if (pe != null && getEnvironments().getTotalEnivronmentPropabilities() > 0)
                result.append(" (" + Math.round((double) p.getSize() * 100 / getEnvironments().getTotalEnivronmentPropabilities()) + "% correct)");
            else
                result.append(" (100% correct)");
        }
        return result.toString();
    }

    /**
     * Method which returns a coloured link name for a planet.
     */
    public String getNameAsColoredLink() {

        String colorString = "";
        if (owner == null) {
            colorString = "black";// malformed
            // gets
            // you
            // black?
        } else
            colorString = owner.getHouseColor();

        String toReturn = "<font color=\"" + colorString + "\">" + getNameAsLink() + "</font>";
        return toReturn;
    }

    @Override
    public int getMinPlanetOwnerShip() {

        int ownership = super.getMinPlanetOwnerShip();
        if (ownership < 0)
            ownership = 0;

        return ownership;
    }

    public boolean isNullOwner() {

        if (this.getInfluence().getInfluence(-1) == this.getConquestPoints())
            return true;

        return false;
    }

}