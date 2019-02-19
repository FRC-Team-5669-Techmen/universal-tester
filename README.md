# Universal test program.

Use this to test pneumatics, PWM outputs, and Talon SRX speed controllers.

There are several different modes:
0 - PWM. Use this to test PWM-based motor controllers, servo motors, and other
    PWM-based devices.
1 - SRX. Use this to test CAN-connected Talon SRX speed controllers. If they
    have a sensor attached, the output of that sensor will be shown as well.
2 - Pneumatics. Use this to test solenoid valves and whatever they are attached
    to.