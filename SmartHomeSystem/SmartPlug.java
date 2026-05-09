import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Represents a smart plug device.
 * A smart plug has an electrical device plugged into it and tracks energy consumption based on usage time.
 * @author irem bekdemir
 * @version 1.0
 * @since 2026
 */
public class SmartPlug extends SmartDevice {

    private double ampere;
    private boolean plugged;
    private double totalEnergy;
    private LocalDateTime lastPlugTime;


    /**
     * Constructs a SmartPlug with the given name.
     * @param name special name of the device
     * @throws SmartExceptions if name is invalid
     */
    public SmartPlug(String name) throws SmartExceptions {
        super(name);
    }

    /**
     * Plugs a device into the smart plug with the given ampere value.
     * @param ampere current drawn by the device
     * @throws SmartExceptions if already plugged or ampere value is invalid
     */
    public void plugIn(double ampere) throws SmartExceptions {
        if (plugged) {
            throw new SmartExceptions("ERROR: There is already an item plugged in to that plug!");
        }

        if (ampere <= 0) {
            throw new SmartExceptions("ERROR: Ampere value must be a positive number!");
        }

        this.ampere = ampere;
        this.plugged = true; 

        if (on) {
            this.lastPlugTime = SmartHomeSystem.currentTime;
        }

    }

    /**
     * Unplugs the currently connected device and ends the energy calculation.
     * @param currentTime current system time
     * @throws SmartExceptions if no device is plugged in
     */
    public void plugOut(LocalDateTime currentTime) throws SmartExceptions {
        
        if (!plugged) {
            throw new SmartExceptions("ERROR: This plug has no item to plug out from that plug!");
        }
        
        updateStatus(currentTime);

        this.plugged = false;
        this.ampere = 0;
        this.lastPlugTime = null; 

    }

    /**
     * Turns the plug on and starts recording the energy change if a device is connected.
     * @param currentTime current system time
     * @throws SmartExceptions if switching fails
     */
    @Override
        public void switchOn(LocalDateTime currentTime) throws SmartExceptions {
            super.switchOn(currentTime);

            if (plugged) {
                lastPlugTime = currentTime;
            } 
        }

    /**
     * Turns the plug off and stops energy tracking.
     * @param currentTime current system time
     * @throws SmartExceptions if switching fails
     */
    @Override
    public void switchOff(LocalDateTime currentTime) throws SmartExceptions {
        
        updateStatus(currentTime);
        super.switchOff(currentTime);
        lastPlugTime = null;
    }

    /**
     * Updates total energy consumption based on the time passed.
     * @param currentTime current system time
     */
    @Override
    public void updateStatus(LocalDateTime currentTime) {

        if (!on || !plugged || lastPlugTime == null) return;

        long seconds = java.time.Duration.between(lastPlugTime, currentTime).getSeconds();

        if (seconds > 0) {
            totalEnergy += (seconds / 3600.0) * ampere * 220;
            lastPlugTime = currentTime;
        }

    }

    /**
     * Returns the current status of the smart plug including energy usage.
     * @return formatted device status string
     */
    @Override
    public String getStatus() {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

        return "Smart Plug " + name + " is " + (on ? "on" : "off") +
                " and consumed " + String.format("%.2f", totalEnergy) +
                "W so far (excluding present usage), and its time to switch its status is " +
                (switchTime == null ? "null" : switchTime.format(format)) + ".";
    }

    /**
     * Hook method triggered when the plug is switched on.
     * @param currentTime current system time
     */
    @Override
    protected void onSwitchOn(LocalDateTime currentTime) {
        if (plugged) lastPlugTime = currentTime;
    }

    /**
     * Hook method triggered when the plug is switched off.
     * @param currentTime current system time
     */
    @Override
    protected void onSwitchOff(LocalDateTime currentTime) {
        lastPlugTime = null;
    }

}