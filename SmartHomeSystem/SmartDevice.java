import java.time.*;

/**
 * Abstract base class for all smart devices in the system.
 * Defines common properties such as name, status (on/off) and scheduled switch time along with core behaviors to be override later on.
 * @author irem bekdemir
 * @version 1.0
 * @since 2026
 */
public abstract class SmartDevice {
    protected String name;
    protected boolean on;
    protected LocalDateTime switchTime;

    /**
     * Constructs a SmartDevice with the given name.
     * @param name special device name
     * @throws SmartExceptions if name is null or empty
     */
    public SmartDevice(String name) throws SmartExceptions {

        if (name == null || name.isEmpty()) {
            throw new SmartExceptions("ERROR: Erroneous command!");
        }

        this.name = name;
        this.on = false;
    }

    /**
     * Switches the device on.
     * @param currentTime current system time
     * @throws SmartExceptions if device is already on
     */
    public void switchOn(LocalDateTime currentTime) throws SmartExceptions {
        if (this.on) {
            throw new SmartExceptions("ERROR: This device is already switched on!");
        }
        this.on = true;
        onSwitchOn(currentTime);
    }

    /**
     * Switches the device off.
     * Also updates device-specific status before turning off.
     * @param currentTime current system time
     * @throws SmartExceptions if device is already off
     */
    public void switchOff(LocalDateTime currentTime) throws SmartExceptions {
        if (!this.on) {
            throw new SmartExceptions("ERROR: This device is already switched off!");
        }

        updateStatus(currentTime);
        this.on = false;
        onSwitchOff(currentTime);
    }

    /**
     * Automatically toggles the device state (on ↔ off) based on scheduled switch time.
     * @param currentTime current system time
     * @throws SmartExceptions if switching fails
     */
    public void autoToggle(LocalDateTime currentTime) throws SmartExceptions {
        if (this.on) {
            this.switchOff(currentTime);
        }else{
            this.switchOn(currentTime);
        }
        this.switchTime = null;
    }

    /**
     * Sets the scheduled switch time for the device.
     * @param currentTime time at which device will toggle
     * @throws SmartExceptions if invalid time is provided
     */
    public void setSwitchTime(LocalDateTime currentTime) throws SmartExceptions {
        this.switchTime = currentTime;
    }

    /**
     * Hook method executed when the device is switched on.
     * Subclasses implement device-specific behavior.
     * @param time current system time
     */
    protected abstract void onSwitchOn(LocalDateTime time);

    /**
     * Hook method executed when the device is switched off.
     * Subclasses implement device-specific behavior.
     * @param time current system time
     */
    protected abstract void onSwitchOff(LocalDateTime time); 
    
    /**
     * Updates device-specific status based on time progression.
     * @param currentTime current system time
     */
    public abstract void updateStatus(LocalDateTime currentTime);

    /**
     * Returns a formatted string describing the current device status.
     * @return device status string
     */
    public abstract String getStatus();

    
}


