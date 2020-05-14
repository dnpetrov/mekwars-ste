package server.campaign.pilot;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import server.campaign.CampaignMain;
import server.campaign.pilot.skills.AstechSkill;
import server.campaign.pilot.skills.BufferedVDNI;
import server.campaign.pilot.skills.ClanPilotTrainingSkill;
import server.campaign.pilot.skills.DodgeManeuverSkill;
import server.campaign.pilot.skills.EdgeSkill;
import server.campaign.pilot.skills.EnhancedInterfaceSkill;
import server.campaign.pilot.skills.GiftedSkill;
import server.campaign.pilot.skills.GunneryBallisticSkill;
import server.campaign.pilot.skills.GunneryLaserSkill;
import server.campaign.pilot.skills.GunneryMissileSkill;
import server.campaign.pilot.skills.IronManSkill;
import server.campaign.pilot.skills.ManeuveringAceSkill;
import server.campaign.pilot.skills.MedTechSkill;
import server.campaign.pilot.skills.MeleeSpecialistSkill;
import server.campaign.pilot.skills.NaturalAptitudeGunnerySkill;
import server.campaign.pilot.skills.NaturalAptitudePilotingSkill;
import server.campaign.pilot.skills.PainResistanceSkill;
import server.campaign.pilot.skills.PainShunt;
import server.campaign.pilot.skills.QuickStudySkill;
import server.campaign.pilot.skills.SPilotSkill;
import server.campaign.pilot.skills.SurvivalistSkill;
import server.campaign.pilot.skills.TacticalGeniusSkill;
import server.campaign.pilot.skills.TraitSkill;
import server.campaign.pilot.skills.VDNI;
import server.campaign.pilot.skills.WeaponSpecialistSkill;

import common.CampaignData;
import common.campaign.pilot.skills.PilotSkill;

