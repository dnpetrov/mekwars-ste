package server.campaign.tools.PlanetDatConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import server.campaign.BuildTable;
import server.campaign.NewbieHouse;
import server.campaign.SHouse;
import server.campaign.mercenaries.MercHouse;
import server.campaign.util.SerializedMessage;

import common.CampaignData;
import common.Continent;
import common.House;
import common.Planet;
import common.UnitFactory;
import common.util.MekwarsFileReader;

public class PlanetDatConverter {

	/**
	 * @param args
	 */
	private static CampaignData data = new CampaignData();
	//private static CampaignMain campaign = new CampaignMain(null);
	
	public static void main(String[] args) {
		
		loadHouses();
	    loadPlanetData();
		savePlanetData();
		}
	
	
	 private static void loadHouses() {
	        File factionFile = new File("./campaign/factions");

	        // Check for new faction save location
	        if (!factionFile.exists() || factionFile.listFiles().length < 1) {
	            CampaignData.mwlog.errLog("Unable to find and load faction data");
	            CampaignData.mwlog.errLog("Going to create from XML");
	            return;
	        }

	        // filter out .bak's
	        FilenameFilter filter = new datFileFilter();
	        File[] factionFileList = factionFile.listFiles(filter);

	        // load each file
	        for (File faction : factionFileList) {
	            try {
	                MekwarsFileReader dis = new MekwarsFileReader(faction);
	                String line = dis.readLine();
	                SHouse h;
	                if (line.startsWith("[N][C]")) {
	                    line = line.substring(6);
	                    h = new NewbieHouse(data.getUnusedHouseID());
	                } else if (line.startsWith("[N]")) {
	                    line = line.substring(3);
	                    h = new NewbieHouse(data.getUnusedHouseID());
	                } else if (line.startsWith("[M]")) {
	                    line = line.substring(3);
	                    h = new MercHouse(data.getUnusedHouseID());
	                } else {
	                    h = new SHouse(data.getUnusedHouseID());
	                }
	                Random r = new Random();
					h.fromString(line, r );
	                addHouse(h);
	                dis.close();
	            } catch (Exception ex) {
	                CampaignData.mwlog.errLog("Unable to load " + faction.getName());
	            }
	        }

	        if (data.getHouse(-1) == null) {
	            SHouse none = new MercHouse();
	            none.setName("None");
	            none.setId(-1);
	            none.setConquerable(false);
	            none.setHouseDefectionTo(false);
	            none.setHouseDefectionFrom(false);
	            none.setAbbreviation("None");
	            none.setHouseColor("black");
	            none.setHousePlayerColors("black");
	            addHouse(none);
	        }
		
	}


	public static void loadPlanetData() {

	        loadPlanetOpFlags();

	        File planetFile = new File("./campaign/planets");
	        FilenameFilter filter = new datFileFilter();
	        
	        if(!planetFile.exists()) {
	        	CampaignData.mwlog.errLog("Unable to open ./campaign/planets");
                return;
	        }
            // dir and files exist. read them.
            File[] planetFileList = planetFile.listFiles(filter);
            for (File planet : planetFileList) {

                try {
                    MekwarsFileReader dis = new MekwarsFileReader(planet);
                    String line = dis.readLine();
                    SPlanetOld p;
                    if (line.startsWith("[N]")) {
                        line = line.substring(3);
                    }
                    p = new SPlanetOld();
                    Random r = new Random();
					p.fromString(line, r , data);
                    addPlanet(p);
                    dis.close();
                } catch (Exception ex) {
                    CampaignData.mwlog.errLog("Unable to load " + planet.getName());
                    CampaignData.mwlog.errLog(ex);
                }
            }
        }
	 
