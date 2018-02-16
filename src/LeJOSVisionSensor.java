import LeJOSSandBox.*;

public class LeJOSVisionSensor {
	public static void main(String[] args) {
		EV3LargeRegulatedMotor objMotorA = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor objMotorB = new EV3LargeRegulatedMotor(MotorPort.B);
		EV3UltrasonicSensor objUltrasonicSensor= new EV3UltrasonicSensor(SensorPort.S1);
		EV3TouchSensor objTouchSensor = new EV3TouchSensor(SensorPort.S2);
		Ev3VisionSensor objVisionSensor = new Ev3VisionSensor(SensorPort.S4);
		float[] distanceSample = new float[3];
	    float[] touchSensorSample = new float[3];
	    float visionvalue;
	
  	   	
       //objMotorA.forward();    
       //objMotorB.forward();
       
       
       visionvalue=objVisionSensor.getSample();
       System.out.println("visionvalue: " + visionvalue);
       int i=0;
       while(visionvalue<0.087){
           objMotorA.forward();    
     	   objMotorB.forward();
           visionvalue=objVisionSensor.getSample();
       }
       
 	   
 	   objMotorA.stop();    
	   objMotorB.stop();
 	   
       
        //objUltrasonicSensor.getDistanceMode().fetchSample(distanceSample, 0);
        //while(distanceSample[0] > 0.3){
        //    System.out.println("Sensor distance: " + distanceSample[0]);
        //    objUltrasonicSensor.getDistanceMode().fetchSample(distanceSample, 0);
        //}
		
		//objMotorA.backward();    
  	   	//objMotorB.backward();
  	   	
	}
}