package LeJOSSandBox;

import coppelia.FloatWA;
import coppelia.IntW;

public class EV3TouchSensor
{
	private boolean currentMode = false;
	private IntW Sensor_handle;
	private String port;
    
    public EV3TouchSensor(String port)
    {
  		currentMode = true;
		
		this.port = port;
        
        init();
    }

    protected void init() {
    	
    	int error;
		currentMode = true;
		
		Sensor_handle = new IntW(0);
		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetObjectHandle(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), this.port, Sensor_handle, VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_oneshot_wait);	

		if (error == 0) {
			currentMode = true;
		
            FloatWA forceVector = new FloatWA(3);
            FloatWA torqueVector = new FloatWA(3);
			
			error = VrepConnectionFactory.getInstance().VrepConnection_get_object().
					simxReadForceSensor(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
							Sensor_handle.getValue(), 
							null, 
							forceVector, 
							torqueVector, 
							VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_streaming);
			
			System.out.println("INFO \t sensor link established");
		}
		else {
			System.out.println("ERROR \t sensor link failed");
		}
    }
 
    /**
     * <b>Lego EV3 Touch sensor, Touch mode</b><br>
     * Detects when its front button is pressed
     * 
     * <p>
     * <b>Size and content of the sample</b><br>
     * The sample contains one element, a value of 0 indicates that the button is not presse, a value of 1 indicates the button is pressed.
     * 
     * <p>
     * 
     * @return A sampleProvider
     * See {@link lejos.robotics.SampleProvider leJOS conventions for
     *      SampleProviders}
     */
    public SensorMode getTouchMode() {
    	return new TouchMode();
    }

	public int sampleSize() {
		return new TouchMode().sampleSize();
	}
	
    private class TouchMode implements SensorMode {
        @Override
        public int sampleSize()
        {
            return 1;
        }

        @Override
        public void fetchSample(float[] sample, int offset)
        {
        	int error;
			
            FloatWA forceVector = new FloatWA(3);
            FloatWA torqueVector = new FloatWA(3);
            
            do{
            	error = VrepConnectionFactory.getInstance().VrepConnection_get_object().
            			simxReadForceSensor(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
    							Sensor_handle.getValue(), 
    							null, 
    							forceVector, 
    							torqueVector, 
    							VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_buffer); 
            }while(error != 0);
            
            float return_value = 0;
            
            if((float)Math.sqrt(Math.pow((double)forceVector.getArray()[0],2)+Math.pow((double)forceVector.getArray()[1],2)+Math.pow((double)forceVector.getArray()[2],2)) > 0.1) {
            	return_value = 1;
            }
            
            sample[offset] = return_value;
        }

        @Override
        public String getName()
        {
           return "Touch";
        }
     
    }

}
