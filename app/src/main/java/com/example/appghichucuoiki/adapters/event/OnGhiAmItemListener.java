package com.example.appghichucuoiki.adapters.event;

import java.io.File;

public interface OnGhiAmItemListener {
    void onClickListener(File file, int position);
    void onDeleteFileRecord(File file, int position);

}
