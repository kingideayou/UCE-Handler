/*
 *
 *  * Copyright © 2018 Rohit Sahebrao Surwase.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.next.uceh;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class UCEDefaultActivity extends AppCompatActivity {
    private String pureStrCurrentErrorLog;

    @SuppressLint({"PrivateResource", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_error_activity);

        Toolbar mToolbarTb = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbarTb);

        TextView tvType = findViewById(R.id.tv_type);
        TextView tvMethodName = findViewById(R.id.tv_method_name);
        TextView tvLineNumber = findViewById(R.id.tv_line_number);
        TextView tvCause = findViewById(R.id.tv_cause);
        TextView tvStackTrace = findViewById(R.id.tv_stack_trace);

        ExceptionInfoBean exceptionInfoBean = getIntent().getParcelableExtra(UCEHandler.EXTRA_EXCEPTION_INFO);
        if (exceptionInfoBean == null) {
            return;
        }
        String errorInfo = getExceptionInfoString(this, exceptionInfoBean);
        tvType.setText(Html.fromHtml("ExceptionType: <br/>" + getHtmlCodeBlueStr(exceptionInfoBean.getExceptionType())));
        String methodName = getHtmlCodeBlueStr(exceptionInfoBean.getClassName()) + "." + getHtmlCodeStr(exceptionInfoBean.getMethodName());
        tvMethodName.setText(Html.fromHtml("MethodName: <br/>" + methodName));
        tvLineNumber.setText(Html.fromHtml(getHtmlCodeBlueStr("LineNumber: " + exceptionInfoBean.getLineNumber())));
        tvCause.setText(exceptionInfoBean.getCause());
        tvStackTrace.setText(errorInfo);
    }

    private void saveErrorLogToFile(boolean isShowToast) {
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent && isExternalStorageWritable()) {
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String strCurrentDate = dateFormat.format(currentDate);
            strCurrentDate = strCurrentDate.replace(" ", "_");
            String errorLogFileName = getApplicationName(UCEDefaultActivity.this) + "_Error-Log_" + strCurrentDate;
            String errorLog = getPureErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
            String fullPath = Environment.getExternalStorageDirectory() + "/AppErrorLogs_UCEH/";
            FileOutputStream outputStream;
            try {
                File file = new File(fullPath);
                file.mkdir();
                File txtFile = new File(fullPath + errorLogFileName + ".txt");
                txtFile.createNewFile();
                outputStream = new FileOutputStream(txtFile);
                outputStream.write(errorLog.getBytes());
                outputStream.close();
                if (txtFile.exists() && isShowToast) {
                    Toast.makeText(this, "File Saved Successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e("REQUIRED", "This app does not have write storage permission to save log file.");
                if (isShowToast) {
                    Toast.makeText(this, "Storage Permission Not Found", Toast.LENGTH_SHORT).show();
                }
                e.printStackTrace();
            }
        }
    }

    private void shareErrorLog() {
        if (TextUtils.isEmpty(pureStrCurrentErrorLog)) {
            pureStrCurrentErrorLog = getPureErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
        }
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Application Crash Error Log");
        share.putExtra(Intent.EXTRA_TEXT, pureStrCurrentErrorLog);
        startActivity(Intent.createChooser(share, "Share Error Log"));
    }

    private void copyErrorToClipboard() {
        String errorInformation = getPureErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("View Error Log", errorInformation);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(UCEDefaultActivity.this, "Error Log Copied", Toast.LENGTH_SHORT).show();
        }
    }

    private String getPureErrorDetailsFromIntent(Context context, Intent intent) {
        ExceptionInfoBean exceptionInfoBean = intent.getParcelableExtra(UCEHandler.EXTRA_EXCEPTION_INFO);
        if (TextUtils.isEmpty(pureStrCurrentErrorLog)) {
            StringBuilder errorReport = UCEHandlerHelper.getFullExceptionInfoString(context, exceptionInfoBean);
            pureStrCurrentErrorLog = errorReport.toString();
            return pureStrCurrentErrorLog;
        } else {
            return pureStrCurrentErrorLog;
        }
    }

    private String getExceptionInfoString(Context context, ExceptionInfoBean exceptionInfoBean) {
        return UCEHandlerHelper.getExceptionInfoString(context, exceptionInfoBean).toString();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private static String getHtmlCodeStr(String string) {
        return "<font color='#9976a8'>" + string + "</font>";
    }

    private static String getHtmlCodeBlueStr(String string) {
        return "<font color='#6a98b9'>" + string + "</font>";
    }

    private static String getHtmlCodeBlueStr(int value) {
        return getHtmlCodeBlueStr(Integer.toString(value));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crash_info_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_copy) {
            copyErrorToClipboard();
        } else if (i == R.id.action_save) {
            saveErrorLogToFile(true);
        } else if (i == R.id.action_share) {
            shareErrorLog();
        }
        return super.onOptionsItemSelected(item);
    }
}