	    public static void loadPlanetOpFlags() {
	        File configFile = new File("./campaign/planetOpFlags.dat");
	        if (!configFile.exists()) {
	            CampaignData.mwlog.errLog("No planetOpFlags.dat. Skipping.");
	            return;
	        }

	        try {
	        	MekwarsFileReader dis = new MekwarsFileReader(configFile);
	            dis.readLine();// Time Stamp

	            String nextLine = dis.readLine();
	            if (nextLine == null) {
	                CampaignData.mwlog.errLog("Timestamp-only planetOpFlags.dat. Skipping.");
	                return;
	            }

	            StringTokenizer st = new StringTokenizer(nextLine, "#");
	            while (st.hasMoreTokens()) {
	                data.getPlanetOpFlags().put(st.nextToken(), st.nextToken());
	            }

	            dis.close();
	        } catch (Exception ex) {
	            CampaignData.mwlog.errLog("Error loading Planet Op Flags.");
	            CampaignData.mwlog.errLog(ex);
	        }
	    }

	    public static void addHouse(SHouse s) {
	        data.addHouse(s);
	    }

	    
	    public static void addPlanet(SPlanetOld oldP) {

	        Planet newP = new Planet();
	        convertPlanet(oldP,newP);
	        	        
	        data.addPlanet(newP);
	    }
	    
	    private static void convertPlanet(SPlanetOld oldP, Planet newP) {
	    	newP.setBaysProvided(oldP.getBaysProvided());
	    	newP.setCompProduction(oldP.getCompProduction());
	    	newP.setConquestPoints(oldP.getConquestPoints());
	    	newP.setEnvironments(oldP.getEnvironments());
	    	convertUnitFactories(oldP.getUnitFactories(),newP);
	    	
	    	newP.setInfluence(oldP.getInfluence());
	    	newP.setName(oldP.getName());
	    	newP.setOriginalOwner(oldP.getOriginalOwner());
	    	newP.setPlanetFlags(oldP.getPlanetFlags());
	    	newP.setPosition(oldP.getPosition());
	    	newP.setConquerable(oldP.isConquerable());
	    	newP.setDescription(oldP.getDescription());
	    	newP.setHomeWorld(oldP.isHomeWorld());
	    	newP.setMinPlanetOwnerShip(oldP.getMinPlanetOwnerShip());
	    	newP.setId(oldP.getId());
	    	newP.setBoardSize(oldP.getBoardSize());
	    	newP.setMapSize(oldP.getMapSize());
	    	
		}


		private static void convertUnitFactories(Vector<UnitFactory> unitFactories, Planet P) {
			for(UnitFactory mft: unitFactories ){		
				
				UnitFactory newfactory = new UnitFactory();
				newfactory.setName(mft.getName());
				newfactory.setSize(mft.getSize());
				newfactory.setFounder(mft.getFounder());
				newfactory.setTicksUntilRefresh(mft.getTicksUntilRefresh());
				newfactory.setRefreshSpeed(mft.getRefreshSpeed());
				newfactory.setType(mft.getType());
				newfactory.setBuildTableFolder(mft.getBuildTableFolder());
				newfactory.setAccessLevel(mft.getAccessLevel());
				P.getUnitFactories().add(newfactory);
            }
		}


		public static void savePlanetData() {


	            savePlanetOpFlags();
	            File planetFile = new File("./campaign/newplanets");
	            if (!planetFile.exists()) {
	                planetFile.mkdir();
	            }
	            synchronized (data.getAllPlanets()) {

	                for (Planet currP : data.getAllPlanets()) {
	                    //SPlanet p = (SPlanet) currP;
	                    String saveName = currP.getName().toLowerCase().trim() + ".dat";
	                    String backupName = currP.getName().toLowerCase().trim() + ".bak";
	                    try {
	                        File planet = new File("./campaign/newplanets/" + saveName);

	                        if (planet.exists()) {

	                            File backupFile = new File("./campaign/newplanets/" + backupName);
	                            if (backupFile.exists()) {
	                                backupFile.delete();
	                            }

	                            planet.renameTo(backupFile);
	                        }

	                        FileOutputStream out = new FileOutputStream("./campaign/newplanets/" + saveName);
	                        PrintStream ps = new PrintStream(out);
	                        
	                        ps.println(SavePlanet(currP));
	                        ps.close();
	                        out.close();
	                    } catch (Exception ex) {
	                        CampaignData.mwlog.errLog("Unable to save planet: " + saveName);
	                        CampaignData.mwlog.errLog(ex);
	                    }
	                }
	            }

	        
	    }

