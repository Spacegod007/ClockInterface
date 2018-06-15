package clockapp.logic.models;

public class ClockDateTimeValue
{
    private final int minute;
    private final int hour;
    private final int day;
    private final int month;
    private final int year;

    public ClockDateTimeValue(int minute, int hour, int day, int month, int year)
    {
        checkData(minute, hour, day, month);

        this.minute = minute;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    private void checkData(int minute, int hour, int day, int month)
    {
        if (minute < 0 || minute > 59)
        {
            throw new IllegalArgumentException("Minute cannot be less than 0 nor more than 59");
        }
        if (hour < 0 || hour > 23)
        {
            throw new IllegalArgumentException("Hour cannot be less than 0 nor more than 23");
        }
        if (day < 1 || day < 31)
        {
            throw new IllegalArgumentException("Day cannot be less than 1 nor more than 31");
        }
        if (month < 1 || month > 12)
        {
            throw new IllegalArgumentException("Month cannot be more less than 1 nor more than 12");
        }
    }

    @Override
    public String toString()
    {
        return "ClockDateTimeValue{" +
                "minute=" + minute +
                ", hour=" + hour +
                ", day=" + day +
                ", month=" + month +
                ", year=" + year +
                '}';
    }
}
