package frc.robot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
//import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;

public class Climb {
  private final Solenoid main_cylinder = new Solenoid(PneumaticsModuleType.CTREPCM, 0);
  private final Solenoid small_cylinder = new Solenoid(PneumaticsModuleType.CTREPCM, 1);
  private final Solenoid angle_cylinder = new Solenoid(PneumaticsModuleType.CTREPCM, 2);
  public Climb_States climb_state;
  //private Timer climb_Timer = new Timer();
  public static Joystick m_rightstick = new Joystick(1);


 //private final Compressor pcmCompressor = new Compressor(0, PneumaticsModuleType.CTREPCM);
  
  public enum Climb_States_Old {
    IDLE,
    START_CLIMB,
    MAIN_EXTEND,
    ANGLE_EXTEND,
    MAIN_RETRACT,
    MAIN_EXTEND_CLIMBRUNG,
    ANGLE_RETRACT_2,
    MAIN_RETRACT_2,
    SMALL_EXTEND,
    MAIN_EXTEND_REACHNEXT,
    ANGLE_EXTEND_NEXT,
    SMALL_RETRACT_NEXT,
    MAIN_RETRACT_NEXT
    
  }

  public enum Climb_States_Old_2 {
    IDLE,
    START_CLIMB,
    MAIN_SMALL_EXTEND,
    SMALL_RETRACT,
    MAIN_RETRACT,
    SMALL_EXTEND,
    ANGLE_RETRACT,
    MAIN_EXTEND,
    ANGLE_EXTEND,
    SMALL_RETRACT_2,  //RAISE INTAKE
    MAIN_RETRACT_2

  }

  public enum Climb_States {
    IDLE,
    START_CLIMB,
    A,
    B,
    C0,
    C,
    D,
    E,
    F,
    G,
    H 

  }

  Climb() { // constructor for climb
      
  }
  
  public void main_cylinder_extend( boolean extend ) {
    if(extend){
      main_cylinder.set(true);
      SmartDashboard.putString("Main", "extended");
     }
     else{
      main_cylinder.set(false);
      SmartDashboard.putString("Main", "retracted");
     }

  }

  public void small_cylinder_extend( boolean extend ) {
    if(extend){
      small_cylinder.set(true);
      SmartDashboard.putString("Small", "extended");
    }
    else{
      small_cylinder.set(false);
      SmartDashboard.putString("Small", "retracted");
    }
      
  }

  public void angle_cylinder_extend( boolean extend ) {
    if(extend){
      angle_cylinder.set(false);
      SmartDashboard.putString("Angle", "extended");
    }
    else{
      angle_cylinder.set(true);
      SmartDashboard.putString("Angle", "retracted");
    }
  }

  public void initclimber(){
    climb_state = Climb_States.START_CLIMB;
    small_cylinder_extend(true);
    main_cylinder_extend(false);
    angle_cylinder_extend(true);
    
  }

 
}
