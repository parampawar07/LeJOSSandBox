package LeJOSSandBox;
public class VrepConnectionFactory {
	public static VrepConnection vrep ;
	
	private VrepConnectionFactory(){
		
	}

	public static VrepConnection getInstance(){
		if(vrep==null){
			vrep=new VrepConnection();
		}
			return vrep;
	
	}
}
