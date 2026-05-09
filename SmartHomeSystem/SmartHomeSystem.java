import java.io.*;
import java.time.*;
import java.util.*;

/**
 * Main class that manages the smart home system.
 * It processes commands, maintains system time and controls all registered smart devices down below.
 * @author irem bekdemir
 * @version 1.0
 * @since 2026
 */
public class SmartHomeSystem {

    public static LocalDateTime currentTime;
    
    public static java.time.format.DateTimeFormatter format = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
     /** List of all smart devices in the system */
    static List<SmartDevice> devices = new ArrayList<>();

    static boolean lastWasZReport = false;

    /**
     * Entry point of the program.
     * Reads commands from input file and writes results to the output file.
     * @param args command line arguments (input file, output file)
     * @throws Exception if file operations fail
     */
    public static void main (String[] args) throws Exception {

        if (args.length < 2) return;

        String lastCommand = null;

       try ( BufferedReader br = new BufferedReader(new FileReader(args[0]));
            PrintWriter out = new PrintWriter(new FileWriter(args[1]))) {

            boolean firstCommand = true;
            String line;
            boolean terminate = false;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                out.println("COMMAND: " + line); 

                String [] parts = line.split("\t");

                if(terminate) continue;

                try {

                    if (firstCommand) {
                        firstCommand = false;

                        if (!parts[0].equals("SetInitialTime")) {
                            out.println("ERROR: First command must be set initial time! Program is going to terminate!");
                            return;
                        }
                    }

                    String result = process(parts);
                    if (result != null) {
                        out.println(result);
                    }           

                } catch (SmartExceptions e) {

                    String msg = e.getMessage();
                    out.println(msg.startsWith("ERROR:") ? msg : "ERROR: " + msg);

                    if (parts[0].equals("SetInitialTime")) {
                        terminate = true;
                        break;
                    }
                }

                lastCommand = parts[0];
            }  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes a single command and executes the corresponding action.
     * @param parts tokenized command input
     * @return result message if applicable, otherwise null
     * @throws SmartExceptions if command is invalid or fails
     */
    public static String process (String[] parts) throws SmartExceptions {

        switch (parts[0]) {

            case "SetInitialTime":

                if (parts.length != 2) {
                    throw new SmartExceptions("ERROR: First command must be set initial time! Program is going to terminate!");
                }

                if (currentTime != null) {
                    throw new SmartExceptions("ERROR: First command must be set initial time! Program is going to terminate!");
                }
                currentTime = parseTime(parts[1]);
                
                return "SUCCESS: Time has been set to " + parts[1] + "!";

            case "SetTime":

                checkInitialTime();

                if (parts.length != 2) {
                    throw new SmartExceptions("ERROR: Erroneous command!");
                }

                LocalDateTime newTime = parseTime(parts[1]);

                if (newTime.equals(currentTime)) {
                    throw new SmartExceptions("ERROR: There is nothing to change!");
                }

                setTime(newTime);
                return null;

            case "SkipMinutes":

                checkInitialTime();

                if (parts.length != 2) {
                    throw new SmartExceptions("ERROR: Erroneous command!");
                }

                int min;

                try {
                    min = Integer.parseInt(parts[1]);
                } catch (Exception e) {
                    throw new SmartExceptions("ERROR: Erroneous command!");
                }

                if (min < 0) {
                    throw new SmartExceptions("ERROR: Time cannot be reversed!");
                }

                if (min == 0) {
                    String errorMsg = "ERROR: There is nothing to skip!";
                    return errorMsg + "\n" + printZReport();
                }

                setTime(currentTime.plusMinutes(min));
                return null;

            case "Add":

                checkInitialTime();
                addDevice(parts);
                return null;

            case "Remove":


                if (parts.length != 2)
                    throw new SmartExceptions("ERROR: Erroneous command!");

                SmartDevice toRemove = find(parts[1]);

                toRemove.updateStatus(currentTime); 

                String status = toRemove.getStatus();

                devices.remove(toRemove);

                return "SUCCESS: Information about removed smart device is as follows:\n" + status;

            case "Switch":

                checkInitialTime();

                if (parts.length != 3){
                    throw new SmartExceptions("ERROR: Erroneous command!");
                }

                SmartDevice device = find(parts[1]);

                if (parts[2].equals("On")) {
                    device.switchOn(currentTime);
                } else if (parts[2].equals("Off")) {
                    device.switchOff(currentTime);
                } else {
                    throw new SmartExceptions("ERROR: Erroneous command!");
                }
                return null;

            case "SetSwitchTime":


                checkInitialTime();

                if (parts.length != 3){
                    throw new SmartExceptions("ERROR: Erroneous command!");
                }
                LocalDateTime t = parseTime(parts[2]);

                if (t.isBefore(currentTime)) {
                    throw new SmartExceptions("ERROR: Switch time cannot be in the past!");
                }

                find(parts[1]).setSwitchTime(t);
                return null;

            case "PlugIn":


                checkInitialTime();

                if (parts.length != 3) {
                    throw new SmartExceptions("ERROR: Erroneous command!");
                }
    
                SmartDevice d = find(parts[1]);

                if (!(d instanceof SmartPlug)) {
                    throw new SmartExceptions("ERROR: This device is not a smart plug!");
                }

                double amp;

                try {
                    amp = Double.parseDouble(parts[2]);

                } catch (Exception e) {
                    throw new SmartExceptions("ERROR: Ampere value must be a number!");
                }

                if (amp <= 0) {
                    throw new SmartExceptions("ERROR: Ampere value must be a positive number!");
                }

                ((SmartPlug) d).plugIn(amp);
                return null;

            case "PlugOut":
                lastWasZReport = false;

                if (parts.length != 2)
                    throw new SmartExceptions("ERROR: Erroneous command!");

                SmartDevice dev = find(parts[1]);

                if (!(dev instanceof SmartPlug)) {
                    throw new SmartExceptions("ERROR: This device is not a smart plug!");
                }

                ((SmartPlug) dev).plugOut(currentTime);
                return null;

            case "ChangeName":
                lastWasZReport = false;

                if (parts.length != 3)
                    throw new SmartExceptions("ERROR: Erroneous command!");

                if (parts[1].equals(parts[2])) {
                    throw new SmartExceptions("ERROR: Both of the names are the same, nothing changed!");
                }

                SmartDevice d1 = find(parts[1]);

                if (findSafe(parts[2]) != null) {
                    throw new SmartExceptions("ERROR: There is already a smart device with same name!");
                }

                d1.name = parts[2];
                return null;


            case "SetKelvin":
                lastWasZReport = false;

                if (parts.length != 3)
                    throw new SmartExceptions("ERROR: Erroneous command!");

                SmartDevice d2 = find(parts[1]);

                if (!(d2 instanceof SmartLamp)) {
                    throw new SmartExceptions("ERROR: This device is not a smart lamp!");
                }

                int kelvin;
                try {
                    kelvin = Integer.parseInt(parts[2]);
                } catch (Exception e) {
                    throw new SmartExceptions("ERROR: Kelvin value must be an integer!");
                }

                ((SmartLamp) d2).setKelvin(kelvin);
                return null;

            case "SetBrightness":
                lastWasZReport = false;

                if (parts.length != 3)
                    throw new SmartExceptions("ERROR: Erroneous command!");

                SmartDevice d3 = find(parts[1]);

                if (!(d3 instanceof SmartLamp)) {
                    throw new SmartExceptions("ERROR: This device is not a smart lamp!");
                }

                int brightness;
                try {
                    brightness = Integer.parseInt(parts[2]);
                } catch (Exception e) {
                    throw new SmartExceptions("ERROR: Brightness must be integer!");
                }

                ((SmartLamp) d3).setBrightness(brightness);
                return null;

            case "SetColor":
                lastWasZReport = false;

                if (parts.length != 4){
                    throw new SmartExceptions("ERROR: Erroneous command!");
                }

                SmartDevice d4 = find(parts[1]);

                if (!(d4 instanceof SmartColorLamp)) {
                    throw new SmartExceptions("ERROR: This device is not a smart color lamp!");
                }

                int color;

                if (!parts[2].startsWith("0x"))
                    throw new SmartExceptions("ERROR: Erroneous command!");

                try {
                    color = Integer.decode(parts[2]);
                } catch (Exception e) {
                    throw new SmartExceptions("ERROR: Erroneous command!");
                }

                if (color < 0 || color > 0xFFFFFF) {
                    throw new SmartExceptions("ERROR: Color code value must be in range of 0x0-0xFFFFFF!");
                }

                int bright;
                try {
                    bright = Integer.parseInt(parts[3]);
                } catch (Exception e) {
                    throw new SmartExceptions("ERROR: Brightness must be integer!");
                }

                if (bright < 0 || bright > 100) {
                    throw new SmartExceptions("ERROR: Brightness must be in range of 0%-100%!");
                }

                ((SmartColorLamp) d4).setColorCode(color);
                ((SmartColorLamp) d4).setBrightness(bright);

                return null;
               

            case "SetWhite":
                lastWasZReport = false;

                if (parts.length != 4){
                    throw new SmartExceptions("ERROR: Erroneous command!");
                }
                SmartDevice d5 = find(parts[1]);

                if (!(d5 instanceof SmartColorLamp)) {
                    throw new SmartExceptions("ERROR: This device is not a smart color lamp!");
                }

                int kel=0;

                try {
                    kel = Integer.parseInt(parts[2]);
                } catch (Exception e) {
                    throw new SmartExceptions("ERROR: Kelvin must be integer!");
                }

                int bri=0;
            
                try {
                    bri = Integer.parseInt(parts[3]);
                } catch (Exception e) {
                    throw new SmartExceptions( "ERROR: Brightness must be integer!");
                }

                if (kel < 2000 || kel > 6500)
                    throw new SmartExceptions("ERROR: Kelvin value must be in range of 2000K-6500K!");

                if (bri < 0 || bri > 100)
                    throw new SmartExceptions("ERROR: Brightness must be in range of 0%-100%!");
                
                ((SmartColorLamp) d5).setWhite(kel, bri);
                return null;

            case "Nop":

                checkInitialTime();

                SmartDevice next = null;

                for (SmartDevice dvc : devices) {
                    
                    if (dvc.switchTime != null) {
                        
                        if (next == null || dvc.switchTime.isBefore(next.switchTime)) {
                            next = dvc;
                        }
                    }
                }

                if (next == null) {
                    throw new SmartExceptions("ERROR: There is nothing to switch!");
                }

                setTime(next.switchTime);
                return printZReport();

            case "ZReport":
                return printZReport();
            
            default:
                
                throw new SmartExceptions("ERROR: Erroneous command!");
            
        }
    }

    /**
     * Updates the system time and triggers scheduled device actions.
     * @param newTime the updated time
     * @throws SmartExceptions if time is set backwards
     */
    static void setTime(LocalDateTime newTime) throws SmartExceptions {

        if (newTime.isBefore(currentTime)) { 
            throw new SmartExceptions("ERROR: Time cannot be reversed!");
        }

        while (true) {

            SmartDevice next = null;

            // Find the earliest scheduled switchTime that occurs before or at the new time
            for (SmartDevice d : devices) {
                if (d.switchTime != null && !d.switchTime.isAfter(newTime)) {
                    if (next == null || d.switchTime.isBefore(next.switchTime)) {
                        next = d;
                    }
                }
            }

            if(next == null) break;

            // Advance system time to the event time
            LocalDateTime eventTime = next.switchTime;
            for (SmartDevice d : devices) {
                d.updateStatus(eventTime);
            }
         
            currentTime = eventTime;
            next.autoToggle(currentTime);
            next.setSwitchTime(null); // Clear the trigger once it's fired
        }

        // Final update
        for (SmartDevice d : devices) {
            d.updateStatus(newTime);
        }

        currentTime = newTime;
    }
    
    /**
     * Generates a ZReport containing current system time and the status of all devices sorted by priority.
     * @return formatted ZReport string
     */
    static String printZReport() {
        List<SmartDevice> copy = new ArrayList<>(devices);

        copy.sort((d1, d2) -> {

        //switchTime priority
        if (d1.switchTime != null && d2.switchTime != null) {
            int cmp = d1.switchTime.compareTo(d2.switchTime);
            if (cmp != 0) return cmp;
        } else if (d1.switchTime != null) {
            return -1;
        } else if (d2.switchTime != null) {
            return 1;
        }

        //type order
        int t1 = getTypePriority(d1);
        int t2 = getTypePriority(d2);
        if (t1 != t2) return Integer.compare(t1, t2);

        // name
        return d1.name.compareTo(d2.name);
    });

    StringBuilder sb = new StringBuilder();
    sb.append("ZReport:\n");
    sb.append("Time is:\t").append(currentTime.format(format));

    //DEvice status
    for (SmartDevice d : copy) {
        sb.append("\n").append(d.getStatus());
    }

    return sb.toString();
}

    /**
     * Sorts devices alphabetically by their names.
     */
    static void sortDevices() {
        devices.sort(Comparator.comparing(d -> d.name));
    }

    /**
     * Parses a string into LocalDateTime using former defined format.
     * @param s date-time string
     * @return parsed LocalDateTime object
     * @throws SmartExceptions if format is not valid
     */
    static LocalDateTime parseTime(String s) throws SmartExceptions {
        try { 
            return LocalDateTime.parse(s, format); 
        } catch (Exception e) { 
            throw new SmartExceptions ("ERROR: Format of the initial date is wrong! Program is going to terminate!"); 
        }
    }

      /**
     * Checks whether the initial system time has been set or not.
     * @throws SmartExceptions if time has not initialized
     */
    static void checkInitialTime() throws SmartExceptions {
        if (currentTime == null) {
            throw new SmartExceptions("ERROR: Initial time has not set yet!");
        }
    }

     /**
     * Finds a device by its own name.
     * @param name device name
     * @return matching SmartDevice
     * @throws SmartExceptions if device is not found
     */
    static SmartDevice find(String name) throws SmartExceptions {
        SmartDevice foundDevice = null;

        for (SmartDevice d : devices) {
            if (d.name.equals(name)) {
                foundDevice = d;
                break;
            }
        }

        if (foundDevice != null) {
            return foundDevice;
        } else {
            throw new SmartExceptions("ERROR: There is not such a device!");
        }
    }

    /**
     * Safely finds a device by name without throwing exception.
     * @param name device name
     * @return SmartDevice if found, otherwise null
     */
    static SmartDevice findSafe(String name) {
        for (SmartDevice d : devices)
            if (d.name.equals(name)) {
                return d;
            }
        return null;
    }

    /**
     * Returns priority value for the device type.
     * Sorts in reports.
     * @param d smart device
     * @return priority value (lower means higher priority)
     */
    static int getTypePriority(SmartDevice d) {
        if (d instanceof SmartPlug) return 1;
        if (d instanceof SmartCamera) return 2;
        if (d instanceof SmartLamp && !(d instanceof SmartColorLamp)) return 3;
        if (d instanceof SmartColorLamp) return 4;
        return 5;
    }

    /**
     * Adds a new smart device to the system based on the given parameters.
     * @param section command parts describing the device
     * @throws SmartExceptions if parameters are invalid
     */
    static void addDevice(String[] section) throws SmartExceptions {

        if (section.length < 3)
            throw new SmartExceptions("ERROR: Erroneous command!");

        String type = section[1];
        String name = section[2];

        if (findSafe(name) != null)
            throw new SmartExceptions("ERROR: There is already a smart device with same name!");

        switch (type) {

            case "SmartLamp":
                SmartLamp lamp = new SmartLamp(name);

                if (section.length != 3 && section.length != 4 && section.length != 6)
                    throw new SmartExceptions("ERROR: Erroneous command!");

                if (section.length >= 4) {
                    if (section[3].equals("On")) {
                        lamp.switchOn(currentTime);
                    } else if (section[3].equals("Off")) {
                        
                    } else {
                        throw new SmartExceptions("ERROR: Erroneous command!");
                    }
                }

                if (section.length > 6)
                    throw new SmartExceptions("ERROR: Erroneous command!"); 

                if (section.length == 6) {
                    int kelvin;
                    int brightness;

                    try {
                        kelvin = Integer.parseInt(section[4]);
                        brightness = Integer.parseInt(section[5]);
                    } catch (Exception e) {
                        throw new SmartExceptions("ERROR: Kelvin and brightness values must be integers!");
                    }

                    if (kelvin < 2000 || kelvin > 6500) {
                        throw new SmartExceptions("ERROR: Kelvin value must be in range of 2000K-6500K!");
                    }

                    if (brightness < 0 || brightness > 100) {
                        throw new SmartExceptions("ERROR: Brightness must be in range of 0%-100%!");
                    }

                    lamp.setKelvin(kelvin);
                    lamp.setBrightness(brightness);
                }

                devices.add(lamp);
                break;

            case "SmartColorLamp":

            if (section.length != 3 && section.length != 4 && section.length != 6)
                throw new SmartExceptions("ERROR: Erroneous command!");

                SmartColorLamp cL = new SmartColorLamp(name);

                if (section.length >= 4) {
                    if (section[3].equals("On")) {
                        cL.switchOn(currentTime);
                    } else if (section[3].equals("Off")) {
                
                    } else {
                        throw new SmartExceptions("ERROR: Erroneous command!");
                    }
                }

                if (section.length > 6)
                    throw new SmartExceptions("ERROR: Erroneous command!");

                if (section.length == 6) {

                    if (section[4].startsWith("0x")) {

                        int color;
                        try {
                            color = Integer.decode(section[4]);
                        } catch (Exception e) {
                            throw new SmartExceptions("ERROR: Erroneous command!");
                        }

                        if (color < 0 || color > 0xFFFFFF)
                            throw new SmartExceptions("ERROR: Color code value must be in range of 0x0-0xFFFFFF!");

                        int brightness;
                        try {
                            brightness = Integer.parseInt(section[5]);
                        } catch (Exception e) {
                            throw new SmartExceptions("ERROR: Brightness value must be integer!");
                        }

                        if (brightness < 0 || brightness > 100)
                            throw new SmartExceptions("ERROR: Brightness must be in range of 0%-100%!");

                        cL.setColorCode(color);
                        cL.setBrightness(brightness);

                    } else {

                        int kelvin;
                        int brightness;

                        try {
                            kelvin = Integer.parseInt(section[4]);
                            brightness = Integer.parseInt(section[5]);
                        } catch (Exception e) {
                            throw new SmartExceptions("ERROR: Kelvin and brightness values must be integers!");
                        }

                        if (kelvin < 2000 || kelvin > 6500)
                            throw new SmartExceptions("ERROR: Kelvin value must be in range of 2000K-6500K!");

                        if (brightness < 0 || brightness > 100)
                            throw new SmartExceptions("ERROR: Brightness must be in range of 0%-100%!");

                        cL.setKelvin(kelvin);
                        cL.setBrightness(brightness);
                    }
                }

                devices.add(cL);
                break;

            case "SmartPlug":

                SmartPlug plug = new SmartPlug(name);

                if (section.length >= 4) {
                    if (section[3].equals("On")) {
                         plug.switchOn(currentTime);
                    } else if (section[3].equals("Off")) {
                        
                    } else { 
                        throw new SmartExceptions("ERROR: Erroneous command!");
                    }
                }

                if (section.length > 5)
                    throw new SmartExceptions("ERROR: Erroneous command!");

                if (section.length == 5) {

                    double amp;

                    try {
                        amp = Double.parseDouble(section[4]);

                    } catch (Exception e) {
                        throw new SmartExceptions("ERROR: Ampere value must be a number!");
                    }

                    if (amp <= 0) {
                        throw new SmartExceptions("ERROR: Ampere value must be a positive number!");
                    }

                    plug.plugIn(amp);
                }

                devices.add(plug);
                break;

            case "SmartCamera":

                if (section.length < 4){
                    throw new SmartExceptions("ERROR: Camera must have MB info!");
                }

                double mb;

                try {
                    mb = Double.parseDouble(section[3]);
                } catch (Exception e) {
                    throw new SmartExceptions("ERROR: MB value must be a number!");
                }

                if (mb <= 0) {
                    throw new SmartExceptions("ERROR: Megabyte value must be a positive number!");
                }

                SmartCamera cam = new SmartCamera(name, mb);

                if (section.length > 5)
                    throw new SmartExceptions("ERROR: Erroneous command!");

                if (section.length == 5) {
                    if (section[4].equals("On")) {
                        cam.switchOn(currentTime);
                    } else if (section[4].equals("Off")) {
                        
                    } else {
                        throw new SmartExceptions("ERROR: Erroneous command!");
                    }
                }

                devices.add(cam);
                break;

            default:
                throw new SmartExceptions("ERROR: Device type is wrong!");
        }
    }
}