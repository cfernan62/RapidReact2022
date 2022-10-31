package frc.robot;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Intake {
    
  private final CANSparkMax IntakeMotor = new CANSparkMax(2, MotorType.kBrushless); 
  private final Spark LeftActuator = new Spark(1);
  private final Spark RightActuator = new Spark(0);
  Servo LegLeft = new Servo (2);
  Servo LegRight = new Servo (3);
  private RelativeEncoder Intake_Encoder = IntakeMotor.getEncoder();
  private final Solenoid Ball_Cylinder = new Solenoid(PneumaticsModuleType.CTREPCM, 4);

  Intake(){
    LeftActuator.setInverted(true);
    Intake_Encoder.setPosition(0);
  }



  public void Raise_Intake (double speed) {
    LeftActuator.set(speed);
    RightActuator.set(speed);
  }

  public void Move_Intake (double speed) {
    IntakeMotor.set(speed);
  }

  public boolean AtSetSpeed( double SetSpeed ){
    double ActualSpeed;

    ActualSpeed = Intake_Encoder.getVelocity();
    SmartDashboard.putNumber("IntakeSpeed", ActualSpeed);
   
    if (SetSpeed > 0){
      if( ActualSpeed >= SetSpeed)   
        return( true );
      else 
        return( false );
    }
    else{
      if( ActualSpeed <= SetSpeed)   
        return( true );
      else 
        return( false );
    }
}

  public void Kick( double SetPointAngle ){
    LegLeft.setAngle( SetPointAngle );
    LegRight.setAngle( SetPointAngle );
  }

  public void ball_cylinder_extend( boolean extend ) {
    if(extend){
      Ball_Cylinder.set(true);
      SmartDashboard.putString("Ball", "extended");
     }
     else{
      Ball_Cylinder.set(false);
      SmartDashboard.putString("Ball", "retracted");
     }
  } 
}
