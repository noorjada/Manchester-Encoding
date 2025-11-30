import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Files;

public class ManchesterEncoding extends JComponent implements ActionListener {
    private String data = "";
    private int graphWidth;

    private JLabel binaryLabel, dataLabel;
    private JButton btnFile, btnBinary;
    private JTextField txtBinary;
    private JScrollPane graphScroll, dataScroll;
    private JPanel userInput1, userInput2, userTotal, userData, graphPanel;
    private JFrame mainFrame;

    private Font font = new Font("Segoe UI", Font.PLAIN, 12);
    private Font font2 = new Font("Segoe UI", Font.BOLD, 18);

    // Modern colors
    private Color buttonColor = new Color(0x000000);
    private Color buttonHoverColor = new Color(0x0056b3);
    private Color textColor = new Color(0x333333);
    private Color graphLineColor1 = new Color(0x007BFF); // Blue for '1'
    private Color graphLineColor0 = new Color(0xFF5733); // Orange for '0'
    private Color graphBackgroundColor = new Color(0xF5F5F5); // Light gray

    ManchesterEncoding() {
        // Set "Windows" Look and Feel
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize components
        btnFile = new JButton("Read Data From File and Generate a Manchester Encoded Signal");
        btnFile.setFont(font);
        btnFile.setBackground(buttonColor);
        btnFile.setForeground(Color.WHITE);
        btnFile.setFocusPainted(false);
        btnFile.setBorderPainted(false);
        btnFile.setOpaque(true);
        btnFile.addActionListener(this);

        binaryLabel = new JLabel("Enter Binary Data");
        binaryLabel.setFont(font);
        binaryLabel.setForeground(textColor);

        txtBinary = new JTextField();
        txtBinary.setFont(font);
        txtBinary.setColumns(15);
        txtBinary.setBorder(BorderFactory.createLineBorder(textColor));

        btnBinary = new JButton("Generate a Manchester Encoded Signal");
        btnBinary.setFont(font);
        btnBinary.setBackground(buttonColor);
        btnBinary.setForeground(Color.WHITE);
        btnBinary.setFocusPainted(false);
        btnBinary.setBorderPainted(false);
        btnBinary.setOpaque(true);
        btnBinary.addActionListener(this);

        // Arrange components in userInput1 panel
        userInput1 = new JPanel(new GridBagLayout());
        userInput1.setFont(font);
        userInput1.setBackground(graphBackgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        userInput1.add(btnFile, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        userInput1.add(binaryLabel, gbc);

        gbc.gridx = 1;
        userInput1.add(txtBinary, gbc);

        gbc.gridx = 2;
        userInput1.add(btnBinary, gbc);

        // Placeholder for user input
        userInput2 = new JPanel(new GridLayout(1, 1));
        userInput2.setBackground(graphBackgroundColor);
        userInput2.add(new JSeparator());

        // Combine input panels into userTotal
        userTotal = new JPanel(new BorderLayout());
        userTotal.setBackground(graphBackgroundColor);
        userTotal.add(userInput1, BorderLayout.CENTER);
        userTotal.add(userInput2, BorderLayout.SOUTH);

        // Setup data display
        dataLabel = new JLabel();
        dataLabel.setFont(font2);
        dataLabel.setForeground(textColor);

        userData = new JPanel(new FlowLayout());
        userData.setBackground(graphBackgroundColor);

        dataScroll = new JScrollPane(userData);
        dataScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dataScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Initialize graphPanel
        graphPanel = new JPanel(new BorderLayout());
        graphPanel.setBackground(graphBackgroundColor);

        // Setup main frame
        mainFrame = new JFrame("Manchester Encoding");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout(10, 10));
        mainFrame.add(userTotal, BorderLayout.NORTH);
        mainFrame.add(dataScroll, BorderLayout.CENTER);
        mainFrame.setSize(1200, 800);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public void getStringData() {
        JFileChooser choice = new JFileChooser(System.getProperty("user.dir"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Documents (*.txt)", "txt");
        choice.setFileFilter(filter);
        choice.setDialogTitle("Select a file to send");
        int option = choice.showOpenDialog(null);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = choice.getSelectedFile();
            byte[] fileData;
            try {
                fileData = Files.readAllBytes(file.toPath());
                try (FileInputStream in = new FileInputStream(file)) {
                    in.read(fileData);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "File Not Found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dataLabel.setText("<html><br><br>Data From File: <br>");

            data = "";
            for (byte b : fileData) {
                dataLabel.setText(dataLabel.getText() + (char) b);
                data += getBits(b);
            }

            dataLabel.setText(dataLabel.getText() + "<br><br>Data in Binary:<br>" + data + "</html>");

            addArea();
            userData.add(dataLabel);
            addArea();
            drawGraph();
        }
    }

    public String getBits(byte b) {
        String result = "";
        for (int i = 0; i < 8; i++)
            result += (b & (1 << i)) == 0 ? "0" : "1";

        return result;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnFile) {
            if (graphPanel.isShowing())
                removeGraph();

            getStringData();
        } else if (e.getSource() == btnBinary) {
            if (graphPanel.isShowing())
                removeGraph();

            if (txtBinary.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Empty Fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            data = "";
            for (int i = 0; i < txtBinary.getText().length(); i++) {
                if (txtBinary.getText().charAt(i) != '0' && txtBinary.getText().charAt(i) != '1') {
                    JOptionPane.showMessageDialog(this, "Invalid Binary Data!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else
                    data += txtBinary.getText().charAt(i) - 48;
            }

            dataLabel.setText("<html><br><br><br>Data in Binary:<br>" + data + "</html>");
            addArea();
            userData.add(dataLabel);
            addArea();
            drawGraph();
        }
    }

    public void addArea() {
        JTextArea area1 = new JTextArea();
        area1.setBackground(Color.WHITE);
        area1.setEditable(false);
        JTextArea area2 = new JTextArea();
        area2.setBackground(new Color(0xF0F0F0)); // Light gray
        area2.setEditable(false);
        JTextArea area3 = new JTextArea();
        area3.setBackground(new Color(0xF0F0F0)); // Light gray
        area3.setEditable(false);
        JTextArea area4 = new JTextArea();
        area4.setBackground(new Color(0xF0F0F0)); // Light gray
        area4.setEditable(false);
        userData.add(area1);
        userData.add(area2);
        userData.add(area3);
        userData.add(area4);
    }

    public void removeGraph() {
        userData.removeAll();
        mainFrame.remove(userData);
        mainFrame.remove(graphPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void drawGraph() {
        graphWidth = (data.length() * 80);

        graphScroll = new JScrollPane(new GetGraph());
        graphScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        graphScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        graphPanel = new JPanel(new BorderLayout());
        graphPanel.add(graphScroll, BorderLayout.CENTER);

        mainFrame.add(graphPanel, BorderLayout.SOUTH);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public class GetGraph extends JPanel {

        GetGraph() {
            this.setPreferredSize(new Dimension(graphWidth, 300));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g.drawString("Manchester Encoded Signal:", 10, 30);

            int period = 80;
            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == '1') {
                    g.setColor(graphLineColor1);
                    g.drawString(data.charAt(i) + "", i * period + 35, 80);
                    g.drawLine(i * period, 200, i * period + 40, 200);
                    g.drawLine(i * period + 40, 200, i * period + 40, 120);
                    g.drawLine(i * period + 40, 120, i * period + 80, 120);
                } else if (data.charAt(i) == '0') {
                    g.setColor(graphLineColor0);
                    g.drawString(data.charAt(i) + "", i * period + 35, 80);
                    g.drawLine(i * period, 120, i * period + 40, 120);
                    g.drawLine(i * period + 40, 120, i * period + 40, 200);
                    g.drawLine(i * period + 40, 200, i * period + 80, 200);
                }

                g.setColor(Color.BLACK);
                if (i + 1 < data.length())
                    if (data.charAt(i) == data.charAt(i + 1))
                        g.drawLine(i * period + 80, 200, i * period + 80, 120);

                g.drawLine(0, 160, graphWidth, 160);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ManchesterEncoding(); // Create and display the Manchester Encoding GUI
            }
        });
    }
}
