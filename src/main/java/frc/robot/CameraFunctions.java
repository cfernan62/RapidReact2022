package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CameraFunctions {
  public static boolean LimelightHasValidTarget = false;
  public static double LimelightDriveCommand = 0.0;
  public static double LimelightSteerCommand = 0.0;
  public double distanceFromLimelightToGoalInches;
  final double DRIVE_K = 0.06;
  final double VMIN = 0.2;
  final double VMAX = 0.5;
  final double TXMIN = 1;
  final double TXMAX = 20;
  final double TAMAX = 3; // increases as target moves closer
  final double STEER_K = (VMAX - VMIN)/(TXMAX - TXMIN);
  //final double STEER_K = 0.05;
  final double DESIRED_TARGET_AREA = 11.0;
  public double tv;
  public double tx;
  public double ty;
  public double ta;
  public double pipe;

  public void Update_Limelight_Tracking() {
    tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
    tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
    ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
    ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);
    pipe = NetworkTableInstance.getDefault().getTable("limelight").getEntry("getpipe").getDouble(0);
    SmartDashboard.putNumber("tv", tv);
    SmartDashboard.putNumber("tx", tx);
    SmartDashboard.putNumber("ty", ty);
    SmartDashboard.putNumber("ta", ta);

    // how many degrees back is your limelight rotated from perfectly vertical?
    double limelightMountAngleDegrees = 25.0;

    // distance from the center of the Limelight lens to the floor
    double limelightLensHeightInches = 31.0;

    // distance from the target to the floor
    double goalHeightInches = 105.6;

    double angleToGoalDegrees = limelightMountAngleDegrees + ty;
    double angleToGoalRadians = angleToGoalDegrees * (3.14159 / 180.0);

    if (tv < 1) {
      LimelightHasValidTarget = false;
      LimelightDriveCommand = 0.0;
      LimelightSteerCommand = 0.0;
    } else {
      LimelightHasValidTarget = true;
      // calculate distance
      distanceFromLimelightToGoalInches = (goalHeightInches - limelightLensHeightInches) / Math.tan(angleToGoalRadians);
      SmartDashboard.putNumber("Distance to Hub", distanceFromLimelightToGoalInches);
      if (tx >= 1.0) {
        LimelightSteerCommand = 0.0 + tx * STEER_K;
      } else if (tx <= -1.0) {
        LimelightSteerCommand = -0.0 + tx * STEER_K;
      } else {
        LimelightSteerCommand = 0;
      }

      if (ta >= 2) // If target is too close, don't move forward
        LimelightDriveCommand = 0.0;
      else {
        LimelightDriveCommand = VMIN + (TAMAX - ta) * DRIVE_K;
        if (LimelightDriveCommand > 0.6)
          LimelightDriveCommand = 0.6;
      }
    }
  }

  public void Set_Pipe(int pipenumber) {
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(pipenumber);
    switch (pipenumber) {
      case 0:
        SmartDashboard.putString("pipeline", "0-Blue");
        break;

      case 1:
        SmartDashboard.putString("pipeline", "1-Red");
        break;

      default:
        SmartDashboard.putString("pipeline", "Don't know");
        break;
    }
  }

  public void Set_LED(Boolean state) {
    if (state == true)
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3); // turn on
    else
      NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1); // turn off
  }
}
