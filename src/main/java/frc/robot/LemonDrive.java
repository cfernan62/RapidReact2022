package frc.robot; 
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

public class LemonDrive{
  private final Spark m_left = new Spark(8);
  private final Spark m_right = new Spark(9);
  private final DifferentialDrive m_drive = new DifferentialDrive (m_left, m_right);

  LemonDrive(){
      m_right.setInverted(true);
  }
  public void Move(double speed, double turn){
    m_drive.arcadeDrive(speed, turn,true);
  }
}