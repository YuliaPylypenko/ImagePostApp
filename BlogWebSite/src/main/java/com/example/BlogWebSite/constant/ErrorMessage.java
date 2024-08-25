package com.example.BlogWebSite.constant;

public class ErrorMessage {

    public static final String USER_NOT_FOUND_BY_ID = "The user does not exist by this id: ";
    public static final String USER_NOT_FOUND_BY_EMAIL = "The user does not exist by this email: ";
    public static final String USER_HAS_NO_PERMISSION = "Current user has no permission for this action";
    public static final String INVALID_USER_VO = "The user's data is not valid.";
    public static final String INVALID_USER_EMAIL = "The user's email format is not valid.";
    public static final String USER_ALREADY_REGISTERED_WITH_THIS_EMAIL = "User with this email is already registered";
    public static final String FILE_NOT_SAVED = "File hasn't been saved";
    public static final String MULTIPART_FILE_BAD_REQUEST =
            "Can`t convert To Multipart Image. Bad inputted image string : ";
    public static final String PARSING_URL_FAILED = "Can't parse image's url: ";
    public static final String INVALID_URI = "The string could not be parsed as a URI reference.";
    public static final String USER_CANNOT_ADD_MORE_THAN_5_POST_IMAGES =
            "User cannot add more than 5 post images";
    public static final String IMAGE_SIZE_EXCEEDS_10MB = "Image size should be less than 10MB.";
    public static final String UNSUPPORTED_IMAGE_FORMAT = "Image should be in JPG or PNG format.";
    public static final String POST_NOT_SAVED = "Post hasn't been saved because of constraint violation";
    public static final String IMPOSSIBLE_UPDATE_POST = "You don't have permissions to edit this post";
    public static final String POST_NOT_FOUND_BY_ID = "Post doesn't exist by this id: ";
    public static final String INVALID_SORTING_VALUE = "Supported sort is: asc|desc";

}
