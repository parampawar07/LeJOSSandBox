package LeJOSSandBox;

import coppelia.IntWA;
import coppelia.remoteApi;


//Make sure to have the server side running in V-REP: 
//in a child script of a V-REP scene, add following command
//to be executed just once, at simulation start:
//
//simExtRemoteApiStart(19999)
//
//then start simulation, and run this program.
//
//IMPORTANT: for each successful call to simxStart, there
//should be a corresponding call to simxFinish at the end!

public class VrepConnection {
	int clientID;
	remoteApi vrep;
	
	public int VrepConnection_get_ID(){
		return clientID;
	}
	public remoteApi VrepConnection_get_object(){
		return vrep;
	}
	
	public VrepConnection(){
		VrepConnection_create();
	}
	
	private void VrepConnection_create(){
		vrep = new remoteApi();
		System.out.println("Program started");
		int error=0;
        vrep.simxFinish(-1); // just in case, close all opened connections
        
        clientID = vrep.simxStart("127.0.0.1",19999,true,true,5000,5);
        if (clientID!=-1){
        	System.out.println("Connected to remote API server");         	
       
            // Now try to retrieve data in a blocking fashion (i.e. a service call):
            IntWA objectHandles = new IntWA(1);
            int ret=vrep.simxGetObjects(clientID,vrep.sim_handle_all,objectHandles,vrep.simx_opmode_blocking);
            if (ret==vrep.simx_return_ok)
                System.out.format("Number of objects in the scene: %d\n",objectHandles.getArray().length);
            else
                System.out.format("Remote API function call returned with error code: %d\n",ret);
            try
            {
                Thread.sleep(2000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        
        
        }       
        else {
            System.out.println("Failed connecting to remote API server");
            System.out.println("Program ended");
        }	        
	}
	
	public void VrepConnection_Close(){
		vrep.simxFinish(clientID);
		System.out.println("Connection is closed");
        System.out.println("Program ended");
	}
}
