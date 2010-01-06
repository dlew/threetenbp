/*
 * Copyright (c) 2007-2010 Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar.field;

import java.util.Locale;

import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * A month-of-year, such as 'July'.
 * <p>
 * <code>MonthOfYear</code> is an enum representing the 12 months of the year -
 * January, February, March, April, May, June, July, August, September, October,
 * November and December.
 * <p>
 * In addition to the textual enum name, each month-of-year has an <code>int</code> value.
 * The <code>int</code> value follows normal usage and the ISO-8601 standard,
 * from 1 (January) to 12 (December). It is recommended that applications use the enum
 * rather than the <code>int</code> value to ensure code clarity.
 * <p>
 * <b>Do not use ordinal() to obtain the numeric representation of <code>MonthOfYear</code>.
 * Use getValue() instead.</b>
 * <p>
 * This enum represents a common concept that is found in many calendar systems.
 * As such, this enum may be used by any calendar system that has the month-of-year
 * concept with a twelve month year where the names are equivalent to those defined.
 * Note that the implementation of {@link DateTimeFieldRule} for month-of-year may
 * vary by calendar system.
 * <p>
 * MonthOfYear is an immutable and thread-safe enum.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public enum MonthOfYear {

    /**
     * The singleton instance for the month of January.
     */
    JANUARY,
    /**
     * The singleton instance for the month of February.
     */
    FEBRUARY,
    /**
     * The singleton instance for the month of March.
     */
    MARCH,
    /**
     * The singleton instance for the month of April.
     */
    APRIL,
    /**
     * The singleton instance for the month of May.
     */
    MAY,
    /**
     * The singleton instance for the month of June.
     */
    JUNE,
    /**
     * The singleton instance for the month of July.
     */
    JULY,
    /**
     * The singleton instance for the month of August.
     */
    AUGUST,
    /**
     * The singleton instance for the month of September.
     */
    SEPTEMBER,
    /**
     * The singleton instance for the month of October.
     */
    OCTOBER,
    /**
     * The singleton instance for the month of November.
     */
    NOVEMBER,
    /**
     * The singleton instance for the month of December.
     */
    DECEMBER;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>MonthOfYear</code> from an <code>int</code> value.
     * <p>
     * <code>MonthOfYear</code> is an enum representing the 12 months of the year.
     * This factory allows the enum to be obtained from the <code>int</code> value.
     * The <code>int</code> value follows the ISO-8601 standard, from 1 (January) to 12 (December).
     * <p>
     * An exception is thrown if the value is invalid. The exception uses the
     * {@link ISOChronology} month-of-year rule to indicate the failed rule.
     *
     * @param monthOfYear  the month-of-year to represent, from 1 (January) to 12 (December)
     * @return the MonthOfYear singleton, never null
     * @throws IllegalCalendarFieldValueException if the month-of-year is invalid
     */
    public static MonthOfYear monthOfYear(int monthOfYear) {
        switch (monthOfYear) {
            case 1:
                return JANUARY;
            case 2:
                return FEBRUARY;
            case 3:
                return MARCH;
            case 4:
                return APRIL;
            case 5:
                return MAY;
            case 6:
                return JUNE;
            case 7:
                return JULY;
            case 8:
                return AUGUST;
            case 9:
                return SEPTEMBER;
            case 10:
                return OCTOBER;
            case 11:
                return NOVEMBER;
            case 12:
                return DECEMBER;
            default:
                throw new IllegalCalendarFieldValueException(ISOChronology.monthOfYearRule(), monthOfYear, 1, 12);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the month-of-year <code>int</code> value.
     * <p>
     * The values are numbered following the ISO-8601 standard,
     * from 1 (January) to 12 (December).
     *
     * @return the month-of-year, from 1 (January) to 12 (December)
     */
    public int getValue() {
        return ordinal() + 1;
    }

    /**
     * Gets the short textual representation of this month-of-year, such as 'Jan' or 'Dec'.
     * <p>
     * This method is notionally specific to {@link ISOChronology} as it uses
     * the month-of-year rule to obtain the text. However, it is expected that
     * the text will be equivalent for all month-of-year rules, thus this aspect
     * of the implementation should be irrelevant to applications.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the month-of-year, never null
     */
    public String getShortText(Locale locale) {
        return ISOChronology.monthOfYearRule().getText(getValue(), locale, TextStyle.SHORT);
    }

    /**
     * Gets the short textual representation of this month-of-year, such as 'January' or 'December'.
     * <p>
     * This method is notionally specific to {@link ISOChronology} as it uses
     * the month-of-year rule to obtain the text. However, it is expected that
     * the text will be equivalent for all month-of-year rules, thus this aspect
     * of the implementation should be irrelevant to applications.
     * <p>
     * If there is no textual mapping for the locale, then the value is
     * returned as per {@link Integer#toString()}.
     *
     * @param locale  the locale to use, not null
     * @return the long text value of the month-of-year, never null
     */
    public String getText(Locale locale) {
        return ISOChronology.monthOfYearRule().getText(getValue(), locale, TextStyle.FULL);
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance representing January.
     *
     * @return true if this instance represents January
     */
    public boolean isJanuary() {
        return (this == JANUARY);
    }

    /**
     * Is this instance representing February.
     *
     * @return true if this instance represents February
     */
    public boolean isFebruary() {
        return (this == FEBRUARY);
    }

    /**
     * Is this instance representing March.
     *
     * @return true if this instance represents March
     */
    public boolean isMarch() {
        return (this == MARCH);
    }

    /**
     * Is this instance representing April.
     *
     * @return true if this instance represents April
     */
    public boolean isApril() {
        return (this == APRIL);
    }

    /**
     * Is this instance representing May.
     *
     * @return true if this instance represents May
     */
    public boolean isMay() {
        return (this == MAY);
    }

    /**
     * Is this instance representing June.
     *
     * @return true if this instance represents June
     */
    public boolean isJune() {
        return (this == JUNE);
    }

    /**
     * Is this instance representing July.
     *
     * @return true if this instance represents July
     */
    public boolean isJuly() {
        return (this == JULY);
    }

    /**
     * Is this instance representing August.
     *
     * @return true if this instance represents August
     */
    public boolean isAugust() {
        return (this == AUGUST);
    }

    /**
     * Is this instance representing September.
     *
     * @return true if this instance represents September
     */
    public boolean isSeptember() {
        return (this == SEPTEMBER);
    }

    /**
     * Is this instance representing October.
     *
     * @return true if this instance represents October
     */
    public boolean isOctober() {
        return (this == OCTOBER);
    }

    /**
     * Is this instance representing November.
     *
     * @return true if this instance represents November
     */
    public boolean isNovember() {
        return (this == NOVEMBER);
    }

    /**
     * Is this instance representing December.
     *
     * @return true if this instance represents December
     */
    public boolean isDecember() {
        return (this == DECEMBER);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next month-of-year.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year. The next month after December is January.
     *
     * @return the next month-of-year, never null
     */
    public MonthOfYear next() {
        return values()[(ordinal() + 1) % 12];
    }

    /**
     * Gets the previous month-of-year.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year. The previous month before January is December.
     *
     * @return the previous month-of-year, never null
     */
    public MonthOfYear previous() {
        return values()[(ordinal() + 12 - 1) % 12];
    }

    /**
     * Rolls the month-of-year, adding the specified number of months.
     * <p>
     * This calculates based on the time-line, thus it rolls around the end of
     * the year from December to January. The months to roll by may be negative.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to roll by, positive or negative
     * @return the resulting month-of-year, never null
     */
    public MonthOfYear roll(int months) {
        return values()[(ordinal() + (months % 12 + 12)) % 12];
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of this month in days using the ISO year.
     * <p>
     * The year specified is the year from the {@link ISOChronology}.
     * Other chronologies should use {@link #lengthInDays(boolean)}.
     *
     * @param year  the year to obtain the length for, not null
     * @return the length of this month in days, from 28 to 31
     */
    public int lengthInDays(Year year) {
        if (year == null) {
            throw new NullPointerException("The year must not be null");
        }
        switch (this) {
            case FEBRUARY:
                return (year.isLeap() ? 29 : 28);
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * Gets the length of this month in days.
     * <p>
     * This takes a flag to determine whether to return the length for a leap year or not.
     *
     * @param leapYear  true if the length is required for a leap year
     * @return the length of this month in days, from 28 to 31
     */
    public int lengthInDays(boolean leapYear) {
        switch (this) {
            case FEBRUARY:
                return (leapYear ? 29 : 28);
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * Gets the minimum length of this month in days.
     *
     * @return the minimum length of this month in days, from 28 to 31
     */
    public int minLengthInDays() {
        switch (this) {
            case FEBRUARY:
                return 28;
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    /**
     * Gets the maximum length of this month in days.
     *
     * @return the maximum length of this month in days, from 29 to 31
     */
    public int maxLengthInDays() {
        switch (this) {
            case FEBRUARY:
                return 29;
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
            default:
                return 31;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the last day of the month.
     * <p>
     * This is a synonym for {@link #lengthInDays(boolean)} and exists to provide
     * a more meaningful API.
     *
     * @param leapYear  true if the length is required for a leap year
     * @return the last day of this month, from 28 to 31
     */
    public int getLastDayOfMonth(boolean leapYear) {
        return lengthInDays(leapYear);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the quarter that this month falls in.
     *
     * @return the quarter-of-year, never null
     */
    public QuarterOfYear getQuarterOfYear() {
        if (ordinal() < 3) {
            return QuarterOfYear.Q1;
        } else if (ordinal() < 6) {
            return QuarterOfYear.Q2;
        } else if (ordinal() < 9) {
            return QuarterOfYear.Q3;
        } else {
            return QuarterOfYear.Q4;
        }
    }

    /**
     * Gets the index of the month within the quarter.
     *
     * @return the month of season, from 1 to 3
     */
    public int getMonthOfQuarter() {
        return (ordinal() % 3) + 1;
    }

}
