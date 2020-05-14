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

package common.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public final class MWLogger {// final - no extension of the server logger

    private static final int rotations = 5; // Configurable
    private static final int normFileSize = 1000000; // Configurable
    private static final int bigFileSize = 5000000; // Configurable
    private static final int hugeFileSize = 10000000; // Configurable

    private static boolean logging = false;
    private static boolean addSeconds = true;
    private static boolean isServer = false;
    
    // private static boolean clientlog = false;

    private File logDir;

    private Logger mainLog; // Log for main channel
    private Logger factionLog; // Log for faction mails (all factions)
    private Logger gameLog; // Log for game reports (started, canceled and
                            // reported)
    private Logger resultsLog; // Log for game results as sent to players
    private Logger cmdLog; // Log for all issued commands
    private Logger pmLog; // Log for PMs
    private Logger bmLog; // Log for Black Market results
    private Logger infoLog; // Log for server generic messages (informative)
    private Logger warnLog; // Log for server warnings (problems)
    private Logger errLog; // Log for server errors (troubles!)
    private Logger modLog; // Log for moderators, normal log (double reg,
                            // 2nd/same, modchannel)
    private Logger tickLog; // Log for tick events (rankings, production, player
                            // stats)
    private Logger ipLog; // Log connecting IPs.
    private Logger testLog; // Log test code
    private Logger debugLog; // for all debug messages

    private HashMap<String, Logger> factionLoggers = new HashMap<String, Logger>();
    
    private MMNetFormatter mmnetFormatter;

    private FileHandler mainHandler;
    //private FileHandler factionHandler;
    private FileHandler gameHandler;
    private FileHandler resultsHandler;
    private FileHandler cmdHandler;
    private FileHandler pmHandler;
    private FileHandler bmHandler;
    private FileHandler infoHandler;
    private FileHandler warnHandler;
    private FileHandler errHandler;
    private FileHandler modHandler;
    private FileHandler tickHandler;
    private FileHandler ipHandler;
    private FileHandler testHandler;
    private FileHandler debugHandler;

    public static class MMNetFormatter extends SimpleFormatter {

        @Override
        public String format(LogRecord record) {

            GregorianCalendar now = new GregorianCalendar();
            now.setTimeInMillis(record.getMillis());
            StringBuilder sb = new StringBuilder();

            DecimalFormat mills = new DecimalFormat("000");
            DecimalFormat secs = new DecimalFormat("00");
            
            sb.append(now.get(Calendar.YEAR));
            sb.append(secs.format(now.get(Calendar.MONTH) + 1));
            sb.append(secs.format(now.get(Calendar.DAY_OF_MONTH)));
            sb.append(" ");
            sb.append(now.get(Calendar.HOUR_OF_DAY));
            sb.append(":");
            sb.append(secs.format(now.get(Calendar.MINUTE)));
            if (addSeconds) {
                sb.append(":");
                sb.append(secs.format(now.get(Calendar.SECOND)));
                sb.append(".");
                sb.append(mills.format(now.get(Calendar.MILLISECOND)));
                sb.append(" ");
                sb.append(formatMessage(record));
                sb.append("\n");
            } else {
                sb.append(" ");
                sb.append(formatMessage(record) + "\n");
            }
            return sb.toString();
        }
    }

    public MWLogger() {

        if (logging)
            return;

        logDir = new File("logs");
        if (!logDir.exists()) {
            try {
                if (!logDir.mkdirs()) {
                    System.err.println("WARNING: logging directory cannot be created!");
                    System.err.println("WARNING: disabling log subsystem");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!logDir.isDirectory()) {
            System.err.println("WARNING: logging directory is not a directory!");
            System.err.println("WARNING: disabling log subsystem");
            return;
        }

        if (!logDir.canWrite()) {
            System.err.println("WARNING: cannot write in logging directory!");
            System.err.println("WARNING: disabling log subsystem");
            return;
        }

        mmnetFormatter = new MMNetFormatter();

    }

    public void createClientLoggers(){
        try {

            infoHandler = new FileHandler(logDir.getPath() + "/infolog", normFileSize, rotations, true);
            infoHandler.setLevel(Level.INFO);
            infoHandler.setFilter(null);
            infoHandler.setFormatter(mmnetFormatter);
            infoHandler.setEncoding("UTF8");
            infoLog = Logger.getLogger("infoLogger");
            infoLog.setUseParentHandlers(false);
            infoLog.addHandler(infoHandler);

            errHandler = new FileHandler(logDir.getPath() + "/errlog", normFileSize, rotations, true);
            errHandler.setLevel(Level.INFO);
            errHandler.setFilter(null);
            errHandler.setEncoding("UTF8");
            errHandler.setFormatter(mmnetFormatter);
            errLog = Logger.getLogger("errLogger");
            errLog.setUseParentHandlers(false);
            errLog.addHandler(errHandler);

            debugHandler = new FileHandler(logDir.getPath() + "/debuglog", normFileSize, rotations, true);
            debugHandler.setLevel(Level.INFO);
            debugHandler.setFilter(null);
            debugHandler.setEncoding("UTF8");
            debugHandler.setFormatter(mmnetFormatter);
            debugLog = Logger.getLogger("debugLogger");
            debugLog.setUseParentHandlers(false);
            debugLog.addHandler(debugHandler);
            
            logging = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void createServerLoggers(){
        try {

            mainHandler = new FileHandler(logDir.getPath() + "/mainlog", bigFileSize, rotations, true);
            mainHandler.setLevel(Level.INFO);
            mainHandler.setFilter(null);
            mainHandler.setFormatter(mmnetFormatter);
            mainHandler.setEncoding("UTF8");
            mainLog = Logger.getLogger("mainLogger");
            mainLog.setUseParentHandlers(false);
            mainLog.addHandler(mainHandler);

            gameHandler = new FileHandler(logDir.getPath() + "/gamelog", bigFileSize, rotations, true);
            gameHandler.setLevel(Level.INFO);
            gameHandler.setFilter(null);
            gameHandler.setFormatter(mmnetFormatter);
            gameHandler.setEncoding("UTF8");
            gameLog = Logger.getLogger("gameLogger");
            gameLog.setUseParentHandlers(false);
            gameLog.addHandler(gameHandler);

            resultsHandler = new FileHandler(logDir.getPath() + "/resultslog", bigFileSize, rotations, true);
            resultsHandler.setLevel(Level.INFO);
            resultsHandler.setFilter(null);
            resultsHandler.setFormatter(mmnetFormatter);
            resultsHandler.setEncoding("UTF8");
            resultsLog = Logger.getLogger("resultsLogger");
            resultsLog.setUseParentHandlers(false);
            resultsLog.addHandler(resultsHandler);

            cmdHandler = new FileHandler(logDir.getPath() + "/cmdlog", hugeFileSize, rotations, true);
            cmdHandler.setLevel(Level.INFO);
            cmdHandler.setFilter(null);
            cmdHandler.setFormatter(mmnetFormatter);
            cmdHandler.setEncoding("UTF8");
            cmdLog = Logger.getLogger("cmdLogger");
            cmdLog.setUseParentHandlers(false);
            cmdLog.addHandler(cmdHandler);

            pmHandler = new FileHandler(logDir.getPath() + "/pmlog", bigFileSize, rotations, true);
            pmHandler.setLevel(Level.INFO);
            pmHandler.setFilter(null);
            pmHandler.setFormatter(mmnetFormatter);
            pmHandler.setEncoding("UTF8");
            pmLog = Logger.getLogger("pmLogger");
            pmLog.setUseParentHandlers(false);
            pmLog.addHandler(pmHandler);

            bmHandler = new FileHandler(logDir.getPath() + "/bmlog", normFileSize, rotations, true);
            bmHandler.setLevel(Level.INFO);
            bmHandler.setFilter(null);
            bmHandler.setEncoding("UTF8");
            bmHandler.setFormatter(mmnetFormatter);
            bmLog = Logger.getLogger("bmLogger");
            bmLog.setUseParentHandlers(false);
            bmLog.addHandler(bmHandler);

            infoHandler = new FileHandler(logDir.getPath() + "/infolog", normFileSize, rotations, true);
            infoHandler.setLevel(Level.INFO);
            infoHandler.setFilter(null);
            infoHandler.setFormatter(mmnetFormatter);
            infoHandler.setEncoding("UTF8");
            infoLog = Logger.getLogger("infoLogger");
            infoLog.setUseParentHandlers(false);
            infoLog.addHandler(infoHandler);

            warnHandler = new FileHandler(logDir.getPath() + "/warnlog", normFileSize, rotations, true);
            warnHandler.setLevel(Level.INFO);
            warnHandler.setFilter(null);
            warnHandler.setFormatter(mmnetFormatter);
            warnHandler.setEncoding("UTF8");
            warnLog = Logger.getLogger("warnLogger");
            warnLog.setUseParentHandlers(false);
            warnLog.addHandler(warnHandler);

            errHandler = new FileHandler(logDir.getPath() + "/errlog", normFileSize, rotations, true);
            errHandler.setLevel(Level.INFO);
            errHandler.setFilter(null);
            errHandler.setEncoding("UTF8");
            errHandler.setFormatter(mmnetFormatter);
            errLog = Logger.getLogger("errLogger");
            errLog.setUseParentHandlers(false);
            errLog.addHandler(errHandler);

            modHandler = new FileHandler(logDir.getPath() + "/modlog", bigFileSize, rotations, true);
            modHandler.setLevel(Level.INFO);
            modHandler.setFilter(null);
            modHandler.setFormatter(mmnetFormatter);
            modHandler.setEncoding("UTF8");
            modLog = Logger.getLogger("modLogger");
            modLog.setUseParentHandlers(false);
            modLog.addHandler(modHandler);

            tickHandler = new FileHandler(logDir.getPath() + "/ticklog", normFileSize, rotations, true);
            tickHandler.setLevel(Level.INFO);
            tickHandler.setFilter(null);
            tickHandler.setFormatter(mmnetFormatter);
            tickHandler.setEncoding("UTF8");
            tickLog = Logger.getLogger("tickLogger");
            tickLog.setUseParentHandlers(false);
            tickLog.addHandler(tickHandler);

            // 104857600 is exactly 100 megabytes
            ipHandler = new FileHandler(logDir.getPath() + "/iplog", 104857600, rotations, true);
            ipHandler.setLevel(Level.INFO);
            ipHandler.setFilter(null);
            ipHandler.setEncoding("UTF8");
            ipHandler.setFormatter(mmnetFormatter);
            ipLog = Logger.getLogger("ipLogger");
            ipLog.setUseParentHandlers(false);
            ipLog.addHandler(ipHandler);
        
            testHandler = new FileHandler(logDir.getPath() + "/testlog", 104857600, rotations, true);
            testHandler.setLevel(Level.INFO);
            testHandler.setFilter(null);
            testHandler.setFormatter(mmnetFormatter);
            testHandler.setEncoding("UTF8");
            testLog = Logger.getLogger("testLogger");
            testLog.setUseParentHandlers(false);
            testLog.addHandler(testHandler);

            debugHandler = new FileHandler(logDir.getPath() + "/debuglog", hugeFileSize, rotations, true);
            debugHandler.setLevel(Level.INFO);
            debugHandler.setFilter(null);
            debugHandler.setFormatter(mmnetFormatter);
            debugHandler.setEncoding("UTF8");
            debugLog = Logger.getLogger("debugLogger");
            debugLog.setUseParentHandlers(false);
            debugLog.addHandler(debugHandler);

            logging = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void mainLog(String s) {
        if (logging) {
            mainLog.info(s);
            mainHandler.flush();
        }
    }

    public void factionLog(String s, String LogName) {
        //errLog("Logging to house file: " + LogName);
    	//factionLog = Logger.getLogger(LogName);
        factionLog = factionLoggers.get(LogName);
        factionLog.info(s);
        factionLog.getHandlers()[0].flush();
    }

    public void gameLog(String s) {
        if (logging) {
            gameLog.info(s);
            gameHandler.flush();
        }
    }

    public void resultsLog(String s) {
        if (logging) {
            resultsLog.info(s);
            resultsHandler.flush();
        }
    }

    public void cmdLog(String s) {

        if (logging) {
            /*
             * exclude hm and factionmail commands, as there is a seperate
             * factionlog
             */
            String lower = s.toLowerCase();
            if (lower.indexOf("hm#") == -1 && lower.indexOf("factionmail#") == -1) {
                cmdLog.info(s);
                cmdHandler.flush();
            }
        }
    }

    public void pmLog(String s) {
        if (logging) {
            pmLog.info(s);
            pmHandler.flush();
        }
    }

    public void bmLog(String s) {
        if (logging) {
            bmLog.info(s);
            bmHandler.flush();
        }
    }

    public void infoLog(String s) {
        if (logging) {
            infoLog.info(s);
            infoHandler.flush();
        }
    }

    public void log(String s) {
        if (logging) {
            infoLog.info(s);
            infoHandler.flush();
        }
    }

    public void warnLog(String s) {
        if (logging) {
            warnLog.info(s);
            warnHandler.flush();
        }
    }

    public void errLog(String s) {
        if (logging) {
            errLog.info(s);
            errHandler.flush();
            if ( isServer && server.campaign.CampaignMain.cm != null)
                server.campaign.CampaignMain.cm.doSendErrLog(s);
        }
    }

    public void errLog(Exception e) {
        if (logging) {
            errLog.warning("[" + e.toString() + "]");
            errHandler.flush();
            if (isServer && server.campaign.CampaignMain.cm != null)
                server.campaign.CampaignMain.cm.doSendErrLog("[" + e.toString() + "]");
            StackTraceElement[] t = e.getStackTrace();
            for (int i = 0; i < t.length; i++) {
                errLog.warning("   " + t[i].toString());
                errHandler.flush();
                if (isServer && server.campaign.CampaignMain.cm != null)
                    server.campaign.CampaignMain.cm.doSendErrLog("   " + t[i].toString());
            }
        }
    }

    public void debugLog(String s) {
        if (logging) {
            debugLog.info(s);
            debugHandler.flush();
        }
    }

    public void debugLog(Exception e) {
        if (logging) {
            debugLog.warning("[" + e.toString() + "]");
            debugHandler.flush();
            StackTraceElement[] t = e.getStackTrace();
            for (int i = 0; i < t.length; i++) {
                debugLog.warning("   " + t[i].toString());
                debugHandler.flush();
                if (isServer && server.campaign.CampaignMain.cm != null) {
                    server.campaign.CampaignMain.cm.doSendErrLog("   " + t[i].toString());
                }

            }
        }
    }

    public void modLog(String s) {
        if (logging) {
            modLog.info(s);
            modHandler.flush();
        }
    }

    public void tickLog(String s) {
        if (logging) {
            tickLog.info(s);
            tickHandler.flush();
        }
    }

    public void ipLog(String s) {
        if (logging) {
            ipLog.info(s);
            ipHandler.flush();
        }
    }

    public void testLog(String s) {
    	if (logging)
    		testLog.info(s);
    }
    
    public void testLog(Exception e) {
    	if (logging) {
    		testLog.warning("[" + e.toString() + "]");
    		StackTraceElement[] t = e.getStackTrace();
    		for (int i = 0; i < t.length; i++) {
    			testLog.warning("   " + t[i].toString());
    		}
    	}
    }
    
    public void enableSeconds(boolean b) {
        MWLogger.addSeconds = b;
    }

    public void enableLogging(boolean b) {
        MWLogger.logging = b;
    }

    public void setServer(boolean b){
        MWLogger.isServer = b;
    }
    
    public void createFactionLogger(String logName) {

        try {
            Handler factionHandler = new FileHandler(logDir.getPath() + "/" + logName, bigFileSize, rotations, true);
            factionHandler.setLevel(Level.INFO);
            factionHandler.setFilter(null);
            factionHandler.setFormatter(mmnetFormatter);
            factionLog = Logger.getLogger(logName.trim());
            factionLog.setUseParentHandlers(false);
            factionLog.addHandler(factionHandler);
            factionLog.info(logName + " log touched");
            factionLoggers.put(logName, factionLog);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
