/*
 * MekWars - Copyright (C) 2004 
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

/* 
 * original author - @McWizard
 * rewritten extensively on 2/04/03. @urgru. 
 * 
 * factories now produce on demand, and only decrement
 * their reset counters during ticks.
 */

package server.campaign.tools.PlanetDatConverter;

import java.io.Serializable;
import java.util.Random;
import java.util.StringTokenizer;

import common.Unit;
import common.UnitFactory;
import common.util.TokenReader;


public class SUnitFactoryOld extends UnitFactory implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1735176578439214960L;
    // VARIABLES
    private SPlanetOld planet;
    
    // CONSTRUCTORS
    public SUnitFactoryOld() {
        // empty
    }

    public SUnitFactoryOld(String Name, SPlanetOld P, String Size, String Faction, int ticksuntilrefresh, int refreshSpeed, int type, String buildTableFolder, int accessLevel) {
        setName(Name);
        setPlanet(P);
        setSize(Size);
        setFounder(Faction);
        setTicksUntilRefresh(ticksuntilrefresh);
        setRefreshSpeed(refreshSpeed);
        setType(type);
        setBuildTableFolder(buildTableFolder);
        setAccessLevel(accessLevel);
    }

    // STRING SAVE METHODS
    /**
     * Used for Serialisation
     * 
     * @return A Serialised form of the UnitFactory
     */
    @Override
    public String toString() {
    	return "";
    }

    /**
     * Used to DE-Serialise a MF
     * 
     * @param s
     *            The Serialised Version
     * @param p
     *            A SPlanetOld where this MF is placed upon
     * @param r
     *            The Random Object
     */
    public void fromString(String s, SPlanetOld p, Random r) {
        s = s.substring(3);
        StringTokenizer ST = new StringTokenizer(s, "*");
        setName(TokenReader.readString(ST));
        setSize(TokenReader.readString(ST));
        setFounder(TokenReader.readString(ST));
        setTicksUntilRefresh(TokenReader.readInt(ST));
        setRefreshSpeed(TokenReader.readInt(ST));

        setBuildTableFolder(TokenReader.readString(ST));

        setType(TokenReader.readInt(ST));
        setLock(TokenReader.readBoolean(ST));
        setAccessLevel(TokenReader.readInt(ST));

        setPlanet(p);
    }

    // METHODS
    public String getIcons() {
        // TODO: Add more icons to make this unambiguous
        String sizeid = "";
        String result = "";
        int size = getWeightclass();
        if (size == Unit.LIGHT)
            sizeid += "l";
        else if (size == Unit.MEDIUM)
            sizeid += "m";
        else if (size == Unit.HEAVY)
            sizeid += "h";
        else if (size == Unit.ASSAULT)
            sizeid += "a";
        if (canProduce(Unit.MEK))
            sizeid += "m";
        else if (canProduce(Unit.VEHICLE))
            sizeid += "v";
        else if (canProduce(Unit.INFANTRY))
            sizeid += "li";// override size w/ light
        else if (canProduce(Unit.BATTLEARMOR))
            sizeid += "b";
        else if (canProduce(Unit.PROTOMEK))
            sizeid += "p";
        else if (canProduce(Unit.AERO))
            sizeid += "ae";

        result += "<img src=\"data/images/" + sizeid + ".gif\">";
        return result;
    }

  
    /**
     * @return Returns the planet.
     */
    public SPlanetOld getPlanet() {
        return planet;
    }

    /**
     * @param planet
     *            The planet to set.
     */
    public void setPlanet(SPlanetOld pl) {
        this.planet = pl;
    }

}
