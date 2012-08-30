/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mobile.ui.code;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.mobile.R.layout;
import com.github.mobile.R.string;
import com.github.mobile.core.code.FullTree.Entry;
import com.github.mobile.core.code.FullTree.Folder;
import com.github.mobile.core.commit.CommitUtils;
import com.github.mobile.util.TypefaceUtils;
import com.viewpagerindicator.R.id;

/**
 * Adapter to display a source code tree
 */
public class CodeTreeAdapter extends MultiTypeAdapter {

    private static final int TYPE_BLOB = 0;

    private static final int TYPE_TREE = 1;

    private final String sizeSingle;

    private final String sizePlural;

    /**
     * @param activity
     */
    public CodeTreeAdapter(Activity activity) {
        super(activity);

        Resources resources = activity.getResources();
        sizeSingle = resources.getString(string.one_byte);
        sizePlural = resources.getString(string.bytes);
    }

    /**
     * @param context
     */
    public CodeTreeAdapter(Context context) {
        super(context);

        Resources resources = context.getResources();
        sizeSingle = resources.getString(string.one_byte);
        sizePlural = resources.getString(string.bytes);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Set root folder to display
     *
     * @param root
     */
    public void setItems(final Folder root) {
        clear();

        addItems(TYPE_TREE, root.folders.values());
        addItems(TYPE_BLOB, root.files.values());
    }

    @Override
    protected int getChildLayoutId(final int type) {
        switch (type) {
        case TYPE_BLOB:
            return layout.blob_item;
        case TYPE_TREE:
            return layout.folder_item;
        default:
            return -1;
        }
    }

    @Override
    protected int[] getChildViewIds(final int type) {
        switch (type) {
        case TYPE_BLOB:
            return new int[] { id.tv_file, id.tv_size };
        case TYPE_TREE:
            return new int[] { id.tv_folder, id.tv_folders, id.tv_files };
        default:
            return null;
        }
    }

    @Override
    protected View initialize(final int type, final View view) {
        switch (type) {
        case TYPE_BLOB:
            TypefaceUtils.setOcticons((TextView) view
                    .findViewById(id.tv_file_icon));
            break;
        case TYPE_TREE:
            TypefaceUtils.setOcticons(
                    (TextView) view.findViewById(id.tv_folder_icon),
                    (TextView) view.findViewById(id.tv_folders_icon),
                    (TextView) view.findViewById(id.tv_files_icon));
        }

        return super.initialize(type, view);
    }

    @Override
    protected void update(final int position, final Object item, final int type) {
        switch (type) {
        case TYPE_BLOB:
            Entry file = (Entry) item;
            setText(id.tv_file, file.name);
            long size = file.entry.getSize();
            if (size != 1)
                setText(id.tv_size, FORMAT_INT.format(size) + ' ' + sizePlural);
            else
                setText(id.tv_size, sizeSingle);

            break;
        case TYPE_TREE:
            Folder folder = (Folder) item;
            setText(id.tv_folder, CommitUtils.getName(folder.name));
            setNumber(id.tv_folders, folder.folders.size());
            setNumber(id.tv_files, folder.files.size());
            break;
        }
    }
}
