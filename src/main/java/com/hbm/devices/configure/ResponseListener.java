package com.hbm.devices.configure;

import java.util.Observable;
import java.util.Observer;

import com.hbm.devices.scan.MessageParser;
import com.hbm.devices.scan.ResponseReceiver;
import com.hbm.devices.scan.messages.Response;

/**
 * This class is used to start a thread which listens to incoming messages. When a message is
 * received, it is parsed into an object via {@link MessageParser}. If this object is a
 * {@link Response} object, it is forwarded to all {@link Observer}.
 * 
 * @since 1.0
 *
 */
public class ResponseListener extends Observable implements Observer, Runnable {

    private ResponseReceiver responseReceiver;
    private MessageParser messageParser;

    /**
     * This is the default constructor to instantiate a ReponseListener
     */
    public ResponseListener() {
        try {
            responseReceiver = new ResponseReceiver();

            messageParser = new MessageParser();

            responseReceiver.addObserver(messageParser);
            messageParser.addObserver(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Don't use this constructor. It is only used for the JUnit tests
     * 
     */
    @SuppressWarnings("unused")
    private ResponseListener(Observable receiver) {
        messageParser = new MessageParser();

        receiver.addObserver(messageParser);
        messageParser.addObserver(this);
    }

    /**
     * This method starts the listening socket to receive incoming responses
     */
    @Override
    public void run() {
        responseReceiver.start();
    }

    /**
     * This method stops the listening socket
     */
    public void stop() {
        responseReceiver.stop();
    }

    /**
     * This method filters the received and parsed packets. So only {@link Response} objects are
     * forwarded.
     */
    @Override
    public void update(Observable o, Object obj) {
        if (obj instanceof Response) {
            Response response = (Response) obj;

            setChanged();
            notifyObservers(response);
        }
    }
}
