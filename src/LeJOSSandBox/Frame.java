package LeJOSSandBox;
import coppelia.IntW;

import java.util.concurrent.TimeUnit;

import LeJOSSandBox.VrepConnectionFactory;
import coppelia.FloatW;
import coppelia.FloatWAA;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

import coppelia.IntW;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFormattedTextField;

public class Frame {
	int x ;
	JFrame frame;
	private JTextField textField;
	private JLabel lblNewOrthosize;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LejosGUI window = new LejosGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public Frame() {
		initialize();
	
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
	
		textField = new JTextField();
		textField.setBounds(146, 64, 86, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		
		JLabel lblOrthosize = new JLabel("OrthoSize");
		lblOrthosize.setBounds(44, 67, 86, 14);
		frame.getContentPane().add(lblOrthosize);
		
		JButton btnSetRenderMode = new JButton("Set RenderMode");
		btnSetRenderMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s = textField.getText();
				int k =Integer.valueOf(s);
				x = k;				
			}
		});
		btnSetRenderMode.setBounds(39, 189, 140, 23);
		frame.getContentPane().add(btnSetRenderMode);
		
		lblNewOrthosize = new JLabel("Render Mode");
		lblNewOrthosize.setBounds(43, 161, 103, 14);
		frame.getContentPane().add(lblNewOrthosize);
		
		textField_1 = new JTextField();
		textField_1.setBounds(146, 111, 86, 20);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JFormattedTextField formattedTextField = new JFormattedTextField();
		formattedTextField.setBounds(156, 158, 226, 20);
		frame.getContentPane().add(formattedTextField);
		formattedTextField.setValue(setFloatValue(x));
		 formattedTextField.getValue();
		//formattedTextField.g
	}
	public int setFloatValue(int i) {
		IntW handle;
		
		int error;
    	int parameterID = 1017;
    	System.out.println(parameterID);
    	int parameterValue = x;
		handle = new IntW(0);
		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxGetObjectHandle(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), "Vision_sensor", handle, VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_oneshot_wait);	
		error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxSetObjectIntParameter(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), handle.getValue(), parameterID, parameterValue, VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_streaming);
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 error = VrepConnectionFactory.getInstance().VrepConnection_get_object().simxSetObjectIntParameter(VrepConnectionFactory.getInstance().VrepConnection_get_ID(), handle.getValue(), parameterID, parameterValue, VrepConnectionFactory.getInstance().VrepConnection_get_object().simx_opmode_buffer);
		return parameterValue;
	
	
}
}
	
	
		
	

