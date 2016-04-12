package si.pronic.zarja;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Artificial Intelligence for ordering icons regarding the usage
 */
public class AI
{
    // Constants for file name
    public static final String AI_PROPERTIES_FILE = "zarja-ai.properties";

    // Max value of counter
    public static final int MAX = 20;

    // Time after last usage to trigger counter value decrease
    public static final long DELTA = 24 * 60 * 60 * 1000;


    // Singleton to AI
    private static AI ai;

    // Properties
    private Properties prop;

    // Storage for app counters
    private Hashtable<String, CountInfo> appCounter = new Hashtable<>();


    /**
     * Constructor
     */
    private AI()
    {
        load();
        revaluateCounters();
    }


    /**
     * Instance getter
     * @return AI
     */
    public static AI getInstance()
    {
        if (ai == null)
        {
            ai = new AI();
        }

        return ai;
    }


    /**
     * Method loads counters
     */
    public void load()
    {
        appCounter = new Hashtable<>();
        prop = new Properties();
        try
        {
            File file = new File(MainActivity.getContext().getFilesDir(), AI_PROPERTIES_FILE);
            FileInputStream in = new FileInputStream(file);
            prop.load(in);

            Enumeration enumer = prop.keys();
            while (enumer.hasMoreElements())
            {
                String app = (String) enumer.nextElement();
                if (app.endsWith("_date"))
                {
                    continue;
                }

                if (app != null && !app.equals(""))
                {
                    int count = Integer.parseInt((String) prop.get(app));
                    long date = Long.parseLong((String) prop.get(app + "_date"));
                    appCounter.put(app, new CountInfo(count, date));
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger("Z").fine("AI properties file not found.");
        }
        catch (IOException ex)
        {
            Logger.getLogger("Z").fine("AI poroperties file could not be read.");
        }
    }


    /**
     * Method stores counters
     */
    public void save()
    {
        try
        {
            File file = new File(MainActivity.getContext().getFilesDir(), AI_PROPERTIES_FILE);
            FileOutputStream out = new FileOutputStream(file);

            Enumeration enumer = appCounter.keys();
            while (enumer.hasMoreElements())
            {
                String app = (String) enumer.nextElement();
                CountInfo countInfo = appCounter.get(app);
                prop.setProperty(app, String.valueOf(countInfo.getCount()));
                prop.setProperty(app + "_date", String.valueOf(countInfo.getDate()));
            }

            prop.store(out, "");
            out.flush();
            out.close();
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger("Z").fine("AI properties file not found.");
        }
        catch (IOException ex)
        {
            Logger.getLogger("Z").fine("AI properties file could not be written.");
        }
    }


    /**
     * Method returns counter for app
     * @param app App package name
     * @return Counter value
     */
    public int getCount(String app)
    {
        if (appCounter.containsKey(app))
        {
            return appCounter.get(app).getCount();
        }

        return -1;
    }


    /**
     * Method sets counter value
     * @param app App package name
     * @param count Counter valkue
     */
    public void setCount(String app, int count)
    {
        appCounter.put(app, new CountInfo(count, System.currentTimeMillis()));
        save();
    }


    /**
     * Method increases app counter value if below MAX
     * @param app App package namne
     */
    public void setAppUsage(String app)
    {
        int count = appCounter.get(app).getCount();

        if (count < MAX)
        {
            appCounter.put(app, new CountInfo(++count, System.currentTimeMillis()));
            save();
        }
    }


    /**
     * Method decreases counters if app not launched within 24 hours
     */
    public void revaluateCounters()
    {
        Enumeration enumer = appCounter.keys();
        while (enumer.hasMoreElements())
        {
            String app = (String) enumer.nextElement();
            if (app != null)
            {
                CountInfo countInfo = appCounter.get(app);
                if (System.currentTimeMillis() - countInfo.getDate() > DELTA)
                {
                    if (countInfo.getCount() > 0)
                    {
                        appCounter.put(app, new CountInfo(countInfo.getCount() - 1, System.currentTimeMillis()));
                    }
                }
            }
        }
        save();
    }


    /**
     * Method resets all app counters
     */
    public void resetCounters()
    {
        Enumeration enumer = appCounter.keys();
        while (enumer.hasMoreElements())
        {
            String app = (String) enumer.nextElement();
            if (app != null)
            {
                CountInfo countInfo = appCounter.get(app);
                if (countInfo != null)
                {
                    appCounter.put(app, new CountInfo(0, System.currentTimeMillis()));
                }
            }
        }
        save();
    }

}
