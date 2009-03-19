/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import java.io.Serializable;
import java.util.Map;

import javax.time.CalendricalException;
import javax.time.calendar.zone.ZoneRules;
import javax.time.calendar.zone.ZoneRulesGroup;

/**
 * A time zone representing the set of rules by which the zone offset
 * varies through the year and historically.
 * <p>
 * Time zones are geographical regions where the same rules for time apply.
 * The rules are defined by governments and change frequently.
 * <p>
 * There are a number of sources of time zone information available,
 * each represented by an instance of {@link ZoneRulesGroup}.
 * One group is provided as standard - 'TZDB' - and more can be added.
 * <p>
 * Each group defines a naming scheme for the regions of the time zone.
 * The format of the region is specific to the group.
 * For example, the 'TZDB' group typically use the format {area}/{city},
 * such as 'Europe/London'.
 * <p>
 * Each group typically produces multiple versions of their data.
 * The format of the version is specific to the group.
 * For example, the 'TZDB' group use the format {year}{letter}, such as '2009b'.
 * <p>
 * In combination, a unique ID is created expressing the time-zone, formed from
 * {groupID}:{regionID}:{versionID}.
 * <p>
 * The version can be set to an empty string. This represents the "floating version".
 * The floating version will always choose the latest applicable set of rules.
 * Applications will probably choose to use the floating version, as it guarantees
 * usage of the latest rules.
 * <p>
 * In addition to the group/region/version combinations, <code>TimeZone</code>
 * can represent a fixed offset. This has an empty group and version ID.
 * It is not possible to have an invalid instance of a fixed time zone.
 * <p>
 * The purpose of capturing all this information is to handle issues when
 * manipulating and persisting time zones. For example, consider what happens if the
 * government of a country changed the start or end of daylight savings time.
 * If you created and stored a date using one version of the rules, and then load it
 * up when a new version of the rules are in force, what should happen?
 * The date might now be invalid (due to a gap in the local time-line).
 * By storing the version of the time zone rules data together with the date, it is
 * possible to tell that the rules have changed and to process accordingly.
 * <p>
 * <code>TimeZone</code> merely represents the identifier of the zone.
 * The actual rules are provided by {@link ZoneRules}.
 * One difference is that serializing this class only stores the reference to the zone,
 * whereas serializing <code>ZoneRules</code> stores the entire set of rules.
 * <p>
 * After deserialization, or by using the special constructor, it is possible for the
 * time zone to represent a group/region/version combination that is unavailable.
 * Since this class can still be loaded even when the rules cannot, the application can
 * continue. For example, a {@link ZonedDateTime} instance could still be queried.
 * The application might also take appropriate corrective action.
 * For example, an application might choose to download missing rules from a central server.
 * <p>
 * TimeZone is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class TimeZone implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 93618758758127L;
    /**
     * The time zone offset for UTC, with an id of 'UTC'.
     */
    public static final TimeZone UTC = new TimeZone("", "UTC", "", ZoneRules.fixed(ZoneOffset.UTC));

    /**
     * The time zone group ID.
     */
    private final String groupID;
    /**
     * The time zone region ID.
     */
    private final String regionID;
    /**
     * The time zone version ID.
     */
    private final String versionID;
    /**
     * The time zone rules.
     */
    private transient volatile ZoneRules rules;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>TimeZone</code> using its ID using a map
     * of aliases to supplement the standard zone IDs.
     * <p>
     * Many users of time zones use short abbreviations, such as PST for
     * 'Pacific Standard Time' and PDT for 'Pacific Daylight Time'.
     * These abbreviations are not unique, and so cannot be used as identifiers.
     * This method allows a map of string to time zone to be setup and reused
     * within an application.
     *
     * @param timeZoneIdentifier  the time zone id, not null
     * @param aliasMap  a map of time zone IDs (typically abbreviations) to time zones, not null
     * @return the TimeZone, never null
     * @throws IllegalArgumentException if the time zone cannot be found
     */
    public static TimeZone timeZone(String timeZoneIdentifier, Map<String, TimeZone> aliasMap) {
        // TODO: review
        ISOChronology.checkNotNull(timeZoneIdentifier, "Time Zone ID must not be null");
        ISOChronology.checkNotNull(aliasMap, "Alias map must not be null");
        TimeZone zone = aliasMap.get(timeZoneIdentifier);
        return zone == null ? timeZone(timeZoneIdentifier) : zone;
    }

    /**
     * Obtains an instance of <code>TimeZone</code> from an identifier.
     * <p>
     * Six forms of identifier are recognized:
     * <ul>
     * <li><code>{groupID}:{regionID}#{versionID}</code> - full
     * <li><code>{groupID}:{regionID}</code> - implies the floating version
     * <li><code>{regionID}#{versionID} - implies 'TZDB' group and specific version
     * <li><code>{regionID} - implies 'TZDB' group and the floating version
     * <li><code>UTC{offset} - fixed time zone
     * <li><code>GMT{offset} - fixed time zone
     * </ul>
     * <p>
     * Most of the formats are based around the group, version and region IDs.
     * The version and region ID formats are specific to the group.
     * If a group does not support versioning, then the version must be an empty string.
     * <p>
     * The default group is 'TZDB' which has versions of the form {year}{letter}, such as '2009b'.
     * The region ID for the 'TZDB' group is generally of the form '{area}/{city}', such as 'Europe/Paris'.
     * This is compatible with most IDs from {@link java.util.TimeZone}.
     * <p>
     * For example, if a provider is loaded with the ID 'MyProvider' containing a zone ID of
     * 'France', then the unique key for version 2.1 would be 'MyProvider:France#2.1'.
     * A specific version of the TZDB provider can be specified using this format,
     * for example 'TZDB:Asia/Tokyo#2008g'.
     * <p>
     * The alternate format is for fixed time zones, where the offset never changes over time.
     * It is intended that {@link ZoneOffset} and {@link OffsetDateTime} are used in preference,
     * however sometimes it is necessary to have a fixed time zone.
     * A fixed time zone is returned if the first three characters are 'UTC' or 'GMT'.
     * The remainder of the ID must be a valid format for {@link ZoneOffset#zoneOffset(String)}.
     * Using 'UTCZ' or 'GMTZ' is valid, but discouraged in favor of 'UTC'.
     * The normalized time zone ID is 'UTC&plusmn;hh:mm:ss'.
     *
     * @param zoneID  the time zone identifier, not null
     * @return the TimeZone, never null
     * @throws CalendricalException if the time zone cannot be found
     */
    public static TimeZone timeZone(String zoneID) {
        ISOChronology.checkNotNull(zoneID, "Time zone ID must not be null");
        if (zoneID.equals("UTC")) {
            return UTC;
            
        } else if (zoneID.startsWith("UTC") || zoneID.startsWith("GMT")) {  // not sure about GMT
            try {
                return timeZone(ZoneOffset.zoneOffset(zoneID.substring(3)));
            } catch (IllegalArgumentException ex) {
                throw new CalendricalException(ex.toString(), ex);
            }
            
        } else {
            int pos = zoneID.indexOf(':');
            ZoneRulesGroup group;
            if (pos >= 0) {
                group = ZoneRulesGroup.getGroup(zoneID.substring(0, pos));  // validates ID
                zoneID = zoneID.substring(pos + 1);
            } else {
                group = ZoneRulesGroup.getGroup("TZDB");  // validates ID
            }
            pos = zoneID.indexOf('#');
            String versionID = "";
            if (pos >= 0) {
                versionID = zoneID.substring(pos + 1);
                zoneID = zoneID.substring(0, pos);
            }
            ZoneRules rules = group.getRules(zoneID, versionID);  // validates IDs
            return new TimeZone(group.getID(), zoneID, versionID, rules);
        }
    }

    /**
     * Obtains an instance of <code>TimeZone</code> representing a fixed time zone.
     * <p>
     * The time zone returned from this factory has a fixed offset for all time.
     * The region ID will return an identifier formed from 'UTC' and the offset.
     * The group and version IDs will both return an empty string.
     * <p>
     * Fixed time zones are {@link #isValid() always valid}.
     *
     * @param offset  the zone offset to create a fixed zone for, not null
     * @return the TimeZone for the offset, never null
     */
    public static TimeZone timeZone(ZoneOffset offset) {
        ISOChronology.checkNotNull(offset, "ZoneOffset must not be null");
        if (offset == ZoneOffset.UTC) {
            return UTC;
        }
        String id = "UTC" + offset.getID();
        ZoneRules zoneRules = ZoneRules.fixed(offset);
        return new TimeZone("", id, "", zoneRules);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param groupID  the time zone rules group ID, not null
     * @param regionID  the time zone region ID, not null
     * @param versionID  the time zone rules version ID, not null
     */
    private TimeZone(String groupID, String regionID, String versionID, ZoneRules rules) {
        super();
        this.groupID = groupID;
        this.regionID = regionID;
        this.versionID = versionID;
        this.rules = rules;
    }

    /**
     * Handle UTC on deserialization.
     *
     * @return the resolved instance, never null
     */
    private Object readResolve() {
        // fixed time zone must always be valid
        if (isFixedOffset()) {
            if ("UTC".equals(regionID)) {
                return UTC;
            } else {
                return TimeZone.timeZone(getID());
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unique time zone ID.
     * <p>
     * The unique key is created from the group ID, version ID and region ID.
     * The format is {groupID}:{regionID}#{versionID}.
     * If the group is 'TZDB' then the {groupID}: is omitted.
     * If the version is floating, then the #{versionID} is omitted.
     * Fixed time zones will only output the region ID.
     *
     * @return the time zone unique ID, never null
     */
    public String getID() {
        if (isFixedOffset()) {
            return regionID;
        }
        if (groupID.equals("TZDB")) {
            return regionID + (versionID.length() == 0 ? "" : "#" + versionID);
        }
        return groupID + ":" + regionID + (versionID.length() == 0 ? "" : "#" + versionID);
    }

    /**
     * Gets the time zone rules group ID, such as 'TZDB'.
     * <p>
     * Time zone rules are provided by groups referenced by an ID.
     * <p>
     * If this is a fixed time zone, the group ID will be an empty string.
     *
     * @return the time zone rules group ID, never null
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * Gets the time zone region identifier, such as 'Europe/London'.
     * <p>
     * The time zone region identifier is of a format specific to the group.
     * The default 'TZDB' group generally uses the format {area}/{city}, such as 'Europe/Paris'.
     *
     * @return the time zone rules region ID, never null
     */
    public String getRegionID() {
        return regionID;
    }

    /**
     * Gets the time zone rules group version, such as '2009b'.
     * <p>
     * Time zone rules change over time as governments change the associated laws.
     * The time zone groups capture these changes by issuing multiple versions
     * of the data. An application can reference the exact set of rules used
     * by using the group ID and version.
     * <p>
     * A floating version is used to ensure that the time zone always uses the
     * latest version of the rules available.
     * <p>
     * If this is a fixed time zone, the version ID will be an empty string.
     *
     * @return the time zone rules version ID, never null, empty if the version is floating
     */
    public String getVersionID() {
        return versionID;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks of the time zone is fixed, such that the offset never varies.
     * <p>
     * It is intended that {@link OffsetDateTime}, {@link OffsetDate} and
     * {@link OffsetTime} are used in preference to fixed offset time zones
     * in {@link ZonedDateTime}.
     *
     * @return true if the time zone is fixed and the offset never changes
     */
    public boolean isFixedOffset() {
        return groupID.length() == 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone rules group for the stored group ID, such as 'TZDB'.
     * <p>
     * Time zone rules are provided by groups referenced by an ID.
     * <p>
     * Fixed time zones are not provided by a group, thus this method throws
     * an exception if the time zone is fixed.
     * <p>
     * Callers of this method need to be aware of an unusual scenario.
     * It is possible to obtain a <code>TimeZone</code> instance even when the
     * rules are not available. This typically occurs when a <code>TimeZone</code>
     * is loaded from a previously stored version but the rules are not available.
     * In this case, the <code>TimeZone</code> instance is still valid, as is
     * any associated object, such as {@link ZonedDateTime}. It is impossible to
     * perform any calculations that require the rules however, and this method
     * will throw an exception.
     *
     * @return the time zone rules group ID, never null
     * @throws CalendricalException if the time zone is fixed
     * @throws CalendricalException if the group ID cannot be found
     */
    public ZoneRulesGroup getGroup() {
        if (isFixedOffset()) {
            throw new CalendricalException("Fixed time zone is not provided by a group");
        }
        return ZoneRulesGroup.getGroup(groupID);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this time zone is valid such that rules can be obtained for it.
     * <p>
     * This will return true if the rules are available for the group, region
     * and version ID combination. If this method returns true, then
     * {@link #getRules()} will return a valid rules instance.
     * <p>
     * A time zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it.
     * <p>
     * If this object declares a floating version of the rules and a background
     * thread is used to update the available rules, then the result of calling
     * this method may vary over time.
     * Each individual call will be still remain thread-safe.
     * <p>
     * If this is a fixed time zone, then it is always valid.
     *
     * @return true if this time zone is valid and rules are available
     */
    public boolean isValid() {
        if (isFixedOffset()) {
            return true;
        }
        if (ZoneRulesGroup.isValidGroup(groupID) == false) {
            return false;
        }
        ZoneRulesGroup group = ZoneRulesGroup.getGroup(groupID);
        return group.isValidRules(regionID, versionID);
    }

    /**
     * Gets the time zone rules allowing calculations to be performed.
     * <p>
     * The rules provide the functionality associated with a time zone,
     * such as finding the offset for a given instant or local date-time.
     * Different rules may be returned depending on the group, version and zone.
     * <p>
     * If this object declares a specific version of the rules, then the result will
     * be of that version. If this object declares a floating version of the rules,
     * then the latest version available will be returned.
     * <p>
     * A time zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it. In this case, calling
     * this method will throw an exception.
     * <p>
     * If this object declares a floating version of the rules and a background
     * thread is used to update the available rules, then the result of calling
     * this method may vary over time.
     * Each individual call will be still remain thread-safe.
     *
     * @return the rules, never null
     * @throws CalendricalException if the zone ID cannot be found
     */
    public ZoneRules getRules() {
        // fixed rules always in transient field
        if (rules != null) {
            return rules;
        }
        ZoneRulesGroup group = ZoneRulesGroup.getGroup(groupID);
        ZoneRules r = group.getRules(regionID, versionID);
        if (versionID.length() > 0) {
            rules = r;
        }
        return r;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this time zone is valid such that rules can be obtained for it
     * which are valid for the specified date-time.
     * <p>
     * This will return true if the rules are available for the group, region
     * and version ID combination that are valid for the specified date-time.
     * If this method returns true, then {@link #getRules(OffsetDateTime)} will
     * return a valid rules instance.
     * <p>
     * A time zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it.
     * <p>
     * If this object declares a floating version of the rules and a background
     * thread is used to update the available rules, then the result of calling
     * this method may vary over time.
     * Each individual call will be still remain thread-safe.
     * <p>
     * If this is a fixed time zone, then it is valid if the offset matches the date-time.
     *
     * @param validDateTime  a date-time for which the rules must be valid, not null
     * @return true if this time zone is valid and rules are available
     */
    public boolean isValid(OffsetDateTime validDateTime) {
        ISOChronology.checkNotNull(validDateTime, "Valid date-time must not be null");
        if (isFixedOffset()) {
            return getRules().getOffset(validDateTime).equals(validDateTime.getOffset());
        }
        if (ZoneRulesGroup.isValidGroup(groupID) == false) {
            return false;
        }
        ZoneRulesGroup group = ZoneRulesGroup.getGroup(groupID);
        return group.isValidRules(regionID, versionID, validDateTime);
    }

    /**
     * Gets the time zone rules allowing calculations to be performed, ensuring that
     * the date-time specified is valid for the returned rules.
     * <p>
     * The rules provide the functionality associated with a time zone,
     * such as finding the offset for a given instant or local date-time.
     * Different rules may be returned depending on the group, version and zone.
     * <p>
     * If this object declares a specific version of the rules, then the result will
     * be of that version providing that the specified date-time is valid for those rules.
     * If this object declares a floating version of the rules, then the latest
     * version of the rules where the date-time is valid will be returned.
     * <p>
     * A time zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it. In this case, calling
     * this method will throw an exception.
     * <p>
     * If this object declares a floating version of the rules and a background
     * thread is used to update the available rules, then the result of calling
     * this method may vary over time.
     * Each individual call will be still remain thread-safe.
     *
     * @param validDateTime  a date-time for which the rules must be valid, not null
     * @return the latest rules for this zone where the date-time is valid, never null
     * @throws CalendricalException if the zone ID cannot be found
     * @throws CalendricalException if no rules match the zone ID and date-time
     */
    public ZoneRules getRules(OffsetDateTime validDateTime) {
        ISOChronology.checkNotNull(validDateTime, "Valid date-time must not be null");
        if (isFixedOffset()) {
            if (getRules().getOffset(validDateTime).equals(validDateTime.getOffset()) == false) {
                throw new CalendricalException("Fixed time zone '" + regionID + "' is invalid for date-time: " + validDateTime);
            }
            return ZoneRules.fixed(getRules().getOffset(validDateTime));
        }
        ZoneRulesGroup group = ZoneRulesGroup.getGroup(groupID);
        return group.getRules(regionID, versionID, validDateTime);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual name of this zone.
     *
     * @return the time zone name, never null
     */
    public String getName() {
        return regionID;  // TODO
    }

    /**
     * Gets the short textual name of this zone.
     *
     * @return the time zone short name, never null
     */
    public String getShortName() {
        return regionID;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified by comparing the ID.
     *
     * @param otherZone  the other zone, null returns false
     * @return true if this zone is the same as that specified
     */
    @Override
    public boolean equals(Object otherZone) {
        if (this == otherZone) {
           return true;
        }
        if (otherZone instanceof TimeZone) {
            TimeZone zone = (TimeZone) otherZone;
            return regionID.equals(zone.regionID) &&
                    versionID.equals(zone.versionID) &&
                    groupID.equals(zone.groupID);
        }
        return false;
    }

    /**
     * A hash code for this time zone ID.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return groupID.hashCode() ^ regionID.hashCode() ^ versionID.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the time zone.
     * <p>
     * This returns {@link #getID()}.
     *
     * @return the time zone ID, never null
     */
    @Override
    public String toString() {
        return getID();
    }

}
