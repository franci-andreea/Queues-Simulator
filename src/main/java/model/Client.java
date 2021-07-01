package model;

import java.util.Comparator;

public class Client implements Comparator<Client>
{
    private int ID;
    private int arrivalTime;
    private int serviceTime;
    private int waitingInQueueTime;

    /**
     * Constructor which initializes the fields of the Client class
     * @param ID - an integer representing the identification value for each client
     * @param arrivalTime - an integer representing the second (moment in the simulation)
     *                      the client is supposed to arrive at the store
     * @param serviceTime - an integer representing the amount of time required to serve the client when it gets to the
     *                      server
     */
    public Client(int ID, int arrivalTime, int serviceTime)
    {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    /**
     * empty constructor used for initialization
     */
    public Client(){}

    /**
     * method toString used to print in a nice way the details of the client
     * @return a string composed of the details attached to the noun Client
     */
    @Override
    public String toString()
    {
        return "Client(" + ID +
                ", " + arrivalTime +
                ", " + serviceTime +
                ')';
    }

    /**
     * method used to compare the clients based on their arrival time in order to sort them in the list
     * @param client1 - the first client to compare
     * @param client2 - the second client to compare
     * @return an integer representing the difference between their arrival time
     */
    @Override
    public int compare(Client client1, Client client2)
    {
        return client1.arrivalTime - client2.arrivalTime;
    }

    /**
     * method used to update the waiting time of a client
     * waiting time = the time a client spends waiting in the line (not being served, but being already in a queue)
     */
    public void updateWaitingTime()
    {
        waitingInQueueTime++;
    }

    /**
     * useful getters and setters that helped me in the implementation
     */
    public int getID()
    {
        return ID;
    }

    public void setID(int ID)
    {
        this.ID = ID;
    }

    public int getArrivalTime()
    {
        return arrivalTime;
    }

    public int getServiceTime()
    {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime)
    {
        this.serviceTime = serviceTime;
    }

    public int getWaitingInQueueTime()
    {
        return waitingInQueueTime;
    }

}
