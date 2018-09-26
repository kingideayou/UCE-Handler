package com.rohitss.uceh;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;

/**
 * <pre>
 *     author : NeXT
 *     time   : 2018/09/25
 *     desc   :
 * </pre>
 */
public class UCEHandlerHelper {

    private static final int MAX_STACK_TRACE_SIZE = 131071; //128 KB - 1

    public static ExceptionInfoBean getExceptionInfoBean(Throwable throwable) {
        return getExceptionInfoBean(throwable, null);
    }

    public static ExceptionInfoBean getExceptionInfoBean(Throwable throwable, Deque<String> activityLog) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stackTraceString = sw.toString();
        if (stackTraceString.length() > MAX_STACK_TRACE_SIZE) {
            String disclaimer = " [stack trace too large]";
            stackTraceString = stackTraceString.substring(0, MAX_STACK_TRACE_SIZE - disclaimer.length()) + disclaimer;
        }

        Throwable rootTr = throwable;
        String cause = throwable.getMessage();
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
            if (throwable.getStackTrace() != null && throwable.getStackTrace().length > 0) {
                rootTr = throwable;
            }
            String msg = throwable.getMessage();
            if (!TextUtils.isEmpty(msg))
                cause = msg;
        }

        String exceptionType = rootTr.getClass().getName();

        String throwClassName;
        String throwMethodName;
        int throwLineNumber;

        if (rootTr.getStackTrace().length > 0) {
            StackTraceElement trace = rootTr.getStackTrace()[0];
            throwClassName = trace.getClassName();
            throwMethodName = trace.getMethodName();
            throwLineNumber = trace.getLineNumber();
        } else {
            throwClassName = "unknown";
            throwMethodName = "unknown";
            throwLineNumber = -1;
        }

        StringBuilder activityLogStringBuilder = new StringBuilder();
        if (UCEHandler.isTrackActivitiesEnabled) {
            while (!activityLog.isEmpty()) {
                activityLogStringBuilder.append(activityLog.poll());
            }
        }

        return ExceptionInfoBean.newInstance()
                .cause(cause)
                .className(throwClassName)
                .methodName(throwMethodName)
                .exceptionType(exceptionType)
                .lineNumber(throwLineNumber)
                .stackTraceString(stackTraceString)
                .activityLogString(activityLogStringBuilder.toString());
    }

    @NonNull
    public static StringBuilder getExceptionInfoString(Context context, ExceptionInfoBean exceptionInfoBean) {
        String LINE_SEPARATOR = "\n";
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("------------ UCE HANDLER Library ------------");
        errorReport.append("\n------------ by NeXT ------------\n");
        errorReport.append(LINE_SEPARATOR);
        if (exceptionInfoBean != null) {
            errorReport.append("Cause: ");
            errorReport.append(LINE_SEPARATOR);
            errorReport.append(exceptionInfoBean.getCause());
            errorReport.append(LINE_SEPARATOR);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Exception Type: ");
            errorReport.append(LINE_SEPARATOR);
            errorReport.append(exceptionInfoBean.getExceptionType());
            errorReport.append(LINE_SEPARATOR);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Class & Method Name: ");
            errorReport.append(LINE_SEPARATOR);
            errorReport.append(exceptionInfoBean.getClassName()).append(".").append(exceptionInfoBean.getMethodName());
            errorReport.append(LINE_SEPARATOR);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("LineNumber: ").append(exceptionInfoBean.getLineNumber());
            errorReport.append(LINE_SEPARATOR);
        }
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n------------ ERROR LOG ------------\n");
        errorReport.append(LINE_SEPARATOR);
        if (exceptionInfoBean != null) {
            errorReport.append(exceptionInfoBean.getStackTraceString());
        }
        errorReport.append(LINE_SEPARATOR);
        if (exceptionInfoBean != null) {
            errorReport.append("\n------------ USER ACTIVITIES ------------\n");
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("User Activities: ");
            errorReport.append(LINE_SEPARATOR);
            errorReport.append(exceptionInfoBean.getActivityLogString());
            errorReport.append(LINE_SEPARATOR);
        }
        errorReport.append("\n------------ DEVICE INFO ------------\n");
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Manufacturer: ");
        errorReport.append(Build.MANUFACTURER);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n------------ APP INFO ------------\n");
        errorReport.append(LINE_SEPARATOR);
        String versionName = getVersionName(context);
        errorReport.append("Version: ");
        errorReport.append(versionName);
        errorReport.append(LINE_SEPARATOR);
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String firstInstallTime = getFirstInstallTimeAsString(context, dateFormat);
        if (!TextUtils.isEmpty(firstInstallTime)) {
            errorReport.append("Installed On: ");
            errorReport.append(firstInstallTime);
            errorReport.append(LINE_SEPARATOR);
        }
        String lastUpdateTime = getLastUpdateTimeAsString(context, dateFormat);
        if (!TextUtils.isEmpty(lastUpdateTime)) {
            errorReport.append("Updated On: ");
            errorReport.append(lastUpdateTime);
            errorReport.append(LINE_SEPARATOR);
        }
        errorReport.append("Current Date: ");
        errorReport.append(dateFormat.format(currentDate));
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("\n------------ END OF LOG ------------\n");
        return errorReport;
    }

    private static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private static String getFirstInstallTimeAsString(Context context, DateFormat dateFormat) {
        long firstInstallTime;
        try {
            firstInstallTime = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .firstInstallTime;
            return dateFormat.format(new Date(firstInstallTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private static String getLastUpdateTimeAsString(Context context, DateFormat dateFormat) {
        long lastUpdateTime;
        try {
            lastUpdateTime = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .lastUpdateTime;
            return dateFormat.format(new Date(lastUpdateTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

}
