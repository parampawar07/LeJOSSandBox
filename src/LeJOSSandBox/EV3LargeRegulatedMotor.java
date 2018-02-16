package LeJOSSandBox;
import coppelia.FloatW;
import coppelia.FloatWA;
import coppelia.IntW;

public class EV3LargeRegulatedMotor {
	private float speed = 360; // default speed 360 deg/s
	final private float maxSpeed = 740; // max speed for large motor
	private boolean currentMode = false; // false = not moving, true = moving
	private IntW Motor_handle;
	private String port;
	private IntW Wheel_handle;
	private String wheel;
	FloatW position = new FloatW(1);
	private int direction = 0; // 1 = forward, -1 = backward, 0 = stopped
	private int resetPosition = 0;
	private FloatWA currentSpeed = new FloatWA(3);
	private int limitAngle = 0;
	int error;
	private boolean endThread = false;
	
    public EV3LargeRegulatedMotor(String port){
    	this.port = port;
    	Motor_handle = new IntW(0);
    	Wheel_handle = new IntW(0);
    	
    	error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetObjectHandle(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), this.port, Motor_handle, VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_oneshot_wait);	

    	this.wheel = port.split("_")[0] + "_wheel_vrep";
    	
    	if (error == 0){
    		System.out.println("Motor link established (" + port + ")");
    		
    		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetJointPosition(
        			VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
        			Motor_handle.getValue(),
        			position,
        			VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_streaming);
    		
    		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetObjectHandle(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), this.wheel, Wheel_handle, VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_oneshot_wait);
    		
    		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetObjectVelocity(
        			VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
        			Wheel_handle.getValue(),
        			null,
        			currentSpeed,
        			VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_streaming);
    		
