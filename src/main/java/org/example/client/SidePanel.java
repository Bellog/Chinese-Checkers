package org.example.client;

import org.example.connection.Packet;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.List;

/**
 * JPanel showing information about players, game logs, and lets user end or rollback turn.
 * Requires overriding {@link #send(Packet)} method in order to use.
 */
public abstract class SidePanel extends JPanel {

    private final JButton endTurnButton;
    private final JButton resetTurnButton;
    private final Font font = new Font("Tahoma", Font.PLAIN, 12);
    /**
     * Shows in-game logs
     */
    private final JTextArea gameLogs;
    /**
     * Player color list used to show association between player number and their pawns' color
     */
    private final List<Color> colorScheme;
    /**
     * Table like JPanel that shows playerInfo in graphical form.
     * <br> See {@link Packet} for more information on playerInfo.
     */
    private final JPanel playerTable;

    /**
     * Creates side panel based on provided parameters
     *
     * @param info        player information table
     * @param colorScheme list of player colors.
     * @param maxHeight   maximum height of this component <br>
     *                    JTextArea occupies as much height as it can, with this parameter its height can be controlled.
     */
    SidePanel(List<List<String>> info, List<Color> colorScheme, int maxHeight) {
        this.colorScheme = colorScheme;

        playerTable = getPlayerTable(info);
        gameLogs = getGameLogs();

        setVisible(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        add(Box.createVerticalStrut(20));
        add(playerTable);
        add(Box.createVerticalStrut(20));

        endTurnButton = getEndTurnButton();
        resetTurnButton = getResetTurnButton();
        add(createButtonPanel());
        add(Box.createVerticalStrut(20));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(gameLogs);
        add(scrollPane);
        // must be last, as it requires scrollPane to be created first
        setPreferredSize(new Dimension(scrollPane.getPreferredSize().width + 10, maxHeight));
    }

    /**
     * Inserts text into logs and formats it appropriately
     *
     * @param text text without newlines at the end
     */
    public void updateLogs(String text) {
        gameLogs.insert("> " + text + "\n", 0);
    }

    /**
     * This method should send packets to the server.
     *
     * @param packet packet to send
     */
    public abstract void send(Packet packet);

    /**
     * Sets status of this panel's buttons
     *
     * @param status status
     */
    public void setStatus(boolean status) {
        endTurnButton.setEnabled(status);
        resetTurnButton.setEnabled(status);
    }

    /**
     * Updates player info
     *
     * @param playerInfo updates player info only if it is it the same format as the one used at instantiation
     *                   of this object. Otherwise, it closes the program
     */
    public void updatePlayerInfo(List<List<String>> playerInfo) {
        try {
            var comps = playerTable.getComponents();
            for (int i = 1; i < 3; i++) {
                JPanel panel = (JPanel) comps[i];
                for (int j = 0; j < playerInfo.size(); j++) {
                    ((JTextField) panel.getComponents()[j]).setText(playerInfo.get(j).get(i - 1));
                }
            }
        } catch (Exception e) {
            System.out.println("connection error, closing the program");
            System.exit(1);
        }
    }

    /**
     * Helper method, extracted from constructor
     *
     * @return not null
     */
    private JTextArea getGameLogs() {
        JTextArea gameLogs = new JTextArea();
        gameLogs.setText("Logs will be shown here\n");
        ((DefaultCaret) gameLogs.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        gameLogs.setColumns(50);
        gameLogs.setEditable(false);
        gameLogs.setFont(font);
        gameLogs.setLineWrap(true);
        return gameLogs;
    }

    /**
     * Helper method, extracted from constructor
     *
     * @return not null
     */
    private JPanel createButtonPanel() {
        var panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());
        var con = new GridBagConstraints();
        con.fill = GridBagConstraints.NONE;
        con.anchor = GridBagConstraints.CENTER;
        con.gridx = 0;
        panel.add(endTurnButton, con);
        con.gridx = 1;
        panel.add(resetTurnButton, con);
        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }

    /**
     * Helper method, extracted from constructor
     *
     * @return not null
     */
    private JButton getResetTurnButton() {
        final JButton resetTurnButton;
        resetTurnButton = new JButton("reset turn");
        resetTurnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetTurnButton.setFont(font);
        resetTurnButton.setEnabled(false);
        resetTurnButton.addActionListener(v -> send(new Packet.PacketBuilder().code(Packet.Codes.TURN_ROLLBACK).build()));
        resetTurnButton.setMaximumSize(resetTurnButton.getPreferredSize());
        return resetTurnButton;
    }

    /**
     * Helper method, extracted from constructor
     *
     * @return not null
     */
    private JButton getEndTurnButton() {
        final JButton endTurnButton;
        endTurnButton = new JButton("end turn");
        endTurnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        endTurnButton.setFont(font);
        endTurnButton.setEnabled(false);
        endTurnButton.addActionListener(v -> send(new Packet.PacketBuilder().code(Packet.Codes.TURN_END).build()));
        endTurnButton.setMaximumSize(endTurnButton.getPreferredSize());
        return endTurnButton;
    }

    /**
     * Helper method, extracted from constructor
     *
     * @return not null
     */
    private JPanel getPlayerTable(List<List<String>> info) {
        var posColumn = new JPanel();
        var playerColumn = new JPanel();
        var colorColumn = new JPanel();
        var panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());
        var con = new GridBagConstraints();
        con.anchor = GridBagConstraints.CENTER;
        con.fill = GridBagConstraints.NONE;
        con.gridx = 0;
        panel.add(colorColumn, con);
        con.gridx = 1;
        panel.add(playerColumn, con);
        con.gridx = 2;
        panel.add(posColumn, con);

        playerColumn.setLayout(new BoxLayout(playerColumn, BoxLayout.Y_AXIS));
        posColumn.setLayout(new BoxLayout(posColumn, BoxLayout.Y_AXIS));
        colorColumn.setLayout(new BoxLayout(colorColumn, BoxLayout.Y_AXIS));

        CreateTableContents(info, playerColumn, posColumn, colorColumn);
        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }

