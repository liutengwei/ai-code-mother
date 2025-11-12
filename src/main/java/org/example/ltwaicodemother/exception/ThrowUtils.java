package org.example.ltwaicodemother.exception;

/**
 * 抛出工具类
 */
public class ThrowUtils {

    /**
     * 如果条件成立，则抛出异常
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition,RuntimeException runtimeException) {
        if (condition){
            throw runtimeException;
        }
    }


    /**
     * 如果条件成立，则抛出异常
     * @param condition
     * @param code
     */
    public static void throwIf(boolean condition,ErrorCode code) {
        throwIf(condition,new BusinessException(code));
    }

    /**
     * 如果条件成立，则抛出异常
     * @param condition
     * @param code
     * @param message
     */
    public static void throwIf(boolean condition,ErrorCode code,String message) {
        throwIf(condition,new BusinessException(code,message));
    }


}