	    public static void savePlanetOpFlags() {
	        // Save Planet Op Flags
	    }

	    private static String SavePlanet(Planet P)
	    {
	    	 SerializedMessage result = new SerializedMessage("#");
	         result.append("PL");
	         result.append(P.getName());
	         result.append(P.getCompProduction());
	         if (P.getUnitFactories() != null) {
	             result.append(P.getUnitFactories().size());
	             for (UnitFactory factory : P.getUnitFactories()) {
	                 // int i = 0; i < getUnitFactories().size(); i++) {
	                 // SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);	            	 
	                 result.append(CreateUnitFactoryString(factory));
	             }
	         } else
	             result.append("0");

	         result.append(P.getPosition().getX());
	         result.append(P.getPosition().getY());
	         StringBuilder houseString = new StringBuilder();
	         for (House house : P.getInfluence().getHouses()) {
	             SHouse next = (SHouse) house;
	             if (next == null)
	                 continue;
	             houseString.append(next.getName());
	             houseString.append("$"); // change for unusual influence
	             houseString.append(P.getInfluence().getInfluence(next.getId()));
	             houseString.append("$"); // change for unusual influence
	         }
	         // No Influences then set influence to NewbieHouse so the planet will
	         // load.
	         if (P.getInfluence().getHouses().size() < 1) {
	         	houseString.append("SOL");
	         	houseString.append("$");
	         	houseString.append(P.getConquestPoints());
	         	houseString.append("$");
	         }

	         result.append(houseString.toString());
	         result.append(P.getEnvironments().size());
	         for (Continent t : P.getEnvironments().toArray()) {
	             result.append(t.getSize());
	             result.append(t.getEnvironment().getName());
	             
	             if(t.getAdvancedTerrain() == null)
	            	 result.append("none");
	         }
	         if (P.getDescription().equals(""))
	             result.append(" ");
	         else
	             result.append(P.getDescription());
	         result.append(P.getBaysProvided());
	         result.append(P.isConquerable());
	         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	         result.append(sdf.format(new Date()));
	         result.append(P.getId());
	         result.append(P.getMinPlanetOwnerShip());
	         result.append(P.isHomeWorld());
	         result.append(P.getOriginalOwner());

	         if (P.getPlanetFlags().size() > 0) {
	             for (String key : P.getPlanetFlags().keySet()) {
	                 result.append(key + "^");
	             }
	         } else
	             result.append("^^");

	         result.append(P.getConquestPoints());

	         return result.toString();
	     }


		private static String CreateUnitFactoryString(UnitFactory factory) {
	        SerializedMessage result = new SerializedMessage("*");
	        result.append("MF");
	        result.append(factory.getName());
	        result.append(factory.getSize());
	        result.append(factory.getFounder());
	        result.append(factory.getTicksUntilRefresh());
	        result.append(factory.getRefreshSpeed());

	        String buildtablefolder = factory.getBuildTableFolder().replaceAll(BuildTable.STANDARD + "\\" + File.separatorChar, "");

	        if (buildtablefolder.trim().length() < 1 || buildtablefolder.equals(BuildTable.STANDARD))
	            result.append("0");
	        else
	            result.append(buildtablefolder);

	        result.append(factory.getType());
	        result.append(factory.isLocked());
	        result.append(factory.getAccessLevel());
	        return result.toString();
		}
	    
}


class datFileFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".dat"));
    }
}
