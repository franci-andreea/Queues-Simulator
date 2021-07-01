package view;

import application.FileWriter;
import controller.SimulationManager;
import model.Client;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionListener;
import java.io.IOException;

public class AppMainWindow
{
    private JFrame mainFrame = new JFrame("Queues Simulation Manager");

    private JPanel inputPanel;
    private JPanel clientPanel;
    private JPanel outputPanel;

    private JButton startSimulationButton;

    private JTextArea outputArea;

    private JLabel numberOfClientsLabel = new JLabel("Number of clients (N):");
    private JTextField numberOfClientsField;
    private JLabel numberOfQueuesLabel = new JLabel("Number of queues (Q):");
    private JTextField numberOfQueuesField;
    private JLabel simulationTimeLabel = new JLabel("Simulation maximum running time:");
    private JTextField simulationTimeField;

    private JLabel minArrivalTimeLabel = new JLabel("Minimum Arrival Time");
    private JTextField minArrivalTimeField;
    private JLabel maxArrivalTimeLabel = new JLabel("Maximum Arrival Time");
    private JTextField maxArrivalTimeField;
    private JLabel minServiceTimeLabel = new JLabel("Minimum Service Time");
    private JTextField minServiceTimeField;
    private JLabel maxServiceTimeLabel = new JLabel("Maximum Service Time");
    private JTextField maxServiceTimeField;

    SimulationManager simulationManager;

    /**
     * Constructor to intialize the graphical user interface components
     */
    public AppMainWindow()
    {
        inputPanel = new JPanel();
        clientPanel = new JPanel();
        outputPanel = new JPanel();

        inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Details"));
        clientPanel.setBorder(BorderFactory.createTitledBorder("Clients' Details"));

        inputPanel.setLayout(new GridLayout(0, 2, 15, 15));
        clientPanel.setLayout(new GridLayout(0, 4));
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));

        numberOfClientsField = new JTextField();
        numberOfQueuesField = new JTextField();
        simulationTimeField = new JTextField();

        inputPanel.add(numberOfClientsLabel);
        inputPanel.add(numberOfClientsField);

        inputPanel.add(numberOfQueuesLabel);
        inputPanel.add(numberOfQueuesField);

        inputPanel.add(simulationTimeLabel);
        inputPanel.add(simulationTimeField);

        minArrivalTimeField = new JTextField();
        maxArrivalTimeField = new JTextField();
        minServiceTimeField = new JTextField();
        maxServiceTimeField = new JTextField();

        clientPanel.add(minArrivalTimeLabel);
        clientPanel.add(minArrivalTimeField);
        clientPanel.add(maxArrivalTimeLabel);
        clientPanel.add(maxArrivalTimeField);
        clientPanel.add(minServiceTimeLabel);
        clientPanel.add(minServiceTimeField);
        clientPanel.add(maxServiceTimeLabel);
        clientPanel.add(maxServiceTimeField);

        startSimulationButton = new JButton("START SIMULATION");

        outputArea = new JTextArea(20, 20);

        outputPanel.add(startSimulationButton);
        outputPanel.add(outputArea);

        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(inputPanel, BorderLayout.LINE_START);
        mainFrame.add(clientPanel, BorderLayout.CENTER);
        mainFrame.add(outputPanel, BorderLayout.PAGE_END);
        mainFrame.setVisible(true);
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Method used to initialize the ActionListener on the START SIMULATION button
     * it extracts the input introduced by the user and converts it to integers
     * it also checks if the values introduced are valid and make sense for a simulation
     */
    public void initializeStartButton()
    {
        this.addStartButtonPressedActionListener(e -> {
            int numberOfClients = Integer.parseInt(numberOfClientsField.getText());
            int numberOfServers = Integer.parseInt(numberOfQueuesField.getText());
            int simulationRunningTime = Integer.parseInt(simulationTimeField.getText());
            int minimumArrivalTime = Integer.parseInt(minArrivalTimeField.getText());
            int maximumArrivalTime = Integer.parseInt(maxArrivalTimeField.getText());
            int minimumServiceTime = Integer.parseInt(minServiceTimeField.getText());
            int maximumServiceTime = Integer.parseInt(maxServiceTimeField.getText());

            if(numberOfClients < 0 || numberOfServers < 0 || simulationRunningTime < 0 || minimumArrivalTime < 0 || maximumArrivalTime < 0 || minimumServiceTime < 0 || maximumServiceTime < 0)
            {
                JOptionPane.showMessageDialog(mainFrame, "Invalid inputs; The values cannot be negative", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            if(minimumArrivalTime > maximumArrivalTime)
            {
                JOptionPane.showMessageDialog(mainFrame, "Minimum arrival time cannot be greater than the maximum arrival time", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            if(minimumServiceTime > maximumServiceTime)
            {
                JOptionPane.showMessageDialog(mainFrame, "Minimum service time cannot be greater that the maximum service time", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            if(minimumArrivalTime >= simulationRunningTime || maximumArrivalTime >= simulationRunningTime)
            {
                JOptionPane.showMessageDialog(mainFrame, "The bounds for the arrival time are not correct, they should be less than the simulation running time", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            simulationManager = new SimulationManager(this, numberOfServers, numberOfClients, simulationRunningTime, minimumArrivalTime, maximumArrivalTime, minimumServiceTime, maximumServiceTime);
            FileWriter fileWriter = new FileWriter();

            try
            {
                fileWriter.deleteContent();
            } catch (IOException ioException)
            {
                ioException.printStackTrace();
            }

            Thread simulationThread = new Thread(simulationManager);
            simulationThread.start();

        });
    }

    /**
     * method to add an action listener on the START SIMULATION button
     * @param actionListener
     */
    public void addStartButtonPressedActionListener(ActionListener actionListener)
    {
        startSimulationButton.addActionListener(actionListener);
    }

    /**
     * method that writes in the text area from the graphical user interface the log event at each second
     * this method overwrites each state (at each second) on the same text area, it does not append to the previous one
     */
    public void writeLogInTextArea()
    {
        outputArea.setText("");
        outputArea.append("Time: " + simulationManager.getCurrentTime() + "\n");
        outputArea.append("Waiting clients: ");

        for(Client client : simulationManager.getGeneratedClients())
            outputArea.append(client.toString() + "; ");
        outputArea.append("\n");

        for(int i = 0; i < simulationManager.getNumberOfServers(); ++i)
        {
            outputArea.append("Queue " + (i + 1) + ": ");
            for(Client client : simulationManager.getScheduler().getServers().get(i).getQueueOfClients())
            {
                outputArea.append("Client(" + client.getID() + ", " + client.getArrivalTime() + ", " + client.getServiceTime() + "); ");
            }
            outputArea.append("\n");
        }

        outputArea.append("\n");
    }
}
