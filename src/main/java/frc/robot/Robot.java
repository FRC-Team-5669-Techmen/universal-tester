/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static int MODE_UP_BUTTON    = 6,
                     MODE_DOWN_BUTTON  = 7,
                     ID_UP_BUTTON      = 8,
                     ID_DOWN_BUTTON    = 9,
                     PARAM_UP_BUTTON   = 10,
                     PARAM_DOWN_BUTTON = 11,
                     INITIALIZE_BUTTON = 1,
                     GO_BUTTON = 6;

  private static int NUM_MODES = 4;
  private int m_mode = 0, m_id = 0, m_param = 0;
  private boolean m_initialized = false, m_buttonPressed = false;
  private VictorSP m_pwm; // Kind of hacky, but this will work with servos connected to the rio too.
  private TalonSRX m_talon;
  private DoubleSolenoid m_solenoid;

  private Joystick throttle = new Joystick(1), joystick = new Joystick(0);

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  private void stopMode() {
    if (m_mode == 0) {
      m_pwm.set(0.0);
      m_pwm.close();
      m_pwm = null;
    } else if (m_mode == 1 || m_mode == 3) {
      m_talon.DestroyObject();
      m_talon = null;
    } else if (m_mode == 2) {
      m_solenoid.set(Value.kOff);
      m_solenoid.close();
      m_solenoid = null;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    if (m_buttonPressed) {
      m_buttonPressed = throttle.getRawButton(MODE_UP_BUTTON)
                     || throttle.getRawButton(MODE_DOWN_BUTTON)
                     || throttle.getRawButton(ID_UP_BUTTON)
                     || throttle.getRawButton(ID_DOWN_BUTTON)
                     || throttle.getRawButton(PARAM_UP_BUTTON)
                     || throttle.getRawButton(PARAM_DOWN_BUTTON)
                     || joystick.getRawButton(INITIALIZE_BUTTON);
    } else if (throttle.getRawButton(MODE_UP_BUTTON)) {
      if (m_initialized) stopMode();
      m_initialized = false;
      m_mode += 1;
      if (m_mode == NUM_MODES) {
        m_mode = 0;
      }
      m_buttonPressed = true;
    } else if (throttle.getRawButton(MODE_DOWN_BUTTON)) {
      if (m_initialized) stopMode();
      m_initialized = false;
      m_mode -= 1;
      if (m_mode == -1) {
        m_mode = NUM_MODES - 1;
      }
      m_buttonPressed = true;
    } else if (throttle.getRawButton(ID_UP_BUTTON)) {
      if (m_initialized) stopMode();
      m_initialized = false;
      m_id += 1;
      m_buttonPressed = true;
    } else if (throttle.getRawButton(ID_DOWN_BUTTON)) {
      if (m_initialized) stopMode();
      m_initialized = false;
      if (m_id > 0) {
        m_id -= 1;
      }
      m_buttonPressed = true;
    } else if (throttle.getRawButton(PARAM_UP_BUTTON)) {
      if (m_initialized) stopMode();
      m_initialized = false;
      m_param += 1;
      m_buttonPressed = true;
    } else if (throttle.getRawButton(PARAM_DOWN_BUTTON)) {
      if (m_initialized) stopMode();
      m_initialized = false;
      if (m_param > 0) {
        m_param -= 1;
      }
      m_buttonPressed = true;
    } else if (joystick.getRawButton(INITIALIZE_BUTTON)) {
      if (m_initialized) {
        stopMode();
        m_initialized = false;
      } else {
        if (m_mode == 0) {
          m_pwm = new VictorSP(m_id);
        } else if (m_mode == 1 || m_mode == 3) {
          m_talon = new TalonSRX(m_id);
          m_talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
          m_talon.setSensorPhase(m_param % 2 == 1); // Reverse if param is odd.
          m_talon.setInverted(m_param % 4 > 1);
        } else if (m_mode == 2) {
          m_solenoid = new DoubleSolenoid(m_id, m_param);
        }
        m_initialized = true;
      }
      m_buttonPressed = true;
    } else {
      m_buttonPressed = false;
    }

    if (m_initialized) {
      System.out.print("  [READY] ");
    } else {
      System.out.print("[WAITING] ");
    }

    double speed = -joystick.getY(), limit = throttle.getRawAxis(1) * -0.5 + 0.5;

    if (m_mode == 0) {
      if (m_initialized) {
        m_pwm.set(speed * limit);
        System.out.print("[Speed=" + String.format("%,14.2f", speed * limit * 100.0) + "%]");
      } else {
        System.out.print("PWM Mode [Port=" + m_id + "] ");
      }
      System.out.println();
    } else if (m_mode == 1) {
      if (m_initialized) {
        m_talon.set(ControlMode.PercentOutput, speed * limit);
        System.out.print("[Output=" + String.format("%,14.2f", speed * limit * 100.0) + "%] ");
        System.out.print("[Position=" + m_talon.getSelectedSensorPosition() + "]");
        System.out.print("[Velocity=" + m_talon.getSelectedSensorVelocity() + "]");
        if (joystick.getRawButton(GO_BUTTON)) {
          m_talon.setSelectedSensorPosition(0);
        }
      } else {
        System.out.print("SRX Speed Mode [CAN ID=" + m_id + "] ");
        System.out.print(m_param % 4 > 1 ? "[Motor=REVERSED] " : "[Motor=NORMAL] ");
        System.out.print(m_param % 2 == 1 ? "[Sensor=REVERSED] " : "[Sensor=NORMAL] ");
      }
      System.out.println();
    } else if (m_mode == 2) {
      if (m_initialized) {
        String action = "Idle";
        if (speed > 0.3) {
          m_solenoid.set(Value.kForward);
          action = "Forward";
        } else if (speed < -0.3) {
          m_solenoid.set(Value.kReverse);
          action = "Reverse";
        } else {
          m_solenoid.set(Value.kOff);
        }
        System.out.print("[Action=" + action + "]");
      } else {
        System.out.print("Solenoid Mode [Extend ID=" + m_id + "] [Contract ID=" + m_param + "] ");
      }
      System.out.println();
    } else if (m_mode == 3) {
      boolean go = joystick.getRawButton(GO_BUTTON);
      if (m_initialized) {
        double position = throttle.getRawAxis(1), scale = throttle.getRawAxis(0);
        position = position * -0.5 + 0.5; // 0 - 1
        scale = scale * -0.5 + 0.5; // 0 - 1
        scale = scale * 6.0 + 1.0; // 1 - 7
        scale = Math.pow(10.0, scale); // 10 - 10 million, logarithmically
        position = position * scale;
        System.out.print("[Position=" + m_talon.getSelectedSensorPosition() + "]");
        System.out.print("[Velocity=" + m_talon.getSelectedSensorVelocity() + "]");
        System.out.print("[Target=" + ((int) position) + "] ");
        System.out.print("[" + (go ? "RUNNING" : "PAUSED") + "] ");
        if (joystick.getRawButton(GO_BUTTON)) {
          m_talon.set(ControlMode.MotionMagic, position);
        } else {
          m_talon.neutralOutput();
        }
      } else {
        System.out.print("SRX Position Mode [CAN ID=" + m_id + "] ");
        System.out.print(m_param % 4 > 1 ? "[Motor=REVERSED] " : "[Motor=NORMAL] ");
        System.out.print(m_param % 2 == 1 ? "[Sensor=REVERSED] " : "[Sensor=NORMAL] ");
      }
      System.out.println();
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
