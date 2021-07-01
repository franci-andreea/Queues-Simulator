package controller;

import application.FileWriter;
import model.Client;
import model.Server;
import view.AppMainWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationManager implements Runnable
{
    private int numberOfServers;
    private int numberOfClients;
    private int maximumRunningTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int minProcessingTime;
    private int maxProcessingTime;
    private int currentTime = 0;

    private float averageWaitingTime = 0;
    private int peakHour = 0;
    private float averageServiceTime = 0;
    private int maximumClients = 0;

    private Scheduler scheduler;

    private AppMainWindow frame;

    private List<Client> generatedClients = new ArrayList<>();
    private List<Client> generatedClientsDuplicate = new ArrayList<>();

    /**
     * Constructor used to intitialize some of the fileds of the SimulationManager class
     * @param frame - a JFrame object representing the graphical user interface's frame where the content is being written
     * @param numberOfServers - an integer representing the number of servers available for the simulation
     * @param numberOfClients - an integer representing the number of clients available for the simulation
     * @param maximumRunningTime - an integer representing the number of seconds a simulation will be running for
     * @param minArrivalTime - an integer representing the minimum time a client will appear to be served
     * @param maxArrivalTime - an integer representing the maximum time a client can come to be served
     * @param minProcessingTime - an integer representing the minimum amount of seconds a client can take in order to be served
     * @param maxProcessingTime - an integer representig the maximum amount of seconds a client can take in order to be served
     */
    public SimulationManager(AppMainWindow frame, int numberOfServers, int numberOfClients, int maximumRunningTime, int minArrivalTime, int maxArrivalTime, int minProcessingTime, int maxProcessingTime)
    {
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.maximumRunningTime = maximumRunningTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minProcessingTime = minProcessingTime;
        this.maxProcessingTime = maxProcessingTime;
        scheduler = new Scheduler(numberOfServers, numberOfClients);
        this.frame = frame;
    }

    /**
     * the run method which the main thread (the whole simulation) executes
     * it focuses on the following approach:
     *  - it generates the required list of random clients
     *  - it starts all the servers (threads) with the help of the scheduler
     *  - while the simulation time has not ended
     *      - look for the clients that are supposed to come at the current moment
     *      - add them to the queues according to the criteria\
     *      - update the current time
     *      - put the main thread on sleep for one second to simulate the time spent
     *      - remove the clients added to the queues from the list of generated clients
     */
    @Override
    public void run()
    {
        try
        {
            FileWriter fileWriter = new FileWriter();
            int totalServiceTime = 0;

            this.generateNRandomClients();
            scheduler.startThreads();

            generatedClientsDuplicate.addAll(generatedClients);

            while(currentTime <= maximumRunningTime)
            {
                System.out.println("second: " + currentTime);
                for(Client currentClient : generatedClients)
                {
                    if(currentClient.getArrivalTime() == currentTime && checkAClientCanBeServed(currentClient))
                    {
                        totalServiceTime = totalServiceTime + currentClient.getServiceTime();
                        scheduler.addClientToAQueue(currentClient);
                        generatedClientsDuplicate.remove(currentClient);
                    }
                    else if(!checkAClientCanBeServed(currentClient))
                        generatedClientsDuplicate.remove(currentClient);
                }

                for(int i = 0; i < scheduler.getServers().size(); ++i)
                    scheduler.getServers().get(i).prettyPrintQueue();

                System.out.println();

                int allClientsInQueues = 0;
                for(int i = 0; i < scheduler.getServers().size(); ++i)
                {
                    allClientsInQueues = allClientsInQueues + scheduler.getServers().get(i).getQueueOfClients().size();
                }

                if(allClientsInQueues > maximumClients)
                {
                    maximumClients = allClientsInQueues;
                    peakHour = currentTime;
                }

                generatedClients.clear();
                generatedClients.addAll(generatedClientsDuplicate);

                fileWriter.writeLogOfEvents(this);
                frame.writeLogInTextArea();
                currentTime++;
                Thread.sleep(1000);
            }

            int totalWaitingTime = 0;
            int totalNumberOfClients = 0;

            for(Server server : scheduler.getServers())
            {
                totalWaitingTime = totalWaitingTime + server.getTotalWaitingTime();
                totalNumberOfClients = totalNumberOfClients + server.getNumberOfClientsServed();
            }

            averageWaitingTime = (float)totalWaitingTime/totalNumberOfClients;
            averageServiceTime = (float)totalServiceTime/numberOfClients;

            fileWriter.writeAnalytics(this);

            scheduler.endThreads();

        } catch (InterruptedException | IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * method used to check if a client can still be served
     * @param client - the client we need to see if it can be served
     * @return a boolean value - true in case it can, otherwise false
     */
    public boolean checkAClientCanBeServed(Client client)
    {
        return client.getServiceTime() + currentTime < maximumRunningTime;
    }

    /**
     * method to generate random clients (with random values for each field)
     * at the end of the method, after sorting all the clients, their IDs were modified such that there is an order among them
     */
    public void generateNRandomClients()
    {
        int processingTime;
        int arrivalTime;

        for(int i = 0; i < numberOfClients; ++i)
        {
            Random random = new Random();
            processingTime = random.nextInt((maxProcessingTime - minProcessingTime) + 1) + minProcessingTime;
            arrivalTime = random.nextInt((maxArrivalTime - minArrivalTime) + 1) + minArrivalTime;

            generatedClients.add(new Client(i + 1, arrivalTime, processingTime));
        }

        generatedClients.sort(new Client());

        for(int i = 0; i < generatedClients.size(); ++i)
        {
            generatedClients.get(i).setID(i + 1);
        }
    }

    /**
     * useful getters that helped me in my implementation
     */
    public int getNumberOfServers() {
        return numberOfServers;
    }

    public Scheduler getScheduler() { return scheduler; }

    public List<Client> getGeneratedClients() { return generatedClients; }

    public int getCurrentTime() { return currentTime; }

    public float getAverageWaitingTime()
    {
        return averageWaitingTime;
    }

    public int getPeakHour()
    {
        return peakHour;
    }

    public float getAverageServiceTime()
    {
        return averageServiceTime;
    }

}
