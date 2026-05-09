import java.time.*;
import java.time.format.DateTimeFormatter;
/**
 * Represents a smart camera device.
 * A smart camera records video when it is on and tracks storage usage based on recording duration.
 */
public class SmartCamera extends SmartDevice {

    private double megabytePerMinute;
    private double totalStorage = 0;
    private LocalDateTime lastRecordTime;

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
    
    /**
     * Constructs a SmartCamera with the given name and storage rate.
     * @param name special name of the device
     * @param mb storage usage per minute
     * @throws SmartExceptions if mb is not positive
     */
    public SmartCamera(String name, double mb) throws SmartExceptions {
        super(name);
        
        if (mb <= 0) {
            throw new SmartExceptions("ERROR: Megabyte value must be a positive number!");
        }

        this.megabytePerMinute = mb;
    }

    /**
     * Turns the camera on and starts recording.
     * @param currentTime current system time
     * @throws SmartExceptions if switching fails
     */
   @Override
    public void switchOn(LocalDateTime currentTime) throws SmartExceptions {
        super.switchOn(currentTime);
        lastRecordTime=currentTime; //Starting the recording
    }

    /**
     * Turns the camera off and stops recording after updating storage usage.
     * @param currentTime current system time
     * @throws SmartExceptions if switching fails
     */
    @Override 
    public void switchOff(LocalDateTime currentTime) throws SmartExceptions {

        updateStatus(currentTime);
        super.switchOff(currentTime);
        lastRecordTime = null; //Stopping the recording
    }

    /**
     * Hook method triggered when the camera is switched on.
     * @param time current system time
     */
    @Override
    protected void onSwitchOn(LocalDateTime time) {
        lastRecordTime = time;
    }

    /**
     * Hook method triggered when the camera is switched off.
     * @param time current system time
     * @author irem bekdemir
     * @version 1.0
     * @since 2026
     */
    @Override
    protected void onSwitchOff(LocalDateTime time) {
        lastRecordTime = null;
    }

    /**
     * Updates total storage usage based on recording duration.
     * @param currentTime current system time
     */
    @Override
    public void updateStatus(LocalDateTime currentTime) {
       
        if (on && lastRecordTime != null) {
            long seconds = Duration.between(lastRecordTime, currentTime).getSeconds();

            if (seconds > 0) {
                totalStorage += (seconds / 60.0) * megabytePerMinute;
                lastRecordTime = currentTime;
            }
        }
    }

    /**
     * Returns the current status of the smart camera including storage usage.
     * @return formatted dvice status 
     */
    @Override
    public String getStatus() {
        
        return "Smart Camera " + name + " is " + (on ? "on" : "off") +
                " and used " + String.format(java.util.Locale.US, "%.2f", totalStorage) +
                " MB of storage so far (excluding present usage), and its time to switch its status is " +
                (switchTime == null ? "null" : switchTime.format(format)) + "."; 
    }
}
