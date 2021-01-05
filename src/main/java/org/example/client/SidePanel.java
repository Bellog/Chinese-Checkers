package org.example.client;

import org.example.connection.Packet;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.List;

/**
 * JPanel showing information about players,
 */
public class SidePanel extends JPanel {
    private final JButton endTurnButton;
    private final Font font = new Font("Tahoma", Font.PLAIN, 12);
    /**
     * Shows in-game logs
     */
    private final JTextArea gameLogs;
    /**
     * Used to send packets
     */
    private final IClient client;
    /**
     * Player color list used to show association between player number and their pawns' color
     */
    private final List<Color> colorScheme;
    /**
     * Table like JPanel that shows playerInfo in graphical form.
     * <br> See {@link Packet} for more information on playerInfo.
     */
    private JPanel playerTable;

    /**
     * Creates side panel based on provided parameters
     *
     * @param info        player information table
     * @param client      used to send information to the server
     * @param colorScheme list of player colors.
     * @param maxHeight   maximum height of this component <br>
     *                    JTextArea occupies as much height as it can, with this parameter its height can be controlled.
     */
    SidePanel(List<List<String>> info, IClient client, List<Color> colorScheme, int maxHeight) {
        this.client = client;
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
        add(endTurnButton);
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

    public void starTurn() {
        endTurnButton.setEnabled(true);
    }

    /**
     * Updates player info
     *
     * @param playerInfo updates player info only if it is it the same format as the one used at instantiation
     *                   of this object.
     */
    public void updatePlayerInfo(List<List<String>> playerInfo) {
        remove(1);
        playerTable = getPlayerTable(playerInfo);
        add(playerTable, 1);
    }

    private JTextArea getGameLogs() {
        JTextArea gameLogs = new JTextArea();
        gameLogs.setText("GAME LOGS\n");
        ((DefaultCaret) gameLogs.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        gameLogs.setColumns(50);
        gameLogs.setEditable(false);
        gameLogs.setFont(font);
        gameLogs.setLineWrap(true);
        return gameLogs;
    }

    private JButton getEndTurnButton() {
        final JButton endTurnButton;
        endTurnButton = new JButton("end turn");
        endTurnButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        endTurnButton.setFont(font);
        endTurnButton.setEnabled(false);
        endTurnButton.addActionListener(v -> {
            this.client.send(new Packet.PacketBuilder().code(Packet.Codes.TURN_END).build());
            endTurnButton.setEnabled(false);
        });
        endTurnButton.setMaximumSize(endTurnButton.getPreferredSize());
        return endTurnButton;
    }

    private JPanel getPlayerTable(List<List<String>> info) {
        var panel = new JPanel();
        var playerColumn = new JPanel();
        var posColumn = new JPanel();

        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());
        var con = new GridBagConstraints();
        con.anchor = GridBagConstraints.CENTER;
        con.fill = GridBagConstraints.NONE;
        con.gridy = 0;
        con.gridx = 0;
        panel.add(playerColumn, con);
        con.gridx = 1;
        con.gridy = 0;
        panel.add(posColumn, con);

        playerColumn.setLayout(new BoxLayout(playerColumn, BoxLayout.Y_AXIS));
        posColumn.setLayout(new BoxLayout(posColumn, BoxLayout.Y_AXIS));

        CreateTableContents(info, playerColumn, posColumn);
        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }

    private void CreateTableContents(List<List<String>> info, JPanel playerColumn, JPanel posColumn) {
        int maxPlayerColWidth = 0;
        int maxPosColWidth = 0;
        for (List<String> row : info) {
            if (maxPlayerColWidth < row.get(0).length())
                maxPlayerColWidth = row.get(0).length();
            if (maxPosColWidth < row.get(0).length())
                maxPosColWidth = row.get(0).length();
        }

        for (int i = 0; i < info.size(); i++) {
            JTextField player = new JTextField(info.get(i).get(0));
            player.setColumns(maxPlayerColWidth);
            if (i > 0)
                player.setForeground(colorScheme.get(i - 1));
            player.setBackground(Color.WHITE);
            player.setHorizontalAlignment(SwingConstants.CENTER);
            player.setFont(font);
            player.setEditable(false);
            playerColumn.add(player);

            JTextField pos = new JTextField(info.get(i).get(1));
            pos.setBackground(Color.WHITE);
            pos.setColumns(maxPosColWidth);
            pos.setHorizontalAlignment(SwingConstants.CENTER);
            pos.setFont(font);
            pos.setEditable(false);
            posColumn.add(pos);
        }
    }
}