package controller;

import model.Client;
import model.Server;

import java.util.ArrayList;
import java.util.List;

public class Scheduler
{
    private List<Server> servers;
    private List<Thread> serversThreads;

    private int numberOfServers;
    private int maximumClientsPerQueue;

    /**
     * Constructor used to intitialize some fields of the Scheduler class
     * @param numberOfServers - an integer representing the number of servers are available for the simulation
     * @param maximumClientsPerQueue - and integer representing the maximum size a queue of a server can have
     */
    public Scheduler(int numberOfServers, int maximumClientsPerQueue)
    {
        this.numberOfServers = numberOfServers;
        this.maximumClientsPerQueue = maximumClientsPerQueue;

        servers = new ArrayList<>();
        serversThreads = new ArrayList<>();

        for(int i = 0; i < numberOfServers; ++i)
        {
            Server server = new Server();
            Thread serverThread = new Thread(server);
            servers.add(server);
            serversThreads.add(serverThread);
        }
    }

    /**
     * method used to start all the threads (each representing a server) at the same time
     */
    public void startThreads()
    {
        for(Thread thread : serversThreads)
        {
            if(!thread.isAlive())
            {
                thread.start();
            }
        }
    }

    /**
     * method used to end all of the threads at the same time
     */
    public void endThreads()
    {
        for(Thread thread : serversThreads)
            thread.stop();

    }

    /**
     * method to add a client to a queue according to the SHORTEST_TIME criteria
     * in this method we are looking for the queue with the smallest processig time at that moment (which one takes the
     * smallest time in seconds to be finished serving all the clients
     * @param client - the client that has to be added to the queue
     */
    public void addClientToAQueue(Client client)
    {
        int minimumProcessingTime = Integer.MAX_VALUE;
        int minimumIndex = -1;

        for(int i = 0; i < servers.size(); ++i)
        {
            if(servers.get(i).getProcessingQueueTime().get() < minimumProcessingTime)
            {
                minimumProcessingTime = servers.get(i).getProcessingQueueTime().get();
                minimumIndex = i;
            }
        }

        servers.get(minimumIndex).addClient(client);
        servers.get(minimumIndex).getProcessingQueueTime().addAndGet(client.getServiceTime());
    }

    /**
     * useful getters that helped me in my implementation
     */
    public List<Server> getServers()
    {
        return servers;
    }
}