    /**
     * Helper method, extracted from getPlayerTable
     */
    private void CreateTableContents(List<List<String>> info, JPanel playerColumn, JPanel posColumn, JPanel colorColumn) {
        int maxPlayerColWidth = 0;
        int maxPosColWidth = 0;
        for (List<String> row : info) {
            if (maxPlayerColWidth < row.get(0).length())
                maxPlayerColWidth = row.get(0).length();
            if (maxPosColWidth < row.get(0).length())
                maxPosColWidth = row.get(0).length();
        }

        for (int i = 0; i < info.size(); i++) {
            JTextField col = new JTextField(" ");
            if (i > 0)
                col.setBackground(colorScheme.get(i - 1));
            else {
                col.setText("Color");
                col.setBackground(Color.WHITE);
            }
            col.setColumns(maxPosColWidth);
            col.setHorizontalAlignment(SwingConstants.CENTER);
            col.setFont(font.deriveFont(Font.BOLD));
            col.setEditable(false);
            colorColumn.add(col);

            JTextField player = new JTextField(info.get(i).get(0));
            player.setColumns(maxPlayerColWidth);
            player.setBackground(Color.WHITE);
            player.setHorizontalAlignment(SwingConstants.CENTER);
            player.setFont(font.deriveFont(Font.BOLD));
            player.setEditable(false);
            playerColumn.add(player);

            JTextField pos = new JTextField(info.get(i).get(1));
            pos.setBackground(Color.WHITE);
            pos.setColumns(maxPosColWidth);
            pos.setHorizontalAlignment(SwingConstants.CENTER);
            pos.setFont(font.deriveFont(Font.BOLD));
            pos.setEditable(false);
            posColumn.add(pos);
        }
    }
}
