package org.example;
import javax.swing.*;

import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GuiClient {

    private Client client;

    private AuctionWindow pannelloAsta;


    public GuiClient(Client c) {
        this.client = c;
        SwingUtilities.invokeLater(() -> {
            RegistrationWindow r = new RegistrationWindow();
            r.setVisible(true);

        });
    }


    public void aggiornaGui(Articolo a,boolean add,boolean modifica){
        pannelloAsta.addArticolo( a ,add,modifica);

    }
    public void mandaNotifiche(String nomeUtente,String nomeArticolo,String cognome,double prezzo,boolean fine){
        pannelloAsta.modificaOfferta(nomeUtente,nomeArticolo,cognome,prezzo,fine);


    }


    class RegistrationWindow extends JFrame {
        private JTextField textNome;
        private JTextField textCognome;
        private JButton continueButton;

        public RegistrationWindow() {

            registraUtente();
        }

        private void registraUtente() {
            setTitle("Registrazione Utente");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(600, 400);
            setLayout(new GridLayout(3, 2));
            //aggiunte
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JLabel titleLabel = new JLabel("Registrazione", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(new Color(0x2E86C1));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(20, 0, 30, 0);
            add(titleLabel, gbc);

            gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            textNome = new JTextField();
            textNome.setBorder(BorderFactory.createTitledBorder("Username"));
            textNome.setFont(new Font("Arial", Font.PLAIN, 18));
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            add(textNome, gbc);

            textCognome = new JTextField();
            textCognome.setBorder(BorderFactory.createTitledBorder("Surname"));
            textCognome.setFont(new Font("Arial", Font.PLAIN, 18));
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            add(textCognome, gbc);

            continueButton = new JButton("Continua");
            continueButton.setBackground(new Color(0x2E86C1));
            continueButton.setForeground(Color.WHITE);
            continueButton.setFont(new Font("Arial", Font.BOLD, 18));
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(30, 10, 10, 10);
            add(continueButton, gbc);
            continueButton.addActionListener(new ActionListener() {
                                                 @Override
                                                 public void actionPerformed(ActionEvent e) {
                                                     String nome = textNome.getText();
                                                     String cognome = textCognome.getText();
                                                     if (nome.isEmpty() || cognome.isEmpty())
                                                         JOptionPane.showMessageDialog(RegistrationWindow.this, "Nome e Cognome sono obbligatori", "Errore", JOptionPane.ERROR_MESSAGE);
                                                     else {if (client.Registracliente(nome, cognome)) {
                                                             client.setNome(nome);
                                                             client.setCognome(cognome);
                                                             dispose();
                                                             pannelloAsta = new AuctionWindow(nome, cognome);
                                                         } else {
                                                             JOptionPane.showMessageDialog(RegistrationWindow.this, "Utente gia registrato", "Errore", JOptionPane.ERROR_MESSAGE);
                                                         }
                                                     }
                                                 }

                                             });
                setLocationRelativeTo(null);
                setVisible(true);

        }
    }

     class AuctionWindow extends JFrame {
        private JTextArea textOfferte;
        private JTextArea textUtenti;
        private JTextArea textNotifiche;
        private JPanel panelOfferte;
        private JTable tabellaOfferta;

        private DefaultTableModel OffertaableModel;
        private String nome ;
        private String cognome;
        private JPanel panelNotifiche;

        private JPanel centerPanel;

        public AuctionWindow(String nome, String cognome) {
            this.nome=nome;
            this.cognome = cognome;
            aprifinestra(nome, cognome);
        }

        private void aprifinestra(String nome, String cognome) {
            setTitle("Asta Online - " + nome + " " + cognome);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
            setLayout(new BorderLayout());

            centerPanel = new JPanel(new GridLayout(1, 2));
            add(centerPanel, BorderLayout.CENTER);
            // Pannello per le offerte
            panelOfferte = new JPanel(new BorderLayout());
            panelOfferte.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Asta", TitledBorder.CENTER, TitledBorder.TOP));
            OffertaableModel = new DefaultTableModel(new Object[]{"Utente", "Offerta", "Articolo", "Ora Fine"}, 0){
                @Override
                public boolean isCellEditable(int row ,int col){
                    return false;
                }
            };
            tabellaOfferta = new JTable(OffertaableModel);
            JScrollPane bidsScrollPane = new JScrollPane(tabellaOfferta);
            panelOfferte.add(bidsScrollPane, BorderLayout.CENTER);
            centerPanel.add(panelOfferte);



            // Pannello delle notifiche
            panelNotifiche = new JPanel(new BorderLayout());
            textNotifiche = new JTextArea();
            textNotifiche.setEditable(false);
            panelNotifiche.add(new JScrollPane(textNotifiche), BorderLayout.CENTER);
            panelNotifiche.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Notifiche",TitledBorder.CENTER,TitledBorder.TOP));
            JScrollPane notificationsScrollPane = new JScrollPane(textNotifiche);
            panelNotifiche.add(notificationsScrollPane, BorderLayout.CENTER);
            centerPanel.add(panelNotifiche);


            // Pannello di controllo
            JPanel panelControl = new JPanel(new FlowLayout());
            JButton btnPiazzaOfferta = new JButton("Piazza Offerta");
            JButton btnMettiAllAsta = new JButton("Metti All'Asta");

            btnPiazzaOfferta.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    piazzaOfferta();
                }
            });

            btnMettiAllAsta.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    finestraAsta();

                }
            });
            panelControl.add(btnPiazzaOfferta);
            panelControl.add(btnMettiAllAsta);
            add(panelControl, BorderLayout.SOUTH);
            setLocationRelativeTo(null);
            setVisible(true);
        }

         protected void modificaOfferta(String nomeUtente, String nomeArticolo, String cognomeclient, double prezzo, boolean fine) {
            if(fine && !client.getNome().equals(nomeUtente) && !client.getCognome().equals(cognomeclient) ) {
                textNotifiche.append("L'asta per " + nomeArticolo + " è stata vinta da " + nomeUtente + " " + cognomeclient + " con prezzo finale" + prezzo + "\n");
            } else if (fine && client.getNome().equals(nomeUtente) && client.getCognome().equals(cognomeclient)) {
                textNotifiche.append("Complementi "+nomeUtente+" "+cognomeclient+" hai vinto l'asta per l'articolo  "+nomeArticolo+"\n");
            } else{
                textNotifiche.append(nomeUtente+" "+cognomeclient+" "+"ha piazzato una nuova offerta di "+prezzo+" per "+nomeArticolo+"\n");
            }
         }

        protected void addArticolo(Articolo a ,boolean add,boolean modifica) {
            if (add && !modifica) {
                OffertaableModel.addRow(new Object[]{a.getUtente().getNome() + " " + a.getUtente().getCognome(), a.getPrezzo(), a.getNome(), a.getFine()});
                textNotifiche.append("L'articolo " + a.getNome() + " è stato aggiunto da " + a.getUtente().getNome() + " " + a.getUtente().getCognome()+" prezzo iniziale " + a.getPrezzo() + "\n");
            }
            if (add && modifica) {
                for (int row = 0; row < OffertaableModel.getRowCount(); row++) {
                    String nomeArticolo = (String) OffertaableModel.getValueAt(row, 2);
                    if (a.getNome().equals(nomeArticolo)) {
                        OffertaableModel.setValueAt(a.getUtente().getNome() + " " + a.getUtente().getCognome(), row, 0);
                        OffertaableModel.setValueAt(a.getPrezzo(), row, 1);
                    }
                }
            }
            if (!modifica && !add) {
                for (int row = 0; row < OffertaableModel.getRowCount(); row++) {
                    String nomeArticolo = (String) OffertaableModel.getValueAt(row, 2);
                    if (a.getNome().equals(nomeArticolo)) {
                        OffertaableModel.removeRow(row);
                    }

                }
            }
        }


        private void piazzaOfferta(){
            JFrame faiOfferta= new JFrame("Fai offerta");
            faiOfferta.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            faiOfferta.setSize(400, 300);
            faiOfferta.setLayout(new GridLayout(5, 2));
            JLabel nomeA = new JLabel("Nome Articolo:");
            JTextField nameField = new JTextField();
            JLabel priceLabel = new JLabel("Prezzo:");
            JTextField priceField = new JTextField();
            JButton submitButton = new JButton("Invia");
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String namoArticolo = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    Utente u = new Utente(nome,cognome);
                    if(namoArticolo.isEmpty()|| priceField.getText().isEmpty())
                        JOptionPane.showMessageDialog(AuctionWindow.this, "Tutti i campi  sono obbligatori", "Errore", JOptionPane.ERROR_MESSAGE);
                    else if (client.piazzaOfferta(namoArticolo,client.getNome(),client.getCognome(),price)) {
                        faiOfferta.dispose();
                        JOptionPane.showMessageDialog(AuctionWindow.this, "L'offerta è stata piazzata");
                    } else{
                        faiOfferta.dispose();
                        JOptionPane.showMessageDialog(AuctionWindow.this, "L'offerta non può essere piazzata ");

                    }

                }
            });
            faiOfferta.add(nomeA);
            faiOfferta.add(nameField);
            faiOfferta.add(priceLabel);
            faiOfferta.add(priceField);
            faiOfferta.add(submitButton);
            faiOfferta.setVisible(true);
        }
         private  void finestraAsta(){
             JFrame mettiarticolo= new JFrame("Inserisci dati Articolo");
             mettiarticolo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
             mettiarticolo.setSize(400, 300);
             mettiarticolo.setLayout(new GridLayout(6, 2));

             // Campi di input
             JLabel nameLabel = new JLabel("Nome Articolo:");
             JTextField nameField = new JTextField();
             JLabel priceLabel = new JLabel("Prezzo:");
             JTextField priceField = new JTextField();
             JLabel startTimeLabel = new JLabel("Ora Inizio Asta: ora:minuti");
             JTextField startTimeField = new JTextField();
             JLabel endTimeLabel = new JLabel("Ora Fine Asta: ora:minuti");
             JTextField endTimeField = new JTextField();
             JLabel data = new JLabel("Data yy/mm/dd:");
             JTextField datafiled = new JTextField();


             // Bottone per inviare i dati
             JButton submitButton = new JButton("Invia");
             submitButton.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     String namoArticolo = nameField.getText();
                     double price = Double.parseDouble(priceField.getText());
                     String startTime = startTimeField.getText();
                     String endTime = endTimeField.getText();
                     Utente u = new Utente(nome,cognome);
                     String data = datafiled.getText();
                     Articolo a = new Articolo(u,namoArticolo,startTime,endTime,price,data);
                     if(namoArticolo.isEmpty()|| priceField.getText().isEmpty()|| startTime.isEmpty()|| endTime.isEmpty())
                         JOptionPane.showMessageDialog(AuctionWindow.this, "Tutti i campi  sono obbligatori", "Errore", JOptionPane.ERROR_MESSAGE);
                     else if(client.mettiArticolo(a)){
                         mettiarticolo.dispose();
                         JOptionPane.showMessageDialog(AuctionWindow.this, "L'articolo sarà messo all'asta");
                     }
                     else {
                         mettiarticolo.dispose();
                         JOptionPane.showMessageDialog(AuctionWindow.this, "L'articolo non può essere messo all'asta,Riprova");
                     }
                 }
             });

             mettiarticolo.add(nameLabel);
             mettiarticolo.add(nameField);
             mettiarticolo.add(priceLabel);
             mettiarticolo.add(priceField);
             mettiarticolo.add(startTimeLabel);
             mettiarticolo.add(startTimeField);
             mettiarticolo.add(endTimeLabel);
             mettiarticolo.add(endTimeField);
             mettiarticolo.add(submitButton);
             mettiarticolo.add(data);
             mettiarticolo.add(datafiled);
             mettiarticolo.setVisible(true);
         }



     }



}


