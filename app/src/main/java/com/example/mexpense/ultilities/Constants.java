package com.example.mexpense.ultilities;

public class Constants {
    public static final int NEW_EXPENSE = -1;
    public static final int NEW_TRIP = -1;

    public static final String DATE_FORMAT = "dd MMM, yyyy";
    public static final String DATE_FORMAT_DATABASE = "yyyy-MM-dd";

    public static final String[] categories = {"Travel", "Flight", "Telephone", "Mortgage", "Meals", "Refreshments", "Gifts", "Medical", "Printing"};
    public static final String[] trips = {"Meeting", "Conference", "Exhibit", "Event", "Team-building"};

    public static final String LIMIT_START_DATE = "1910-01-01";
    public static final String LIMIT_END_DATE = "2099-01-01";

    public static final String COLUMN_ID_TRIP = "trip_id";
    public static final String COLUMN_NAME_TRIP = "name"; // Required
    public static final String COLUMN_DESTINATION_TRIP = "destination"; // Required
    public static final String COLUMN_START_DATE_TRIP = "startDate"; // Required
    public static final String COLUMN_END_DATE_TRIP = "endDate"; // Required
    public static final String COLUMN_REQUIRED_ASSESSMENT_TRIP = "assessment"; // Optional
    public static final String COLUMN_PARTICIPANT_TRIP = "participants";
    public static final String COLUMN_DESCRIPTION_TRIP = "description";
    public static final String COLUMN_TOTAL_TRIP = "total";

    public static final String COLUMN_ID_EXPENSE = "expense_id";
    public static final String COLUMN_CATEGORY_EXPENSE = "name"; // Required
    public static final String COLUMN_COST_EXPENSE = "cost"; // Required
    public static final String COLUMN_AMOUNT_EXPENSE = "amount";
    public static final String COLUMN_DATE_EXPENSE = "date"; // Required
    public static final String COLUMN_COMMENT_EXPENSE = "comment"; // Optional
    public static final String COLUMN_TRIP_ID_EXPENSE = "trip_id";
    public static final String COLUMN_LATITUDE_EXPENSE = "latitude";
    public static final String COLUMN_LONGITUDE_EXPENSE = "longitude";
    public static final String COLUMN_IMAGE_PATH_EXPENSE = "image";
}
