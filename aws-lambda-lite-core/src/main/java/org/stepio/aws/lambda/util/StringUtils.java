package org.stepio.aws.lambda.util;

/**
 * Implements basic validations, similar to {@link org.springframework.util.StringUtils}.
 */
public class StringUtils {

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static boolean hasLength(CharSequence str) {
        return str != null && str.length() > 0;
    }
}