public class SPilotSkills {
    private static Hashtable<Integer, SPilotSkill> pilotSkills = new Hashtable<Integer, SPilotSkill>();

	
	 public static void initializePilotSkills() {
	        // PilotSkills
	        pilotSkills.put(new Integer(PilotSkill.DodgeManeuverSkillID), (new DodgeManeuverSkill(PilotSkill.DodgeManeuverSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.ManeuveringAceSkillID), (new ManeuveringAceSkill(PilotSkill.ManeuveringAceSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.MeleeSpecialistSkillID), (new MeleeSpecialistSkill(PilotSkill.MeleeSpecialistSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.PainResistanceSkillID), (new PainResistanceSkill(PilotSkill.PainResistanceSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.AstechSkillID), (new AstechSkill(PilotSkill.AstechSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.NaturalAptitudeGunnerySkillID), (new NaturalAptitudeGunnerySkill(PilotSkill.NaturalAptitudeGunnerySkillID)));
	        pilotSkills.put(new Integer(PilotSkill.NaturalAptitudePilotingSkillID), (new NaturalAptitudePilotingSkill(PilotSkill.NaturalAptitudePilotingSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.IronManSkillID), (new IronManSkill(PilotSkill.IronManSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.GunneryBallisticSkillID), (new GunneryBallisticSkill(PilotSkill.GunneryBallisticSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.GunneryLaserSkillID), (new GunneryLaserSkill(PilotSkill.GunneryLaserSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.GunneryMissileSkillID), (new GunneryMissileSkill(PilotSkill.GunneryMissileSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.TacticalGeniusSkillID), (new TacticalGeniusSkill(PilotSkill.TacticalGeniusSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.WeaponSpecialistSkillID), (new WeaponSpecialistSkill(PilotSkill.WeaponSpecialistSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.SurvivalistSkillID), (new SurvivalistSkill(PilotSkill.SurvivalistSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.TraitID), (new TraitSkill(PilotSkill.TraitID)));
	        pilotSkills.put(new Integer(PilotSkill.EnhancedInterfaceID), (new EnhancedInterfaceSkill(PilotSkill.EnhancedInterfaceID)));
	        pilotSkills.put(new Integer(PilotSkill.QuickStudyID), (new QuickStudySkill(PilotSkill.QuickStudyID)));
	        pilotSkills.put(new Integer(PilotSkill.GiftedID), (new GiftedSkill(PilotSkill.GiftedID)));
	        pilotSkills.put(new Integer(PilotSkill.MedTechID), (new MedTechSkill(PilotSkill.MedTechID)));
	        pilotSkills.put(new Integer(PilotSkill.EdgeSkillID), (new EdgeSkill(PilotSkill.EdgeSkillID)));
	        pilotSkills.put(new Integer(PilotSkill.ClanPilotTraingID), (new ClanPilotTrainingSkill(PilotSkill.ClanPilotTraingID)));
	        pilotSkills.put(new Integer(PilotSkill.VDNIID), (new VDNI(PilotSkill.VDNIID)));
	        pilotSkills.put(new Integer(PilotSkill.BufferedVDNIID), (new BufferedVDNI(PilotSkill.BufferedVDNIID)));
	        pilotSkills.put(new Integer(PilotSkill.PainShuntID), (new PainShunt(PilotSkill.PainShuntID)));
	    }
	 
	    public static SPilotSkill getRandomSkill(SPilot p, int unitType) {
	        int total = 0;

	        Iterator<SPilotSkill> it = pilotSkills.values().iterator();
	        Hashtable<Integer, Integer> skilltable = new Hashtable<Integer, Integer>();
	        if (p.getSkills().has(PilotSkill.TraitID)) {
	            // SPilotSkill skill =
	            // (SPilotSkill)p.getSkills().getPilotSkill(SPilotSkill.TraitID);
	            String trait = p.getTraitName();
	            if (trait.indexOf("*") > -1) {
	                trait = trait.substring(0, trait.indexOf("*"));
	            }
	            Vector<String> traitsList = CampaignMain.cm.getFactionTraits(p.getCurrentFaction());
	            traitsList.trimToSize();
	            for (String traitNames : traitsList) {
	                StringTokenizer traitName = new StringTokenizer(traitNames, "*");
	                String traitString = traitName.nextToken();
	                if (traitString.equalsIgnoreCase(trait)) {
	                    while (traitName.hasMoreElements()) {
	                        int traitid = Integer.parseInt(traitName.nextToken());
	                        int traitMod = Integer.parseInt(traitName.nextToken());
	                        skilltable.put(traitid, traitMod);
	                    }
	                }
	            }
	        }

	        // check for trait mods and add them
	        while (it.hasNext()) {
	            SPilotSkill skill = it.next();
	            total += skill.getChance(unitType, p);
	        }

	        if (total == 0) {
	            return null;
	        }
	        /*
	         * int rnd = 1; if (total > 1) rnd = getR().nextInt(total) + 1;
	         */
	        it = pilotSkills.values().iterator();
	        Vector<SPilotSkill> skillBuilder = new Vector<SPilotSkill>(total, 1);

	        try {
	            while (it.hasNext()) {
	                SPilotSkill skill = it.next();
	                int chance = skill.getChance(unitType, p);
	                if (skilltable.get(new Integer(skill.getId())) != null) {
	                    chance += skilltable.get(skill.getId());
	                }

	                for (int pos = 0; pos < chance; pos++) {
	                    skillBuilder.add(skill);
	                }
	                skillBuilder.trimToSize();
	                /*
	                 * //CampaignData.mwlog.errLog("Pilot: "+p.getName()+" Skill:
	                 * "+skill.getName()+" Rnd "+rnd+ " chance: "+chance); if ( rnd
	                 * <= chance ) return skill; //else rnd -=
	                 * skill.getChance(unitType,p);
	                 */
	            }

	            return skillBuilder.elementAt(CampaignMain.cm.getRandomNumber(skillBuilder.size()));
	        } catch (Exception ex) {
	            CampaignData.mwlog.errLog("Problems during skill earning! Skill Table Size = " + skillBuilder.size() + " total = " + total);
	            return null;
	        }
	    }

	    /**
	     * Create a skill from a string. Used by CreateUnitCommand.
	     */
	    public static SPilotSkill getPilotSkill(String skill) {

	        for (SPilotSkill pSkill : pilotSkills.values()) {
	            if (pSkill.getName().equalsIgnoreCase(skill) || pSkill.getAbbreviation().equalsIgnoreCase(skill)) {
	                return pSkill;
	            }
	        }

	        return null;
	    }

	    /**
	     * Get a pilot skill by ID number. Used to unstring SPilots in pfiles.
	     */
	    public static SPilotSkill getPilotSkill(int id) {
	        return pilotSkills.get(id);
	    }
}
