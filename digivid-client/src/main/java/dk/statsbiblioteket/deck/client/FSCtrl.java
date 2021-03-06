package dk.statsbiblioteket.deck.client;

import org.apache.log4j.Logger;
import dk.statsbiblioteket.deck.Constants;
import dk.statsbiblioteket.deck.rmiInterface.compute.Compute;

import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: bytestroop
 * Date: Nov 23, 2006
 * Time: 11:12:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class FSCtrl {

    //"start","record",userName,clientHostIP,encoderIP,cardName,captureFormat,captureLength,captureSize,fileName

    //todo: make this an argument
    //private static String host = "//node02.portend.net";
    //private static String host = "encoder1.sb.statsbiblioteket.dk";
    //private static String hostIP = "172.18.249.253"; //encoder1.sb.statsbiblioteket.dk
    static Logger log = Logger.getLogger(FSCtrl.class.getName());

    //private static String prop;
    //private static int propInt;
    //private static final String configure = Constants.DEFAULT_CLIENTCONF_DIRECTORY + "/adminRMIClient.xml";
    //private static final String configureServer = Constants.DEFAULT_SERVERCONF_DIRECTORY + "/server.xml";

    final int encoderRMIport = Constants.DEFAULT_RMI_CLIENT_PORT ;

    private String command;
    private String recordType;
    private String userName;
    private String clientHostIP;
    private String encoderIP;
    private int cardName;
    private String channelID;
    private int frameWidth;
    private int frameHeight;
    private String captureFormat;
    private int captureBitrate;
    private int captureMaxBitrate;
    private String fileName;
    private String recordSerial;
    private long origStartDate;
    private long captureTime;
    private int captureSize;
    private String extension;


    /**
     *
     * @param encoderIP
     */
    public FSCtrl(String encoderIP) {
        this.encoderIP = encoderIP;
        this.extension = Constants.DEFAULT_EXTENSION;

        //log.debug("File Name" + fileName);
    };

     /**
     * Construct a task to operate the server to the specified
     * control command.
     */
     public FSCtrl(String recordType,
                   String userName,
                   String clientHostIP,
                   String encoderIP,
                   int cardName,
                   String channelID,
                   String fileName,
                   String recordSerial,
                   long origStartDate,
                   long captureTime,
                   int captureSize) {

        this.recordType = recordType;
        this.userName = userName;
        this.clientHostIP = clientHostIP;
        this.encoderIP = encoderIP;
        this.cardName = cardName;
        this.channelID = channelID;
        this.fileName = fileName;
        this.recordSerial = recordSerial;
        this.origStartDate = origStartDate;
        this.captureTime= captureTime;
        this.captureSize= captureSize;

        log.debug("RMI_Port: " + encoderRMIport);
        log.debug("RMI_IP: " + encoderIP);
        log.debug("Recording started on Encoder: " + command);
        log.debug("Recording Type: " + recordType);
        log.debug("Recording Person: " + userName);
        log.debug("Recording started from Host_IP: " + clientHostIP);
        log.debug("Recording started on Encoder: " + encoderIP);
        log.debug("Recording started on Device: " + cardName);
        log.debug("Recording File name: " + fileName);
        log.debug("Recording Serial: " + recordSerial);
        log.debug("Recording expected duration: " + captureTime);

     }

    public List<ArrayList> getFileListUnix() throws RemoteException {

        System.out.println("Bind dk.statsbiblioteket.deck.client.RecorderCtrl");
        if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
        }

        LocateRegistry.getRegistry (encoderRMIport);

        String name = "//" + encoderIP + ":" +encoderRMIport+ "/Compute";
        System.out.println("Client looks up name address: " + name);

        Compute comp = null;
        try {
            comp = (Compute) Naming.lookup(name);

        } catch (NotBoundException nb) {
            System.out.println("Not Bound");
            nb.printStackTrace();
        } catch (MalformedURLException murl) {
            System.out.println("Check URL");
            murl.printStackTrace();
        } catch (RemoteException re) {
            re.printStackTrace();
        }

        System.out.println("Execute the Command...");

        UnixFileLister task = new UnixFileLister(extension);

        List fileList = new ArrayList() ;

        try {
            fileList = (ArrayList) (comp.executeTask(task));
        } catch (RemoteException e1) {
            log.error(" Failure, Task not executed! ", e1);
            e1.printStackTrace();
        }
        System.out.println("ok");
        return fileList;

    }

    public Integer getProgress() throws RemoteException {

        System.out.println("Bind dk.statsbiblioteket.deck.client.FSCtrl");
        if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
        }

        LocateRegistry.getRegistry (encoderRMIport);

        String name = "//" + encoderIP + ":" +encoderRMIport+ "/Compute";
        System.out.println("Client looks up name address: " + name);

        Compute comp = null;
        try {
            comp = (Compute) Naming.lookup(name);
        } catch (NotBoundException nb) {
            System.out.println("Not Bound");
            nb.printStackTrace();
        } catch (MalformedURLException murl) {
            System.out.println("Check URL");
            murl.printStackTrace();
        } catch (RemoteException re) {
            re.printStackTrace();
        }

        System.out.println("Execute the Command...");

        System.out.println(captureTime);

        FileProgress task = new FileProgress(fileName,recordSerial,captureTime);

        Integer percentage = 0;
        try {
            percentage = (Integer) (comp.executeTask(task));

        } catch (RemoteException e1) {
            log.error(" Failure, Task not executed! ", e1);
            e1.printStackTrace();
        }
        System.out.println("ok " + percentage +"%");
        return percentage;

    }

    /**
     * Returns a list of which each entry is an array.
     * The first element is the file name and the second is the
     * file length in units of 1024 bytes.
     * @return
     * @throws RemoteException
     */
    public List<String[]> getFileInfo() throws RemoteException {

        System.out.println("Bind dk.statsbiblioteket.deck.client.RecorderCtrl");
        if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
        }

        LocateRegistry.getRegistry (encoderRMIport);

        String name = "//" + encoderIP + ":" +encoderRMIport+ "/Compute";
        System.out.println("Client looks up name address: " + name);

        Compute comp = null;
        try {
            comp = (Compute) Naming.lookup(name);
        } catch (NotBoundException nb) {
            System.out.println("Not Bound");
            nb.printStackTrace();
            throw new RuntimeException(nb);
        } catch (MalformedURLException murl) {
            System.out.println("Check URL");
            murl.printStackTrace();
            throw new RuntimeException(murl);
        } catch (RemoteException re) {
            re.printStackTrace();
            throw new RuntimeException(re);
        }

        System.out.println("Execute the Command...");

        FileLister task = new FileLister(extension);

        List<String[]> fileInfo = new ArrayList<String[]>() ;
        try {
            fileInfo = (List<String[]>)(comp.executeTask(task));
        } catch (RemoteException e1) {
            log.error(" Failure, Task not executed! ", e1);
            e1.printStackTrace();
        }
        System.out.println("ok");
        return fileInfo;

    }

    public boolean isEven(int number) {
        if (number % 2 == 0) {
          return true;
        }
        else return false;
    }

    public boolean isOdd(int number) {
        if(! isEven(number));
        return true;
    }

    private String getMoment() {
        long now = System.currentTimeMillis();
        String startTime = String.valueOf(now);
        return startTime;
    }
}
