package frc.robot;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Timer;
///import com.ctre.phoenix.sensors.PigeonIMU;


public class RealDrive {

  private final CANSparkMax left_motor1 = new CANSparkMax(20, MotorType.kBrushless);
  private final CANSparkMax left_motor2 = new CANSparkMax(21, MotorType.kBrushless);
  private final CANSparkMax right_motor1 = new CANSparkMax(22, MotorType.kBrushless);
  private final CANSparkMax right_motor2 = new CANSparkMax(23, MotorType.kBrushless);
  MotorControllerGroup m_left = new MotorControllerGroup(left_motor1, left_motor2);
  MotorControllerGroup m_right = new MotorControllerGroup(right_motor1, right_motor2);
  DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);
  private Timer auto_Timer = new Timer();
  ///public PigeonIMU _pigeon = new PigeonIMU(10);
  int _loopCount = 0;
  private Intake my_Intake;
  public RelativeEncoder leftEncoder = left_motor1.getEncoder();
  public RelativeEncoder rightEncoder = right_motor1.getEncoder();
  public Integer dbcount;

  public enum Auto_States {
    IDLE,
    START_AUTO,
    SHOOT_INTAKE,
    REVERSE_BOT,
    STOP_BOT_1,
    ROTATE_180,
    STOP_BOT_2,
    LOWER_INTAKE,
    STOP_BOT_3,
    PICKUP_CARGO,
    STOP_BOT_4,
    ROTATE_180_2,
    STOP_BOT_5,
    RAISE_INTAKE,
    STOP_BOT_6,
    DRIVE_FORWARD,
    STOP_BOT_7,
    SHOOT_INTAKE_2,
    STOP_BOT_8,
    ROTATE_ANGLE,
    STOP_BOT_9,
    PICKUP_CARGO_2,
    ROTATE_ANGLE_2,
    RAISE_INTAKE_2,
    DRIVE_FORWARD_2,
    SHOOT_INTAKE_3,
    ROTATE_90_LEFT,
    REVERSE_BOT_2,
    ROTATE_90_RIGHT,

  }

  public Auto_States auto_control_state;

  RealDrive(Intake m) {
    m_right.setInverted(true);
    my_Intake = m;

  }

  public void SetIdleModeToCoast(boolean coast) {
    if (coast) {
      left_motor1.setIdleMode(IdleMode.kCoast);
      left_motor2.setIdleMode(IdleMode.kCoast);
      right_motor1.setIdleMode(IdleMode.kCoast);
      right_motor2.setIdleMode(IdleMode.kCoast);
    } else {
      left_motor1.setIdleMode(IdleMode.kBrake);
      left_motor2.setIdleMode(IdleMode.kBrake);
      right_motor1.setIdleMode(IdleMode.kBrake);
      right_motor2.setIdleMode(IdleMode.kBrake);
    }
  }

  public void Move(double speed, double turn) {
    // m_drive.arcadeDrive(-speed, turn, true);
    m_drive.curvatureDrive(-speed, turn, true);
  }

  public void Auto_Control() {
    switch (auto_control_state) {

      case IDLE:
        Move(0, 0);
        break;

      case START_AUTO:
        Move(0, 0);
        dbcount = 0;
        ///_pigeon.setYaw(0);
        auto_control_state = Auto_States.SHOOT_INTAKE;
        auto_Timer.reset();
        auto_Timer.start();
        break;

      case SHOOT_INTAKE:
        if (auto_Timer.get() > 2) {
          Move(0, 0);
          my_Intake.Move_Intake(0);
          auto_control_state = Auto_States.STOP_BOT_3;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          my_Intake.Move_Intake(-0.38);
          Move(0, 0);
          if (my_Intake.AtSetSpeed(-1290)){
            if (++dbcount > 2)
            my_Intake.ball_cylinder_extend(true);
           }
          else{
            dbcount = 0;
            my_Intake.ball_cylinder_extend(false);
          }
        }
        break;

      case STOP_BOT_1:
        if (auto_Timer.get() > 0.1) {
          auto_control_state = Auto_States.ROTATE_180; //change back to rotate 180
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          Move(0, 0);
        }
        break;

     /// case ROTATE_180:
       //// if (_pigeon.getYaw() >= 160) {
        //  Move(0, 0);
         // rightEncoder.setPosition(0);
        //  auto_control_state = Auto_States.STOP_BOT_2;
       /// } else {
       ///   Move(0, 0.29);
       // }
       /// break;

      case STOP_BOT_2:
        if (auto_Timer.get() > 0.1) {
          auto_control_state = Auto_States.PICKUP_CARGO;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          Move(0, 0);
        }
        break;

      case LOWER_INTAKE:
        if (auto_Timer.get() > 2) {
          Move(0, 0);
          my_Intake.Raise_Intake(0);
          auto_control_state = Auto_States.STOP_BOT_3;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          my_Intake.Raise_Intake(-1);
          Move(0, 0);
        }
        break;

      case STOP_BOT_3:
        if (auto_Timer.get() > 0.1) {
          auto_control_state = Auto_States.PICKUP_CARGO;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          Move(0, 0);
        }
        break;

      case PICKUP_CARGO:
        if (auto_Timer.get() > 2.7) { //back to negative 22
          Move(0, 0);
          rightEncoder.setPosition(0);
          my_Intake.Move_Intake(0);
          auto_control_state = Auto_States.IDLE; //back to stop 4
          auto_Timer.reset();
          auto_Timer.start();
        } else {
        //my_Intake.Raise_Intake(-1);
         //my_Intake.Move_Intake(0.25);
          Move(0.45, 0); 
        }
        break;

      case STOP_BOT_4:
        if (auto_Timer.get() > 0.1) {
          auto_control_state = Auto_States.ROTATE_180_2;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          Move(0, 0);
        }
        break;

      ///case ROTATE_180_2:
       /// if (_pigeon.getYaw() <= 15) {
         /// Move(0, 0);
         /// rightEncoder.setPosition(0);
         /// auto_control_state = Auto_States.STOP_BOT_5;
       ///} else {
        ///  Move(0, -0.38);
       /// }
       //// break;

      case STOP_BOT_5:
        if (auto_Timer.get() > 0.1) {
          auto_control_state = Auto_States.DRIVE_FORWARD;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          Move(0, 0);
        }
        break;

      case RAISE_INTAKE:
        if (auto_Timer.get() > 2) {
          Move(0, 0);
          my_Intake.Raise_Intake(0);
          auto_control_state = Auto_States.STOP_BOT_6;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          my_Intake.Raise_Intake(1);
          Move(0, 0);
        }
        break;

      case STOP_BOT_6:
        if (auto_Timer.get() > 0.1) {
          auto_control_state = Auto_States.DRIVE_FORWARD;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          Move(0, 0);
        }
        break;

      case DRIVE_FORWARD:
        if (auto_Timer.get() > 2.5) {
          Move(0, 0);
          rightEncoder.setPosition(0);
          auto_control_state = Auto_States.STOP_BOT_7;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          my_Intake.Raise_Intake(1);
          Move(-0.3, 0);
        }
        break;

      case STOP_BOT_7:
        if (auto_Timer.get() > 0.85) {
          dbcount = 0;
          my_Intake.ball_cylinder_extend(false);
          auto_control_state = Auto_States.SHOOT_INTAKE_2;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          Move(0, 0);
        }
        break;

      case SHOOT_INTAKE_2:
        if (auto_Timer.get() > 3) {
          Move(0, 0);
          my_Intake.Move_Intake(0);
          auto_control_state = Auto_States.IDLE;
          auto_Timer.reset();
          auto_Timer.start();
        } else {
          my_Intake.Move_Intake(-0.38);
          Move(0, 0);
          if (my_Intake.AtSetSpeed(-1290)){
            if (++dbcount > 2)
            if (auto_Timer.get() > 1)
              my_Intake.ball_cylinder_extend(true);
           }
          else{
            dbcount = 0;
            my_Intake.ball_cylinder_extend(false);
          }
        }
        break;


      /*
       * public void Auto_Control_Two() {
       * switch (auto_control_state) {
       * 
       * case IDLE:
       * Move(0, 0);
       * break;
       * 
       * case START_AUTO:
       * Move(0, 0);
       * _pigeon.setYaw(0);
       * auto_control_state = Auto_States.SHOOT_INTAKE;
       * auto_Timer.reset();
       * auto_Timer.start();
       * break;
       * 
       * case SHOOT_INTAKE:
       * if (auto_Timer.get() > 2) {
       * Move(0, 0);
       * my_Intake.Move_Intake(0);
       * auto_control_state = Auto_States.STOP_BOT_1;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * my_Intake.Move_Intake(-0.25);
       * Move(0, 0);
       * if (my_Intake.AtSetSpeed(-1000))
       * my_Intake.ball_cylinder_extend(true);
       * else
       * my_Intake.ball_cylinder_extend(false);
       * }
       * break;
       * 
       * case STOP_BOT_1:
       * if (auto_Timer.get() > 0.75) {
       * auto_control_state = Auto_States.ROTATE_180;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * Move(0, 0);
       * }
       * break;
       * 
       * case ROTATE_180:
       * if (_pigeon.getYaw() >= 180) {
       * Move(0, 0);
       * rightEncoder.setPosition(0);
       * auto_control_state = Auto_States.STOP_BOT_2;
       * } else {
       * Move(0, 0.2);
       * }
       * break;
       * 
       * case STOP_BOT_2:
       * if (auto_Timer.get() > 0.75) {
       * auto_control_state = Auto_States.LOWER_INTAKE;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * Move(0, 0);
       * }
       * break;
       * 
       * case LOWER_INTAKE:
       * if (auto_Timer.get() > 2) {
       * Move(0, 0);
       * my_Intake.Raise_Intake(0);
       * auto_control_state = Auto_States.STOP_BOT_3;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * my_Intake.Raise_Intake(-1);
       * Move(0, 0);
       * }
       * break;
       * 
       * case STOP_BOT_3:
       * if (auto_Timer.get() > 0.75) {
       * auto_control_state = Auto_States.PICKUP_CARGO;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * Move(0, 0);
       * }
       * break;
       * 
       * case PICKUP_CARGO:
       * if (rightEncoder.getPosition() > 68.4) { //Encoder distance estimate,
       * calibrate value
       * Move(0, 0);
       * rightEncoder.setPosition(0);
       * my_Intake.Raise_Intake(0);
       * auto_control_state = Auto_States.STOP_BOT_4;
       * } else {
       * my_Intake.Move_Intake(0.6);
       * Move(-0.4, 0);
       * }
       * break;
       * 
       * case STOP_BOT_4:
       * if (auto_Timer.get() > 0.75) {
       * auto_control_state = Auto_States.ROTATE_ANGLE;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * Move(0, 0);
       * }
       * break;
       * 
       * case ROTATE_ANGLE:
       * if (_pigeon.getYaw() <= 0) { // Need to figure out angle
       * Move(0, 0);
       * rightEncoder.setPosition(0);
       * auto_control_state = Auto_States.STOP_BOT_5;
       * } else {
       * Move(0, -0.2);
       * }
       * break;
       * 
       * case STOP_BOT_5:
       * if (auto_Timer.get() > 0.75) {
       * auto_control_state = Auto_States.PICKUP_CARGO_2;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * Move(0, 0);
       * }
       * break;
       * 
       * case PICKUP_CARGO_2:
       * if (rightEncoder.getPosition() > 68.4) {
       * Move(0, 0);
       * rightEncoder.setPosition(0);
       * auto_control_state = Auto_States.STOP_BOT_6;
       * } else {
       * my_Intake.Move_Intake(0.6);
       * Move(-0.5, 0);
       * }
       * break;
       * 
       * case STOP_BOT_6:
       * if (auto_Timer.get() > 0.75) {
       * auto_control_state = Auto_States.ROTATE_ANGLE_2;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * Move(0, 0);
       * }
       * break;
       * 
       * case ROTATE_ANGLE_2:
       * if (_pigeon.getYaw() <= 0) { // Figure out angle
       * Move(0, 0);
       * rightEncoder.setPosition(0);
       * auto_control_state = Auto_States.STOP_BOT_7;
       * } else {
       * Move(0, -0.2);
       * }
       * break;
       * 
       * case STOP_BOT_7:
       * if (auto_Timer.get() > 0.75) {
       * auto_control_state = Auto_States.RAISE_INTAKE_2;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * Move(0, 0);
       * }
       * break;
       * 
       * case RAISE_INTAKE_2:
       * if (auto_Timer.get() > 2) {
       * Move(0, 0);
       * my_Intake.Raise_Intake(0);
       * auto_control_state = Auto_States.STOP_BOT_8;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * my_Intake.Raise_Intake(1);
       * Move(0, 0);
       * }
       * break;
       * 
       * case STOP_BOT_8:
       * if (auto_Timer.get() > 0.75) {
       * auto_control_state = Auto_States.DRIVE_FORWARD_2;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * Move(0, 0);
       * }
       * break;
       * 
       * case DRIVE_FORWARD_2:
       * if (rightEncoder.getPosition() > 68.4) {
       * Move(0, 0);
       * rightEncoder.setPosition(0);
       * auto_control_state = Auto_States.STOP_BOT_9;
       * } else {
       * Move(-0.5, 0);
       * }
       * break;
       * 
       * case STOP_BOT_9:
       * if (auto_Timer.get() > 0.75) {
       * auto_control_state = Auto_States.SHOOT_INTAKE_2;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * Move(0, 0);
       * }
       * break;
       * 
       * case SHOOT_INTAKE_2:
       * if (auto_Timer.get() > 2) {
       * Move(0, 0);
       * my_Intake.Move_Intake(0);
       * auto_control_state = Auto_States.IDLE;
       * auto_Timer.reset();
       * auto_Timer.start();
       * } else {
       * my_Intake.Move_Intake(-0.5);
       * Move(0, 0);
       * if (my_Intake.AtSetSpeed(-2000))
       * my_Intake.ball_cylinder_extend(true);
       * else
       * my_Intake.ball_cylinder_extend(false);
       * }
       * break;
       * 
       * }
       */

    }

    /*
     * public void Auto_Control_3() {
     * switch (auto_control_state) {
     * 
     * case IDLE:
     * Move(0, 0);
     * break;
     * 
     * case START_AUTO:
     * Move(0, 0);
     * _pigeon.setYaw(0);
     * auto_control_state = Auto_States.SHOOT_INTAKE;
     * auto_Timer.reset();
     * auto_Timer.start();
     * break;
     * 
     * case SHOOT_INTAKE:
     * if (auto_Timer.get() > 2) {
     * Move(0, 0);
     * my_Intake.Move_Intake(0);
     * auto_control_state = Auto_States.STOP_BOT_1;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * my_Intake.Move_Intake(-0.5);
     * Move(0, 0);
     * if (my_Intake.AtSetSpeed(-2000))
     * my_Intake.ball_cylinder_extend(true);
     * else
     * my_Intake.ball_cylinder_extend(false);
     * }
     * break;
     * 
     * case STOP_BOT_1:
     * if (auto_Timer.get() > 0.75) {
     * auto_control_state = Auto_States.ROTATE_180;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * Move(0, 0);
     * }
     * break;
     * 
     * case ROTATE_180:
     * if (_pigeon.getYaw() >= 180) {
     * Move(0, 0);
     * rightEncoder.setPosition(0);
     * auto_control_state = Auto_States.STOP_BOT_2;
     * } else {
     * Move(0, 0.2);
     * }
     * break;
     * 
     * case STOP_BOT_2:
     * if (auto_Timer.get() > 0.75) {
     * auto_control_state = Auto_States.PICKUP_CARGO;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * Move(0, 0);
     * }
     * break;
     * 
     * case LOWER_INTAKE:
     * if (auto_Timer.get() > 2) {
     * Move(0, 0);
     * my_Intake.Raise_Intake(0);
     * auto_control_state = Auto_States.PICKUP_CARGO;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * my_Intake.Raise_Intake(-1);
     * Move(0, 0);
     * }
     * break;
     * 
     * case STOP_BOT_3:
     * if (auto_Timer.get() > 0.75) {
     * auto_control_state = Auto_States.PICKUP_CARGO;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * Move(0, 0);
     * }
     * break;
     * 
     * case PICKUP_CARGO:
     * if (rightEncoder.getPosition() > 68.4) {
     * Move(0, 0);
     * rightEncoder.setPosition(0);
     * my_Intake.Raise_Intake(0);
     * auto_control_state = Auto_States.STOP_BOT_3;
     * } else {
     * my_Intake.Raise_Intake(1);
     * Move(-0.5, 0);
     * }
     * break;
     * 
     * case STOP_BOT_4:
     * if (auto_Timer.get() > 0.75) {
     * auto_control_state = Auto_States.ROTATE_180_2;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * Move(0, 0);
     * }
     * break;
     * 
     * case ROTATE_180_2:
     * if (_pigeon.getYaw() <= 0) {
     * Move(0, 0);
     * rightEncoder.setPosition(0);
     * auto_control_state = Auto_States.STOP_BOT_4;
     * } else {
     * Move(0, -0.2);
     * }
     * break;
     * 
     * case STOP_BOT_5:
     * if (auto_Timer.get() > 0.75) {
     * auto_control_state = Auto_States.DRIVE_FORWARD;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * Move(0, 0);
     * }
     * break;
     * 
     * case RAISE_INTAKE:
     * if (auto_Timer.get() > 2) {
     * Move(0, 0);
     * my_Intake.Raise_Intake(0);
     * auto_control_state = Auto_States.REVERSE_BOT;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * my_Intake.Raise_Intake(1);
     * Move(0, 0);
     * }
     * break;
     * 
     * case STOP_BOT_6:
     * if (auto_Timer.get() > 0.75) {
     * auto_control_state = Auto_States.SHOOT_INTAKE_2;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * Move(0, 0);
     * }
     * break;
     * 
     * case DRIVE_FORWARD:
     * if (rightEncoder.getPosition() > 68.4) {
     * Move(0, 0);
     * rightEncoder.setPosition(0);
     * auto_control_state = Auto_States.STOP_BOT_1;
     * } else {
     * Move(-0.5, 0);
     * }
     * break;
     * 
     * case STOP_BOT_7:
     * if (auto_Timer.get() > 0.75) {
     * auto_control_state = Auto_States.SHOOT_INTAKE_2;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * Move(0, 0);
     * }
     * break;
     * 
     * case SHOOT_INTAKE_2:
     * if (auto_Timer.get() > 2) {
     * Move(0, 0);
     * my_Intake.Move_Intake(0);
     * auto_control_state = Auto_States.IDLE;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * my_Intake.Move_Intake(-1);
     * Move(0, 0);
     * }
     * break;
     * 
     * case REVERSE_BOT:
     * if (auto_Timer.get() > 1) {
     * Move(0, 0);
     * auto_control_state = Auto_States.IDLE;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * Move(0.4, 0);
     * }
     * break;
     * 
     * case ROTATE_90_LEFT:
     * if (_pigeon.getYaw() >= 90) {
     * Move(0, 0);
     * rightEncoder.setPosition(0);
     * auto_control_state = Auto_States.STOP_BOT_2;
     * } else {
     * Move(0, -0.25);
     * }
     * break;
     * 
     * case PICKUP_CARGO_2:
     * if (rightEncoder.getPosition() > 160) { //figure out value from hub to human
     * player station
     * Move(0, 0);
     * rightEncoder.setPosition(0);
     * my_Intake.Raise_Intake(0);
     * auto_control_state = Auto_States.STOP_BOT_3;
     * } else {
     * my_Intake.Raise_Intake(1);
     * Move(-0.5, 0);
     * }
     * break;
     * 
     * case REVERSE_BOT_2:
     * if (auto_Timer.get() > 1) {
     * Move(0, 0);
     * auto_control_state = Auto_States.IDLE;
     * auto_Timer.reset();
     * auto_Timer.start();
     * } else {
     * Move(0.4, 0);
     * }
     * break;
     * 
     * case ROTATE_90_RIGHT:
     * if (_pigeon.getYaw() >= 90) {
     * Move(0, 0);
     * rightEncoder.setPosition(0);
     * auto_control_state = Auto_States.STOP_BOT_2;
     * } else {
     * Move(0, -0.25);
     * }
     * break;
     * 
     */
    SmartDashboard.putNumber("RightEncoder", rightEncoder.getPosition());
  }
}
