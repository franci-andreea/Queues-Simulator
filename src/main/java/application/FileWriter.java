package application;

import controller.SimulationManager;
import model.Client;

import java.io.IOException;

public class FileWriter
{
    /**
     * method used to write the log of events (at each second) in a .txt file
     * @param simulationManager - an object representing the simulation we have going on and from which we extract useful
     *                            data
     * @throws IOException
     */
    public void writeLogOfEvents(SimulationManager simulationManager) throws IOException
    {
        java.io.FileWriter fileWriter = new java.io.FileWriter("LogEvents.txt", true);

        fileWriter.write("Time: " + simulationManager.getCurrentTime() + "\n");
        fileWriter.write("Waiting clients: ");

        for(Client client : simulationManager.getGeneratedClients())
            fileWriter.write(client.toString() + "; ");

        fileWriter.write("\n");

        for(int i = 0; i < simulationManager.getNumberOfServers(); ++i)
        {
            fileWriter.write("Queue " + (i + 1) + ": ");

            if(simulationManager.getScheduler().getServers().get(i).getQueueOfClients().size() == 0)
                fileWriter.write("closed");

            for(Client client : simulationManager.getScheduler().getServers().get(i).getQueueOfClients())
            {
                fileWriter.write("Client(" + client.getID() + ", " + client.getArrivalTime() + ", " + client.getServiceTime() + "); ");
            }
            fileWriter.write("\n");
        }

        fileWriter.write("\n");

        fileWriter.close();
    }

    /**
     * method used to erase the previous content written in the previous run of the application
     * @throws IOException
     */
    public void deleteContent() throws IOException
    {
        java.io.FileWriter fileWriter = new java.io.FileWriter("LogEvents.txt");

        fileWriter.write("");

        fileWriter.close();
    }

    /**
     * method used to write the analytics of the whole simulation at the end of the .txt file
     * @param simulationManager - an object representing the simulation we have going on and from which we extract useful
     *                            data
     * @throws IOException
     */
    public void writeAnalytics(SimulationManager simulationManager) throws IOException
    {
        java.io.FileWriter fileWriter = new java.io.FileWriter("LogEvents.txt", true);

        fileWriter.write("Average Waiting Time: " + simulationManager.getAverageWaitingTime() + "\n");
        fileWriter.write("Average Service Time: " + simulationManager.getAverageServiceTime() + "\n");
        fileWriter.write("Peak Hour: " + simulationManager.getPeakHour() + "\n");

        fileWriter.close();
    }
}
