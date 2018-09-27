package com.next.uceh;

import android.support.annotation.Nullable;

/**
 * <pre>
 *     author : NeXT
 *     time   : 2018/09/25
 *     desc   :
 * </pre>
 */
public interface UCECallback {

    void exceptionInfo(@Nullable ExceptionInfoBean exceptionInfoBean);

    void throwable(@Nullable Throwable throwable);

}
