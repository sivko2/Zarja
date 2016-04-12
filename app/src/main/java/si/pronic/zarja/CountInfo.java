package si.pronic.zarja;


/**
 * Counter data
 */
public class CountInfo
{
    // Count value
    private int count;

    // Timestamp of last count change
    private long date;


    /**
     * Constructor
     * @param count Counter vaslue
     * @param date Last fate in ms from 01.01.70
     */
    public CountInfo(int count, long date)
    {
        this.count = count;
        this.date = date;
    }


    // getters and setters

    public int getCount()
    {
        return count;
    }


    public void setCount(int count)
    {
        this.count = count;
    }


    public long getDate()
    {
        return date;
    }


    public void setDate(long date)
    {
        this.date = date;
    }
}
