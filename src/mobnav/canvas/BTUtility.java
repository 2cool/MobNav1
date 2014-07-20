package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */
import javax.bluetooth.*;
import java.util.*;


   public class BTUtility extends Thread implements DiscoveryListener {
        Vector remoteDevices = new Vector();
        Vector deviceNames = new Vector();
        DiscoveryAgent discoveryAgent;
        // obviously, 0x1101 is the UUID for
        // the Serial Profile
        UUID[] uuidSet = {new UUID(0x1101) };
        // 0x0100 is the attribute for the service name element
        // in the service record
        int[] attrSet = {0x0100};
        public BTUtility() {         
            try {
                LocalDevice localDevice = LocalDevice.getLocalDevice();
                discoveryAgent = localDevice.getDiscoveryAgent();
               // discoveryForm.append("BTUtility() Searching ...\n");
                discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
            } catch(Exception e) {
                
            }
        }
        public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass cod) {
            remoteDevices.addElement(remoteDevice);
           
        }
        public void inquiryCompleted(int discType) {
            if (remoteDevices.size() > 0) {
                // the discovery process was a success
                // so let's out them in a List and display it to the user
                for (int i=0; i<remoteDevices.size(); i++){
                    try{
                       Interface.devicesList.append(((RemoteDevice)remoteDevices.elementAt(i)).getFriendlyName(true), null);
                    } catch (Exception e){
                       Interface.devicesList.append(((RemoteDevice)remoteDevices.elementAt(i)).getBluetoothAddress(), null);
                    }
                }
                 synchronized(Interface.devicesListMon){
                    Interface.devicesListMon.notify();
                }
                //display.setCurrent(i.devicesList);
            } else {
			// handle this
		}
        }
        public void run(){
            try {
                RemoteDevice remoteDevice = (RemoteDevice)remoteDevices.elementAt(Interface.devicesList.getSelectedIndex());
                discoveryAgent.searchServices(attrSet, uuidSet, remoteDevice , this);

            } catch(Exception e) {
                
            }
        }
        public void servicesDiscovered(int transID, ServiceRecord[] servRecord){
            for(int i = 0; i < servRecord.length; i++) {
              ///  DataElement serviceNameElement = servRecord[i].getAttributeValue(0x0100);
               // String _serviceName = (String)serviceNameElement.getValue();
               // String serviceName = _serviceName.trim();
                Interface.btConnectionURL = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                System.out.println(Interface.btConnectionURL);
                synchronized(Interface.devicesListMon){
                    Interface.devicesListMon.notify();
                }

            }
          //  display.setCurrent(readyToConnectForm);
		//readyToConnectForm.append("\n\nNote: the connection URL is: " + btConnectionURL);
        }
        public void serviceSearchCompleted(int transID, int respCode) {
            if (respCode == DiscoveryListener.SERVICE_SEARCH_COMPLETED) {
                // the service search process was successful
            } else {
                // the service search process has failed
            }
        }
   }
