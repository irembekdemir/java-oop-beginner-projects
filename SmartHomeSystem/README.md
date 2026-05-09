# Smart Home System

## Project Description

Smart Home System is a Java-based simulation project that manages different types of smart devices through command-based input.
The system supports device management, scheduling, energy/storage tracking, and reporting functionalities.

The project demonstrates object-oriented programming concepts such as:

- Inheritance
- Polymorphism
- Abstraction
- Encapsulation
- Exception handling

---

## Supported Device Types
### SmartLamp
A standard smart lamp with adjustable:
- Kelvin value (2000K–6500K)
- Brightness level (0%–100%)
- SmartColorLamp

An advanced smart lamp that supports:

- RGB color mode
- White light mode
- Brightness adjustment
  
### SmartPlug

A smart plug that:
- Tracks connected device energy consumption
- Supports plug-in / plug-out operations
- Calculates total consumed energy

### SmartCamera

A smart security camera that:
- Records while active
- Tracks storage usage over time

---

## Features
- Add and remove smart devices
- Turn devices on/off
- Schedule automatic switching
- Skip system time
- Generate detailed ZReports
- Track:
-- Energy consumption
-- Storage usage
-- Device states
- Error handling with custom exceptions
 
## Project Structure
```text
SmartHomeSystem.java     -> Main system controller
SmartDevice.java         -> Abstract base device class
SmartLamp.java           -> Basic smart lamp
SmartColorLamp.java      -> RGB smart lamp
SmartPlug.java           -> Smart plug device
SmartCamera.java         -> Smart camera device
SmartExceptions.java     -> Custom exception class

```

## Input / Output
The program works with two files:
- Input file containing commands
- Output file where results are written

Program execution:
``` java SmartHomeSystem input.txt output.txt ```

## Example Commands
```text 
SetInitialTime    2026-05-09_12:00:00
Add    SmartPlug    Plug1
Switch    Plug1    On
PlugIn    Plug1    2.5
SkipMinutes    60
ZReport
```

## Technologies Used
- Java
- Java Collections Framework
- LocalDateTime API
- Object-Oriented Programming
  
## Error Handling

The system uses a custom exception class:
```SmartExceptions```
All invalid operations and command errors are handled with descriptive error messages.

## Author

Developed by [Github Account]((https://github.com/irembekdemir)

## Version
```1.0```
