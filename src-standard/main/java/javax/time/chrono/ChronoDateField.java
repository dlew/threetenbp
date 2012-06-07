/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.chrono;

import static javax.time.calendrical.LocalDateUnit.DAYS;
import static javax.time.calendrical.LocalDateUnit.ERAS;
import static javax.time.calendrical.LocalDateUnit.FOREVER;
import static javax.time.calendrical.LocalDateUnit.MONTHS;
import static javax.time.calendrical.LocalDateUnit.WEEKS;
import static javax.time.calendrical.LocalDateUnit.YEARS;

import javax.time.CalendricalException;
import javax.time.DateTimes;
import javax.time.calendrical.CalendricalObject;
import javax.time.calendrical.DateTimeBuilder;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeValueRange;
import javax.time.calendrical.LocalDateField;
import javax.time.calendrical.PeriodUnit;

/**
 * The set of fields that can be accessed using a chronology.
 * <p>
 * These fields are the minimal set of fields supported by multiple calendar systems
 * as expressed via {@link Chrono}. The fields duplicate some of those in
 * {@link LocalDateField} however they are used in a different way.
 * The {@code LocalDateField} fields will always access the ISO calendar system,
 * whereas these fields will always access the supplied calendar system.
 * Only if no calendar system is specified will these fields default to ISO.
 * 
 * <h4>Implementation notes</h4>
 * This is a final, immutable and thread-safe enum.
 */
public enum ChronoDateField implements DateTimeField {

    /**
     * The calendar system day-of-week.
     */
    DAY_OF_WEEK("ChronoDayOfWeek", DAYS, WEEKS),
    /**
     * The calendar system day-of-month.
     */
    DAY_OF_MONTH("ChronoDayOfMonth", DAYS, MONTHS),
    /**
     * The calendar system day-of-year.
     */
    DAY_OF_YEAR("ChronoDayOfYear", DAYS, YEARS),
    /**
     * The calendar system month-of-year.
     */
    MONTH_OF_YEAR("ChronoMonthOfYear", MONTHS, YEARS),
    /**
     * The calendar system year-of-era.
     */
    YEAR_OF_ERA("ChronoYearOfEra", YEARS, ERAS),
    /**
     * The calendar system proleptic-year.
     */
    PROLEPTIC_YEAR("ChronoProlepticYear", YEARS, FOREVER),
    /**
     * The calendar system era.
     */
    ERA("ChronoEra", ERAS, FOREVER);

    private final String name;
    private final PeriodUnit baseUnit;
    private final PeriodUnit rangeUnit;

    private ChronoDateField(String name, PeriodUnit baseUnit, PeriodUnit rangeUnit) {
        this.name = name;
        this.baseUnit = baseUnit;
        this.rangeUnit = rangeUnit;
    }

    public String getName() {
        return name;
    }

    public PeriodUnit getBaseUnit() {
        return baseUnit;
    }

    public PeriodUnit getRangeUnit() {
        return rangeUnit;
    }

    @Override
    public long get(CalendricalObject calendrical) {
//        LocalDate date = calendrical.extract(LocalDate.class);
//        if (date != null) {
//            return getDateRules().get(date);
//        }
//        DateTimeBuilder builder = calendrical.extract(DateTimeBuilder.class);
//        if (builder.containsFieldValue(this)) {
//            return builder.getFieldValue(this);
//        }
//        throw new CalendricalException("Unable to obtain " + getName() + " from calendrical: " + calendrical.getClass());
        ChronoDate<?> date = ChronoDate.from(calendrical);
        return date.get(this);
    }

    @Override
    public int compare(CalendricalObject calendrical1, CalendricalObject calendrical2) {
        return DateTimes.safeCompare(get(calendrical1), get(calendrical2));
    }

    public DateTimeField bindTo(Chrono chrono) {
        return null;
    }

    @Override
    public DateTimeValueRange getValueRange() {
        switch (this) {
            case DAY_OF_WEEK: return LocalDateField.DAY_OF_WEEK.getValueRange();
            case DAY_OF_MONTH: return LocalDateField.DAY_OF_MONTH.getValueRange();
            case DAY_OF_YEAR: return LocalDateField.DAY_OF_YEAR.getValueRange();
            case MONTH_OF_YEAR: return LocalDateField.MONTH_OF_YEAR.getValueRange();
            case YEAR_OF_ERA: return DateTimeValueRange.of(1, DateTimes.MAX_YEAR);
            case PROLEPTIC_YEAR: return LocalDateField.YEAR.getValueRange();
            case ERA: return DateTimeValueRange.of(0, 1);
        }
        throw new CalendricalException("Unknown field");
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public DateTimeValueRange range(CalendricalObject calendrical) {
        return null;
    }
   
    @Override
    public <R extends CalendricalObject> R set(R calendrical, long newValue) {
        return null;
    }

    @Override
    public <R extends CalendricalObject> R roll(R calendrical, long roll) {
        return null;
    }

    @Override
    public boolean resolve(DateTimeBuilder builder, long value) {
        return false;
    }

}
