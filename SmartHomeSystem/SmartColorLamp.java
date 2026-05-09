import java.time.format.DateTimeFormatter;

/**
 * Represents an advanced smart lamp that supports both color mode and white light mode.
 * It extends SmartLamp by adding RGB color functionality.
 * @author irem bekdemir
 * @version 1.0
 * @since 2026
 */
public class SmartColorLamp extends SmartLamp {

    /** RGB color code (0x000000 - 0xFFFFFF) */
    private int colorCode;
    private Mode mode = Mode.WHITE;

    /**
     * Enum representing lamp operating modes.
     */
    public enum Mode {
        /** Color mode using RGB values */
        COLOR, 

        /** White light mode using kelvin value */
        WHITE
    }

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    /**
     * Constructs a SmartColorLamp with the given name.
     * @param name special name of the device
     * @throws SmartExceptions if name is invalid
     */
    public SmartColorLamp (String name) throws SmartExceptions {
        super(name);
    }

    /**
     * Sets the lamp to color mode with the given RGB color code.
     * @param code color code (0x000000 - 0xFFFFFF)
     * @throws SmartExceptions if value is out of range
     */
    public void setColorCode(int code) throws SmartExceptions {
        if (code < 0 || code > 0xFFFFFF) {
            throw new SmartExceptions("ERROR: Color code value must be in range of 0x0-0xFFFFFF!");
        }

        this.mode = Mode.COLOR;
        this.colorCode = code;
    }

    /**
     * Sets the lamp to white mode with specified kelvin and brightness values.
     * @param kelvin color temperature (2000K - 6500K)
     * @param brightness brightness level (0-100)%
     * @throws SmartExceptions if values are invalid
     */
    public void setWhite (int kelvin, int brightness) throws SmartExceptions{
        setKelvin(kelvin);
        setBrightness(brightness);
        this.mode = Mode.WHITE;
    }

    /**
     * Returns the current status of the smart color lamp.
     * Displays either RGB color or kelvin value depending on mode.
     * @return formatted device status string
     */
    @Override
    public String getStatus() {

        String value;

        if (mode == Mode.COLOR) {
                value = String.format("0x%06X", colorCode);
        } else {
            value = kelvin + "K";
        }        

        return "Smart Color Lamp " + name + " is " + (on ? "on" : "off") +
                " and its color value is " + value
                + " with " + brightness + "% brightness, and its time to switch its status is " +
                (switchTime == null ? "null" : switchTime.format(format)) + ".";
       
    }
}