package controller;

import view.AppMainWindow;

public class Controller
{
    private AppMainWindow appMainWindow;

    public void start()
    {
        appMainWindow = new AppMainWindow();

        appMainWindow.initializeStartButton();
    }
}
