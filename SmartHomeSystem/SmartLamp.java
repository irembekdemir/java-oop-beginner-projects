import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a basic smart lamp device.
 * A smart lamp has adjustable brightness and temperature (kelvin).
 * @author irem bekdemir
 * @version 1.0
 * @since 2026
 */
public class SmartLamp extends SmartDevice {

    protected int kelvin = 4000;
    protected int brightness = 100;

    /**
     * Constructs a SmartLamp with the given name.
     * @param name unique name of the device
     * @throws SmartExceptions if name is invalid
     */
    public SmartLamp(String name) throws SmartExceptions {
        super(name);
    }

    /**
     * Sets the temperature of the lamp.
     *
     * @param k kelvin value (must be between 2000K and 6500K)
     * @throws SmartExceptions if value is out of range
     */
    public void setKelvin(int k) throws SmartExceptions {
        if ( k < 2000 || k > 6500) {
            throw new SmartExceptions("ERROR: Kelvin value must be in range of 2000K-6500K!");
        }
        this.kelvin = k;
    }

    /**
     * Sets the brightness level of the lamp.
     * @param b brightness value (0-100)
     * @throws SmartExceptions if value is out of range
     */
    public void setBrightness(int b) throws SmartExceptions {
        if (b <0 || b > 100) {
            throw new SmartExceptions("ERROR: Brightness must be in range of 0%-100%!");
        }
        this.brightness = b;
    }

    /**
     * Updates device status based on current system time.
     * (No additional behavior for SmartLamp)
     * @param currentTime current system time
     */
    @Override
    public void updateStatus( LocalDateTime currentTime) {}

    /**
     * Hook method executed when the lamp is switched on.
     * @param time switch-on time
     */
    @Override
    protected void onSwitchOn(LocalDateTime time) {}

    /**
     * Hook method executed when the lamp is switched off.
     * @param time switch-off time
     */
    @Override
    protected void onSwitchOff(LocalDateTime time) {}

     /**
     * Returns the current status of the smart lamp as a the format described.
     * @return device status information
     */
    @Override 
    public String getStatus() {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        return "Smart Lamp " + name + " is " + (on ? "on" : "off")
            + " and its kelvin value is " + kelvin + "K with "
            + brightness + "% brightness, and its time to switch its status is "
            + (switchTime == null ? "null" : switchTime.format(format)) + ".";
    }
}