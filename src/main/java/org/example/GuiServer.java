package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class GuiServer {

    private Server s;

    private Amministrazione p ;
    public GuiServer(Server server) {
        this.s = server;
        SwingUtilities.invokeLater(() -> {
            p = new Amministrazione();
            p.setVisible(true);

        });

    }

    public void aggionaU() {
        p.aggiornaUtenti();
    }

    public void aggiornaArticolo() {
        p.aggiornaArticoli();
    }


    class Amministrazione extends JFrame {
        private DefaultTableModel notificationTableModel;
        private DefaultListModel<String> userListModel;
        private JList<String> userList;

        public Amministrazione() {


            createWidgets();
        }

        private void createWidgets() {
            setTitle("Amministratore");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // Panel principale
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            mainPanel.setBackground(new Color(238, 238, 238));

            // Panel per le notifiche
            JPanel notificationPanel = new JPanel(new BorderLayout());
            notificationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Notifications"));
            notificationPanel.setBackground(Color.WHITE);

            userListModel = new DefaultListModel<>();
            userList = new JList<>(userListModel);
            userList.setBorder(BorderFactory.createTitledBorder("Utenti Connessi"));

            JPanel userPanel = new JPanel();
            userPanel.setLayout(new BorderLayout());
            userPanel.add(new JScrollPane(userList), BorderLayout.CENTER);
            userPanel.setPreferredSize(new Dimension(200, 0));

            // Tabella delle notifiche
            String[] columnNames = {"Articolo", "Utente","Data", "Start Time", "End Time", "Offerta piu alta"};
            notificationTableModel = new DefaultTableModel(columnNames, 0);
            JTable notificationTable = new JTable(notificationTableModel);
            notificationTable.setFillsViewportHeight(true);
            JScrollPane notificationScrollPane = new JScrollPane(notificationTable);

            notificationPanel.add(notificationScrollPane, BorderLayout.CENTER);


            mainPanel.add(notificationPanel, BorderLayout.CENTER);
            mainPanel.add(userPanel, BorderLayout.EAST);


            add(mainPanel);

            setLocationRelativeTo(null);
            setVisible(true);


        }

        public void aggiornaUtenti() {
            SwingUtilities.invokeLater(() -> {
                userListModel.clear();
                for (Utente u : Server.ListadiUtenti) {
                    userListModel.addElement(u.getNome() + " " + u.getCognome());
                }
            });
        }

        public void aggiornaArticoli() {
            SwingUtilities.invokeLater(() -> {
                notificationTableModel.setRowCount(0);
                for (Articolo a : Server.listaArticoli) {
                    Object[] row = {
                            a.getNome(),
                            a.getUtente().getNome() + " " + a.getUtente().getCognome(),
                            a.getData(),
                            a.getInizio(),
                            a.getFine(),
                            a.getPrezzo()
                    };
                    notificationTableModel.addRow(row);
                }
            });
        }

    }
}

