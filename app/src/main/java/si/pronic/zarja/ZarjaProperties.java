package si.pronic.zarja;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Property manager for settings
 */
public class ZarjaProperties
{
    // File name constant
    public static final String ZARJA_PROPERTIES_FILE = "zarja.properties";


    // Properties
    private static Properties prop;

    // Vales to store
    private static boolean leftHanded = false;
    private static boolean haptic = true;
    private static boolean sound = true;
    private static int speed = 1;


    /**
     * Loads settings
     */
    public static void load()
    {
        prop = new Properties();
        try
        {
            File file = new File(MainActivity.getContext().getFilesDir(), ZARJA_PROPERTIES_FILE);
            FileInputStream in = new FileInputStream(file);
            prop.load(in);
            leftHanded = Boolean.parseBoolean(prop.getProperty("lefthanded"));
            haptic = Boolean.parseBoolean(prop.getProperty("haptic"));
            sound = Boolean.parseBoolean(prop.getProperty("sound"));
            speed = Integer.parseInt(prop.getProperty("speed"));
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger("Z").fine("Properties file not found.");
            save();
        }
        catch (IOException ex)
        {
            Logger.getLogger("Z").fine("Properties file could not be read.");
        }
    }


    /**
     * Stores settings
     */
    public static void save()
    {
        prop.setProperty("lefthanded", String.valueOf(leftHanded));
        prop.setProperty("haptic", String.valueOf(haptic));
        prop.setProperty("sound", String.valueOf(sound));
        prop.setProperty("speed", String.valueOf(speed));
        try
        {
            File file = new File(MainActivity.getContext().getFilesDir(), ZARJA_PROPERTIES_FILE);
            FileOutputStream out = new FileOutputStream(file);
            prop.store(out, "");
            out.flush();
            out.close();
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger("Z").fine("Properties file not found.");
        }
        catch (IOException ex)
        {
            Logger.getLogger("Z").fine("Properties file could not be written.");
        }
    }


    // Setters and getters

    public static boolean isLeftHanded()
    {
        return leftHanded;
    }


    public static void setLeftHanded(boolean leftHanded)
    {
        ZarjaProperties.leftHanded = leftHanded;
    }


    public static boolean isHaptic()
    {
        return haptic;
    }


    public static void setHaptic(boolean haptic)
    {
        ZarjaProperties.haptic = haptic;
    }


    public static boolean isSound()
    {
        return sound;
    }


    public static void setSound(boolean sound)
    {
        ZarjaProperties.sound = sound;
    }


    public static int getSpeed()
    {
        return speed;
    }


    public static void setSpeed(int speed)
    {
        ZarjaProperties.speed = speed;
    }
}
