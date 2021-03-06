/*
 * MekWars - Copyright (C) 2004
 *
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet) Original author Helge Richter (McWizard)
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */

package client.gui;

import client.MWClient;
import client.campaign.CArmy;
import client.campaign.CBMUnit;
import client.campaign.CPlayer;
import client.campaign.CUnit;
import client.gui.dialog.*;
import common.Army;
import common.CampaignData;
import common.Unit;
import common.campaign.pilot.Pilot;
import common.util.SpringLayoutHelper;
import common.util.TokenReader;
import common.util.UnitUtils;
import megamek.client.ui.swing.MechTileset;
import megamek.client.ui.swing.unitDisplay.UnitDisplay;
import megamek.common.Entity;
import megamek.common.Infantry;
import megamek.common.Mech;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

//@Salient
//import client.gui.dialog.TableViewerDialog; //for testing/debug

/**
 * Headquarters Panel
 */

public class CHQPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -5137503055464771160L;

    public static final int DG = 220;
    public static final int DB = 220;
    public static final int DR = 220;
    public static final Color DGRAY = new Color(DR, DG, DB);
    public static final Color TAN_LIGHT = new Color(DR - 10, DB - 10, DG - 30);
    public static final Color TAN_MEDIUM = new Color(DR - 30, DB - 30, DG - 50);
    public static final Color TAN_HEAVY = new Color(DR - 55, DB - 55, DG - 75);
    public static final Color TAN_ASSAULT = new Color(DR - 75, DB - 75, DG - 95);
    public static final Color GRAY_LIGHT = new Color(DR, DG, DB);
    public static final Color GRAY_MEDIUM = new Color(DR - 17, DB - 17, DG - 17);
    public static final Color GRAY_HEAVY = new Color(DR - 40, DB - 40, DG - 40);
    public static final Color GRAY_ASSAULT = new Color(DR - 65, DB - 65, DG - 65);
    public static final Color CLASSIC_LIGHT = new Color(220, 220, 220);
    public static final Color CLASSIC_MEDIUM = new Color(DR - 30, DB - 30, DG - 50);
    public static final Color CLASSIC_HEAVY = new Color(DR - 65, DB - 65, DG - 25);
    public static final Color CLASSIC_ASSAULT = new Color(DR - 45, DB - 93, DG - 45);
    public static final Color FORSALE_COLOR = new Color(50, 170, 35);
    public static final Color UNMAINTAINED_COLOR = new Color(190, 150, 55);
    public static final Color LOCKED_COLOR = new Color(128, 0, 128);
    public static final Color VACANT_COLOR = new Color(160, 190, 115);
    public static final Color REPAIRING_COLOR = new Color(0, 255, 127);
    public static final Color QUEUED_COLOR = new Color(75, 0, 130);
    public static final Color CRITICAL_DAMAGE_COLOR = new Color(255, 0, 0);
    public static final Color ARMOR_DAMAGE_COLOR = new Color(238, 238, 0);
    public static final Color HAS_ALL_AMMO_COLOR = new Color(255, 128, 255);
    public static final Color CLASSIC_IN_ARMIES_COLOR = new Color(65, 170, 55);
    public static final Color OTHER_IN_ARMIES_COLOR = new Color(DR - 43, DB - 33, DG - 4);

    MWClient mwclient;

    CPlayer Player;

    public MekTableModel MekTable;

    protected MechTableMouseAdapter mouseAdapter;

    // graphical components

    private GridBagConstraints gridBagConstraints;

    private JPanel pnlMeks;
    private JScrollPane spMeks;
    private JTable tblMeks;
    private JPanel pnlMeksBtns;
    private JButton btnAddLance;
    private JButton btnRemoveAllArmies;
    private JButton setCamoButton;
    private JButton newbieResetUnitsButton;
    private JButton repairAllUnitsButton;
    private JButton reloadAllUnitsButton;
    //@Salient (mwosux@gmail.com) added for SolFreeBuild option
    private JButton solFreeBuildButton;
    private boolean useAdvanceRepairs = false;
    private boolean useUnitLocking = false;

    public CHQPanel(MWClient client) {
        mwclient = client;
        Player = mwclient.getPlayer();
        MekTable = new MekTableModel();
        mouseAdapter = new MechTableMouseAdapter();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        init();
        refresh();
    }

    /**
     * Public call which reinitializes the HQ panel. Hacky and evil, but lets camo and # columns in HQ display get updated on the fly.
     */
    public void reinitialize() {

        // mwclient.getMainFrame().getMainPanel().selectFirstTab();
        // mwclient.getMainFrame().getMainPanel().getCommPanel().selectFirstTab();

        // remove all the old components.
        removeAll();
        // this.setVisible(false);

        Player = mwclient.getPlayer();
        MekTable = new MekTableModel();
        mouseAdapter = new MechTableMouseAdapter();
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        // gridBagConstraints.insets = new Insets(0, 0, 0, 10);

        init();
        refresh();
        // this.setVisible(true);
    }

    private void init() {
        pnlMeks = new JPanel();
        spMeks = new JScrollPane();
        tblMeks = new JTable();
        pnlMeksBtns = new JPanel();
        btnAddLance = new JButton();
        btnRemoveAllArmies = new JButton();
        setCamoButton = new JButton();
        newbieResetUnitsButton = new JButton();
        repairAllUnitsButton = new JButton();
        reloadAllUnitsButton = new JButton();
        //@Salient (mwosux@gmail.com) added for SolFreeBuild option
        solFreeBuildButton = new JButton();
        // pnlMekIcon = new MechInfo(mwclient);
        // btnShowMek = new JButton();

        setLayout(new GridBagLayout());

        createMeksPanel();
        // tpMain.addTab("Meks", null, pnlMeks, "Command Your Meks");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlMeks, gridBagConstraints);
        useAdvanceRepairs = mwclient.isUsingAdvanceRepairs();
        useUnitLocking = Boolean.parseBoolean(mwclient.getserverConfigs("LockUnits"));
    }

    private void createMeksPanel() {
        // pnlMeks.setLayout(new BoxLayout(pnlMeks, BoxLayout.Y_AXIS));
        pnlMeks.setLayout(new GridBagLayout());
        spMeks.setPreferredSize(new Dimension(300, 400));
        tblMeks.setBackground(new Color(255, 255, 255));
        tblMeks.setForeground(new Color(0, 0, 0));
        tblMeks.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblMeks.setDoubleBuffered(true);
        tblMeks.setMaximumSize(new Dimension(2147483647, 10000));
        tblMeks.setPreferredScrollableViewportSize(new Dimension(300, 400));
        tblMeks.setPreferredSize(new Dimension(300, 400));
        tblMeks.setRowHeight(100);
        tblMeks.setRowSelectionAllowed(false);
        tblMeks.setColumnSelectionAllowed(false);
        tblMeks.setCellSelectionEnabled(true);
        tblMeks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblMeks.setModel(MekTable);
        tblMeks.getColumnModel().getColumn(0).setPreferredWidth(120);
        /*
         * tblMeks.getColumnModel().getColumn(1).setPreferredWidth(90); tblMeks.getColumnModel().getColumn(2).setPreferredWidth(90); tblMeks.getColumnModel().getColumn(3).setPreferredWidth(90); tblMeks.getColumnModel().getColumn(4).setPreferredWidth(90); tblMeks.getColumnModel().getColumn(5).setPreferredWidth(30);
         */
        tblMeks.getTableHeader().setReorderingAllowed(false);
        for (int i = 0; i < tblMeks.getColumnCount(); i++) {
            tblMeks.getColumnModel().getColumn(i).setCellRenderer(MekTable.getRenderer());
        }
        tblMeks.addMouseListener(mouseAdapter);
        tblMeks.addMouseMotionListener(mouseAdapter);

        spMeks.setViewportView(tblMeks);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        // gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        pnlMeks.add(spMeks, gridBagConstraints);

        // set up the row of buttons under the table
        pnlMeksBtns.setLayout(new BoxLayout(pnlMeksBtns, BoxLayout.Y_AXIS));
        makeButtons();// makes pnlMeksBtns

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // gridBagConstraints.ipadx = 30;
        gridBagConstraints.weightx = 1.0;
        // gridBagConstraints.weighty = 0.0;
        // gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        pnlMeks.add(pnlMeksBtns, gridBagConstraints);
    }

    public void refresh() {
        useAdvanceRepairs = mwclient.isUsingAdvanceRepairs();
        MekTable.refreshModel();
        tblMeks.setPreferredSize(new Dimension(tblMeks.getWidth(), tblMeks.getRowHeight() * MekTable.getRowCount()));
        tblMeks.revalidate();
        mwclient.getPlayer().sortArmies();
    }

    // try to remove all armies
    private void btnRemoveAllArmiesActionPerformed(ActionEvent evt) {
    	//no armies... don't bother   		//Baruk Khazad! 20151204 - start block 1
        if (mwclient.getPlayer().getArmies().size() == 0)
        	return;
        //get confirm
        int result = JOptionPane.showConfirmDialog(mwclient.getMainFrame(), "Are you sure you want to remove all of your armies?", "Remove all armies?", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.NO_OPTION)
          	return;		//Baruk Khazad! 20151204 - end block 1
        // only remove all if he's logged in, not fighting/active/logout/discon
        if (mwclient.getMyStatus() != MWClient.STATUS_RESERVE) {
            return;
        }

        for (CArmy currA : mwclient.getPlayer().getArmies()) {
            if (!currA.isPlayerLocked()) {
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c removearmy#" + currA.getID());
            }
        }
    }// end btnRemoveAllArmiesActionPerformed

    private void newbieResetUnitsButtonActionPerformed(ActionEvent evt) {
        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c request#resetunits");
    }

    private void repairAllUnitsButtonActionPerformed(ActionEvent evt) {
        if (mwclient.getPlayer().getHangar().size() > 0) {
            new BulkRepairDialog(mwclient, mwclient.getPlayer().getHangar().firstElement().getId(), BulkRepairDialog.TYPE_BULK, BulkRepairDialog.UNIT_TYPE_ALL);
        }
    }

    private void reloadAllUnitsButtonActionPerformed(ActionEvent evt) {
        if (mwclient.getPlayer().getHangar().size() > 0) {
            int result = JOptionPane.showConfirmDialog(mwclient.getMainFrame(), "Are you sure you want to reload all the ammo on all your units?", "Reload all units?", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
        		for (CUnit unit : mwclient.getPlayer().getHangar()) {
                	if (!UnitUtils.hasAllAmmo(unit.getEntity())) {
                		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c RELOADALLAMMO#" + unit.getId());
                	}
        		}

            	refresh();
            }
        }
    }

    private void btnAddLanceActionPerformed(ActionEvent evt) {
        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c cra#" + mwclient.getConfigParam("DEFAULTARMYNAME"));
    }

    private void setCamoButtonActionPerformed(ActionEvent evt) {
        CamoSelectionDialog camoDialog = new CamoSelectionDialog(mwclient.getMainFrame(), mwclient);
        camoDialog.setVisible(true);
    }

    //@Salient (mwosux@gmail.com) added for SolFreeBuild option
    private void solFreeBuildButtonActionPerformed(ActionEvent evt) {
        SolFreeBuildDialog solDialog = new SolFreeBuildDialog(mwclient);
        solDialog.setVisible(true);
    }

    public void makeButtons() {

        JPanel hqButtonSpring = new JPanel(new SpringLayout());
        pnlMeksBtns.removeAll();

        btnAddLance.setText("Create New Army");
        btnAddLance.addActionListener(this::btnAddLanceActionPerformed);
        hqButtonSpring.add(btnAddLance);

        btnRemoveAllArmies.setText("Remove All Armies");
        btnRemoveAllArmies.addActionListener(this::btnRemoveAllArmiesActionPerformed);
        hqButtonSpring.add(btnRemoveAllArmies);

        /*
         * Inefficient block of code used to check and see if a "Reset Units" button should be displayed. Hacky and ugly, but by no means unduly burdensome for the Client.
         */
        int numButtons = 3;
        CPlayer player = mwclient.getPlayer();
        if (player != null) {
            if (player.getMyHouse().getName().equalsIgnoreCase(mwclient.getserverConfigs("NewbieHouseName"))) {
                newbieResetUnitsButton.setText("Reset Units");
                newbieResetUnitsButton.addActionListener(this::newbieResetUnitsButtonActionPerformed);
                hqButtonSpring.add(newbieResetUnitsButton);
                numButtons++;
            }
        }

        if (useAdvanceRepairs) {
            repairAllUnitsButton.setText("Repair All Units");
            repairAllUnitsButton.addActionListener(this::repairAllUnitsButtonActionPerformed);
            hqButtonSpring.add(repairAllUnitsButton);
            numButtons++;

            reloadAllUnitsButton.setText("Reload All Units");
            reloadAllUnitsButton.addActionListener(this::reloadAllUnitsButtonActionPerformed);
            hqButtonSpring.add(reloadAllUnitsButton);
            numButtons++;
        }

        setCamoButton.setText("Change Camo");
        setCamoButton.addActionListener(this::setCamoButtonActionPerformed);
        hqButtonSpring.add(setCamoButton);

        //@Salient add sol free build button
        if (player != null) {
            if (player.getMyHouse().getName().equalsIgnoreCase(mwclient.getserverConfigs("NewbieHouseName"))
            		&& mwclient.getserverConfigs("Sol_FreeBuild").equalsIgnoreCase("true")) {
                solFreeBuildButton.setText("Create Unit");
                solFreeBuildButton.addActionListener(this::solFreeBuildButtonActionPerformed);
                hqButtonSpring.add(solFreeBuildButton);
                numButtons++;
            }
            //also spawn button if post defection option is set
            if (!player.getMyHouse().getName().equalsIgnoreCase(mwclient.getserverConfigs("NewbieHouseName"))
                    && mwclient.getserverConfigs("FreeBuild_PostDefection").equalsIgnoreCase("true")) {
                solFreeBuildButton.setText("Create Unit");
                solFreeBuildButton.addActionListener(this::solFreeBuildButtonActionPerformed);
                hqButtonSpring.add(solFreeBuildButton);
                numButtons++;
            }
        }

        // do the spring layout on the buttons, then add them to the box
        SpringLayoutHelper.setupSpringGrid(hqButtonSpring, 1, numButtons);
        pnlMeksBtns.add(hqButtonSpring);
        pnlMeksBtns.validate();
        pnlMeksBtns.repaint();
    }

    @SuppressWarnings({"StatementWithEmptyBody", "rawtypes", "unchecked"})
    class MechTableMouseAdapter extends MouseInputAdapter implements ActionListener {

        // VARS
        private boolean isDrag;
        // private MekTableModel tableModel;

        private Image dragImage;
        private Rectangle2D dragRect;
        private Point offset;

        private CUnit dragUnit = null;
        private CArmy startArmy = null;
        private CArmy currArmy = null;

        private final Cursor exchangeCursor;
        private final Cursor positionCursor;
        private final Cursor addCursor;
        private final Cursor removeCursor;
        private final Cursor notAllowedCursor;
        private final Cursor dupeCursor;
        private final Cursor maxCursor;

        // CONSTRUCTOR
        public MechTableMouseAdapter() {
            super();

            Image plusI = Toolkit.getDefaultToolkit().createImage("./data/images/hqadd.gif");
            Image minusI = Toolkit.getDefaultToolkit().createImage("./data/images/hqremove.gif");
            Image exchangeI = Toolkit.getDefaultToolkit().createImage("./data/images/hqexchange.gif");
            Image positionI = Toolkit.getDefaultToolkit().createImage("./data/images/hqposition.gif");
            Image notallowedI = Toolkit.getDefaultToolkit().createImage("./data/images/hqnotallowed.gif");
            Image dupeI = Toolkit.getDefaultToolkit().createImage("./data/images/hqdouble.gif");
            Image maxI = Toolkit.getDefaultToolkit().createImage("./data/images/hqmax.gif");
            addCursor = Toolkit.getDefaultToolkit().createCustomCursor(plusI, new Point(0, 0), "addcursor");
            removeCursor = Toolkit.getDefaultToolkit().createCustomCursor(minusI, new Point(0, 0), "removecursor");
            exchangeCursor = Toolkit.getDefaultToolkit().createCustomCursor(exchangeI, new Point(0, 0), "exchangecursor");
            positionCursor = Toolkit.getDefaultToolkit().createCustomCursor(positionI, new Point(0, 0), "positioncursor");
            notAllowedCursor = Toolkit.getDefaultToolkit().createCustomCursor(notallowedI, new Point(0, 0), "noallowedcursor");
            dupeCursor = Toolkit.getDefaultToolkit().createCustomCursor(dupeI, new Point(0, 0), "dupecursor");
            maxCursor = Toolkit.getDefaultToolkit().createCustomCursor(maxI, new Point(0, 0), "maxcursor");
        }

        // METHODS
        @Override
        public void mousePressed(MouseEvent e) {

            int row = tblMeks.rowAtPoint(e.getPoint());
            int col = tblMeks.columnAtPoint(e.getPoint());
            dragUnit = MekTable.getMekAt(row, col);
            startArmy = MekTable.getArmyAt(row);

            if (dragUnit != null && e.getButton() == MouseEvent.BUTTON1) {

                // make isDrag true and save origins
                isDrag = true;

                // determine the offset
                offset = new Point(28, 22);// TODO: Make this a real offset,
                // not a simple re-centering.

                // Get a MechInfo image from the table cell renderer. The
                // renderer sets entity, camo etc. as part of normal drawing.
                MechInfo unitImage = (MechInfo) tblMeks.getCellRenderer(row, col).getTableCellRendererComponent(tblMeks, null, false, false, row, col);

                // save the image, drawn from mechinfo, to use as a drag
                // under-image
                dragImage = unitImage.getEmbeddedImage();
                dragRect = new Rectangle2D.Float();
                dragRect.setRect(e.getX(), e.getY(), 84, 72);

                // give the image some alpha
                AlphaFilter aFilter = new AlphaFilter(95);
                dragImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(dragImage.getSource(), aFilter));
            }

            /*
             * and ... check to see if this should trigger a popup.
             */
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            /*
             * If this was a drag, try to drop the unit into a target army or the hangar.
             */
            if (isDrag) {

                // regardless of outcome, clear drag image.
                tblMeks.paintImmediately(dragRect.getBounds());

                boolean validRelease = false;
                if (tblMeks.contains(e.getPoint())) {
                    validRelease = true;
                }

                int row = tblMeks.rowAtPoint(e.getPoint());
                int col = tblMeks.columnAtPoint(e.getPoint());
                CUnit exchangeUnit = MekTable.getMekAt(row, col);
                currArmy = MekTable.getArmyAt(row);

                // null finish army. moving to hangar.
                if (currArmy == null && validRelease) {

                    // if the unit is from an army, remove it
                    if (startArmy != null) {
                        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c EXM#" + startArmy.getID() + "," + dragUnit.getId());
                    }

                }// end if(release over hangar)

                // finish army exists
                else if (validRelease) {

                    // from hangar to an army
                    if (startArmy == null) {

                        // army # or empty space. add the unit.
                        if (exchangeUnit == null) {
                            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c EXM#" + currArmy.getID() + ",-1" + "#" + dragUnit.getId());
                        } else if (dragUnit.getId() != exchangeUnit.getId()) {
                            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c EXM#" + currArmy.getID() + "," + exchangeUnit.getId() + "#" + dragUnit.getId());
                        }
                    }

                    // within the same army, change positions
                    else if (currArmy.getID() == startArmy.getID() && exchangeUnit != null && dragUnit.getId() != exchangeUnit.getId()) {
                        int newpos = 0;
                        for (Unit currU : currArmy.getUnits()) {
                            if (currU.getId() == exchangeUnit.getId()) {
                                break;
                            }
                            newpos++;
                        }
                        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c unitposition#" + startArmy.getID() + "#" + dragUnit.getId() + "#" + newpos);
                    }

                }// end else(target army exists)

                // revert to normal cursor
                tblMeks.setCursor(Cursor.getDefaultCursor());

            }// end if(isDrag)

            isDrag = false;
            maybeShowPopup(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            if (isDrag) {

                // repaint the old image location
                tblMeks.paintImmediately(dragRect.getBounds());

                // determine new boundaries for the rectangle
                dragRect.setRect(e.getX() - offset.x, e.getY() - offset.y, 84, 72);

                // place the label in a new location
                Graphics2D g = (Graphics2D) tblMeks.getGraphics();
                g.drawImage(dragImage, AffineTransform.getTranslateInstance(dragRect.getX(), dragRect.getY()), null);

                /*
                 * Update the cursor depending on current drag status. If dragging a unit into an army which already contains the unit, mark ineligible. Else, show the drag cursor.
                 */
                int row = tblMeks.rowAtPoint(e.getPoint());
                int col = tblMeks.columnAtPoint(e.getPoint());
                CUnit currUnit = MekTable.getMekAt(row, col);
                currArmy = MekTable.getArmyAt(row);

                // null curr army. is an attempt to move to hangar.
                if (currArmy == null) {

                    // if the unit is from an army, could remove. show minus.
                    if (startArmy != null && mwclient.getMyStatus() == MWClient.STATUS_RESERVE) {
                        tblMeks.setCursor(removeCursor);
                    } else if (startArmy != null) {
                        tblMeks.setCursor(notAllowedCursor);
                    } else {
                        tblMeks.setCursor(Cursor.getDefaultCursor());
                    }

                }// end if(release over hangar)

                // currArmy exists
                else {

                    // from hangar to an army
                    if (startArmy == null) {

                        if (mwclient.getMyStatus() != MWClient.STATUS_RESERVE) {
                            tblMeks.setCursor(notAllowedCursor);
                        } else if (Player.getAmountOfTimesUnitExistsInArmies(dragUnit.getId()) >= Integer.parseInt(mwclient.getserverConfigs("UnitsInMultipleArmiesAmount"))) {
                            tblMeks.setCursor(maxCursor);
                        } else if (currArmy.getUnit(dragUnit.getId()) != null) {
                            tblMeks.setCursor(dupeCursor);
                        } else if (currUnit == null) {
                            tblMeks.setCursor(addCursor);
                        } else if (dragUnit.getId() != currUnit.getId()) {
                            tblMeks.setCursor(exchangeCursor);
                        }
                    }

                    // within the same army, change positions
                    else if (currArmy.getID() == startArmy.getID()) {

                        if (currUnit != null && dragUnit.getId() != currUnit.getId() && mwclient.getMyStatus() != MWClient.STATUS_FIGHTING) {
                            tblMeks.setCursor(positionCursor);
                        } else {
                            tblMeks.setCursor(notAllowedCursor);
                        }
                    } else {
                        tblMeks.setCursor(Cursor.getDefaultCursor());
                    }

                }// end else(target army exists)

            }

        }

        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getClickCount() == 2) {

                int row = tblMeks.rowAtPoint(e.getPoint());
                int col = tblMeks.columnAtPoint(e.getPoint());
                CUnit mek = MekTable.getMekAt(row, col);

                if (mek != null) {
                    JFrame infoWindow = new JFrame();
                    UnitDisplay unitdisplay = new UnitDisplay(null);
                    Entity theEntity = mek.getEntity();
                    theEntity.loadAllWeapons();
                    infoWindow.getContentPane().add(unitdisplay);
                    infoWindow.setSize(300, 400);
                    infoWindow.setResizable(true);
                    infoWindow.setTitle(mek.getModelName());
                    infoWindow.setLocationRelativeTo(null);
                    infoWindow.setVisible(true);
                    unitdisplay.displayEntity(theEntity);
                }
            }
            tblMeks.repaint();
        }

        /**
         * Private method called on click and release. Checks to see if if mouse event should open a contextual menu (right click, OS X control+click, etc) and
         * shows a popup menu if appropriate.
         */
        private void maybeShowPopup(MouseEvent e) {
            JPopupMenu popup = new JPopupMenu();
            if (e.isPopupTrigger()) {
                int row = tblMeks.rowAtPoint(e.getPoint());
                int col = tblMeks.columnAtPoint(e.getPoint());
                JMenuItem menuItem;

                if (col == 0 && row >= MekTable.getRowsForArmies()) {
                    JMenu primeSortMenu = new JMenu("Sort (1st)");
                    JMenu secondarySortMenu = new JMenu("Sort (2nd)");
                    JMenu tertiarySortMenu = new JMenu("Sort (3rd)");

                    popup.add(primeSortMenu);
                    popup.add(secondarySortMenu);
                    popup.add(tertiarySortMenu);

                    // Choices [note - this array must be duplicated in
                    // CPlayer's sortHangar()]
                    String[] choices = { "Name", "Battle Value", "Gunnery Skill", "ID Number", "MP (Jumping)", "MP (Walking)", "Pilot Kills", "Unit Type", "Weight (Class)", "Weight (Tons)", "No Sort" };

                    // indicate current selections w/ Italics
                    String menuName;
                    // boolean selectionFound = true;

                    // prime sort menu construction
                    for (int i = 0; i < choices.length; i++) {

                        menuName = choices[i];
                        if (mwclient.getConfigParam("PRIMARYHQSORTORDER").equals(choices[i])) {
                            menuName = "<HTML><i>" + menuName + "</i></HTML>";
                            // selectionFound = false;
                        }
                        menuItem = new JMenuItem(menuName);
                        menuItem.setActionCommand("PHQS|" + choices[i]);
                        menuItem.addActionListener(this);
                        primeSortMenu.add(menuItem);

                        if (i + 2 == choices.length) {
                            primeSortMenu.addSeparator();
                        }
                    }

                    // reset selectionFound
                    // selectionFound = true;

                    // secondary sort menu construction
                    for (int i = 0; i < choices.length; i++) {

                        menuName = choices[i];
                        if (mwclient.getConfigParam("SECONDARYHQSORTORDER").equals(choices[i])) {
                            menuName = "<HTML><i>" + menuName + "</i></HTML>";
                            // selectionFound = false;
                        }
                        menuItem = new JMenuItem(menuName);
                        menuItem.setActionCommand("SHQS|" + choices[i]);
                        menuItem.addActionListener(this);
                        secondarySortMenu.add(menuItem);

                        if (i + 2 == choices.length) {
                            secondarySortMenu.addSeparator();
                        }
                    }

                    // reset selectionFound
                    // selectionFound = true;

                    // tertiary sort menu construction
                    for (int i = 0; i < choices.length; i++) {

                        menuName = choices[i];
                        if (mwclient.getConfigParam("TERTIARYHQSORTORDER").equals(choices[i])) {
                            menuName = "<HTML><i>" + menuName + "</i></HTML>";
                            // selectionFound = false;
                        }
                        menuItem = new JMenuItem(menuName);
                        menuItem.setActionCommand("THQS|" + choices[i]);
                        menuItem.addActionListener(this);
                        tertiarySortMenu.add(menuItem);

                        if (i + 2 == choices.length) {
                            tertiarySortMenu.addSeparator();
                        }
                    }

                    popup.show(e.getComponent(), e.getX(), e.getY());

                }

                else if (row < 0 || col == 0) {

                    CArmy l = MekTable.getArmyAt(row);
                    if (l != null) {

                        int lid = l.getID();
                        if (l.getBV() > 0) {

                            /*
                             * if (!l.isReady()){ menuItem = new JMenuItem("Set Active"); menuItem.setActionCommand("SA|"+lid); menuItem.addActionListener(this); popup.add(menuItem); } else { menuItem = new JMenuItem("Set Inactive"); menuItem.setActionCommand("SI|"+lid); menuItem.addActionListener(this); popup.add(menuItem); }
                             */

                            menuItem = new JMenuItem("Attack Options");
                            menuItem.setActionCommand("AO|" + lid);
                            menuItem.addActionListener(this);
                            boolean canCheckFromReserve = Boolean.parseBoolean(mwclient.getserverConfigs("ProbeInReserve"));
                            if (mwclient.getMyStatus() != MWClient.STATUS_ACTIVE && !canCheckFromReserve) {
                                menuItem.setEnabled(false);
                            }
                            popup.add(menuItem);

                            menuItem = new JMenuItem("Check Access");
                            menuItem.setActionCommand("CAA|" + lid);
                            menuItem.addActionListener(this);
                            popup.add(menuItem);

                            // only show "Limits" option if limits allowed
                            boolean limitsAllowed = Boolean.parseBoolean(mwclient.getserverConfigs("AllowLimiters"));
                            if (limitsAllowed) {
                                JMenu limitmenu = new JMenu("Limits");
                                popup.add(limitmenu);
                                menuItem = new JMenuItem("Set Lower Unit Limit");
                                menuItem.setActionCommand("SLUL|" + lid);
                                menuItem.addActionListener(this);
                                limitmenu.add(menuItem);
                                menuItem = new JMenuItem("Set Upper Unit Limit");
                                menuItem.setActionCommand("SUUL|" + lid);
                                menuItem.addActionListener(this);
                                limitmenu.add(menuItem);
                            }

                            // Only show when Force Size is used.
                            if (Boolean.parseBoolean(mwclient.getserverConfigs("UseOperationsRule"))) {
                                menuItem = new JMenuItem("Force Size To Face");
                                popup.add(menuItem);
                                menuItem.setActionCommand("SFS|" + lid);
                                menuItem.addActionListener(this);
                            }

                            AttackMenu aMenu = new AttackMenu(mwclient, lid, "-1");
                            aMenu.updateMenuItems(false);
                            popup.add(aMenu);

                            popup.addSeparator();
                        }

                        menuItem = new JMenuItem("Lock Army");
                        menuItem.setActionCommand("LA|" + lid);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                        if (mwclient.getPlayer().getArmy(lid).isPlayerLocked()) {
                            menuItem.setVisible(false);
                        }
                        menuItem = new JMenuItem("Unlock Army");
                        menuItem.setActionCommand("ULA|" + lid);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                        if (!mwclient.getPlayer().getArmy(lid).isPlayerLocked()) {
                            menuItem.setVisible(false);
                        }
                        menuItem = new JMenuItem("Remove Army");
                        menuItem.setActionCommand("RA|" + lid);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                        menuItem = new JMenuItem("Rename Army");
                        menuItem.setActionCommand("NA|" + lid);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                        menuItem = new JMenuItem("Disable Army");
                        menuItem.setActionCommand("DAA|" + lid);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                        if (mwclient.getPlayer().getArmy(lid).isDisabled()) {
                            menuItem.setVisible(false);
                        }
                        menuItem = new JMenuItem("Enable Army");
                        menuItem.setActionCommand("DAA|" + lid);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);
                        if (!mwclient.getPlayer().getArmy(lid).isDisabled()) {
                            menuItem.setVisible(false);
                        }

                        JMenu primeSortMenu = new JMenu("Sort (1st)");
                        // JMenu secondarySortMenu = new JMenu("Sort (2nd)");
                        // JMenu tertiarySortMenu = new JMenu("Sort (3rd)");

                        popup.add(primeSortMenu);
                        // popup.add(secondarySortMenu);
                        // popup.add(tertiarySortMenu);

                        // Choices [note - this array must be duplicated in
                        // CPlayer's sortArmies()]
                        String[] choices = { "Name", "Battle Value", "ID Number", "Max Tonnage", "Avg Walk MP", "Avg Jump MP", "Number Of Units", "No Sort" };

                        // indicate current selections w/ Italics
                        String menuName;
                        // boolean selectionFound = true;

                        // prime sort menu construction
                        for (int i = 0; i < choices.length; i++) {

                            menuName = choices[i];
                            if (mwclient.getConfigParam("PRIMARYARMYSORTORDER").equalsIgnoreCase(choices[i])) {
                                menuName = "<HTML><i>" + menuName + "</i></HTML>";
                                // selectionFound = false;
                            }
                            menuItem = new JMenuItem(menuName);
                            menuItem.setActionCommand("PAS|" + choices[i]);
                            menuItem.addActionListener(this);
                            primeSortMenu.add(menuItem);

                            if (i + 2 == choices.length) {
                                primeSortMenu.addSeparator();
                            }
                        }

                        // reset selectionFound
                        // selectionFound = true;

                        /*
                         * secondary sort menu construction for (int i = 0; i < choices.length; i++) { menuName = choices[i]; if (mwclient.getConfigParam("SECONDARYARMYSORTORDER").equals(choices[i])) { menuName = "<HTML><i>" + menuName + "</i></HTML>"; //selectionFound = false; } menuItem = new JMenuItem(menuName); menuItem.setActionCommand("SAS|" + choices[i]); menuItem.addActionListener(this); secondarySortMenu.add(menuItem); if (i + 2 == choices.length) secondarySortMenu.addSeparator(); } //reset selectionFound //selectionFound = true; //tertiary sort menu construction for (int i = 0; i < choices.length; i++) { menuName = choices[i]; if (mwclient.getConfigParam("TERTIARYARMYSORTORDER").equals(choices[i])) { menuName = "<HTML><i>" + menuName + "</i></HTML>"; //selectionFound = false; } menuItem = new JMenuItem(menuName); menuItem.setActionCommand("TAS|" + choices[i]);
                         * menuItem.addActionListener(this); tertiarySortMenu.add(menuItem); if (i + 2 == choices.length) tertiarySortMenu.addSeparator(); }
                         */
                        popup.addSeparator();

                        menuItem = new JMenuItem("Show To Faction");
                        menuItem.setActionCommand("SATH|" + lid);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        // disable showtofaction if army has 0 units
                        if (mwclient.getPlayer().getArmy(lid).getUnits().size() <= 0) {
                            menuItem.setEnabled(false);
                        }

                        CArmy army = mwclient.getPlayer().getArmy(lid);

                        JMenu challengeMenu = new JMenu("Request Match");
                        popup.add(challengeMenu);

                        JMenu allArmies = new JMenu("All Armies");
                        JMenu singleArmy = new JMenu("This Army");

                        challengeMenu.add(singleArmy);
                        challengeMenu.add(allArmies);

                        // disable if army has 0 units
                        if (mwclient.getPlayer().getArmy(lid).getUnits().size() <= 0) {
                            challengeMenu.setEnabled(false);
                        }

                        JMenu submenu = new JMenu("Unit");

                        JMenu requestMenu = new JMenu("BV Only");

                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|1|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|1|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);

                        requestMenu = new JMenu("Unit Count and BV");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|2|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|2|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);

                        requestMenu = new JMenu("Unit Classes and BV");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|3|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|3|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);
                        singleArmy.add(submenu);

                        submenu = new JMenu("Total Weight");
                        requestMenu = new JMenu("Total Weight");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|4|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|4|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);

                        requestMenu = new JMenu("Total Weight with BV");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|5|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|5|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);

                        requestMenu = new JMenu("Total Weight and Unit Count");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|6|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|6|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);

                        requestMenu = new JMenu("Total Weight, Unit Count and BV");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|7|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|7|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);
                        singleArmy.add(submenu);

                        submenu = new JMenu("Unit Types");
                        requestMenu = new JMenu("Unit Types");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|8|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|8|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);

                        requestMenu = new JMenu("Unit Types with BV");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|9|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|9|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);
                        singleArmy.add(submenu);

                        submenu = new JMenu("Unit Models");
                        requestMenu = new JMenu("Unit Models");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|10|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|10|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);

                        requestMenu = new JMenu("Unit Models with BV");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|11|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|11|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);
                        singleArmy.add(submenu);

                        submenu = new JMenu("Actual Weight");
                        requestMenu = new JMenu("Actual Unit Weights");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|12|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|12|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);

                        requestMenu = new JMenu("Actual Unit Weights with BV");
                        menuItem = new JMenuItem("None");
                        menuItem.setActionCommand("MPC|13|" + lid + "|none");
                        menuItem.addActionListener(this);
                        requestMenu.add(menuItem);
                        for (String op : army.getLegalOperations()) {
                            menuItem = new JMenuItem(op);
                            menuItem.setActionCommand("MPC|13|" + lid + "|" + op);
                            menuItem.addActionListener(this);
                            requestMenu.add(menuItem);
                        }
                        submenu.add(requestMenu);
                        singleArmy.add(submenu);

                        submenu = new JMenu("Unit");

                        menuItem = new JMenuItem("BV Only");
                        // All armies so set the lid to -1;
                        menuItem.setActionCommand("MPC|1|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);

                        menuItem = new JMenuItem("Unit Count and BV");
                        menuItem.setActionCommand("MPC|2|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);

                        menuItem = new JMenuItem("Unit Classes and BV");
                        menuItem.setActionCommand("MPC|3|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);
                        allArmies.add(submenu);

                        submenu = new JMenu("Total Weight");
                        menuItem = new JMenuItem("Total Weight");
                        menuItem.setActionCommand("MPC|4|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);

                        menuItem = new JMenuItem("Total Weight with BV");
                        menuItem.setActionCommand("MPC|5|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);

                        menuItem = new JMenuItem("Total Weight and Unit Count");
                        menuItem.setActionCommand("MPC|6|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);

                        menuItem = new JMenuItem("Total Weight, Unit Count and BV");
                        menuItem.setActionCommand("MPC|7|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);
                        allArmies.add(submenu);

                        submenu = new JMenu("Unit Types");
                        menuItem = new JMenuItem("Unit Types");
                        menuItem.setActionCommand("MPC|8|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);

                        menuItem = new JMenuItem("Unit Types with BV");
                        menuItem.setActionCommand("MPC|9|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);
                        allArmies.add(submenu);

                        submenu = new JMenu("Unit Models");
                        menuItem = new JMenuItem("Unit Models");
                        menuItem.setActionCommand("MPC|10|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);

                        menuItem = new JMenuItem("Unit Models with BV");
                        menuItem.setActionCommand("MPC|11|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);
                        allArmies.add(submenu);

                        submenu = new JMenu("Actual Weight");
                        menuItem = new JMenuItem("Actual Unit Weights");
                        menuItem.setActionCommand("MPC|12|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);

                        menuItem = new JMenuItem("Actual Unit Weights with BV");
                        menuItem.setActionCommand("MPC|13|-1|none");
                        menuItem.addActionListener(this);
                        submenu.add(menuItem);
                        allArmies.add(submenu);

                    }

                    popup.show(e.getComponent(), e.getX(), e.getY());
                } else if (row < MekTable.getRowsForArmies()) {
                    CUnit cm;
                    CArmy l = MekTable.getArmyAt(row);
                    int mid;
                    int lid = l.getID();
                    cm = MekTable.getMekAt(row, col);
                    boolean hasUnitsFree = false;

                    /*
                     * CONSTRUCT the ADD menu here. It will be added to the actual format later. @urgru 12/7/04
                     */
                    JMenu addMenu = new JMenu("Add");
                    if (mwclient.getPlayer().getHangar().size() > 0 && !l.isLocked()) {
                        Object[] mechArray = mwclient.getPlayer().getHangar().toArray();
                        if (mechArray.length > 0) {
                            Vector<Vector<JMenuItem>> SubMenus = new Vector<>(1, 1);

                            /*
                             * 6 entries Weights: 0-3 Protomech: 4 Infantry: 5
                             */
                            for (int i = 0; i < 6; i++) {
                                SubMenus.add(new Vector<>(1, 1));
                            }

                            for (Object element : mechArray) {
                                CUnit mm = (CUnit) element;
                                if (mm.getStatus() == Unit.STATUS_UNMAINTAINED || mm.getStatus() == Unit.STATUS_FORSALE) {
                                    continue;
                                }
                                if (Player.getAmountOfTimesUnitExistsInArmies(mm.getId()) >= Integer.parseInt(mwclient.getserverConfigs("UnitsInMultipleArmiesAmount"))) {
                                    continue;
                                }
                                if (l.getUnit(mm.getId()) == null) {// only add
                                    // if unit
                                    // isn't
                                    // already
                                    // in army
                                    hasUnitsFree = true;
                                    if (mm.getType() == Unit.MEK || mm.getType() == Unit.VEHICLE || mm.getType() == Unit.AERO) {
                                        menuItem = new JMenuItem(mm.getModelName() + " (" + mm.getPilot().getGunnery() + "/" + mm.getPilot().getPiloting() + ") " + mm.getBVForMatch() + " BV");
                                    } else if (mm.getType() == Unit.INFANTRY || mm.getType() == Unit.BATTLEARMOR) {
                                        if (((Infantry) mm.getEntity()).canMakeAntiMekAttacks()) {
                                            menuItem = new JMenuItem(mm.getModelName() + " (" + mm.getPilot().getGunnery() + "/" + mm.getPilot().getPiloting() + ") " + mm.getBVForMatch() + " BV");
                                        } else {
                                            menuItem = new JMenuItem(mm.getModelName() + " (" + mm.getPilot().getGunnery() + ") " + mm.getBVForMatch() + " BV");
                                        }
                                    } else {
                                        menuItem = new JMenuItem(mm.getModelName() + " (" + mm.getPilot().getGunnery() + ") " + mm.getBVForMatch() + " BV");
                                    }
                                    menuItem.setActionCommand("EXM|" + lid + "|" + "-1" + "|" + mm.getId());
                                    menuItem.addActionListener(this);

                                    if (mm.getType() == Unit.PROTOMEK) {
                                        SubMenus.elementAt(4).add(menuItem);// into
                                        // proto
                                        // slot
                                    } else if (mm.getType() == Unit.INFANTRY || mm.getType() == Unit.BATTLEARMOR) {
                                        SubMenus.elementAt(5).add(menuItem);// into
                                        // BA
                                        // slot
                                    } else {// else, sort by weightclass
                                        int size = mm.getWeightclass();
                                        SubMenus.elementAt(size).add(menuItem);
                                    }
                                }
                            }
                            for (int i = 0; i < SubMenus.size(); i++) {
                                Vector<JMenuItem> SizeMenu = SubMenus.elementAt(i);
                                if (SizeMenu.size() > 10) {
                                    // More than one menu of the given size
                                    // class is needed
                                    int iterations = SizeMenu.size() / 10 + 1;
                                    for (int j = 0; j < iterations; j++) {
                                        int mechcount = 0;

                                        JMenu menux;
                                        if (i < 4) {
                                            menux = new JMenu(Unit.getWeightClassDesc(i) + " " + (j + 1));
                                        } else if (i == 4) {// proto
                                            menux = new JMenu("Proto " + (j + 1));
                                        } else {// BA, can assume this is i ==
                                            // 5.
                                            menux = new JMenu("Infantry " + (j + 1));
                                        }

                                        while (!SizeMenu.isEmpty() && mechcount < 10) {
                                            menux.add(SizeMenu.elementAt(0));
                                            SizeMenu.removeElementAt(0);
                                            SizeMenu.trimToSize();
                                            mechcount++;
                                        }

                                        // if adding proto or infantry menu,
                                        // check previous elements
                                        // to see if a divider should be added
                                        if (i >= 4) {

                                            Component[] components = addMenu.getMenuComponents();
                                            if (i == 4 && components.length != 0) {
                                                addMenu.addSeparator();
                                            } else if (i == 5) {
                                                boolean hasProtoMenu = false;
                                                for (Component currComponent : components) {
                                                    if (currComponent instanceof JMenu) {
                                                        JMenu currMenu = (JMenu) currComponent;
                                                        if (currMenu.getText().startsWith("Proto")) {
                                                            hasProtoMenu = true;
                                                        }
                                                    }
                                                }
                                                if (!hasProtoMenu && components.length > 0 && menux.getComponentCount() > 0) {
                                                    addMenu.addSeparator();
                                                }
                                            }
                                        }

                                        addMenu.add(menux);
                                    }
                                } else {// Only one menu for the given size
                                    // class is needed

                                    JMenu menux;
                                    if (i < 4) {
                                        menux = new JMenu(Unit.getWeightClassDesc(i));
                                    } else if (i == 4) {// proto
                                        menux = new JMenu("Proto");
                                    } else {// BA, can assume i = 5.
                                        menux = new JMenu("Infantry");
                                    }

                                    // if adding proto or infantry menu, check
                                    // previous elements
                                    // to see if a divider should be added
                                    boolean hasProtoMenu = false;
                                    Component[] components = addMenu.getMenuComponents();
                                    if (i == 5) {
                                        for (Component currComponent : components) {
                                            if (currComponent instanceof JMenu) {
                                                JMenu currMenu = (JMenu) currComponent;
                                                if (currMenu.getText().startsWith("Proto")) {
                                                    hasProtoMenu = true;
                                                }
                                            }
                                        }
                                    }

                                    for (int j = 0; j < SizeMenu.size(); j++) {
                                        menux.add(SizeMenu.elementAt(j));

                                        if (i == 5 && j == 0 && !hasProtoMenu && components.length > 0) {
                                            addMenu.addSeparator();
                                        } else if (i == 4 && j == 0 && components.length > 0) {
                                            addMenu.addSeparator();
                                        }

                                        addMenu.add(menux);
                                    }
                                }
                            }
                        }

                        // disable the menu if there are no units to add
                        addMenu.setEnabled(hasUnitsFree);

                    }// end ADD menu contruction

                    // if the unit isnt null, include remove/show/etc
                    if (cm != null) {

                        /*
                         * the unit isnt null, so construct the link menu here. It will be added to the actual format later. @Torren 12/19/04
                         */
                        JMenu linkMenu = new JMenu("Link");
                        if (l.getUnits().size() > 0 && !l.isLocked()) {
                            Vector<CUnit> Masters = new Vector<>(1, 1);
                            Enumeration<Unit> c3M = l.getUnits().elements();
                            while (c3M.hasMoreElements()) {
                                CUnit c3Unit = (CUnit) c3M.nextElement();
                                if (c3Unit.equals(cm)) {
                                    continue;
                                }
                                if (cm.getC3Level() != Unit.C3_IMPROVED) {
                                    if ((c3Unit.getC3Level() == Unit.C3_MASTER || c3Unit.getC3Level() == Unit.C3_MMASTER) && c3Unit.checkC3mNetworkHasOpen(l, cm.getC3Level())) {
                                        Masters.add(c3Unit);
                                    }
                                } else if (cm.getC3Level() == Unit.C3_IMPROVED) {
                                    if (c3Unit.getC3Level() == Unit.C3_IMPROVED && c3Unit.checkC3iNetworkHasOpen(l)) {
                                        Masters.add(c3Unit);
                                    }
                                }
                            }
                            for (int i = 0; i < Masters.size(); i++) {
                                CUnit mm = Masters.elementAt(i);
                                if (l.getUnit(mm.getId()) != null) {
                                    menuItem = new JMenuItem(mm.getModelName() + " " + mm.getBVForMatch() + " BV");
                                    menuItem.setActionCommand("LCN|" + lid + "|" + cm.getId() + "|" + mm.getId());
                                    menuItem.addActionListener(this);
                                    linkMenu.add(menuItem);
                                }
                            }
                            /*
                             * if ( cm.getC3Level() == CUnit.C3_MASTER ){ linkMenu.addSeparator(); menuItem = new JMenuItem("Set as Company Commander"); menuItem.setActionCommand("LCN|"+lid+"|"+ cm.getId() +"|"+cm.getId()); menuItem.addActionListener(this); linkMenu.add(menuItem); }
                             */
                        }// end Link menu contruction

                        // Link menu has been preformed. Proceed with the usual
                        // bits.
                        mid = cm.getId();

                        // move to hangar
                        if (!l.isLocked()) {
                            String text = "Move To Hangar";
                            menuItem = new JMenuItem(text);
                            menuItem.setActionCommand("MH|" + lid + "|" + mid);
                            menuItem.addActionListener(this);
                            popup.add(menuItem);
                        }

                        /*
                         * EXCHANGE. Derived from ADD. Same, but returns clicked unit to hangar.
                         */
                        if (mwclient.getPlayer().getHangar().size() > 0 && !l.isLocked()) {
                            JMenu jm = new JMenu("Exchange");
                            popup.add(jm);
                            Object[] mechs = mwclient.getPlayer().getHangar().toArray();
                            if (mechs.length > 0) {
                                Vector<Vector<JMenuItem>> SubMenus = new Vector<>();

                                /*
                                 * 6 entries Weights: 0-3 Protomech: 4 Infantry: 5
                                 */
                                for (int i = 0; i < 6; i++) {
                                    SubMenus.add(new Vector<>(1, 1));
                                }

                                for (Object mech : mechs) {
                                    CUnit mm = (CUnit) mech;
                                    if (mm.getStatus() == Unit.STATUS_UNMAINTAINED || mm.getStatus() == Unit.STATUS_FORSALE) {
                                        continue;
                                    }
                                    if (Player.getAmountOfTimesUnitExistsInArmies(mm.getId()) >= Integer.parseInt(mwclient.getserverConfigs("UnitsInMultipleArmiesAmount"))) {
                                        continue;
                                    }
                                    if (l.getUnit(mm.getId()) == null) {// only
                                        // allow
                                        // exchange
                                        // if
                                        // unit
                                        // isn't
                                        // already
                                        // in
                                        // army
                                        if (mm.getType() == Unit.MEK || mm.getType() == Unit.VEHICLE || mm.getType() == Unit.AERO) {
                                            menuItem = new JMenuItem(mm.getModelName() + " (" + mm.getPilot().getGunnery() + "/" + mm.getPilot().getPiloting() + ") " + mm.getBVForMatch() + " BV");
                                        } else if (mm.getType() == Unit.INFANTRY || mm.getType() == Unit.BATTLEARMOR) {
                                            if (((Infantry) mm.getEntity()).canMakeAntiMekAttacks()) {
                                                menuItem = new JMenuItem(mm.getModelName() + " (" + mm.getPilot().getGunnery() + "/" + mm.getPilot().getPiloting() + ") " + mm.getBVForMatch() + " BV");
                                            } else {
                                                menuItem = new JMenuItem(mm.getModelName() + " (" + mm.getPilot().getGunnery() + ") " + mm.getBVForMatch() + " BV");
                                            }
                                        } else {
                                            menuItem = new JMenuItem(mm.getModelName() + " (" + mm.getPilot().getGunnery() + ") " + mm.getBVForMatch() + " BV");
                                        }
                                        menuItem.setActionCommand("EXM|" + lid + "|" + cm.getId() + "|" + mm.getId());
                                        menuItem.addActionListener(this);

                                        if (mm.getType() == Unit.PROTOMEK) {
                                            SubMenus.elementAt(4).add(menuItem);// into
                                            // proto
                                            // slot
                                        } else if (mm.getType() == Unit.INFANTRY || mm.getType() == Unit.BATTLEARMOR) {
                                            SubMenus.elementAt(5).add(menuItem);// into
                                            // BA
                                            // slot
                                        } else {// else, sort by weightclass
                                            int size = mm.getWeightclass();
                                            SubMenus.elementAt(size).add(menuItem);
                                        }
                                    }
                                }
                                for (int i = 0; i < SubMenus.size(); i++) {
                                    Vector<JMenuItem> SizeMenu = SubMenus.elementAt(i);
                                    JMenu menux;
                                    if (SizeMenu.size() > 10) {
                                        // More than one menu of the given size
                                        // class is needed
                                        int iterations = SizeMenu.size() / 10 + 1;
                                        for (int j = 0; j < iterations; j++) {
                                            int mechcount = 0;

                                            if (i < 4) {
                                                menux = new JMenu(Unit.getWeightClassDesc(i) + " " + (j + 1));
                                            } else if (i == 4) {// proto
                                                menux = new JMenu("Proto " + (j + 1));
                                            } else {// BA, assume an i of 5
                                                menux = new JMenu("Infantry " + (j + 1));
                                            }

                                            while (!SizeMenu.isEmpty() && mechcount < 10) {
                                                menux.add(SizeMenu.elementAt(0));
                                                SizeMenu.removeElementAt(0);
                                                SizeMenu.trimToSize();
                                                mechcount++;
                                            }

                                            // if adding proto or infantry menu,
                                            // check previous elements
                                            // to see if a divider should be
                                            // added
                                            if (i >= 4) {

                                                Component[] components = addMenu.getMenuComponents();
                                                if (i == 4 && components.length != 0) {
                                                    jm.addSeparator();
                                                } else if (i == 5) {
                                                    boolean hasProtoMenu = false;
                                                    for (Component currComponent : components) {
                                                        if (currComponent instanceof JMenu) {
                                                            JMenu currMenu = (JMenu) currComponent;
                                                            if (currMenu.getText().startsWith("Proto")) {
                                                                hasProtoMenu = true;
                                                            }
                                                        }
                                                    }
                                                    if (!hasProtoMenu && components.length > 0 && menux.getComponentCount() > 0) {
                                                        jm.addSeparator();
                                                    }
                                                }
                                            }

                                            jm.add(menux);
                                        }
                                    } else {// Only one menu for the given size
                                        // class is needed

                                        if (i < 4) {
                                            menux = new JMenu(Unit.getWeightClassDesc(i));
                                        } else if (i == 4) {// proto
                                            menux = new JMenu("Proto");
                                        } else {// BA, assume an i of 5.
                                            menux = new JMenu("Infantry");
                                        }

                                        // if adding proto or infantry menu,
                                        // check previous elements
                                        // to see if a divider should be added
                                        boolean hasProtoMenu = false;
                                        Component[] components = jm.getMenuComponents();
                                        if (i == 5) {
                                            for (Component currComponent : components) {
                                                if (currComponent instanceof JMenu) {
                                                    JMenu currMenu = (JMenu) currComponent;
                                                    if (currMenu.getText().startsWith("Proto")) {
                                                        hasProtoMenu = true;
                                                    }
                                                }
                                            }
                                        }

                                        for (int j = 0; j < SizeMenu.size(); j++) {
                                            menux.add(SizeMenu.elementAt(j));

                                            if (i == 5 && j == 0 && !hasProtoMenu && components.length > 0) {
                                                jm.addSeparator();
                                            } else if (i == 4 && j == 0 && components.length > 0) {
                                                jm.addSeparator();
                                            }

                                            jm.add(menux);
                                        }
                                    }

                                }
                            }  // end exchange


                            // hasUnitsFree is set during add menu creation, but
                            // applies equally to the Exchange menu.
                            jm.setEnabled(hasUnitsFree);

                            /*
                             * The ADD menu, constructed previously
                             */
                            popup.add(addMenu);

                            /*
                             * The POSITION menu. Moves units around -within- the army. Only shown if there are enough units to warrant movement (>1).
                             */
                            if (l.getAmountOfUnits() > 1) {
                                JMenu pjm = new JMenu("Position");
                                popup.add(pjm);
                                int currPos = 0;
                                for (Unit u : l.getUnits()) {
                                    CUnit currUnit = (CUnit) u;
                                    if (currUnit.getId() != mid) {
                                        menuItem = new JMenuItem("Move to #" + (currPos + 1));
                                        menuItem.setActionCommand("RPU|" + lid + "|" + cm.getId() + "|" + currPos);
                                        menuItem.addActionListener(this);
                                        pjm.add(menuItem);
                                    }
                                    currPos++;
                                }
                            }// end position menu construction

                        }// end codeblack for Exchange AND Add AND Position
                        if (cm.getC3Level() != Unit.C3_NONE) {
                            popup.add(linkMenu);
                        }
                        if (cm.hasBeenC3LinkedTo(l) || l.getC3Network().get(cm.getId()) != null) {
                            menuItem = new JMenuItem("Unlink");
                            menuItem.setActionCommand("LCN|" + lid + "|" + cm.getId() + "|-1");
                            menuItem.addActionListener(this);
                            popup.add(menuItem);

                        }
                        // divide army composition/unit display options.
                        popup.addSeparator();

                        // Add Show Mek Option
                        menuItem = new JMenuItem("View Unit");
                        menuItem.setActionCommand("SM|" + row + "|" + col);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        // Add Customize Unit Option
                        menuItem = new JMenuItem("Customize Unit");
                        menuItem.setActionCommand("CMU|" + row + "|" + col);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        // Add Autoeject Option
                        if (cm.getEntity() instanceof Mech) {
                            Mech mech = (Mech) cm.getEntity();
                            if (mech.isAutoEject()) {
                                menuItem = new JMenuItem("Disable Autoeject");
                                menuItem.setActionCommand("DAE|" + row + "|" + col);
                            } else {
                                menuItem = new JMenuItem("Enable Autoeject");
                                menuItem.setActionCommand("EAE|" + row + "|" + col);
                            }
                            menuItem.addActionListener(this);
                            popup.add(menuItem);
                        }

                        if (l.isCommander(cm.getId())) {
                            menuItem = new JMenuItem("Remove Commander");
                            menuItem.setActionCommand("REMOVEUNITCOMMANDER|" + row + "|" + col + "|" + lid);
                        } else {
                            menuItem = new JMenuItem("Set Commander");
                            menuItem.setActionCommand("SETUNITCOMMANDER|" + row + "|" + col + "|" + lid);
                        }
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                    }// end if(cm in click area != null)
                    else {
                        popup.add(addMenu);
                    }

                    popup.show(e.getComponent(), e.getX(), e.getY());
                } else {
                    CUnit cm = MekTable.getMekAt(row, col);
                    if (cm != null) {

                        menuItem = new JMenuItem("View Unit");
                        menuItem.setActionCommand("SM|" + row + "|" + col);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        // Add Customize Unit Option
                        menuItem = new JMenuItem("Customize Unit");
                        menuItem.setActionCommand("CMU|" + row + "|" + col);
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        if (useAdvanceRepairs) {

                            JMenu repairs = new JMenu("Repairs");
                            if (UnitUtils.hasArmorDamage(cm.getEntity()) || UnitUtils.hasCriticalDamage(cm.getEntity())) {
                                if (!Boolean.parseBoolean(mwclient.getserverConfigs("UseSimpleRepair"))) {
                                    // Add repair unit option
                                    menuItem = new JMenuItem("Repair Unit");
                                    menuItem.setActionCommand("ARU|" + row + "|" + col);
                                    menuItem.addActionListener(this);
                                    repairs.add(menuItem);
                                    menuItem = new JMenuItem("Bulk Repair");
                                    menuItem.setActionCommand("BUR|" + row + "|" + col);
                                    menuItem.addActionListener(this);
                                    repairs.add(menuItem);
                                } else {
                                    menuItem = new JMenuItem("Repair Unit");
                                    menuItem.setActionCommand("SUR|" + row + "|" + col);
                                    menuItem.addActionListener(this);
                                    repairs.add(menuItem);
                                }

                            }

                            if (Boolean.parseBoolean(mwclient.getserverConfigs("UsePartsRepair")) && (cm.getType() == Unit.MEK || cm.getType() == Unit.VEHICLE)) {
                                menuItem = new JMenuItem("Salvage Unit Crits");
                                menuItem.setActionCommand("SUC|" + row + "|" + col);
                                menuItem.addActionListener(this);
                                repairs.add(menuItem);
                                menuItem = new JMenuItem("Bulk Salvage");
                                menuItem.setActionCommand("BSU|" + row + "|" + col);
                                menuItem.addActionListener(this);
                                repairs.add(menuItem);
                            }

                            if (UnitUtils.isRepairing(cm.getEntity())) {
                                // Add display repair job option
                                menuItem = new JMenuItem("Display Repair Jobs");
                                menuItem.setActionCommand("DRJ|" + row + "|" + col);
                                menuItem.addActionListener(this);
                                repairs.add(menuItem);
                            }

                            if (mwclient.getRMT() != null && mwclient.getRMT().hasQueuedOrders(cm.getId()) || mwclient.getSMT() != null && mwclient.getSMT().hasQueuedOrders(cm.getId())) {
                                // Add display pending job option
                                menuItem = new JMenuItem("Display Pending Work Orders");
                                menuItem.setActionCommand("DPWO|" + row + "|" + col);
                                menuItem.addActionListener(this);
                                repairs.add(menuItem);
                                // Add stop all pending jobs
                                menuItem = new JMenuItem("Stop All Pending Work Orders");
                                menuItem.setActionCommand("SAPWO|" + row + "|" + col);
                                menuItem.addActionListener(this);
                                repairs.add(menuItem);
                            }

                            if (!UnitUtils.hasAllAmmo(cm.getEntity())) {
                                menuItem = new JMenuItem("Reload All Ammo");
                                menuItem.setActionCommand("RAA|" + row + "|" + col);
                                menuItem.addActionListener(this);
                                repairs.add(menuItem);
                            }
                            if (repairs.getItemCount() > 0) {
                                popup.add(repairs);
                            }
                        }

                        // Add Autoeject Option
                        if (cm.getEntity() instanceof Mech) {
                            Mech mech = (Mech) cm.getEntity();
                            if (mech.isAutoEject()) {
                                menuItem = new JMenuItem("Disable Autoeject");
                                menuItem.setActionCommand("DAE|" + row + "|" + col);
                            } else {
                                menuItem = new JMenuItem("Enable Autoeject");
                                menuItem.setActionCommand("EAE|" + row + "|" + col);
                            }
                            menuItem.addActionListener(this);
                            popup.add(menuItem);
                        }

                        popup.addSeparator();

                        if (!useAdvanceRepairs) {
                            if (cm.getStatus() == Unit.STATUS_UNMAINTAINED) {
                                menuItem = new JMenuItem("Maintain");
                                menuItem.setActionCommand("MM|" + cm.getId());
                            } else {
                                menuItem = new JMenuItem("Unmaintain");
                                menuItem.setActionCommand("UMM|" + cm.getId());
                            }
                            menuItem.addActionListener(this);
                            popup.add(menuItem);
                        }
                        if (cm.isOmni()) {
                            menuItem = new JMenuItem("Repod Unit");
                            menuItem.setActionCommand("RM|" + cm.getId());
                            menuItem.addActionListener(this);
                            popup.add(menuItem);
                        }

                        JMenu hm = new JMenu("Transactions");
                        int numItems = 0;
                        if(!cm.isChristmasUnit() || Boolean.parseBoolean(mwclient.getserverConfigs("Christmas_AllowDonate"))) {
                        	menuItem = new JMenuItem("Donate Unit");
                        	menuItem.setActionCommand("DO|" + cm.getId());
                        	menuItem.addActionListener(this);
                        	hm.add(menuItem);
                        	numItems++;
                        }
                        if(!cm.isChristmasUnit() || Boolean.parseBoolean(mwclient.getserverConfigs("Christmas_AllowScrap"))) {
                        	menuItem = new JMenuItem("Scrap Unit");
                        	menuItem.setActionCommand("S|" + cm.getId());
                        	menuItem.addActionListener(this);
                        	hm.add(menuItem);
                        	numItems++;
                        }
                        //@Salient for SOL free build
                        if(Player.getHouse().equalsIgnoreCase(mwclient.getserverConfigs("NewbieHouseName")) && Boolean.parseBoolean(mwclient.getserverConfigs("Sol_FreeBuild"))) {
                        	menuItem = new JMenuItem("Delete Unit");
                        	menuItem.setActionCommand("DL|" + cm.getId());
                        	menuItem.addActionListener(this);
                        	hm.add(menuItem);
                        	numItems++;
                        }
                        if (!cm.isChristmasUnit() || Boolean.parseBoolean(mwclient.getserverConfigs("Christmas_AllowTransfer"))) {
                        	menuItem = new JMenuItem("Transfer Unit");
                            menuItem.setActionCommand("TM|" + cm.getId());
                            menuItem.addActionListener(this);
                            hm.add(menuItem);
                            numItems++;
                        }
                        if(numItems > 0) {
                            popup.add(hm);
                        }
                        // Test unit for BM access
                        boolean canSellUnit = true;
                        if (cm.isChristmasUnit() && !Boolean.parseBoolean(mwclient.getserverConfigs("Christmas_AllowBM"))) {
                        	canSellUnit = false;
                        }
                        if (cm.getType() == Unit.MEK && !Boolean.parseBoolean(mwclient.getserverConfigs("MeksMayBeSoldOnBM"))) {
                            canSellUnit = false;
                        } else if (cm.getType() == Unit.VEHICLE && !Boolean.parseBoolean(mwclient.getserverConfigs("VehsMayBeSoldOnBM"))) {
                            canSellUnit = false;
                        } else if (cm.getType() == Unit.BATTLEARMOR && !Boolean.parseBoolean(mwclient.getserverConfigs("BAMayBeSoldOnBM"))) {
                            canSellUnit = false;
                        } else if (cm.getType() == Unit.AERO && !Boolean.parseBoolean(mwclient.getserverConfigs("AerosMayBeSoldOnBM"))) {
                            canSellUnit = false;
                        } else if (cm.getType() == Unit.PROTOMEK && !Boolean.parseBoolean(mwclient.getserverConfigs("ProtosMayBeSoldOnBM"))) {
                            canSellUnit = false;
                        } else if (cm.getType() == Unit.INFANTRY && !Boolean.parseBoolean(mwclient.getserverConfigs("InfantryMayBeSoldOnBM"))) {
                            canSellUnit = false;
                        } else if (Boolean.parseBoolean(mwclient.getserverConfigs("BMNoClan")) && cm.getEntity().isClan()) {
                            canSellUnit = false;
                        }

                        // Test for faction BM access
                        StringTokenizer blockedFactions = new StringTokenizer(mwclient.getserverConfigs("BMNoSell"), "$");
                        while (blockedFactions.hasMoreTokens()) {
                            if (Player.getMyHouse().getName().equals(blockedFactions.nextToken())) {
                                canSellUnit = false;
                            }
                        }

                        if (canSellUnit && cm.getStatus() != Unit.STATUS_FORSALE) {
                            menuItem = new JMenuItem("Sell on BM");
                            menuItem.setActionCommand("AB|" + cm.getId());
                            menuItem.addActionListener(this);
                            hm.add(menuItem);
                        }

                        if (cm.getStatus() == Unit.STATUS_FORSALE) {
                            menuItem = new JMenuItem("Recall from BM");
                            menuItem.setActionCommand("RFM|" + cm.getId());
                            menuItem.addActionListener(this);
                            hm.add(menuItem);
                        }

                        if (Boolean.parseBoolean(mwclient.getserverConfigs("UseDirectSell")) && cm.getStatus() != Unit.STATUS_FORSALE) {
                            menuItem = new JMenuItem("Direct Sell Unit");
                            menuItem.setActionCommand("DSU|" + cm.getId());
                            menuItem.addActionListener(this);
                            hm.add(menuItem);
                        }

                        JMenu pm = new JMenu("Pilot");
                        popup.add(pm);
                        // Cannot Retire or rename Vacant pilots.
                        if (!cm.hasVacantPilot()) {
                            menuItem = new JMenuItem("Retire");
                            menuItem.setActionCommand("RT|" + cm.getId());
                            menuItem.addActionListener(this);
                            pm.add(menuItem);
                            menuItem = new JMenuItem("Rename");
                            menuItem.setActionCommand("RP|" + cm.getId());
                            menuItem.addActionListener(this);
                            pm.add(menuItem);
                            if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
                                menuItem = new JMenuItem("Promote Pilot");
                                menuItem.setActionCommand("PP|" + cm.getId());
                                menuItem.addActionListener(this);
                                pm.add(menuItem);
                                if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanSellPilotUpgrades"))) {
                                    menuItem = new JMenuItem("Demote Pilot");
                                    menuItem.setActionCommand("DP|" + cm.getId());
                                    menuItem.addActionListener(this);
                                    pm.add(menuItem);
                                }
                            }
                        }

                        // Pilot Queues Block
                        boolean ppqsEnabled = Boolean.parseBoolean(mwclient.getserverConfigs("AllowPersonalPilotQueues"));
                        if (ppqsEnabled && cm.isSinglePilotUnit()) {

                            // load possible pilots
                            Object[] pilots = Player.getPersonalPilotQueue().getPilotQueue(cm.getType(), cm.getWeightclass()).toArray();
                            JMenu jm = new JMenu("Exchange");

                            // option to remove pilot, if that hasn't been done
                            // already
                            if (!cm.hasVacantPilot()) {
                                pm.addSeparator();
                                menuItem = new JMenuItem("Remove");
                                menuItem.setActionCommand("EXP|" + cm.getId() + "|-1");
                                menuItem.addActionListener(this);
                                pm.add(menuItem);
                            } else {
                                jm = new JMenu("Assign");
                            }

                            /*
                             * Set up the actual menu, *IF* the pilot queue has a non-zero size.
                             */
                            if (pilots.length == 0) {
                                jm.setEnabled(false);
                            } else {

                                /*
                                 * Contruction of EXCHANGE pilot. Derived from the other exchange options.
                                 */
                                pm.add(jm);

                                for (int i = 0; i < pilots.length; i++) {
                                    Pilot mm = (Pilot) pilots[i];
                                    String pilotString;
                                    String skills = mm.getSkillString(true);
                                    if (cm.getType() == Unit.MEK) {
                                        pilotString = mm.getName() + " (" + mm.getGunnery() + "/" + mm.getPiloting();
                                        if (skills.trim().equals("")) {
                                            pilotString += ")";
                                        } else {
                                            pilotString += ", " + skills + ")";
                                        }

                                        if (mm.getHits() > 0) {
                                            pilotString += " Hits: " + mm.getHits();
                                        }

                                    } else {
                                        pilotString = mm.getName() + " (" + mm.getGunnery();
                                        if (skills.trim().equals("")) {
                                            pilotString += ")";
                                        } else {
                                            pilotString += ", " + skills + ")";
                                        }
                                    }
                                    menuItem = new JMenuItem(pilotString);

                                    menuItem.setActionCommand("EXP|" + cm.getId() + "|" + i);
                                    menuItem.addActionListener(this);
                                    jm.add(menuItem);
                                }
                            }
                        }

                        popup.addSeparator();

                        menuItem = new JMenuItem("Show To Faction");
                        menuItem.setActionCommand("SUTH|" + cm.getId());
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        menuItem = new JMenuItem("Remove From All");
                        menuItem.setActionCommand("RFAA|" + cm.getId());
                        menuItem.addActionListener(this);
                        popup.add(menuItem);

                        /*
                         * Disable RFAA option if unit isnt actually IN any of the player's armies.
                         */
                        boolean isInArmy = false;
                        for (CArmy currA : mwclient.getPlayer().getArmies()) {
                            if (currA.getUnit(cm.getId()) != null) {
                                isInArmy = true;
                                break;
                            }
                        }

                        if (!isInArmy) {
                            menuItem.setEnabled(false);
                        }

                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }

                    else if (Player.getFreeBays() > 0) {
                        int hangernum = (row - MekTable.getRowsForArmies()) * (MekTable.getColumnCount() - 1) + col - 1;
                        if (hangernum == mwclient.getPlayer().getHangar().size()) {// only
                            // show in first free cell
                            if (useAdvanceRepairs) {
                                menuItem = new JMenuItem("Sell Excess Bays");
                                menuItem.setActionCommand("SEB");
                            } else {
                                menuItem = new JMenuItem("Fire Excess Techs");
                                menuItem.setActionCommand("FET");
                            }
                            menuItem.addActionListener(this);
                            popup.add(menuItem);
                            popup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                    // {
                    // JMenu buy = createBuySubMenu();
                    // popup.add(buy);
                    // }
                    // else if (Player.getFreeBays() > 0)
                    // {
                    // menuItem = new JMenuItem("Low Funds");
                    // popup.add(menuItem);
                    // }
                    // else
                    // {
                    // menuItem = new JMenuItem("No Room");
                    // popup.add(menuItem);
                    // }
                    // popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

        public void actionPerformed(ActionEvent actionEvent) {

            String s = actionEvent.getActionCommand();
            StringTokenizer st = new StringTokenizer(s, "|");
            String command = st.nextToken();

            // exchange mek
            if (command.equalsIgnoreCase("EXM")) {
                int lid = Integer.parseInt(st.nextToken());
                int mid = Integer.parseInt(st.nextToken());
                int hid = Integer.parseInt(st.nextToken());
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c EXM#" + lid + "," + mid + "#" + hid);
                // move to hanger
            } else if (command.equalsIgnoreCase("MH")) {
                int lid = Integer.parseInt(st.nextToken());
                int mid = Integer.parseInt(st.nextToken());
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c EXM#" + lid + "," + mid);
                // add lance
            } else if (command.equalsIgnoreCase("AA")) {
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c cra#" + mwclient.getConfigParam("DEFAULTARMYNAME"));
                // set lance active
            } else if (command.equalsIgnoreCase("SA")) {
                // int lid = Integer.parseInt(st.nextToken());
                // MekTable.getLanceAt(lid).setReady(true);
                // set lance inactive
            } else if (command.equalsIgnoreCase("SI")) {
                // int lid = Integer.parseInt(st.nextToken());
                // MekTable.getLanceAt(lid).setReady(false);
                // check attack options
            } else if (command.equalsIgnoreCase("AO")) {
                int lid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderCheckAttack_actionPerformed(lid);
                // check access
            } else if (command.equalsIgnoreCase("CAA")) {
                int armyID = Integer.parseInt(st.nextToken());
                JComboBox attackCombo = new JComboBox(mwclient.getAllOps().keySet().toArray()); //Barukkhazad! 20151108 removed castings
                attackCombo.setEditable(false);

                attackCombo.grabFocus();
                attackCombo.getEditor().selectAll();

                JOptionPane jop = new JOptionPane(attackCombo, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
                JDialog dlg = jop.createDialog(mwclient.getMainFrame(), "Select Operation.");
                attackCombo.grabFocus();
                attackCombo.getEditor().selectAll();
                dlg.setVisible(true);

                if ((Integer) jop.getValue() == JOptionPane.CANCEL_OPTION) {
                    return;
                }

                String attackName = (String) attackCombo.getSelectedItem();
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c checkarmyeligibility#" + armyID + "#" + attackName);
                // Remove Army
            } else if (command.equalsIgnoreCase("RA")) {
                int lid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderRemoveLance_actionPerformed(lid);
                // rename army
            } else if (command.equalsIgnoreCase("LA")) {
                int lid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderPlayerLockArmy_actionPerformed(lid);
                // lock army
            } else if (command.equalsIgnoreCase("ULA")) {
                int lid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderPlayerUnlockArmy_actionPerformed(lid);
                // unlock army
            } else if (command.equalsIgnoreCase("DAA")) {
                int lid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderDisableArmy_actionPerformed(lid);
            } else if (command.equalsIgnoreCase("NA")) {
                int mid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderNameArmy_actionPerformed(mid);
                // set Lower Unit Limit
            } else if (command.equalsIgnoreCase("SLUL")) {
                int lid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderSetLowerUnitLimit_actionPerformed(lid);
                // set upper Unit Limit
            } else if (command.equalsIgnoreCase("SUUL")) {
                int lid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderSetUpperUnitLimit_actionPerformed(lid);
                // Set Force Size you plan on facing
            } else if (command.equalsIgnoreCase("SFS")) {
                int aid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderSetForceSizeToFace_actionPerformed(aid);
                // showtofaction - army
            } else if (command.equalsIgnoreCase("SATH")) {
                int lid = Integer.parseInt(st.nextToken());
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c sth#a#" + lid);
                // make public challenge
            } else if (command.equalsIgnoreCase("MPC")) {

                int mode = Integer.parseInt(st.nextToken());
                int lid = Integer.parseInt(st.nextToken());
                boolean useForceSize = Boolean.parseBoolean(mwclient.getserverConfigs("UseOperationsRule"));
                float opForceSize = Army.NO_LIMIT;
                double forceSizeMod = 1;

                String operation = st.nextToken();

                for (CArmy currArmy : mwclient.getPlayer().getArmies()) {

                    if (lid != -1 && currArmy.getID() != lid) {
                        continue;
                    }

                    String challengeString = mwclient.getConfigParam("CHALLENGESTRING");
                    StringBuilder toSend = new StringBuilder(challengeString != null ? challengeString : "");

                    if (useForceSize) {
                        opForceSize = currArmy.getOpForceSize();
                        if (opForceSize > 0) {
                            forceSizeMod = currArmy.forceSizeModifier(opForceSize);
                        }
                    }
                    // load the default if a non-entry is set.
                    if (toSend.toString().trim().equals("")) {
                        toSend = new StringBuilder("Looking for a game at");// matches default
                        // config
                    }

                    // BV only
                    if (mode == 1) {
                        toSend.append(" ").append(Math.round(currArmy.getBV() * forceSizeMod)).append(" BV");
                        if (forceSizeMod > 1) {
                            toSend.append(" vs ").append(opForceSize).append(" units");
                        }
                        toSend.append(".");
                    }
                    // BV and Count
                    else if (mode == 2) {
                        int armySize = currArmy.getUnits().size();
                        toSend.append(" ").append(Math.round(currArmy.getBV() * forceSizeMod)).append(" BV");
                        if (forceSizeMod > 1) {
                            toSend.append(" vs ").append(opForceSize).append(" units");
                        }
                        toSend.append(", with  ").append(armySize).append(" unit");
                        if (armySize > 1) {
                            toSend.append("s.");
                        } else {
                            toSend.append(".");
                        }
                    }

                    // BV and class info
                    else if (mode == 3) {

                        int assaultM = 0;
                        int heavyM = 0;
                        int mediumM = 0;
                        int lightM = 0;
                        int protoM = 0;
                        int ba = 0;
                        int vehs = 0;
                        int aero = 0;
                        int assaultV = 0;
                        int heavyV = 0;
                        int mediumV = 0;
                        int lightV = 0;
                        int inf = 0;

                        boolean showVeeWeights = Boolean.parseBoolean(mwclient.getserverConfigs("ShowVehWeightclassInChallenges"));

                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        // boolean firstUnit = true;
                        while (e.hasMoreElements()) {

                            CUnit currUnit = (CUnit) e.nextElement();

                            // mechs
                            if (currUnit.getType() == Unit.MEK || currUnit.getType() == Unit.QUAD) {
                                if (currUnit.getWeightclass() == Unit.ASSAULT) {
                                    assaultM++;
                                } else if (currUnit.getWeightclass() == Unit.HEAVY) {
                                    heavyM++;
                                } else if (currUnit.getWeightclass() == Unit.MEDIUM) {
                                    mediumM++;
                                } else {
                                    lightM++;
                                }
                            }

                            // protos
                            else if (currUnit.getType() == Unit.PROTOMEK) {
                                protoM++;
                            } else if (currUnit.getType() == Unit.VEHICLE) {
                                vehs++;
                                if (showVeeWeights) {
                                    if (currUnit.getWeightclass() == Unit.ASSAULT) {
                                        assaultV++;
                                    } else if (currUnit.getWeightclass() == Unit.HEAVY) {
                                        heavyV++;
                                    } else if (currUnit.getWeightclass() == Unit.MEDIUM) {
                                        mediumV++;
                                    } else {
                                        lightV++;
                                    }
                                }
                            }
                            // Ba's
                            else if (currUnit.getType() == Unit.BATTLEARMOR) {
                                ba++;
                            } else if (currUnit.getType() == Unit.AERO) {
                                aero++;
                            } else {
                                // assume infantry
                                inf++;
                            }
                        }

                        // assemble the string
                        toSend.append(" ").append(Math.round(currArmy.getBV() * forceSizeMod)).append(" BV");
                        if (forceSizeMod > 1) {
                            toSend.append(" vs ").append(opForceSize).append(" units");
                        }
                        toSend.append(".");
                        if (assaultM > 0) {
                            toSend.append(" ").append(assaultM).append("A,");
                        }
                        if (heavyM > 0) {
                            toSend.append(" ").append(heavyM).append("H,");
                        }
                        if (mediumM > 0) {
                            toSend.append(" ").append(mediumM).append("M,");
                        }
                        if (lightM > 0) {
                            toSend.append(" ").append(lightM).append("L,");
                        }
                        if (protoM > 0) {
                            toSend.append(" ").append(protoM).append(" Protos,");
                        }
                        if (ba > 0) {
                            toSend.append(" ").append(ba).append(" BAs,");
                        }
                        if (ba > 0) {
                            toSend.append(" ").append(aero).append(" Aeros,");
                        }
                        if (vehs > 0) {
                            if (showVeeWeights) {
                                if (assaultV > 0) {
                                    toSend.append(" ").append(assaultV).append("A Vehs,");
                                }
                                if (heavyV > 0) {
                                    toSend.append(" ").append(heavyV).append("H Vehs,");
                                }
                                if (mediumV > 0) {
                                    toSend.append(" ").append(mediumV).append("M Vehs,");
                                }
                                if (lightV > 0) {
                                    toSend.append(" ").append(lightV).append("L Vehs,");
                                }
                            } else {
                                toSend.append(" ").append(vehs).append(" Vehs,");
                            }
                        }

                        else if (inf > 0) {
                            toSend.append(" ").append(inf).append(" Inf,");
                        }

                        // replace final comma with a period.
                        int sendLength = toSend.lastIndexOf(",");
                        toSend = new StringBuilder(toSend.substring(0, sendLength) + ".");
                    }

                    else if (mode == 4) {
                        int Tonnage = 0;
                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        while (e.hasMoreElements()) {
                            CUnit unit = (CUnit) e.nextElement();
                            Tonnage += (int) unit.getEntity().getWeight();
                        }
                        toSend.append(" ").append(Tonnage).append(" tons.");

                    }

                    else if (mode == 5) {
                        int Tonnage = 0;
                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        while (e.hasMoreElements()) {
                            CUnit unit = (CUnit) e.nextElement();
                            Tonnage += (int) unit.getEntity().getWeight();
                        }
                        toSend.append(" ").append(Math.round(currArmy.getBV() * forceSizeMod)).append(" BV");
                        if (forceSizeMod > 1) {
                            toSend.append(" vs ").append(opForceSize).append(" units");
                        }
                        toSend.append(", at ").append(Tonnage).append(" tons");
                    }

                    else if (mode == 6) {
                        int Tonnage = 0;
                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        while (e.hasMoreElements()) {
                            CUnit unit = (CUnit) e.nextElement();
                            Tonnage += (int) unit.getEntity().getWeight();
                        }
                        toSend.append(" ").append(Tonnage).append(" tons, with ").append(currArmy.getUnits().size());
                        if (currArmy.getUnits().size() == 1) {
                            toSend.append(" unit.");
                        } else {
                            toSend.append(" units.");
                        }
                    }

                    else if (mode == 7) {
                        int Tonnage = 0;
                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        while (e.hasMoreElements()) {
                            CUnit unit = (CUnit) e.nextElement();
                            Tonnage += (int) unit.getEntity().getWeight();
                        }
                        toSend.append(" ").append(Math.round(currArmy.getBV() * forceSizeMod)).append(" BV");
                        if (forceSizeMod > 1) {
                            toSend.append(" vs ").append(opForceSize).append(" units");
                        }
                        toSend.append(", at ").append(Tonnage).append(" tons, with ").append(currArmy.getUnits().size());
                        if (currArmy.getUnits().size() == 1) {
                            toSend.append(" unit.");
                        } else {
                            toSend.append(" units.");
                        }
                    }

                    else if (mode == 8) {
                        int assault = 0;
                        int heavy = 0;
                        int medium = 0;
                        int light = 0;

                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        while (e.hasMoreElements()) {
                            CUnit unit = (CUnit) e.nextElement();
                            switch (unit.getWeightclass()) {
                            case Unit.ASSAULT:
                                assault++;
                                break;
                            case Unit.LIGHT:
                                light++;
                                break;
                            case Unit.MEDIUM:
                                medium++;
                                break;
                            case Unit.HEAVY:
                                heavy++;
                                break;
                            }
                        }
                        if (assault > 0) {
                            toSend.append(" ").append(assault).append("A,");
                        }
                        if (heavy > 0) {
                            toSend.append(" ").append(heavy).append("H,");
                        }
                        if (medium > 0) {
                            toSend.append(" ").append(medium).append("M,");
                        }
                        if (light > 0) {
                            toSend.append(" ").append(light).append("L,");
                        }
                        // replace final comma with a period.
                        int sendLength = toSend.lastIndexOf(",");
                        toSend = new StringBuilder(toSend.substring(0, sendLength) + ".");

                    }

                    else if (mode == 9) {
                        int assault = 0;
                        int heavy = 0;
                        int medium = 0;
                        int light = 0;

                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        while (e.hasMoreElements()) {
                            CUnit unit = (CUnit) e.nextElement();
                            switch (unit.getWeightclass()) {
                            case Unit.ASSAULT:
                                assault++;
                                break;
                            case Unit.LIGHT:
                                light++;
                                break;
                            case Unit.MEDIUM:
                                medium++;
                                break;
                            case Unit.HEAVY:
                                heavy++;
                                break;
                            }
                        }
                        toSend.append(" ").append(Math.round(currArmy.getBV() * forceSizeMod)).append(" BV");
                        if (forceSizeMod > 1) {
                            toSend.append(" vs ").append(opForceSize).append(" units");
                        }
                        toSend.append(", with");
                        if (assault > 0) {
                            toSend.append(" ").append(assault).append("A,");
                        }
                        if (heavy > 0) {
                            toSend.append(" ").append(heavy).append("H,");
                        }
                        if (medium > 0) {
                            toSend.append(" ").append(medium).append("M,");
                        }
                        if (light > 0) {
                            toSend.append(" ").append(light).append("L,");
                        }
                        // replace final comma with a period.
                        int sendLength = toSend.lastIndexOf(",");
                        toSend = new StringBuilder(toSend.substring(0, sendLength) + ".");

                    }

                    else if (mode == 10) {
                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        while (e.hasMoreElements()) {
                            CUnit unit = (CUnit) e.nextElement();
                            toSend.append(" <a href=\"MEKINFO").append(unit.getUnitFilename())
                                    .append("#").append(unit.getBVForMatch())
                                    .append("#").append(unit.getPilot().getGunnery())
                                    .append("#").append(unit.getPilot().getPiloting()).append("\">")
                                    .append(unit.getModelName()).append("</a>,");
                        }
                        // replace final comma with a period.
                        int sendLength = toSend.lastIndexOf(",");
                        toSend = new StringBuilder(toSend.substring(0, sendLength) + ".");

                    }

                    else if (mode == 11) {

                        toSend.append(" ").append(Math.round(currArmy.getBV() * forceSizeMod)).append(" BV");
                        if (forceSizeMod > 1) {
                            toSend.append(" vs ").append(opForceSize).append(" units");
                        }
                        toSend.append(",");
                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        while (e.hasMoreElements()) {
                            CUnit unit = (CUnit) e.nextElement();
                            toSend.append(" <a href=\"MEKINFO").append(unit.getUnitFilename())
                                    .append("#").append(unit.getBVForMatch())
                                    .append("#").append(unit.getPilot().getGunnery())
                                    .append("#").append(unit.getPilot().getPiloting()).append("\">")
                                    .append(unit.getModelName()).append("</a>,");
                        }
                        // replace final comma with a period.
                        int sendLength = toSend.lastIndexOf(",");
                        toSend = new StringBuilder(toSend.substring(0, sendLength) + ".");

                    }

                    else if (mode == 12) {

                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        TreeMap<Double, Integer> unitWeights = new TreeMap<>();
                        while (e.hasMoreElements()) {
                            CUnit unit = (CUnit) e.nextElement();
                            if (!unitWeights.containsKey(unit.getEntity().getWeight())) {
                                unitWeights.put(unit.getEntity().getWeight(), 1);
                            } else {
                                unitWeights.put(unit.getEntity().getWeight(), unitWeights.get(unit.getEntity().getWeight()) + 1);
                            }
                        }

                        for (Double weight : unitWeights.keySet()) {
                            toSend.append(" ").append(unitWeights.get(weight)).append("x ")
                                    .append(weight.intValue()).append(" tons,");
                        }
                        // replace final comma with a period.
                        int sendLength = toSend.lastIndexOf(",");
                        toSend = new StringBuilder(toSend.substring(0, sendLength) + ".");

                    }

                    else if (mode == 13) {

                        toSend.append(" ").append(currArmy.getBV()).append(" BV,");
                        Enumeration<Unit> e = currArmy.getUnits().elements();
                        TreeMap<Double, Integer> unitWeights = new TreeMap<>();
                        while (e.hasMoreElements()) {
                            CUnit unit = (CUnit) e.nextElement();
                            if (!unitWeights.containsKey(unit.getEntity().getWeight())) {
                                unitWeights.put(unit.getEntity().getWeight(), 1);
                            } else {
                                unitWeights.put(unit.getEntity().getWeight(), unitWeights.get(unit.getEntity().getWeight()) + 1);
                            }
                        }

                        for (Double weight : unitWeights.keySet()) {
                            toSend.append(" ").append(unitWeights.get(weight)).append("x ").append(weight.intValue()).append(" tons,");
                        }
                        // replace final comma with a period.
                        int sendLength = toSend.lastIndexOf(",");
                        toSend = new StringBuilder(toSend.substring(0, sendLength) + ".");

                    }

                    if (currArmy.getName().trim().length() > 0) {
                        toSend.append(" \"").append(currArmy.getName()).append("\"");
                    }

                    if (operation.length() > 1 && !operation.equalsIgnoreCase("none")) {
                        toSend.append(" (").append(operation).append(")");
                    }

                    mwclient.sendChat(toSend.toString());
                    // if lid != -1 it means only send one army and if we've
                    // gotten this far that means we've matched
                    // the army with the correct ID.
                    if (lid != -1) {
                        break;
                    }
                }
                // showtofaction - unit
            } else if (command.equalsIgnoreCase("SUTH")) {
                int mid = Integer.parseInt(st.nextToken());
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c sth#u#" + mid);
                // rename pilot
            } else if (command.equalsIgnoreCase("RP")) {
                int mid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderNamePilot_actionPerformed(mid);
                // Promote Pilot
            } else if (command.equalsIgnoreCase("PP")) {
                int mid = Integer.parseInt(st.nextToken());
                new PromotePilotDialog(mwclient, mid, false);
                // Demote pilot
            } else if (command.equalsIgnoreCase("DP")) {
                int mid = Integer.parseInt(st.nextToken());
                new PromotePilotDialog(mwclient, mid, true);
                // retire pilot
            } else if (command.equalsIgnoreCase("RT")) {
                int mid = Integer.parseInt(st.nextToken());
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c retirepilot#" + mid);// send
                // directly
                // show mek
            } else if (command.equalsIgnoreCase("SM")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                Entity theEntity = mek.getEntity();
                JFrame infoWindow = new JFrame();
                UnitDisplay unitDisplay = new UnitDisplay(null);
                theEntity.loadAllWeapons();
                infoWindow.getContentPane().add(unitDisplay);
                infoWindow.setSize(300, 400);
                infoWindow.setResizable(false);
                infoWindow.setTitle(mek.getModelName());
                infoWindow.setLocationRelativeTo(mwclient.getMainFrame());
                infoWindow.setVisible(true);
                unitDisplay.displayEntity(theEntity);
            } else if (command.equalsIgnoreCase("CMU")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                Entity theEntity = mek.getEntity();
                // JFrame InfoWindow = new JFrame();
                theEntity.loadAllWeapons();
                CustomUnitDialog customizeUnit = new CustomUnitDialog(mwclient, theEntity, mek.getPilot(), mek);
                customizeUnit.setVisible(true);

            }// Repair a unit
            else if (command.equalsIgnoreCase("ARU")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                new AdvancedRepairDialog(mwclient, mek.getId(), false);
            }// Repair a unit
            else if (command.equalsIgnoreCase("BUR")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                new BulkRepairDialog(mwclient, mek.getId(), BulkRepairDialog.TYPE_BULK, BulkRepairDialog.UNIT_TYPE_SINGLE);
            }// Repair a unit
            else if (command.equalsIgnoreCase("SUR")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                new BulkRepairDialog(mwclient, mek.getId(), BulkRepairDialog.TYPE_SIMPLE, BulkRepairDialog.UNIT_TYPE_SINGLE);
            } // Salvage a unit
            else if (command.equalsIgnoreCase("BSU")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                new BulkRepairDialog(mwclient, mek.getId(), BulkRepairDialog.TYPE_SALVAGE, BulkRepairDialog.UNIT_TYPE_SINGLE);
            } else if (command.equalsIgnoreCase("SUC")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                new AdvancedRepairDialog(mwclient, mek.getId(), true);
            }// Display Unit Repair Jobs
            else if (command.equalsIgnoreCase("DRJ")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);

                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c DisplayUnitRepairJobs#" + mek.getId());
            }// Display Pending Work Orders
            else if (command.equalsIgnoreCase("DPWO")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                if (mwclient.getRMT() != null) {
                    mwclient.systemMessage(mwclient.getRMT().getRepairQueue(mek.getId()));
                }
                if (mwclient.getSMT() != null) {
                    mwclient.systemMessage(mwclient.getSMT().getSalvageQueue(mek.getId()));
                }
            }// Stop all pending work orders
            else if (command.equalsIgnoreCase("SAPWO")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                if (mwclient.getRMT() != null) {
                    mwclient.getRMT().removeAllWorkOrders(mek.getId());
                }
                if (mwclient.getSMT() != null) {
                    mwclient.getSMT().removeAllWorkOrders(mek.getId());
                }
                mwclient.systemMessage("Cancelled all pending work orders.");
            }// Reload all ammo
            else if (command.equalsIgnoreCase("RAA")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                int result = JOptionPane.showConfirmDialog(mwclient.getMainFrame(), "Are you sure you want to reload all the ammo on this unit " + mwclient.getPlayer().getName() + "?", "Reload it?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c RELOADALLAMMO#" + mek.getId());
                }
            }
            // Estimate Unit Repairs
            else if (command.equalsIgnoreCase("EUR")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                int year = Integer.parseInt(mwclient.getserverConfigs("CampaignYear"));
                CUnit mek = MekTable.getMekAt(row, col);
                int greenTechCost;
                int regTechCost;
                int vetTechCost;
                int eliteTechCost;

                double repairCost;
                if (Boolean.parseBoolean(mwclient.getserverConfigs("UseRealRepairCosts"))) {
                    repairCost = UnitUtils.getTotalDamagedPartCost(mek.getEntity(), year);
                    repairCost *= Double.parseDouble(mwclient.getserverConfigs("RealRepairCostMod"));

                } else {
                    repairCost = mwclient.getTotalRepairCosts(mek.getEntity());

                }
                greenTechCost = mwclient.getTechLaborCosts(mek.getEntity(), UnitUtils.TECH_GREEN);
                regTechCost = mwclient.getTechLaborCosts(mek.getEntity(), UnitUtils.TECH_REG);
                vetTechCost = mwclient.getTechLaborCosts(mek.getEntity(), UnitUtils.TECH_VET);
                eliteTechCost = mwclient.getTechLaborCosts(mek.getEntity(), UnitUtils.TECH_ELITE);
                mwclient.systemMessage("It'll cost you at least the following to repair your " + mek.getModelName() + ".<br><table>" + "<tr><th>Green Tech:</th><th>" + mwclient.moneyOrFluMessage(true, true, (int) repairCost, false) + " in parts and " + mwclient.moneyOrFluMessage(true, true, greenTechCost, false) + " in labor for a total of " + mwclient.moneyOrFluMessage(true, true, (int) repairCost + greenTechCost, false) + ".</th></tr>" + "<tr><th>Reg Tech:</th><th>" + mwclient.moneyOrFluMessage(true, true, (int) repairCost, false) + " in parts and " + mwclient.moneyOrFluMessage(true, true, regTechCost, false) + " in labor for a total of " + mwclient.moneyOrFluMessage(true, true, (int) repairCost + regTechCost, false) + ".</th></tr>" + "<tr><th>Vet Tech:</th><th>" + mwclient.moneyOrFluMessage(true, true, (int) repairCost, false) + " in parts and "
                        + mwclient.moneyOrFluMessage(true, true, vetTechCost, false) + " in labor for a total of " + mwclient.moneyOrFluMessage(true, true, (int) repairCost + vetTechCost, false) + ".</th></tr>" + "<tr><th>Elite Tech:</th><th>" + mwclient.moneyOrFluMessage(true, true, (int) repairCost, false) + " in parts and " + mwclient.moneyOrFluMessage(true, true, eliteTechCost, false) + " in labor for a total of " + mwclient.moneyOrFluMessage(true, true, (int) repairCost + eliteTechCost, false) + ".</th></tr></table>");

            }// remove from all armies
            else if (command.equalsIgnoreCase("RFAA")) {

                // id of selected unit
                int mid = Integer.parseInt(st.nextToken());

                // check all armies for the selected unit
                for (CArmy currA : mwclient.getPlayer().getArmies()) {
                    if (currA.getUnit(mid) != null) {
                        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c EXM#" + currA.getID() + "," + mid);
                    }
                }

                // transfer mek
            } else if (command.equalsIgnoreCase("TM")) {
                int mid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderTransferUnit_actionPerformed(null, mid);
                // repod mek
            } else if (command.equalsIgnoreCase("RM")) {
                int mid = Integer.parseInt(st.nextToken());
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c repod#" + mid);
                // add to bm
            } else if (command.equalsIgnoreCase("AB")) {
                int mid = Integer.parseInt(st.nextToken());
                mwclient.getMainFrame().jMenuCommanderAddToBM_actionPerformed(mid);
                // remove from market
            } else if (command.equalsIgnoreCase("RFM")) {
                int mid = Integer.parseInt(st.nextToken());
                TreeMap<Integer, CBMUnit> marketUnits = mwclient.getCampaign().getBlackMarket();
                for (CBMUnit currU : marketUnits.values()) {
                    if (currU.getUnitID() == mid) {
                        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c recall#" + currU.getAuctionID());
                        break;
                    }
                }
                // direct sell unit
            } else if (command.equalsIgnoreCase("DSU")) {
                String mid = st.nextToken();
                mwclient.getMainFrame().jMenuCommanderDirectSell_actionPerformed(null, mid);
                // scrap mek
            } else if (command.equalsIgnoreCase("S")) {
                int num = Integer.parseInt(st.nextToken());
                int result = JOptionPane.showConfirmDialog(mwclient.getMainFrame(), "Are you sure you want to scrap this unit?", "Scrap it?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c scrap#" + num);
                    // Maintain Mek
                }
            //@Salient for SOL freebuild option
            } else if (command.equalsIgnoreCase("DL")) {
                int num = Integer.parseInt(st.nextToken());
                //int result = JOptionPane.showConfirmDialog(mwclient.getMainFrame(), "Are you sure you want to Remove this unit?", "Delete it?", JOptionPane.YES_NO_OPTION);
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "SOLDELETEUNIT " + num);
            } else if (command.equalsIgnoreCase("MM")) {
                int num = Integer.parseInt(st.nextToken());
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setmaintained#" + num);
                mwclient.refreshGUI(MWClient.REFRESH_HQPANEL);
                // unmaintain mek
            } else if (command.equalsIgnoreCase("UMM")) {
                int num = Integer.parseInt(st.nextToken());
                int result = JOptionPane.showConfirmDialog(mwclient.getMainFrame(), "Are you sure you want to stop maintaining this unit?", "Unmaintain?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setunmaintained#" + num);
                }
                mwclient.refreshGUI(MWClient.REFRESH_HQPANEL);
                // donate mek
            } else if (command.equalsIgnoreCase("DO")) {
                int mid = Integer.parseInt(st.nextToken());
                int result = JOptionPane.showConfirmDialog(mwclient.getMainFrame(), "Are you sure you want to donate this unit?", "Donate?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c donate#" + mid);
                    // buy mek
                }
            } else if (command.equalsIgnoreCase("LCN")) {
                int lid = Integer.parseInt(st.nextToken());
                int mid = Integer.parseInt(st.nextToken());
                int hid = Integer.parseInt(st.nextToken());
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c linkunit#" + lid + "#" + mid + "#" + hid);
            } else if (command.equalsIgnoreCase("EAE")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                Mech mech = (Mech) mek.getEntity();
                mech.setAutoEject(true);
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setautoeject#" + mech.getExternalId() + "#" + true);
            } else if (command.equalsIgnoreCase("DAE")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                CUnit mek = MekTable.getMekAt(row, col);
                Mech mech = (Mech) mek.getEntity();
                mech.setAutoEject(false);
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setautoeject#" + mech.getExternalId() + "#" + false);
                // exchange pilot
            } else if (command.equalsIgnoreCase("EXP")) {
                int uid = Integer.parseInt(st.nextToken());
                int pid = Integer.parseInt(st.nextToken());
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c EXP#" + uid + "#" + pid);
            } else if (command.equalsIgnoreCase("FET")) {// fire excess techs
                mwclient.getMainFrame().jMenuCommanderFireTechs_actionPerformed();
            } else if (command.equalsIgnoreCase("SEB")) {// sell excess bays
                mwclient.getMainFrame().jMenuCommanderSellBays_actionPerformed();
            } else if (command.equals("RPU")) {// reposition unit
                int armyid = Integer.parseInt(st.nextToken());
                int unitid = Integer.parseInt(st.nextToken());
                int newpos = Integer.parseInt(st.nextToken());
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c unitposition#" + armyid + "#" + unitid + "#" + newpos);
            } else if (command.equals("PHQS")) {// primary HQ sort
                mwclient.getConfig().setParam("PRIMARYHQSORTORDER", st.nextToken());
                mwclient.getConfig().saveConfig();
                mwclient.getPlayer().sortHangar();
            } else if (command.equals("SHQS")) {
                mwclient.getConfig().setParam("SECONDARYHQSORTORDER", st.nextToken());
                mwclient.getConfig().saveConfig();
                mwclient.getPlayer().sortHangar();
            } else if (command.equals("THQS")) {
                mwclient.getConfig().setParam("TERTIARYHQSORTORDER", st.nextToken());
                mwclient.getConfig().saveConfig();
                mwclient.getPlayer().sortHangar();
            } else if (command.equals("PAS")) {// primary HQ sort
                mwclient.getConfig().setParam("PRIMARYARMYSORTORDER", st.nextToken());
                mwclient.getConfig().saveConfig();
                mwclient.getPlayer().sortArmies();
            } else if (command.equals("SAS")) {
                mwclient.getConfig().setParam("SECONDARYARMYSORTORDER", st.nextToken());
                mwclient.getConfig().saveConfig();
                mwclient.getPlayer().sortArmies();
            } else if (command.equals("TAS")) {
                mwclient.getConfig().setParam("TERTIARYARMYSORTORDER", st.nextToken());
                mwclient.getConfig().saveConfig();
                mwclient.getPlayer().sortArmies();
            } else if (command.equalsIgnoreCase("REMOVEUNITCOMMANDER")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                String armyId = st.nextToken();
                CUnit mek = MekTable.getMekAt(row, col);
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setunitcommander#" + mek.getId() + "#" + armyId + "#false");
                // exchange pilot
            } else if (command.equalsIgnoreCase("SETUNITCOMMANDER")) {
                int row = Integer.parseInt(st.nextToken());
                int col = Integer.parseInt(st.nextToken());
                String armyId = st.nextToken();
                CUnit mek = MekTable.getMekAt(row, col);
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setunitcommander#" + mek.getId() + "#" + armyId + "#true");
                // exchange pilot
            }

            tblMeks.repaint();
        }
    }

    /*
     * Original source: http://docs.rinet.ru/J21/ch25.htm#AnAlphaImageFilter Original author: Michael Morrison
     */
    private static class AlphaFilter extends RGBImageFilter {
        int alphaLevel;

        public AlphaFilter(int alpha) {
            alphaLevel = alpha;
            canFilterIndexColorModel = true;
        }

        @Override
        public int filterRGB(int x, int y, int rgb) {
            // Adjust the alpha value
            int alpha = rgb >> 24 & 0xff;
            alpha = alpha * alphaLevel / 255;

            // Return the result
            return rgb & 0x00ffffff | alpha << 24;
        }
    }

    public class MekTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = -7918520064078379615L;

        public int getColumnCount() {
            return Integer.parseInt(mwclient.getConfigParam("UNITAMOUNT")) + 1;
            // return this.columnNames.length;
        }

        // should be based on the number of mechs you can own
        public int getRowCount() {
            int hangarRows = getRowsForHangar();
            int armyRows = getRowsForArmies();
            return hangarRows + armyRows;
        }

        // number of rows consumed by given army
        public int getRowsForArmy(CArmy army) {
            int toReturn = (int) Math.ceil((double) army.getAmountOfUnits() / (double) (getColumnCount() - 1));
            return Math.max(toReturn, 1);
        }

        // number of rows consumed by hangar
        public int getRowsForHangar() {

            /*
             * no matter how many free bays a person has, return only one. this this solitary space shows players' remaining technicians. also - do not allow any adjustment in HQ display for negative bays.
             */
            int freebays = Player.getFreeBays();
            if (freebays > 1) {
                freebays = 1;
            }
            if (freebays < 0) {
                freebays = 0;
            }

            return (int) Math.ceil((double) (freebays + Player.getHangar().size()) / (getColumnCount() - 1));
        }

        public int getRowsForArmies() {

            int total = 0;
            for (CArmy currA : Player.getArmies()) {
                total += getRowsForArmy(currA);
            }

            return total;
        }

        @Override
        public String getColumnName(int col) {
            if (col == 0) {
                return "Army";
            }
            return "Unit " + col;
        }

        public CArmy getArmyAt(int row) {

            for (CArmy currA : Player.getArmies()) {
                int uses = getRowsForArmy(currA);
                if (uses > row) {
                    return currA;
                }
                row -= uses;
            }

            return null;
        }

        public int getOffset(int row) {

            for (CArmy currA : Player.getArmies()) {

                int uses = getRowsForArmy(currA);
                if (uses > row) {
                    return row * (getColumnCount() - 1);
                }

                row -= uses;
            }

            return 0;
        }

        public CUnit getMekAt(int row, int col) {
            if (row < 0) {
                return null;
            }
            if (col != 0) {
                if (row < getRowsForArmies()) {
                    CArmy army = getArmyAt(row);
                    Vector<Unit> mechs = new Vector<>(army.getUnits());
                    int offset = getOffset(row) + col - 1;
                    if (offset < mechs.size()) {
                        return (CUnit) mechs.elementAt(offset);
                    }
                    return null;
                }
                int hangernum = (row - getRowsForArmies()) * (getColumnCount() - 1) + col - 1;
                if (hangernum >= 0 && hangernum < Player.getHangar().size()) {
                    return Player.getHangar().get(hangernum);
                }
            }
            return null;
        }

        public Object getValueAt(int row, int col) {

            if (row < 0) {
                return "";
            }

            if (col == 0) {
                // System.err.println("Rows of armies: "+getRowsForArmies());
                if (row < getRowsForArmies()) {
                    return getArmyDescription(row);
                }
                // else
                return "Hangar";
            }

            CUnit unit = getMekAt(row, col);
            if (unit == null && row < getRowsForArmies()) {
                return " - ";
            } else if (unit == null) {// and in hangar row
                int hangarNum = (row - getRowsForArmies()) * (getColumnCount() - 1) + col - 1;
                if (hangarNum == Player.getHangar().size()) { // only show in first free cell
                    if (useAdvanceRepairs) {
                        return "Free Bays: " + mwclient.getPlayer().getFreeBays();
                    }
                    // else
                    return "Idle Techs: " + mwclient.getPlayer().getFreeBays();
                }
                // else
                return "";
            }

            // else
            CArmy army = getArmyAt(row);
            StringBuilder result = new StringBuilder(unit.getModelName());
            String basePilotSkill = mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse())
                    .getBasePilotSkill(unit.getType());
            String skillSet = unit.getPilot().getSkillString(false, basePilotSkill);
            StringTokenizer skills = new StringTokenizer(skillSet, ",");
            while (skills.hasMoreElements()) {
                skills.nextElement();
                result.append("*");
            }
            if (army != null) {
                if (unit.hasBeenC3LinkedTo(army)) {
                    result.append(" |M|");
                } else if (army.getC3Network().get(unit.getId()) != null) {
                    result.append(" |L|");
                }
                if (!Boolean.parseBoolean(mwclient.getConfig().getParam("RIGHTCOMMANDER")) &&
                        !Boolean.parseBoolean(mwclient.getConfig().getParam("LEFTCOMMANDER")) &&
                        army.isCommander(unit.getId())
                ) {
                    result.append(" Cmdr");
                }
            }

            return result.toString();
        }

        private Object getArmyDescription(int row) {
            CArmy army = getArmyAt(row);

            // only return the army description on the 1st row
            // ie - return w/ no content on 2nd/3rd/etc. row
            if (getRowsForArmy(army) > 1) {

                // yes, i know this code blows. sod off.
                int rowsUsed = 0;
                for (CArmy currArmy : Player.getArmies()) {
                    if (currArmy.getID() == army.getID() && rowsUsed != row) {
                        return "";
                    }
                    // else
                    rowsUsed += getRowsForArmy(currArmy);
                }// end while
            }

            int lid = army.getID();

            String armyName = army.getName();
            if (armyName.length() > 11) {
                armyName = armyName.substring(0, 11);
            }

            String htmlDesc = "<html><center><b>Army #" + lid + "</b><br>";
            if (army.isPlayerLocked()) {
                htmlDesc += "(locked)<br>";
            }

            if (army.isDisabled()) {
                htmlDesc += "(disabled)<br>";
            }

            // only show army name if one is actually set
            if (!isFakeName(armyName)) {
                if (armyName.length() > 10) {
                    htmlDesc += armyName.subSequence(0, 9) + "...<br>";
                } else {
                    htmlDesc += armyName + "<br>";
                }
            }

            boolean useOpRule = Boolean.parseBoolean(mwclient.getserverConfigs("UseOperationsRule"));
            String modifiedBV = "";
            if (useOpRule && army.getOpForceSize() < army.getUnits().size() && army.getOpForceSize() > 0) {
                modifiedBV = "(" + Math.round(army.getBV() * army.forceSizeModifier(army.getOpForceSize())) + ")";
            }

            htmlDesc += "BV: " + army.getBV() + modifiedBV + "<br>" + getRangeString(army) + "</center>";
            if (useOpRule && army.getOpForceSize() < army.getUnits().size() && army.getOpForceSize() > 0) {
                htmlDesc += "Force Size: " + army.getOpForceSize() + "<br>";
            }

            // Put in the tonnage info
            htmlDesc += "Tons: " + (int)army.getTotalTonnage() + "<br>";
            //toReturn += army.getSkillInfoForDisplay();
            htmlDesc += "</HTML>";
            return htmlDesc;
        }

        private String getRangeString(CArmy army) {
            String range = "";

            boolean limitsAllowed = Boolean.parseBoolean(mwclient.getserverConfigs("AllowLimiters"));
            if (limitsAllowed) {
                // lower limit
                if (army.getLowerLimiter() == Army.NO_LIMIT) {
                    range = "No Lower";
                } else if (army.getAmountOfUnits() - army.getLowerLimiter() < 1) {
                    range = "1";
                } else {
                    range = "" + (army.getAmountOfUnits() - army.getLowerLimiter());
                }

                range += " - ";

                // upper limit
                if (army.getUpperLimiter() == Army.NO_LIMIT) {
                    range += "No Upper";
                } else {
                    range += "" + (army.getAmountOfUnits() + army.getUpperLimiter());
                }

                // overwrite if there are no limits at all
                if (army.getLowerLimiter() == Army.NO_LIMIT && army.getUpperLimiter() == Army.NO_LIMIT) {
                    range = "No Limits";
                }
            }
            return range;
        }

        private boolean isFakeName(String armyName) {
            if (armyName.equals("") || armyName.equals(" ")) {
                return true;
            } else if (armyName.toLowerCase().equals("no name")) {
                return true;
            } else if (armyName.toLowerCase().equals("none")) {
                return true;
            } else if (armyName.toLowerCase().equals("clear")) {
                return true;
            } else if (armyName.toLowerCase().equals("untitled")) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void refreshModel() {
            fireTableDataChanged();
        }

        public MekTableModel.Renderer getRenderer() {
            return new MekTableModel.Renderer(mwclient);
        }

        public class Renderer extends MechInfo implements TableCellRenderer {

            /**
             *
             */
            private static final long serialVersionUID = -300922977373422309L;

            int meknum;

            MechTileset mt = new MechTileset(new File("data/images/units/"));
            Color dcolor = new Color(220, 220, 220);

            public Renderer(MWClient client) {
                super(client);
                try {
                    mt.loadFromFile("mechset.txt");
                } catch (IOException ex) {
                    CampaignData.mwlog.errLog("Unable to read data/images/units/mechset.txt");
                }
            }

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = this;
                setOpaque(true);
                setText(getValueAt(row, column).toString());
                setToolTipText(null);
                c.setBackground(dcolor);
                String scheme = mwclient.getConfig().getParam("HQCOLORSCHEME").toLowerCase();
                CArmy army = getArmyAt(row);

                if (army != null) {
                    if (column == 0) {
                        setImageVisible(false);
                        setToolTipText(army.getSkillInfoForDisplay());
                        if (army.isLocked()) {
                            c.setBackground(new Color(235, 225, 5));
                        }
                        return c;
                    }
                } else if (column == 0) {
                    // Hangar Color (pale purple)
                    c.setBackground(new Color(dcolor.getRed() - 33, dcolor.getBlue() - 33, dcolor.getGreen() - 7));
                    return c;
                }
                CUnit unit = getMekAt(row, column);
                if (unit != null) {

                    int inNumberofArmies = Player.getAmountOfTimesUnitExistsInArmies(unit.getId());
                    String unitToolTip = getUnitToolTipText(army, unit, inNumberofArmies);
                    setToolTipText(unitToolTip);
                    setUnit(unit, army);
                    setImageVisible(true);

                    c.setBackground(getBackgroundForUnit(scheme, army, unit, inNumberofArmies));
                }

                else {
                    setImageVisible(false);
                    meknum = (row - getRowsForArmies()) * getColumnCount() - 1 + column;
                    int freebays = Player.getFreeBays();
                    if (freebays < 0) {
                        freebays = 0;
                    }
                    if (meknum > freebays + Player.getHangar().size()) {
                        setText("");
                    }
                }
                return c;
            }

            private String getUnitToolTipText(CArmy army, CUnit unit, int inNumberofArmies) {
                StringBuilder unitText = new StringBuilder();

                if (unit.getC3Level() > 0) {

                    if (unit.getC3Level() == Unit.C3_SLAVE) {
                        unitText.append("C3 Slave");
                    } else if (unit.getC3Level() == Unit.C3_MASTER) {
                        unitText.append("C3 Master");
                    } else if (unit.getC3Level() == Unit.C3_MMASTER) {
                        unitText.append("C3 Dual Master");
                    } else if (unit.getC3Level() == Unit.C3_IMPROVED) {
                        unitText.append("C3 Improved");
                    }

                    if (army != null && army.getC3Network().get(unit.getId()) != null) {
                        Integer master = army.getC3Network().get(unit.getId());
                        if (unit.getC3Level() == Unit.C3_IMPROVED) {
                            unitText.append(" linked to #").append(master);
                        } else {
                            unitText.append(" to #").append(master);
                        }
                    }

                    if (army != null && unit.hasBeenC3LinkedTo(army)) {
                        if (unit.getC3Level() == Unit.C3_IMPROVED) {
                            unitText.append(" master for");
                        } else {
                            unitText.append(" for");
                        }

                        Enumeration<Integer> c3Key = army.getC3Network().keys();
                        Enumeration<Integer> c3Unit = army.getC3Network().elements();
                        while (c3Key.hasMoreElements()) {
                            Integer slave = c3Key.nextElement();
                            Integer master = c3Unit.nextElement();
                            if (master == unit.getId()) {
                                unitText.append(" #").append(slave);
                            }
                        }

                    }
                }
                if (mwclient.getPlayer().getMyHouse().getNonFactionUnitsCostMore()) {
                    String techCostString = "";
                    if (unit.getC3Level() > 0) {
                        techCostString = unitText.toString() + "<br>";
                    }

                    String techsForConfigKey = "TechsFor" + Unit.getWeightClassDesc(unit.getWeightclass()) + Unit.getTypeClassDesc(unit.getType());
                    int baseTechAmount = Integer.parseInt(mwclient.getserverConfigs(techsForConfigKey));
                    float nonFactionUnitsIncreasedTechs = Float.parseFloat(mwclient.getserverConfigs("NonFactionUnitsIncreasedTechs"));
                    boolean houseSupportsUnit = mwclient.getPlayer().getMyHouse().houseSupportsUnit(unit.getUnitFilename());
                    int numTechs = (int) (baseTechAmount * (houseSupportsUnit ? 1 : nonFactionUnitsIncreasedTechs));

                    techCostString += "Techs required: " + numTechs;
                    unitText.setLength(0);
                    unitText.append(techCostString);
                }
                if (Boolean.parseBoolean(mwclient.getConfigParam("ShowUnitTechBase"))) {
                    if (mwclient.getPlayer().getMyHouse().getNonFactionUnitsCostMore()) {
                        unitText.append("<br>");
                    }
                    if (unit.getEntity().isClan()) {
                        unitText.append("Tech Base: Clan<br>");
                    } else {
                        unitText.append("Tech Base: IS<br>");
                    }
                }
                unitText.append("Targeting: ").append(unit.getTargetSystemTypeDesc()).append("<br>");
                if (unit.isSupportUnit()) {
                    unitText.append("[Support]<br>");
                }

                //@salient EXPANDEDUNITTOOLTIP
                if(Boolean.parseBoolean(mwclient.getConfig().getParam("EXPANDEDUNITTOOLTIP"))) {
                    unitText.append("<font color=\"purple\">");
                    unitText.append("<b>[General]</b><br>");
                    unitText.append("Weight: ").append(unit.getEntity().getWeight()).append(" Tons (")
                            .append(unit.getEntity().getWeightClassName()).append(")<br>");
                    unitText.append("Armor: ").append(unit.getEntity().getArmorWeight()).append(" Tons (")
                            .append(unit.getEntity().getTotalArmor()).append(" Pts)<br>");
                    int walk = unit.getEntity().getWalkMP();
                    int run = unit.getEntity().getRunMPwithoutMASC();
                    int jump = unit.getEntity().getJumpMP();
                    int masc = unit.getEntity().getRunMP();
                    unitText.append("Movement: ").append(walk).append("/").append(run);

                    if(unit.getEntity().getMASC() != null)
                        unitText.append("(").append(masc).append(")");

                    if(jump != 0)
                        unitText.append("/").append(jump).append("<br>");
                    else
                        unitText.append("<br>");

                    unitText.append("Heat Capacity: ").append(unit.getEntity().getHeatCapacity()).append("<br>");

                    if(unit.getEntity().canFlipArms())
                        unitText.append("Arms Flip: <font color=\"green\">YES</font><br>");
                    else
                        unitText.append("Arms Flip: <font color=\"red\">NO</font><br>");

                    unitText.append("</font>");
                    //End General (purple)

                    unitText.append("<font color=\"blue\">");
                    unitText.append("<b>[Weapons]</b><br>");
                    unit.getEntity().getWeaponList().forEach(weapon -> {
                        unitText.append(weapon.getName()).append(" (");
                        if(weapon.isRearMounted())
                            unitText.append(unit.getEntity().getLocationAbbr(weapon.getLocation())).append(") (R)<br>");
                        else
                            unitText.append(unit.getEntity().getLocationAbbr(weapon.getLocation())).append(")<br>");

                        });
                    unitText.append("</font>");
                    //End Weapons (blue)

                    //Quirks...
                    if(Boolean.parseBoolean(mwclient.getserverConfigs("EnableQuirks"))) {
                        unitText.append("<font color=\"teal\">");
                        unitText.append("<b>[Quirks]</b><br>");

                        StringTokenizer st = new StringTokenizer(unit.getHtmlQuirksList(), "*");

                        while(st.hasMoreTokens())
                            unitText.append(TokenReader.readString(st));

                        unitText.append("</font>");
                        //End Quirks (teal)
                    }
                }

                String unitToolTip;
                // If you have a unit in more then one army, list all the armies it is in.
                if (inNumberofArmies > 1) {
                    String armiesText = "";
                    if (unit.getC3Level() > 0) {
                        armiesText = unitText.toString() + "<br>";
                    }

                    armiesText += "In armies " + Player.getArmiesUnitIsIn(unit.getId());
                    unitToolTip = unit.getDisplayInfo(armiesText);
                } else {
                    unitToolTip = unit.getDisplayInfo(unitText.toString());
                }
                return unitToolTip;
            }

            private Color getBackgroundForUnit(String scheme, CArmy army, CUnit unit, int inNumberofArmies) {
                if (unit.getStatus() == Unit.STATUS_FORSALE) {
                    return FORSALE_COLOR;
                } else if (unit.getStatus() == Unit.STATUS_UNMAINTAINED) {
                    return UNMAINTAINED_COLOR;
                } else if (useUnitLocking && unit.isLocked()) {
                    return LOCKED_COLOR;
                } else if (!mwclient.getConfig().isUsingStatusIcons()) {
                    if (unit.getPilot().getName().equals("Vacant")) {
                        // RFE 1545928 -Color for pilotless units
                        return VACANT_COLOR;
                    } else if (useAdvanceRepairs && UnitUtils.isRepairing(unit.getEntity())) {
                        return REPAIRING_COLOR;
                    } else if (useAdvanceRepairs && mwclient.getRMT() != null && mwclient.getRMT().hasQueuedOrders(unit.getId())) {
                        return QUEUED_COLOR;
                    } else if (useAdvanceRepairs && UnitUtils.hasCriticalDamage(unit.getEntity())) {
                        return CRITICAL_DAMAGE_COLOR;
                    } else if (useAdvanceRepairs && UnitUtils.hasArmorDamage(unit.getEntity())) {
                        return ARMOR_DAMAGE_COLOR;
                    } else if (useAdvanceRepairs && !UnitUtils.hasAllAmmo(unit.getEntity())) {
                        return HAS_ALL_AMMO_COLOR;
                    } else if (army == null && inNumberofArmies > 0) {
                        if (scheme.equals("classic")) {
                            return CLASSIC_IN_ARMIES_COLOR; // dark green
                        } else {
                            // all non-classic sets (light blue)
                            return OTHER_IN_ARMIES_COLOR;
                        }
                    } else {
                        return getUnitBackgroundByWeightClass(scheme, unit);
                    } // end else(should fill by weight)
                } else if (army == null && inNumberofArmies > 0) {
                    if (scheme.equals("classic")) {
                        return CLASSIC_IN_ARMIES_COLOR; // dark green
                    } else {
                        // all non-classic sets (light blue)
                        return OTHER_IN_ARMIES_COLOR;
                    }
                } else {
                    return getUnitBackgroundByWeightClass(scheme, unit);
                }
            }

            private Color getUnitBackgroundByWeightClass(String scheme, CUnit unit) {
                if (scheme.equals("tan")) {
                    switch (unit.getWeightclass()) {
                        case Unit.LIGHT:
                            return TAN_LIGHT;
                        case Unit.MEDIUM:
                            return TAN_MEDIUM;
                        case Unit.HEAVY:
                            return TAN_HEAVY;
                        case Unit.ASSAULT:
                            return TAN_ASSAULT;
                    }
                } else if (scheme.equals("grey")) {
                    switch (unit.getWeightclass()) {
                        case Unit.LIGHT:
                            return GRAY_LIGHT;
                        case Unit.MEDIUM:
                            return GRAY_MEDIUM;
                        case Unit.HEAVY:
                            return GRAY_HEAVY;
                        case Unit.ASSAULT:
                            return GRAY_ASSAULT;
                    }
                } else {
                    switch (unit.getWeightclass()) {
                        case Unit.LIGHT:
                            return CLASSIC_LIGHT;
                        case Unit.MEDIUM:
                            return CLASSIC_MEDIUM;
                        case Unit.HEAVY:
                            return CLASSIC_HEAVY;
                        case Unit.ASSAULT:
                            return CLASSIC_ASSAULT;
                    }
                }
                // fall-through
                return DGRAY;
            }
            
        }// end Renderer

    }// end MekTableModel

}
