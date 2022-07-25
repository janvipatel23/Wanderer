package ca.dal.cs.wanderer.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SuccessMessages {
    // Success messages
    PIN_SAVE_SUCCESS("Pin saved successfully"),
    PINS_RETRIEVE_SUCCESS("Pins retrieved successfully"),
    SINGLE_PIN_RETRIEVE_SUCCESS("Pin retrieved successfully"),
    PIN_DELETE_SUCCESS("Pin deleted successfully"),
    PIN_UPDATE_SUCCESS("Pin updated successfully"),
    PIN_RATING_SUCCESS("Pin rating updated successfully"),
    PIN_RATING_RETRIEVE_SUCCESS("Rating retrieved successfully"),
    COMMENT_ADD_SUCCESS("Comment added successfully"),
    COMMENTS_RETRIEVE_SUCCESS("Comments retrieved successfully"),
    BLOG_CREATE_SUCCESS("Blog created successfully"),
    BLOG_RETRIEVE_SUCCESS("Blog retrieved successfully"),
    ALL_BLOG_RETRIEVE_SUCCESS("All Blogs retrieved successfully"),
    BLOG_DELETE_SUCCESS("Blog deleted successfully");
    private final String successMessage;
}
