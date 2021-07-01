package model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable
{
    private BlockingQueue<Client> queueOfClients;
    private AtomicInteger processingQueueTime;

    private int totalWaitingTime = 0;
    private int numberOfClientsServed = 0;

    /**
     * Constructor used to intialize some fields of the Server class
     * @queueOfClients - a BlockingQueue representing the clients waiting in line for being served at a server
     * @processingQueueTime - an AtomicInteger representing the total time required for a queue to become empty (to have
     *                        all the clients from that queue served)
     */
    public Server()
    {
        this.queueOfClients = new LinkedBlockingQueue<>();
        this.processingQueueTime = new AtomicInteger(0);
    }

    /**
     * method run executed by the created threads
     * it focuses on the following approach:
     *  - it takes the first client from the queue
     *  - it is put on sleep for the amount of seconds it takes to serve the client (serviceTime seconds)
     *  - the waiting time of the other clients (if any) remaining in the queue is updated (for other purposes)
     *  - after the client is served, it is removed from the queue
     */
    @Override
    public void run()
    {
        while(true)
        {
            Client currentClient;
            try
            {
                if (!queueOfClients.isEmpty())
                {
                    currentClient = queueOfClients.peek();
                    if (currentClient != null)
                    {
                        while (currentClient.getServiceTime() > 0)
                        {
                            Thread.sleep(1000);
                            currentClient.setServiceTime(currentClient.getServiceTime() - 1);
                            for(Client client : queueOfClients)
                            {
                                if(client != currentClient)
                                {
                                    client.updateWaitingTime();
                                }
                            }
                        }
                    }
                    processingQueueTime.addAndGet(-currentClient.getServiceTime());

                    totalWaitingTime = totalWaitingTime + currentClient.getWaitingInQueueTime();
                    numberOfClientsServed++;

                    queueOfClients.poll();
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * method to add a client to a queue
     * @param client - the client that has to be added
     */
    public void addClient(Client client)
    {
        queueOfClients.add(client);
    }

    /**
     * useful method to verify my implementation in the console
     * it is used to print in a nice way the queues and the clients at each second (moment) of the simulation
     */
    public void prettyPrintQueue()
    {
        System.out.print("Queue: ");
        for(Client client : queueOfClients)
        {
            System.out.print("Client(" + client.getID() + ", " + client.getArrivalTime() + ", " + client.getServiceTime() + "), ");
        }
        System.out.println();
    }

    /**
     * useful getters that helped me in my implementation
     */
    public BlockingQueue<Client> getQueueOfClients()
    {
        return queueOfClients;
    }

    public AtomicInteger getProcessingQueueTime()
    {
        return processingQueueTime;
    }

    public int getTotalWaitingTime()
    {
        return totalWaitingTime;
    }

    public int getNumberOfClientsServed()
    {
        return numberOfClientsServed;
    }

}
