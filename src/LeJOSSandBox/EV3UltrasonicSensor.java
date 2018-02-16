package LeJOSSandBox;

import coppelia.IntW;
import coppelia.FloatWA;

public class EV3UltrasonicSensor {
	private boolean currentMode = false;
	private IntW Sensor_handle;
	private String port;

    /**
    * Create the Ultrasonic sensor class.
    * 
    * @param port (S1)
    */
	public EV3UltrasonicSensor(String port) {
		currentMode = true;
		
		this.port = port;
		
		enable();	
	}

	/**
    * <b>Lego EV3 Ultrasonic sensor, Distance mode</b><br>
    * Measures distance to an object in front of the sensor
    * 
    * <p>
    * <b>Size and content of the sample</b><br>
    * The sample contains one elements representing the distance (in metres) to an object in front of the sensor.
    * unit).
    * 
    * @return A sampleProvider
    */
	public SampleProvider getDistanceMode() {
		return new DistanceMode();
	}

    /**
    * Enable the sensor.
    */
	public void enable() {
		int error;
		currentMode = true;
		
		Sensor_handle = new IntW(0);
		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetObjectHandle(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), this.port, Sensor_handle, VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_oneshot_wait);	

		if (error == 0) {
			currentMode = true;
		
			FloatWA detectedPoint= new FloatWA(3);
			
			error = VrepConnectionFactory.getInstance().VrepConnection_get_object().
					simxReadProximitySensor(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
							Sensor_handle.getValue(), 
							null, 
							detectedPoint, 
							null, 
							null, 
							VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_streaming);
			
			System.out.println("INFO \t sensor link established");
		}
		else {
			System.out.println("ERROR \t sensor link failed");
		}
	}

   /**
   * Disable the sensor.
   */
	public void disable() {
		if (currentMode == true) {
			Sensor_handle = null;
			currentMode = false;
		}
	}

   /**
   * Indicate that the sensor is enabled.
   * 
   * @return True, when the sensor is enabled. <br>
   *         False, when the sensor is disabled.
   */
	public boolean isEnabled() {
		return currentMode;
	}
	
	public int sampleSize() {
		return new DistanceMode().sampleSize();
	}
	
	public void fetchSample(float[] sample, int offset) {
		new DistanceMode().fetchSample(sample, offset);
	}

	private class DistanceMode implements SampleProvider, SensorMode {
		@Override
		public int sampleSize() {
			return 1;
		}

		@Override
		public void fetchSample(float[] sample, int offset) {
			int error;
			
            FloatWA detectedPoint= new FloatWA(3);
            
            do{
            	error = VrepConnectionFactory.getInstance().VrepConnection_get_object().
    					simxReadProximitySensor(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
    							Sensor_handle.getValue(), 
    							null, 
    							detectedPoint, 
    							null, 
    							null, 
    							VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_buffer); 
            }while(error != 0);
			
            sample[offset] = (float)Math.sqrt(Math.pow((double)detectedPoint.getArray()[0],2)+Math.pow((double)detectedPoint.getArray()[1],2)+Math.pow((double)detectedPoint.getArray()[2],2));
		}

		@Override
		public String getName() {
			return "Distance";
		}
	}		
}
