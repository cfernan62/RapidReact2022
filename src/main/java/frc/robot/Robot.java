// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
//import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Climb.Climb_States;
import frc.robot.RealDrive.Auto_States;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.XboxController;

import edu.wpi.first.cameraserver.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private CameraFunctions LimeLight = new CameraFunctions();
  public final Joystick m_stick = new Joystick(1);
  public final XboxController m_control = new XboxController(0);
  // private LemonDrive LDrive = new LemonDrive();
  private Climb climber = new Climb();
  public Intake m_intake = new Intake();
  private RealDrive RDrive = new RealDrive(m_intake);
  // private Timer climb_Timer = new Timer();
  public Climb_States climb_state;
  Boolean BlueCargo;
  double MaxSpeed;

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    CameraServer.startAutomaticCapture();
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    climber.small_cylinder_extend(false);
    climber.main_cylinder_extend(false);
    climber.angle_cylinder_extend(false);
    LimeLight.Set_LED(false);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and
   * test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_intake.ball_cylinder_extend(false);
    RDrive.auto_control_state = Auto_States.START_AUTO;
    LimeLight.Set_LED(true);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putNumber("Encoder", RDrive.rightEncoder.getPosition());
    // RDrive.Auto_Control();
    RDrive.Auto_Control();
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    BlueCargo = Preferences.getBoolean("BlueCargo", false);
    MaxSpeed = Preferences.getDouble("MaxSpeed", 1);
    climb_state = Climb_States.IDLE;
    climber.small_cylinder_extend(false);
    climber.main_cylinder_extend(false);
    climber.angle_cylinder_extend(true);
    LimeLight.Set_LED(true);
    SmartDashboard.putString("climb", "Teleop Init Climber");
    RDrive.SetIdleModeToCoast(true);
    SmartDashboard.putNumber("Max", MaxSpeed);
    SmartDashboard.putNumber("Encoder", RDrive.rightEncoder.getVelocity());

  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    double fwdspeed;
    double turnspeed;
    double intakespeed;

    fwdspeed = m_stick.getY();
    turnspeed = m_stick.getX();

    if (fwdspeed < 0.08 && fwdspeed > -0.08)
      fwdspeed = 0;

    if (LimeLight != null) { // Do we have LimeLight connected
      if (m_stick.getRawButton(3)) // turn limelight on
        LimeLight.Set_LED(true);

      if (m_stick.getRawButton(4)) // turn limelight off
        LimeLight.Set_LED(false);

      LimeLight.Update_Limelight_Tracking();

      SmartDashboard.putNumber("lime drive", CameraFunctions.LimelightDriveCommand);
      SmartDashboard.putNumber("lime steer", CameraFunctions.LimelightSteerCommand);

      if (m_stick.getRawButton(5))
        LimeLight.Set_Pipe(4); // select red cargo //edit new for reflective tape

      if (m_stick.getRawButton(6))
        LimeLight.Set_Pipe(0); // select blue cargo
    }

    m_intake.Raise_Intake(-m_control.getLeftY());
    intakespeed = m_control.getRightY();
    if (intakespeed > -0.05 && intakespeed < 0.05)
      intakespeed = 0;

    if (m_control.getRawButton(1)) {
      m_intake.Move_Intake(-0.5); // Kicking CARGO out
      if (m_intake.AtSetSpeed(-2000))
        m_intake.ball_cylinder_extend(true);
      else
        m_intake.ball_cylinder_extend(false);
    } else if (m_control.getRawButton(4)) {
      m_intake.Move_Intake(-0.35); // Kicking CARGO out
      if (m_intake.AtSetSpeed(-1300))
        m_intake.ball_cylinder_extend(true);
      else
        m_intake.ball_cylinder_extend(false);
    } else if (intakespeed > 0.05) {
      if (intakespeed > 0.65)
        intakespeed = 0.65;
      m_intake.Move_Intake(intakespeed);
      m_intake.ball_cylinder_extend(false);
    } else {
      m_intake.Move_Intake(0);
      m_intake.ball_cylinder_extend(false);
    }

    SmartDashboard.putNumber("intake speed", intakespeed);
    // SmartDashboard.getNumber("intake speed", RDrive.leftEncoder);

    if (RDrive != null) {
      if (m_stick.getRawButton(1) && LimeLight != null) { // use limelight to move robot
        RDrive.Move(-CameraFunctions.LimelightDriveCommand, CameraFunctions.LimelightSteerCommand);
      } else { // using joystick to use robot
        fwdspeed = m_stick.getY();
        turnspeed = m_stick.getX();
        if (fwdspeed > MaxSpeed)
          fwdspeed = MaxSpeed;
        if (fwdspeed < -MaxSpeed)
          fwdspeed = -MaxSpeed;
        RDrive.Move(fwdspeed, turnspeed);
      }
      if (RDrive._loopCount++ > 10) {
        RDrive._loopCount = 0;
        double[] ypr = new double[3];
        ///RDrive._pigeon.getYawPitchRoll(ypr);
        SmartDashboard.putNumber("yaw", ypr[0]);
      }
      if (climber != null) { // Do we have a climber system?
        ClimbHangar();
      }

      SmartDashboard.putNumber("MaxSpeed", MaxSpeed);
      SmartDashboard.putBoolean("BlueCargo", BlueCargo);
      SmartDashboard.putNumber("forward speed", fwdspeed);
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    LimeLight.Set_LED(false);
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
    RDrive.rightEncoder.setPosition(0);
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    // double fwdspeed;
    // double turnspeed;

    SmartDashboard.putNumber("encodertest", RDrive.rightEncoder.getPosition());

    /*
     * if (RDrive.rightEncoder.getPosition() < -14) {
     * RDrive.Move(0, 0);
     * } else {
     * RDrive.Move(-0.25, 0);
     * }
     */

    if (climber != null) { // Do we have a climber system?

      if (m_control.getRawButtonPressed(5))
        climber.main_cylinder_extend(true);

      if (m_control.getRawButtonPressed(6))
        climber.main_cylinder_extend(false);

      if (m_control.getRawButtonPressed(3))
        climber.small_cylinder_extend(true);

      if (m_control.getRawButtonPressed(4))
        climber.small_cylinder_extend(false);

      if (m_control.getRawButtonPressed(1))
        climber.angle_cylinder_extend(true);

      if (m_control.getRawButtonPressed(2))
        climber.angle_cylinder_extend(false);

    }
    if (m_control.getRawButtonPressed(7))
      m_intake.ball_cylinder_extend(true);
    if (m_control.getRawButtonPressed(8))
      m_intake.ball_cylinder_extend(false);
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }

  public void ClimbHangar() {

    switch (climb_state) {
      case IDLE:
        SmartDashboard.putString("climb", "IDLE");
        climber.small_cylinder_extend(false);
        climber.main_cylinder_extend(false);
        climber.angle_cylinder_extend(true);
        if (m_control.getRawButtonPressed(7)) {
          climb_state = Climb_States.START_CLIMB;
          SmartDashboard.putString("climb", "START_CLIMB");
        }
        break;

      case START_CLIMB:
        if (m_control.getRawButtonPressed(7)) {
          climb_state = Climb_States.A;
          SmartDashboard.putString("climb", "REACH_BAR_2"); // 1st pull up
          climber.main_cylinder_extend(true);
          climber.small_cylinder_extend(false);
          climber.angle_cylinder_extend(true);
        }
        break;

      case A:
        if (m_control.getRawButtonPressed(7)) {
          climb_state = Climb_States.B;
          SmartDashboard.putString("climb", "PULL_UP_BAR_2");
          climber.main_cylinder_extend(false);
          climber.small_cylinder_extend(false);
          climber.angle_cylinder_extend(true);
          if (m_control.getRawButtonPressed(8)) {
            climb_state = Climb_States.H;
          }
        }
        break;

      case B:
        if (m_control.getRawButtonPressed(7)) {
          climb_state = Climb_States.C0;
          SmartDashboard.putString("climb", "XFER_ON_SPRING_1");
          climber.main_cylinder_extend(false);
          climber.small_cylinder_extend(true);
          climber.angle_cylinder_extend(true);
          if (m_control.getRawButtonPressed(8)) {
            climb_state = Climb_States.H;
          }
        }
        break;

      case C0:
        if (m_control.getRawButtonPressed(7)) {
          climb_state = Climb_States.C;
          SmartDashboard.putString("climb", "XFER_ON_SPRING_2");
          climber.main_cylinder_extend(false);
          climber.small_cylinder_extend(true);
          climber.angle_cylinder_extend(false);
          if (m_control.getRawButtonPressed(8)) {
            climb_state = Climb_States.H;
          }
        }
        break;

      case C:
        if (m_control.getRawButtonPressed(7)) {
          climb_state = Climb_States.D;
          SmartDashboard.putString("climb", "TILT_BACK_BAR_3");
          climber.main_cylinder_extend(false);
          climber.small_cylinder_extend(true);
          climber.angle_cylinder_extend(false);
          if (m_control.getRawButtonPressed(8)) {
            climb_state = Climb_States.H;
          }
        }
        break;

      case D:
        if (m_control.getRawButtonPressed(7)) {
          climb_state = Climb_States.E;
          SmartDashboard.putString("climb", "REACH_BAR_3");
          climber.main_cylinder_extend(true);
          climber.small_cylinder_extend(true);
          climber.angle_cylinder_extend(false);
          if (m_control.getRawButtonPressed(8)) {
            climb_state = Climb_States.H;
          }
        }
        break;

      case E:
        if (m_control.getRawButtonPressed(7)) {
          climb_state = Climb_States.F;
          SmartDashboard.putString("climb", "TILT_UP_BAR_3");
          climber.main_cylinder_extend(true);
          climber.small_cylinder_extend(true);
          climber.angle_cylinder_extend(true);
          if (m_control.getRawButtonPressed(8)) {
            climb_state = Climb_States.H;
          }
        }
        break;

      case F:
        if (m_control.getRawButtonPressed(7)) {
          climb_state = Climb_States.G;
          SmartDashboard.putString("climb", "UNHITCH_BAR_2");
          climber.main_cylinder_extend(true);
          climber.small_cylinder_extend(false);
          climber.angle_cylinder_extend(true);
          if (m_control.getRawButtonPressed(8)) {
            climb_state = Climb_States.H;
          }
        }
        break;

      case G:
        if (m_control.getRawButtonPressed(7)) {
          climb_state = Climb_States.B;
          SmartDashboard.putString("climb", "RESTART");
          climber.main_cylinder_extend(false);
          climber.small_cylinder_extend(false);
          climber.angle_cylinder_extend(true);
          if (m_control.getRawButtonPressed(8)) {
            climb_state = Climb_States.H;
          }
        }
        break;

      case H:
        if (m_control.getRawButtonPressed(8)) {
          climb_state = Climb_States.START_CLIMB;
          SmartDashboard.putString("climb", "START_CLIMB");
          climber.small_cylinder_extend(false);
          climber.main_cylinder_extend(false);
          climber.angle_cylinder_extend(true);
        }
        break;

    }

  }

}