    		try{
    			Thread.sleep(1000);
    		}
    		catch(InterruptedException ex){
    			Thread.currentThread().interrupt();
    		}
		}
		else {
			System.out.println("ERROR \t motor link failed (" + error + ")");
		}
    }
    
    /**
     * Reset the tachometer associated with this motor by saving current tachometer count as reset position value.
     * Note calling this method will cause any current move operation to be halted.
     */
    public void resetTachoCount(){
    	do{
    		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetJointPosition(
          			VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
          			Motor_handle.getValue(),
          			position,
          			VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_buffer);
    	}while(error != 0);
    	
    	resetPosition = (int)(Math.toDegrees(position.getValue()));
    	
    	stop();
    }
    
    /**
     * Return the current tachometer count based on joint and reset position values.
     * @return current tachometer in degrees 
     */
    public int getTachoCount(){
        do{
        	error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetJointPosition(
        			VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
        			Motor_handle.getValue(),
        			position,
        			VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_buffer);
        }while(error != 0);
    	
        return (int)(Math.toDegrees(position.getValue()) - this.resetPosition);
    }
    
	/**
	 * @return true if the motor is currently in motion
	 */
    public boolean isMoving(){
    	return currentMode;
    }
    
    /**
     * @return 740 degrees/sec as maximum speed 
     */
    public float getMaxSpeed(){
    	return this.maxSpeed;
    }
    
    /**
     * Return the angle that this motor is rotating to.
     * @return angle in degrees
     */
    public int getLimitAngle(){
    	return this.limitAngle;
    }
    
    /**
     * @return current target speed in degrees/sec
     */
    public float getSpeed(){
    	return this.speed;
    }
    
    /**
     * @return current velocity in degrees/sec
     */
    public float getRotationSpeed(){
    	do{
    	error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetObjectVelocity(
    			VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
    			Wheel_handle.getValue(),
    			null,
    			currentSpeed,
    			VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_buffer);
    	}while(error != 0);
    
      	return (float)Math.toDegrees(Math.sqrt(
    			Math.pow(currentSpeed.getArray()[0],2)+
    			Math.pow(currentSpeed.getArray()[1],2)+
    			Math.pow(currentSpeed.getArray()[2],2)
      			));
    }
    
    /**
     * Rotate by the requested number of degrees. Wait for the move to complete.
     * @param angle number of degrees to rotate relative to the current position
     */
    public void rotate(int angle){
    	if (angle != 0){
    		int currentPosition = getTachoCount();
    		int targetPosition = currentPosition + angle;
    	
    		if(angle > 0){
    			forward();
    		}
    		else {
    			backward();
			}
    		
    		while((targetPosition > currentPosition && direction == 1) || 
    				(targetPosition < currentPosition && direction == -1)){
    			currentPosition = getTachoCount(); 
    			
    			if (endThread == true){
    				System.out.println("Rotate thread ended (" + port + ")");
    				break;
    			}
    		}
    		
    		if (!endThread){
    			stop();
    		}
    	}
    }
    
    /**
     * Rotate by the request number of degrees.
     * @param angle in degrees to rotate relative to the current position
     * @param immediateReturn if true do not wait for the move to complete
     */
    public void rotate(int angle, boolean immediateReturn){
    	if(immediateReturn){
    		endThread = true;	//end existing rotate or rotateTo Thread
    		final int angleThread = angle;
    		
    		Thread rotateThread = new Thread(){
    			public void run(){
    				endThread = false;
    				
    				rotate(angleThread);
    			}
    		};
    	
    		System.out.println("Rotate thread started (" + port + ")");
    		rotateThread.start();
    	}
    	else{
    		rotate(angle);
    	}
    }
    
    /**
     * Rotate to the target angle. Do not return until the move is complete.
     * @param limitAngle angle in degrees to rotate to
     */
    public void rotateTo(int limitAngle){
    	this.limitAngle = limitAngle;
    	int currentPosition = getTachoCount();
    	
    	if(currentPosition != this.limitAngle){
    		if(currentPosition < this.limitAngle){
    			forward();
    		}
    		else {
    			backward();
    		}
    	}
    	
    	while((this.limitAngle > currentPosition && direction == 1) || 
    			(this.limitAngle < currentPosition && direction == -1)){ 
    		currentPosition = getTachoCount();
    		
    		if (endThread == true){
    			System.out.println("RotateTo Thread ended (" + port + ")");
    			break;
    		}
    	}
    	
    	if (!endThread){
    		stop();
    	}
    }
    
    /**
     * Rotate to the target angle.
     * @param limitAngle angle in degress to rotate to
     * @param immediateReturn if true do not wait for the move to complete
     */
    public void rotateTo(int limitAngle, boolean immediateReturn){
    	if(immediateReturn){
    		endThread = true;	//end existing rotate or rotateTo Thread
    		this.limitAngle = limitAngle;
    		final int limitAngleThread = limitAngle;
    		
    		Thread rotateToThread = new Thread(){
    			public void run(){
    				endThread = false;
    				rotateTo(limitAngleThread);
    			}
    		};
    	
    		System.out.println("RotateTo thread started (" + port + ")");
    		rotateToThread.start();
    	}
    	else{
    		rotateTo(limitAngle);
    	}
    }
    
    /**
     * Sets desired motor speed, in degrees per second.
     * @param speed value in degrees/sec
     */
    public void setSpeed(int speed){
    	setSpeed((float)speed);
    }
    
    /**
     * Sets desired motor speed, in degrees per second.
     * @param speed value in degrees/sec
     */
    public void setSpeed(float speed){        
    	if (speed <= Math.abs(maxSpeed)){
    		this.speed = Math.abs(speed);
    	}
    	else {
    		this.speed = maxSpeed;
    	}
    		
    	if (direction == -1){
    		backward();
    	}        	
    	
    	if (direction == 1){
        	forward();
    	}
    }
    
    /**
	 * Causes motor to rotate forward.
	 */
    public void forward(){
    	endThread = true;	//end existing rotate or rotateTo Thread 
    	
    	do{
    		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxSetJointTargetVelocity(
    					VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
    					Motor_handle.getValue(), 
    					(float)Math.toRadians(speed), 
    					VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_blocking);
    	}while(error != 0);
    	
    	currentMode = true;
		direction = 1;
		endThread = false;
	
		System.out.println("Motor move forward (" + port + ")");
    }
    
    /**
	 * Causes motor to rotate backwards.
	 */
    public void backward(){
    	endThread = true; 	//end existing rotate or rotateTo Thread
    	
    	do{
    		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().
    					simxSetJointTargetVelocity(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
        				Motor_handle.getValue(), 
        				-(float)Math.toRadians(speed), 
        				VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_blocking);
    	}while(error != 0);
    	
    	this.currentMode = true;
		this.direction = -1;
		endThread = false;
		
    	System.out.println("Motor move backward (" + port + ")");
    }
    
	/**
	 * Causes motor to stop.
	 * Cancels any rotate() or rotateTo() orders in progress.
	 */
    public void stop(){
    	endThread = true;	//end existing rotate or rotateTo Thread
    	
        do{
        	error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxSetJointTargetVelocity(
        				VrepConnectionFactory.getInstance().VrepConnection_get_ID(), 
        				Motor_handle.getValue(), 
        				(float)0, 
        				VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_blocking);
        }while(error != 0);
        
    	this.currentMode = false;
		this.direction = 0;
		endThread = false;
		
		System.out.println("Motor stopped (" + port + ")");
    }
    
	/**
	 * Causes motor to stop.
	 * Cancels any rotate() or rotateTo() orders in progress.
	 * @param immediateReturn if true do not wait for the motor to actually stop
	 */
    public void stop(boolean immediateReturn){
    	if(immediateReturn){
    		endThread = true;	//end existing rotate or rotateTo Thread
    		
    		Thread stopThread = new Thread(){
    			public void run(){
    				endThread = false;
    				
    				EV3LargeRegulatedMotor.this.stop();
    			}
    		};
    	
    		stopThread.start();
    	}
    	else{
    		stop();
    	}
    }
    
    public void close(){
    	
    }
    
    public void startSynchronization(){
    	int error;
    	error=VrepConnectionFactory.getInstance().vrep.simxSynchronous(VrepConnectionFactory.vrep.clientID, true);
    	
    		if (error == 0 ){			
    			System.out.println("INFO \t Start synchronisation");		
    		}
    		else{
    			System.out.println("ERROR \t Synchronisation does not work (" + error + ")");
    		}    	
    	}
    
    public void endSynchronization(){
    	int error;
    	error=VrepConnectionFactory.getInstance().vrep.simxSynchronous(VrepConnectionFactory.vrep.clientID, false);
    	
    		if (error == 0 ){			
    			System.out.println("INFO \t End synchronisation ");		
    		}
    		else{
    			System.out.println("ERROR \t Synchronisation does not work  (" + error + ")");
    		}    	
    	} 	

    //------------------------------ not implemented functions ------------------------------
	/**
	 * Not implemented
	 */
    public void waitComplete(){
    	System.out.println("waitComplete() not implemented");
    }

	/**
	 * Not implemented
	 */
    public boolean suspendRegulation(){
    	System.out.println("suspendRegulation() not implemented");
    	
    	return false;
    }
    
	/**
	 * Not implemented
	 */
    public boolean isStalled(){
    	System.out.println("isStalled() not implemented");
    	
    	return false;
    }
    
	/**
	 * Not implemented
	 */
    public void setStallThreshhold(int error, int time){
    	System.out.println("setStallThreshhold(int error, int time) not implemented");
    }
    
	/**
	 * Not implemented
	 */
    public void setAcceleration(int acceleration){
    	System.out.println("setAcceleration(int acceleration) not implemented");
    }
    
	/**
	 * Not implemented
	 */
    public int getAcceleration(){
    	System.out.println("getAcceleration() not implemented");
    	
    	return 0;
    }
    
	/**
	 * Not implemented
	 */
    public void lock(int power){
    	System.out.println("lock(int power) not implemented");
    }
    
	/**
	 * Not implemented
	 */
    public void synchronizeWith(){
    	System.out.println("synchronizeWith() not implemented");
    }
}
