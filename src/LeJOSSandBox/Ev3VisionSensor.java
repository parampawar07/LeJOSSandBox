
package LeJOSSandBox;
import coppelia.IntW;

import java.util.concurrent.TimeUnit;

import LeJOSSandBox.VrepConnectionFactory;
import coppelia.FloatWAA;


public class Ev3VisionSensor
{
	private IntW handle;
	private String port;
    
    public Ev3VisionSensor(String port)
    {
    	this.port = port;
        init();
    }
    
    private void simxSetObjectIntParameter(int i, IntW handle2, int j, int k, int simx_opmode_streaming) {
				simxSetObjectIntParameter(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), handle2,1002,35,VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_streaming);
			// TODO Auto-generated method stub	
		
	} 
    public void init() {
    	
    	int error;
		
		handle = new IntW(0);
		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetObjectHandle(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), "Vision_sensor", handle, VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_oneshot_wait);	
		System.out.println("errorcode:"+error);
		if (error == 0) {
			FloatWAA auxValues= new FloatWAA(15);
				
			error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxReadVisionSensor(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), handle.getValue(), null, auxValues, VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_streaming);
							 	
			System.out.println("INFO \t sensor link established");
			System.out.println("inside error:" + error );
			System.out.println("clientID: " + VrepConnectionFactory.getInstance().VrepConnection_get_ID());
			System.out.println("sensor_handle: " + handle.getValue() );
			
		}
		else {
			System.out.println("ERROR \t sensor link failed");
		}
    }
    

		
		public float getSample() {
			System.out.println("in");
			int error;
			float colorValue;
			FloatWAA auxValues= new FloatWAA(15);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            do{	
            	error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxReadVisionSensor(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), handle.getValue(), null, auxValues, VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_streaming);
            	
            	try {
    				TimeUnit.SECONDS.sleep(1);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            	error = VrepConnectionFactory.getInstance().VrepConnection_get_object().
				simxReadVisionSensor(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
						handle.getValue(), 
						null, 
						auxValues,  
						VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_buffer);
            	
            	 System.out.println("Buffer error:"+error);
            	 System.out.println("Array length1 :" + auxValues.getArray()[0].getArray()[0]);
            	 System.out.println("Array length 2:" + auxValues.getArray()[0].getArray()[1]);
            	 System.out.println("Array length 3:" + auxValues.getArray()[0].getArray()[2]);
            	 System.out.println("Array length 4:" + auxValues.getArray()[0].getArray()[3]);
            	 System.out.println("Array length 5:" + auxValues.getArray()[0].getArray()[4]);
            	 System.out.println("Array length 6:" + auxValues.getArray()[0].getArray()[5]);
            	 System.out.println("Array length 7:" + auxValues.getArray()[0].getArray()[6]);
            	 System.out.println("Array length 8:" + auxValues.getArray()[0].getArray()[7]);
            	 System.out.println("Array length 9:" + auxValues.getArray()[0].getArray()[8]);
            	 System.out.println("Array length 10:" + auxValues.getArray()[0].getArray()[9]);
            } while(error != 0);
            colorValue = auxValues.getArray()[0].getArray()[0];
            //VrepConnectionFactory.getInstance().VrepConnection_get_object().simxFinish(VrepConnectionFactory.getInstance().VrepConnection_get_ID());
            
            System.out.println("inner");
            return colorValue;
		}

		
	}		
  	


